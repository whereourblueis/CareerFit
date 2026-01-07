package com.codelab.micproject.payment.repository;

import com.codelab.micproject.payment.domain.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RefundRepository extends JpaRepository<Refund, Long> {}