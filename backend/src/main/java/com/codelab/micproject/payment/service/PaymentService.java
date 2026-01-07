package com.codelab.micproject.payment.service;

import com.codelab.micproject.account.consultant.domain.ConsultantLevel;
import com.codelab.micproject.account.consultant.repository.ConsultantMetaRepository;
import com.codelab.micproject.account.user.domain.User;
import com.codelab.micproject.account.user.domain.UserRole;
import com.codelab.micproject.account.user.repository.UserRepository;
import com.codelab.micproject.booking.domain.Appointment;
import com.codelab.micproject.booking.domain.AppointmentStatus;
import com.codelab.micproject.booking.dto.SlotDto;
import com.codelab.micproject.booking.repository.AppointmentRepository;
import com.codelab.micproject.booking.repository.AvailabilityRepository;
import com.codelab.micproject.payment.domain.Order;
import com.codelab.micproject.payment.domain.OrderStatus;
import com.codelab.micproject.payment.domain.OrderAppointment;
import com.codelab.micproject.payment.domain.Payment;
import com.codelab.micproject.payment.domain.PaymentStatus;
import com.codelab.micproject.payment.dto.CheckoutRequest;
import com.codelab.micproject.payment.dto.CheckoutResponse;
import com.codelab.micproject.payment.dto.PaymentWebhook;
import com.codelab.micproject.payment.repository.OrderAppointmentRepository;
import com.codelab.micproject.payment.repository.OrderRepository;
import com.codelab.micproject.payment.repository.PaymentRepository;
import com.codelab.micproject.security.oauth2.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;
    private final ConsultantMetaRepository metaRepository;
    private final AppointmentRepository appointmentRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderAppointmentRepository orderAppointmentRepository;
    private final AvailabilityRepository availabilityRepository; // ← 슬롯 유효성 검증용

    // 1) 체크아웃: 예약 REQUESTED 생성 + 주문/결제 레코드 생성 + 결제URL 반환
    @Transactional
    public CheckoutResponse checkout(UserPrincipal me, CheckoutRequest req) {
        var user = userRepository.findById(me.id()).orElseThrow();
        var consultant = userRepository.findById(req.consultantId()).orElseThrow();
        if (consultant.getRole() != UserRole.CONSULTANT) {
            throw new IllegalStateException("not a consultant");
        }
        if (req.slots().size() != req.bundle().count) {
            throw new IllegalStateException("slot count must match bundle count");
        }

        // 가격 산정 (등급 기본가)
        var meta = metaRepository.findByConsultant(consultant).orElse(null);
        var level = meta != null ? meta.getLevel() : ConsultantLevel.JUNIOR;
        BigDecimal unit = (meta != null && meta.getBasePrice() != null)
                ? meta.getBasePrice()
                : switch (level) {
            case JUNIOR -> BigDecimal.valueOf(30000);
            case SENIOR -> BigDecimal.valueOf(50000);
            case EXECUTIVE -> BigDecimal.valueOf(90000);
        };
        BigDecimal total = unit.multiply(BigDecimal.valueOf(req.bundle().count));

        // 주문 생성
        var order = Order.builder()
                .user(user)
                .consultant(consultant)
                .bundleCount(req.bundle().count)
                .unitPrice(unit)
                .totalPrice(total)
                .status(OrderStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .build();
        orderRepository.save(order);

        // 예약 생성 + 주문과 연결
        for (var s : req.slots()) {
            // (A) 가용시간 매칭/정렬 검증
            if (!isValidSlotForConsultant(consultant, s)) {
                throw new IllegalArgumentException("invalid slot for consultant availability");
            }
            // (B) 더블부킹 방지(겹침 검사)
            boolean overlap = !appointmentRepository
                    .findByConsultantAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
                            consultant, s.endAt(), s.startAt()
                    ).isEmpty();
            if (overlap) {
                throw new IllegalStateException("time slot already booked");
            }
            // (C) 생성/연결
            var a = Appointment.builder()
                    .consultant(consultant)
                    .user(user)
                    .startAt(s.startAt())
                    .endAt(s.endAt())
                    .status(AppointmentStatus.REQUESTED)
                    .build();
            appointmentRepository.save(a);
            orderAppointmentRepository.save(
                    OrderAppointment.builder().order(order).appointment(a).build()
            );
        }

        // 결제 생성 (READY)
        var pay = Payment.builder()
                .order(order)
                .method(req.method())
                .status(PaymentStatus.READY)
                .amount(total)
                .createdAt(OffsetDateTime.now())
                .build();
        paymentRepository.save(pay);

        // 더미 결제 URL
        String url = "https://pg.example.com/pay?orderId=" + order.getId();
        return new CheckoutResponse(order.getId(), total, url);
    }

    // 2) PG 웹훅: 멱등 처리 + 예약 확정/취소
    @Transactional
    public void webhook(PaymentWebhook webhook) {
        var order = orderRepository.findById(webhook.orderId()).orElseThrow();
        // 쓰기 락으로 동시 호출 방지
        var payment = paymentRepository.lockByOrderId(order.getId()).orElseThrow();

        // 이미 최종 상태면 멱등 처리
        if (payment.getStatus() == PaymentStatus.SUCCESS || payment.getStatus() == PaymentStatus.FAILED) return;
        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.CANCELED) return;

        // (선택) 동일 트랜잭션ID 재사용 방지
        if (webhook.pgTransactionId() != null
                && paymentRepository.findByPgTransactionId(webhook.pgTransactionId()).isPresent()) return;

        if ("SUCCESS".equalsIgnoreCase(webhook.status())) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPgTransactionId(webhook.pgTransactionId());
            order.setStatus(OrderStatus.PAID);
            // 예약 확정 + 미팅 URL
            for (var oa : order.getAppointments()) {
                var a = oa.getAppointment();
                a.setStatus(AppointmentStatus.APPROVED);
                a.setMeetingUrl("https://meet.example.com/" + a.getId());
            }
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.CANCELED);
            for (var oa : order.getAppointments()) {
                var a = oa.getAppointment();
                a.setStatus(AppointmentStatus.CANCELLED);
            }
        }
    }

    /** 컨설턴트의 가용시간에 맞는 슬롯인지 검증(MVP: 슬롯 길이 = slotMinutes 1칸) */
    private boolean isValidSlotForConsultant(User consultant, SlotDto s) {
        var availList = availabilityRepository.findByConsultant(consultant);
        if (availList.isEmpty()) return false;

        var start = s.startAt();
        var end = s.endAt();
        if (end.isBefore(start)) return false;

        var day = start.atZoneSameInstant(java.time.ZoneId.systemDefault()).toLocalDate();
        var wd = day.getDayOfWeek().getValue(); // 1~7

        for (var av : availList) {
            if (av.getWeekday() != wd) continue;

            var zone = av.getZoneId() != null
                    ? java.time.ZoneId.of(av.getZoneId())
                    : java.time.ZoneId.systemDefault();

            var startLocal = java.time.LocalTime.parse(av.getStartTime());
            var endLocal = java.time.LocalTime.parse(av.getEndTime());

            // 요청 슬롯을 가용 타임존 기준으로 로컬화
            var startLocalFromReq = s.startAt().atZoneSameInstant(zone).toLocalTime();
            var endLocalFromReq = s.endAt().atZoneSameInstant(zone).toLocalTime();

            // 범위 내부인지
            if (startLocalFromReq.isBefore(startLocal) || endLocalFromReq.isAfter(endLocal)) continue;

            // 간격 정렬 체크 (한 슬롯 길이만 허용)
            long minutesFromStart = java.time.Duration.between(startLocal, startLocalFromReq).toMinutes();
            long minutesDuration = java.time.Duration.between(startLocalFromReq, endLocalFromReq).toMinutes();
            if (minutesFromStart % av.getSlotMinutes() != 0) continue;
            if (minutesDuration != av.getSlotMinutes()) continue;

            return true;
        }
        return false;
    }
}
