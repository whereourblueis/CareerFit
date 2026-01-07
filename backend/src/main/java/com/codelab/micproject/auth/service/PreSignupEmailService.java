package com.codelab.micproject.auth.service;

import com.codelab.micproject.auth.domain.PreEmailToken;
import com.codelab.micproject.auth.repository.PreEmailTokenRepository;
import com.codelab.micproject.common.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PreSignupEmailService {

    private final PreEmailTokenRepository preRepo;
    private final EmailService emailService;

    @Value("${app.email.verify-base-url}")      // ex) http://localhost:8080/api/auth/email/verify
    private String verifyBaseUrl;

    /** 이메일로 사전인증 토큰 발송 */
    @Transactional
    public void send(String email) {
        // 이전 토큰 정리(선택)
        preRepo.deleteByEmail(email);

        String token = UUID.randomUUID().toString();
        PreEmailToken pet = PreEmailToken.builder()
                .email(email)
                .token(token)
                .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                .verified(false)
                .build();
        preRepo.save(pet);

        String link = verifyBaseUrl + "?token=" + token;
        String subject = "[CareerFit] 이메일 인증을 완료해 주세요";
        String html = """
            <h3>이메일 인증</h3>
            <p>아래 링크를 눌러 인증을 완료해 주세요.</p>
            <a href="%s">%s</a>
        """.formatted(link, link);

        // 기본 발송 채널(예: NAVER). 바꾸고 싶으면 EmailService.Provider.GMAIL 로 변경.
        emailService.sendVerificationEmail(EmailService.Provider.NAVER, email, token);
        // (혹은 emailService.sendHtml( ... ) 직접 호출 구조라면 적절히 변경)
    }

    /** 토큰 검증 → verified=true 로 마킹 후 이메일 반환 */
    @Transactional
    public String verify(String token) {
        PreEmailToken pet = preRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        if (pet.isVerified()) {
            return pet.getEmail(); // 이미 인증됨
        }
        if (pet.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("만료된 토큰입니다.");
        }
        pet.setVerified(true);
        preRepo.save(pet);
        return pet.getEmail();
    }

    /** 해당 이메일이 사전인증 완료 상태인지 */
    @Transactional(readOnly = true)
    public boolean isVerified(String email) {
        return preRepo.findTopByEmailOrderByIdDesc(email)
                .map(PreEmailToken::isVerified)
                .orElse(false);
    }

    /** 가입 완료 시 사전 토큰 정리(선택) */
    @Transactional
    public void cleanup(String email) {
        preRepo.deleteByEmail(email);
    }
}
