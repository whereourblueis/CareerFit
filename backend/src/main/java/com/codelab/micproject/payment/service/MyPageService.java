package com.codelab.micproject.payment.service;

import com.codelab.micproject.account.user.repository.UserRepository;
import com.codelab.micproject.payment.domain.Order;
import com.codelab.micproject.payment.dto.OrderView;
import com.codelab.micproject.payment.repository.OrderRepository;
import com.codelab.micproject.security.oauth2.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<OrderView> myOrders(UserPrincipal me) {
        var user = userRepository.findById(me.id()).orElseThrow();
        return orderRepository.findByUser(user).stream().map(this::toView).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderView> myConsultantOrders(UserPrincipal me) {
        var meUser = userRepository.findById(me.id()).orElseThrow();
        return orderRepository.findByConsultant(meUser).stream().map(this::toView).toList();
    }

    private OrderView toView(Order o) {
        var apps = o.getAppointments().stream()
                .map(oa -> oa.getAppointment())
                .map(a -> new OrderView.AppointmentSummary(
                        a.getId(), a.getStartAt(), a.getEndAt(), a.getStatus().name(), a.getMeetingUrl()
                )).toList();

        return new OrderView(
                o.getId(),
                o.getConsultant().getId(),
                o.getConsultant().getName(),
                o.getBundleCount(),
                o.getUnitPrice(),
                o.getTotalPrice(),
                o.getStatus().name(),
                o.getCreatedAt(),
                apps
        );
    }
}