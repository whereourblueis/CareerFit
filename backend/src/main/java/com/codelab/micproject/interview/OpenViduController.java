package com.codelab.micproject.interview;

import io.openvidu.java.client.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.codelab.micproject.interview.PracticeVideo;
import com.codelab.micproject.interview.InterviewPracticeVideoRepository;
import com.codelab.micproject.interview.S3UploadService;
import org.springframework.web.bind.annotation.PostMapping;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.Map;

@RestController
@RequestMapping("/api/openvidu")
public class OpenViduController {

    @Value("${openvidu.url}")
    private String openviduUrl;
    @Value("${openvidu.secret}")
    private String openviduSecret;
    private OpenVidu openvidu;

    private final S3UploadService s3UploadService;
    private final InterviewPracticeVideoRepository practiceVideoRepository;

    public OpenViduController(S3UploadService s3UploadService, InterviewPracticeVideoRepository practiceVideoRepository) {
        this.s3UploadService = s3UploadService;
        this.practiceVideoRepository = practiceVideoRepository;
    }

    @PostConstruct
    public void init() {
        this.openvidu = new OpenVidu(openviduUrl, openviduSecret);
    }
    
    // [수정] 아래에 누락된 메서드들을 다시 추가하고, 괄호 쌍을 맞춥니다.

    /**
     * @param params The Session properties
     * @return The Session ID
     */
    @PostMapping("/sessions")
    public ResponseEntity<String> initializeSession(@RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        SessionProperties properties = SessionProperties.fromJson(params).build();
        Session session = openvidu.createSession(properties);
        return new ResponseEntity<>(session.getSessionId(), HttpStatus.OK);
    }

    /**
     * @param sessionId The Session in which to create the Connection
     * @param params    The Connection properties
     * @return The Token
     */
    @PostMapping("/sessions/{sessionId}/connections")
    public ResponseEntity<String> createConnection(@PathVariable("sessionId") String sessionId,
                                                   @RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openvidu.getActiveSession(sessionId);
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
        Connection connection = session.createConnection(properties);
        return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
    }

    /**
     * OpenVidu 서버로부터 녹화 관련 웹훅 이벤트를 수신합니다.
     * OpenVidu 설정에 'openvidu.recording.notification=http://<your-backend-host>/api/openvidu/webhook' 와 같이
     * 이 엔드포인트를 웹훅 URL로 등록해야 합니다.
     *
     * @param params OpenVidu가 보내는 이벤트 페이로드
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> recordingWebhook(@RequestBody(required = false) Map<String, Object> params) {
        if (params == null || !params.containsKey("event")) {
            return ResponseEntity.ok().build();
        }

        // 녹화 파일이 준비되었다는 이벤트인지 확인합니다.
        String event = (String) params.get("event");
        if (!"recordingStatusChanged".equals(event)) {
            return ResponseEntity.ok().build();
        }

        // 녹화 상태가 'ready' (다운로드 가능) 상태인지 확인합니다.
        String status = (String) params.get("status");
        if (!"ready".equals(status)) {
            return ResponseEntity.ok().build();
        }

        // 녹화 파일 정보를 추출합니다.
        String recordingId = (String) params.get("id");
        String downloadUrl = (String) params.get("url"); // 다운로드 URL

        // 다운로드 URL이 없으면 처리할 수 없음
        if (downloadUrl == null || downloadUrl.isBlank()) {
            System.err.println("Webhook 'ready' status received but no download URL was provided for recording " + recordingId);
            return ResponseEntity.ok().build();
        }

        File tempFile = null;
        try {
            // 1. OpenVidu 서버에서 녹화 파일을 임시 파일로 다운로드합니다.
            URL url = new URL(downloadUrl);
            tempFile = Files.createTempFile("recording-", ".mp4").toFile();
            try (InputStream in = url.openStream()) {
                Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            // 2. 임시 파일을 S3에 업로드합니다.
            String s3FileName = "practice-videos/" + recordingId + ".mp4";
            String videoUrl = s3UploadService.uploadFile(s3FileName, tempFile);

            // 3. 데이터베이스에 영상 정보를 저장합니다.
            PracticeVideo practiceVideo = new PracticeVideo(videoUrl);
            practiceVideoRepository.save(practiceVideo);

            // 실제 프로덕션 코드에서는 로깅 라이브러리(SLF4J 등)를 사용해야 합니다.
            System.out.println("녹화 파일이 성공적으로 S3에 업로드되었습니다: " + videoUrl);

        } catch (Exception e) {
            System.err.println("녹화 파일 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            // 오류가 발생해도 OpenVidu 서버에겐 정상 응답(200 OK)을 보내 재전송을 막습니다.
        } finally {
            // 4. 임시 파일을 삭제합니다.
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile.toPath());
                } catch (IOException ioException) {
                    System.err.println("임시 녹화 파일 삭제 실패: " + tempFile.getAbsolutePath());
                }
            }
        }

        return ResponseEntity.ok().build();
    }
}

