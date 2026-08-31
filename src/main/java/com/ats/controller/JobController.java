package com.ats.controller;

import com.ats.dto.JobDTO;
import com.ats.service.JobParserService;
import com.ats.service.PdfParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * REST controller for job description parsing.
 */
@RestController
@RequestMapping("/api/job")
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);

    private final JobParserService jobParser;
    private final PdfParserService pdfParser;

    public JobController(JobParserService jobParser, PdfParserService pdfParser) {
        this.jobParser = jobParser;
        this.pdfParser = pdfParser;
    }

    /**
     * Parse job description from text.
     */
    @PostMapping("/parse")
    public ResponseEntity<JobDTO> parseJobDescription(@RequestBody String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        log.info("Parsing job description ({} chars)", rawText.length());
        JobDTO job = jobParser.parse(rawText);
        return ResponseEntity.ok(job);
    }

    /**
     * Parse job description from uploaded PDF.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JobDTO> uploadJobDescription(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            byte[] pdfBytes = file.getBytes();
            String rawText = pdfParser.extractText(pdfBytes);
            JobDTO job = jobParser.parse(rawText);
            return ResponseEntity.ok(job);
        } catch (IOException e) {
            log.error("Failed to read uploaded JD file", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
