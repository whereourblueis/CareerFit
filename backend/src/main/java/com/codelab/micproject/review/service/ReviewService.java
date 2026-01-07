package com.codelab.micproject.review.service;

import com.codelab.micproject.account.user.domain.UserRole;
import com.codelab.micproject.account.user.repository.UserRepository;
import com.codelab.micproject.booking.domain.AppointmentStatus;
import com.codelab.micproject.payment.repository.OrderAppointmentRepository;
import com.codelab.micproject.review.domain.Review;
import com.codelab.micproject.review.dto.CreateReview;
import com.codelab.micproject.review.dto.ReviewView;
import com.codelab.micproject.review.repository.ReviewRepository;
import com.codelab.micproject.security.oauth2.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final OrderAppointmentRepository orderAppointmentRepository; // 리뷰 진입 조건 확인용

    @Transactional
    public ReviewView create(UserPrincipal me, CreateReview req) {
        var reviewer   = userRepository.findById(me.id()).orElseThrow();
        var consultant = userRepository.findById(req.consultantId()).orElseThrow();

        // 컨설턴트 권한 검증
        if (consultant.getRole() != UserRole.CONSULTANT) {
            throw new IllegalStateException("대상 사용자는 컨설턴트가 아닙니다.");
        }

        // (옵션) 같은 유저→같은 컨설턴트 중복 리뷰 방지
        if (reviewRepository.existsByReviewerAndConsultant(reviewer, consultant)) {
            throw new IllegalStateException("이미 이 컨설턴트에 대한 리뷰를 작성하셨습니다.");
        }

        // ----- 리뷰 작성 진입 조건 -----
        // 1) DONE 예약 존재 여부
        boolean allowed = orderAppointmentRepository
                .existsByAppointment_ConsultantAndAppointment_UserAndAppointment_Status(
                        consultant, reviewer, AppointmentStatus.DONE);

        // 2) (선택) 승인됐고 이미 종료된 예약도 허용
        if (!allowed) {
            allowed = orderAppointmentRepository
                    .existsByAppointment_ConsultantAndAppointment_UserAndAppointment_StatusAndAppointment_EndAtBefore(
                            consultant, reviewer, AppointmentStatus.APPROVED, OffsetDateTime.now());
        }

        if (!allowed) {
            throw new IllegalStateException("면접 종료 이후에만 리뷰를 작성할 수 있습니다.");
        }
        // -----------------------------

        var r = Review.builder()
                .reviewer(reviewer)
                .consultant(consultant)
                .rating(req.rating())
                .comment(req.comment())
                .build();
        reviewRepository.save(r);
        return toView(r);
    }

    @Transactional(readOnly = true)
    public List<ReviewView> listForConsultant(Long consultantId) {
        var consultant = userRepository.findById(consultantId).orElseThrow();
        return reviewRepository.findByConsultantOrderByCreatedAtDesc(consultant)
                .stream()
                .map(this::toView)
                .toList();
    }

    private ReviewView toView(Review r) {
        return new ReviewView(
                r.getId(),
                r.getReviewer().getId(),
                r.getConsultant().getId(),
                r.getRating(),
                r.getComment(),
                r.getCreatedAt()
        );
    }
}
