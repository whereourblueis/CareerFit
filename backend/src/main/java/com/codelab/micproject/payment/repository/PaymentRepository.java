package com.codelab.micproject.payment.repository;

import com.codelab.micproject.payment.domain.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    // 동일 거래번호 중복 방지(선택. PG에서 같은 트랜잭션ID만 온다고 가정)
    Optional<Payment> findByPgTransactionId(String pgTransactionId);

    // 멱등 보장을 위해 웹훅 처리 시 쓰기 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.order.id = :orderId")
    Optional<Payment> lockByOrderId(@Param("orderId") Long orderId);
}