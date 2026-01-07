package com.codelab.micproject.payment.domain;


import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(indexes = {@Index(columnList = "order_id")})
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", unique = true)
    private Order order;


    private String method; // CARD, KAKAO, NAVER 등(스켈레톤)
    @Enumerated(EnumType.STRING) private PaymentStatus status;


    @Column(precision = 12, scale = 0) private BigDecimal amount;
    private String pgTransactionId; // PG가 부여하는 거래번호(가짜 값)
    private OffsetDateTime createdAt;

    @Version
    private Long version;
}