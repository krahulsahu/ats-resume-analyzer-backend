package com.ats.controller;

import com.ats.dto.ResumeDTO;
import com.ats.service.PdfParserService;
import com.ats.service.ResumeParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * REST controller for resume upload and parsing.
 */
@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private static final Logger log = LoggerFactory.getLogger(ResumeController.class);

    private final PdfParserService pdfParser;
    private final ResumeParserService resumeParser;

    public ResumeController(PdfParserService pdfParser, ResumeParserService resumeParser) {
        this.pdfParser = pdfParser;
        this.resumeParser = resumeParser;
    }

    /**
     * Upload a resume PDF and parse it into structured data.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeDTO> uploadResume(@RequestParam("file") MultipartFile file) {
        log.info("Resume upload: name={}, size={}, type={}", file.getOriginalFilename(),
                file.getSize(), file.getContentType());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            return ResponseEntity.badRequest().build();
        }

        try {
            byte[] pdfBytes = file.getBytes();
            String rawText = pdfParser.extractText(pdfBytes);
            ResumeDTO resume = resumeParser.parse(rawText);
            return ResponseEntity.ok(resume);
        } catch (IOException e) {
            log.error("Failed to read uploaded file", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Parse resume from raw text (alternative to PDF upload).
     */
    @PostMapping("/parse")
    public ResponseEntity<ResumeDTO> parseResumeText(@RequestBody String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        ResumeDTO resume = resumeParser.parse(rawText);
        return ResponseEntity.ok(resume);
    }
}
