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
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
        log.info("Resume upload: name={}, size={}, type={}", filename, file.getSize(), contentType);

        if (file.isEmpty()) {
            log.warn("Uploaded file is empty");
            return ResponseEntity.badRequest().build();
        }

        // Validate by filename extension or content type
        boolean isPdf = (filename != null && filename.toLowerCase().endsWith(".pdf"))
                || (contentType != null && (contentType.equalsIgnoreCase("application/pdf")
                || contentType.equalsIgnoreCase("application/x-pdf")
                || contentType.equalsIgnoreCase("application/octet-stream")));

        if (!isPdf) {
            log.warn("Rejected non-PDF file: name={}, type={}", filename, contentType);
            return ResponseEntity.badRequest().build();
        }

        try {
            byte[] pdfBytes = file.getBytes();
            String rawText = pdfParser.extractText(pdfBytes);
            ResumeDTO resume = resumeParser.parse(rawText);
            return ResponseEntity.ok(resume);
        } catch (Exception e) {
            log.error("Failed to parse uploaded resume PDF", e);
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
