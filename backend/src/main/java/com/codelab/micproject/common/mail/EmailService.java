package com.codelab.micproject.common.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Qualifier("gmailSender")
    private final JavaMailSender gmailSender;

    @Qualifier("naverSender")
    private final JavaMailSender naverSender;

    @Value("${app.email.verify-base-url}")
    private String verifyBaseUrl;

    @Value("${app.mail.gmail.from}")
    private String gmailFrom;

    @Value("${app.mail.naver.from}")
    private String naverFrom;

    public enum Provider { GMAIL, NAVER }

    public void sendVerificationEmail(Provider provider, String to, String token) {
        String subject = "[CareerFit] 이메일 인증을 완료해 주세요";
        String verifyLink = verifyBaseUrl + "?token=" + token;
        String html = """
            <h3>이메일 인증</h3>
            <p>아래 링크를 눌러 인증을 완료해 주세요:</p>
            <a href="%s">%s</a>
        """.formatted(verifyLink, verifyLink);

        JavaMailSender sender = (provider == Provider.GMAIL) ? gmailSender : naverSender;
        String from = (provider == Provider.GMAIL) ? gmailFrom : naverFrom;
        sendHtml(sender, from, to, subject, html);
    }

    private void sendHtml(JavaMailSender sender, String from, String to, String subject, String html) {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(from);       // ★ SMTP username과 동일해야 함
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            sender.send(message);
        } catch (Exception e) {
            // 원인 파악을 위해 스택트레이스/메시지 그대로 남기기
            // (컨트롤러 레벨에서는 사용자에겐 "서버 오류"만 노출)
            System.err.println("Mail send failed: " + e.getMessage());
            throw new IllegalStateException("이메일 전송 실패", e);
        }
    }

    public void sendHtmlAutoFrom(String to, String subject, String html) {
        Provider provider = to.endsWith("@naver.com") ? Provider.NAVER : Provider.GMAIL;
        JavaMailSender sender = (provider == Provider.GMAIL) ? gmailSender : naverSender;
        String from = (provider == Provider.GMAIL) ? gmailFrom : naverFrom; // @Value로 주입
        sendHtml(sender, from, to, subject, html);
    }
}
