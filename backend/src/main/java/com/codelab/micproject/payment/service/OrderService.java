package com.codelab.micproject.payment.service;

import com.codelab.micproject.account.user.repository.UserRepository;
import com.codelab.micproject.booking.domain.AppointmentStatus;
import com.codelab.micproject.payment.domain.*;
import com.codelab.micproject.payment.repository.*;
import com.codelab.micproject.security.oauth2.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service @RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;

    /** 사용자 본인이 주문 취소 */
    @Transactional
    public void cancelMyOrder(UserPrincipal me, Long orderId, String reason) {
        var user = userRepository.findById(me.id()).orElseThrow();
        var order = orderRepository.findById(orderId).orElseThrow();
        if (!order.getUser().getId().equals(user.getId()))
            throw new IllegalStateException("not your order");

        // 이미 취소/환불된 주문이면 멱등 처리
        if (order.getStatus() == OrderStatus.CANCELED) return;

        // CREATED: 결제 전 취소 -> 그냥 취소
        if (order.getStatus() == OrderStatus.CREATED) {
            order.setStatus(OrderStatus.CANCELED);
            order.getAppointments().forEach(oa ->
                    oa.getAppointment().setStatus(AppointmentStatus.CANCELLED));
            // READY 상태의 Payment가 있다면 FAILED로
            paymentRepository.findByOrderId(order.getId()).ifPresent(p -> {
                if (p.getStatus() == PaymentStatus.READY) p.setStatus(PaymentStatus.FAILED);
            });
            return;
        }

        // PAID: 환불 레코드 남기고 취소
        if (order.getStatus() == OrderStatus.PAID) {
            // (비즈니스 룰 예시) 이미 종료된 예약이 있으면 거절
            boolean hasPast = order.getAppointments().stream()
                    .anyMatch(oa -> oa.getAppointment().getEndAt().isBefore(OffsetDateTime.now()));
            if (hasPast) throw new IllegalStateException("some appointments already finished");

            // 환불 레코드 (PG 연동 전이므로 즉시 COMPLETED)
            refundRepository.save(Refund.builder()
                    .order(order)
                    .amount(order.getTotalPrice())
                    .status(RefundStatus.COMPLETED)
                    .reason(reason)
                    .createdAt(OffsetDateTime.now())
                    .completedAt(OffsetDateTime.now())
                    .build());

            // 상태 갱신
            order.setStatus(OrderStatus.CANCELED);
            order.getAppointments().forEach(oa ->
                    oa.getAppointment().setStatus(AppointmentStatus.CANCELLED));
        }
    }
}