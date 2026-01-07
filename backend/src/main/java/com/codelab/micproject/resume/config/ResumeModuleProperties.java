package com.codelab.micproject.resume.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * 이력서 모듈 관련 설정(application.yml 의 resume.* 바인딩)
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "resume")
public class ResumeModuleProperties {
    private int maxFileSizeMb = 10;
    private List<String> supportedTypes;
}
