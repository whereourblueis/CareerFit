package com.codelab.micproject.account.consultant.service;

import com.codelab.micproject.account.consultant.domain.ConsultantLevel;
import com.codelab.micproject.account.consultant.domain.ConsultantMeta;
import com.codelab.micproject.account.consultant.dto.ConsultantCardDto;
import com.codelab.micproject.account.consultant.repository.ConsultantMetaRepository;
import com.codelab.micproject.account.profile.domain.Profile;
import com.codelab.micproject.account.profile.repository.ProfileRepository;
import com.codelab.micproject.account.user.domain.User;
import com.codelab.micproject.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultantService {

    private final ProfileRepository profileRepository;
    private final ConsultantMetaRepository metaRepository;
    private final ReviewRepository reviewRepository;

    public List<ConsultantCardDto> list(String levelFilter, Double minRating, String sort) {
        // … (네 기존 정렬/필터 로직 유지)
        // 필요하다면 여기서 levelFilter, minRating 적용
        // 아래 toCard(User)만 교체하면 화면의 오류는 해결됨.
        return List.of(); // 실제 구현은 네 기존 코드 사용
    }

    private ConsultantCardDto toCard(User u) {
        Profile p = profileRepository.findByUser(u).orElse(null);

        ConsultantMeta meta = metaRepository.findByConsultant(u).orElse(null);
        ConsultantLevel level = (meta != null) ? meta.getLevel() : ConsultantLevel.JUNIOR;

        BigDecimal price = (meta != null && meta.getBasePrice() != null)
                ? meta.getBasePrice()
                : defaultPrice(level);

        // ✅ 평균/개수는 쿼리 메서드로 바로 조회
        Double avgObj = reviewRepository.avgRatingByConsultant(u); // null 가능
        double avg = (avgObj != null) ? avgObj : 0.0;
        long count = reviewRepository.countByConsultant(u);

        return new ConsultantCardDto(
                u.getId(),
                u.getName(),
                level.name(),
                (p != null ? p.getBio() : null),
                price,
                avg,
                count
        );
    }

    private BigDecimal defaultPrice(ConsultantLevel level) {
        return switch (level) {
            case JUNIOR   -> BigDecimal.valueOf(30000);
            case SENIOR   -> BigDecimal.valueOf(60000);
            case EXECUTIVE-> BigDecimal.valueOf(90000);
        };
    }
}
