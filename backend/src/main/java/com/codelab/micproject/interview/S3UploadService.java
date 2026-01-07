package com.codelab.micproject.interview;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 파일을 S3에 업로드하고, 해당 파일에 접근할 수 있는 URL을 반환합니다.
     * @param fileName S3에 저장될 파일 이름 (예: "videos/practice-123.mp4")
     * @param file 업로드할 파일 객체
     * @return 업로드된 파일의 전체 URL
     */
    public String uploadFile(String fileName, File file) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName) // 파일 이름(경로 포함)
                .build();

        // 파일을 S3에 업로드합니다.
        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));

        // 업로드된 파일의 URL을 생성하여 반환합니다.
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(fileName)).toExternalForm();
    }
}
