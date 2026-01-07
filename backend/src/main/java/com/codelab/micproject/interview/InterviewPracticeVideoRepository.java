package com.codelab.micproject.interview;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewPracticeVideoRepository extends JpaRepository<PracticeVideo, Long> {
    // TODO: 추후 특정 사용자의 영상 목록을 찾는 메서드를 추가할 수 있습니다.
    // List<PracticeVideo> findByUserIdOrderByCreatedAtDesc(Long userId);
}
