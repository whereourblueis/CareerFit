package com.codelab.micproject.booking.service;

import com.codelab.micproject.account.consultant.domain.*;
import com.codelab.micproject.account.consultant.repository.ConsultantMetaRepository;
import com.codelab.micproject.account.user.domain.UserRole;
import com.codelab.micproject.account.user.repository.UserRepository;
import com.codelab.micproject.booking.domain.*;
import com.codelab.micproject.booking.dto.*;
import com.codelab.micproject.booking.repository.AppointmentRepository;
import com.codelab.micproject.security.oauth2.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;


@Service @RequiredArgsConstructor
public class BookingService {
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final ConsultantMetaRepository metaRepository;


    @Transactional
    public AppointmentView request(UserPrincipal me, RequestAppointment req){
        var user = userRepository.findById(me.id()).orElseThrow();
        var consultant = userRepository.findById(req.consultantId()).orElseThrow();
        if (consultant.getRole() != UserRole.CONSULTANT) throw new IllegalStateException("not a consultant");
        boolean overlap = !appointmentRepository
                .findByConsultantAndStartAtLessThanEqualAndEndAtGreaterThanEqual(consultant, req.endAt(), req.startAt()).isEmpty();
        if (overlap) throw new IllegalStateException("time slot already booked");
        var a = Appointment.builder().consultant(consultant).user(user)
                .startAt(req.startAt()).endAt(req.endAt())
                .status(AppointmentStatus.REQUESTED).build();
        appointmentRepository.save(a);
        return new AppointmentView(a.getId(), consultant.getId(), user.getId(), a.getStartAt(), a.getEndAt(), a.getStatus().name(), a.getMeetingUrl());
    }


    @Transactional
    public List<AppointmentView> requestBatch(UserPrincipal me, RequestAppointmentBatch req){
        var user = userRepository.findById(me.id()).orElseThrow();
        var consultant = userRepository.findById(req.consultantId()).orElseThrow();
        if (consultant.getRole() != UserRole.CONSULTANT) throw new IllegalStateException("not a consultant");
        if (req.slots().size() != req.bundle().count) throw new IllegalStateException("slot count must match bundle count");


        return req.slots().stream().map(s -> {
            boolean overlap = !appointmentRepository
                    .findByConsultantAndStartAtLessThanEqualAndEndAtGreaterThanEqual(consultant, s.endAt(), s.startAt()).isEmpty();
            if (overlap) throw new IllegalStateException("time slot already booked");
            var a = Appointment.builder().consultant(consultant).user(user)
                    .startAt(s.startAt()).endAt(s.endAt())
                    .status(AppointmentStatus.REQUESTED).build();
            appointmentRepository.save(a);
            return new AppointmentView(a.getId(), consultant.getId(), user.getId(), a.getStartAt(), a.getEndAt(), a.getStatus().name(), a.getMeetingUrl());
        }).toList();
    }


    @Transactional
    public AppointmentView approve(UserPrincipal me, Long appointmentId){
        var a = appointmentRepository.findById(appointmentId).orElseThrow();
        var meUser = userRepository.findById(me.id()).orElseThrow();
        if (!a.getConsultant().getId().equals(meUser.getId())) throw new IllegalStateException("only consultant can approve");
        a.setStatus(AppointmentStatus.APPROVED);
        a.setMeetingUrl("https://meet.example.com/"+a.getId());
        return new AppointmentView(a.getId(), a.getConsultant().getId(), a.getUser().getId(), a.getStartAt(), a.getEndAt(), a.getStatus().name(), a.getMeetingUrl());
    }


    // 결제 전 견적 계산 (등급 가격 × 번들 수)
    @Transactional(readOnly = true)
    public QuoteResponse quote(Long consultantId, SessionBundle bundle){
        var consultant = userRepository.findById(consultantId).orElseThrow();
        var meta = metaRepository.findByConsultant(consultant).orElse(null);
        var level = meta!=null? meta.getLevel(): ConsultantLevel.JUNIOR;
        BigDecimal unit = meta!=null && meta.getBasePrice()!=null? meta.getBasePrice():
                switch (level){
                    case JUNIOR -> BigDecimal.valueOf(30000);
                    case SENIOR -> BigDecimal.valueOf(50000);
                    case EXECUTIVE -> BigDecimal.valueOf(90000);
                };
        return new QuoteResponse(consultantId, level.name(), bundle.count, unit, unit.multiply(BigDecimal.valueOf(bundle.count)));
    }
}