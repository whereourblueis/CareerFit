package com.codelab.micproject.review.domain;


import com.codelab.micproject.account.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.OffsetDateTime;


@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="reviewer_id")
    private User reviewer;


    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="consultant_id")
    private User consultant;


    private int rating; // 1~5
    @Column(length = 1000) private String comment;


    @CreationTimestamp
    private OffsetDateTime createdAt;
}