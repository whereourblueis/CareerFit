package com.codelab.micproject.payment.repository;


import com.codelab.micproject.account.user.domain.User;
import com.codelab.micproject.payment.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByConsultant(User consultant);
}