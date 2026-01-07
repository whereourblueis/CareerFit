package com.codelab.micproject.resume.service;

import com.codelab.micproject.resume.exception.InvalidFileTypeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 파일 → 텍스트 파싱 역할
 * - PDF: PDFBox
 * - DOC/DOCX: Apache POI
 * - 안전을 위해 앞부분만 프리뷰로 저장(전량 저장은 보안/용량 이슈)
 */
@Component
@Slf4j
public class ResumeParser {

    public String parseToText(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new InvalidFileTypeException("Content-Type 식별 불가");
        }

        try {
            byte[] bytes = file.getBytes();

            if (contentType.equals("application/pdf")) {
                return parsePdf(bytes);
            } else if (contentType.equals("application/msword")) {
                return parseDoc(bytes);
            } else if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                return parseDocx(bytes);
            } else {
                throw new InvalidFileTypeException("허용되지 않는 형식: " + contentType);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 파싱 실패", e);
        }
    }

    private String parsePdf(byte[] bytes) throws IOException {
        try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(bytes))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        }
    }


    private String parseDoc(byte[] bytes) throws IOException {
        try (HWPFDocument doc = new HWPFDocument(new ByteArrayInputStream(bytes))) {
            try (WordExtractor extractor = new WordExtractor(doc)) {
                return extractor.getText();
            }
        }
    }

    private String parseDocx(byte[] bytes) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(bytes))) {
            try (XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
                return extractor.getText();
            }
        }
    }

    /** 보안상 DB에는 프리뷰(앞 2,000자)만 저장 */
    public String preview(String fullText) {
        if (fullText == null) return "";
        byte[] raw = fullText.getBytes(StandardCharsets.UTF_8);
        int limit = Math.min(raw.length, 2000);
        return new String(raw, 0, limit, StandardCharsets.UTF_8);
    }
}
