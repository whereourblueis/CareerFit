package com.codelab.micproject.interview;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 생성 시간을 자동으로 기록하기 위해 추가
public class PracticeVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: 추후 사용자(User) 엔티티와 연관관계 매핑이 필요합니다. (예: @ManyToOne)
    // private Long userId;

    @Column(nullable = false, length = 1024)
    private String videoUrl;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public PracticeVideo(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
