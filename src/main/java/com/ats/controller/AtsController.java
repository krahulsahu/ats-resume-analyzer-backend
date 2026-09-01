package com.ats.controller;

import com.ats.dto.AtsReportDTO;
import com.ats.dto.JobDTO;
import com.ats.dto.ResumeDTO;
import com.ats.dto.SuggestionDTO;
import com.ats.service.AtsScoreService;
import com.ats.service.PdfGeneratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for ATS score calculation and multi-template PDF report generation.
 */
@RestController
@RequestMapping("/api/ats")
public class AtsController {

    private static final Logger log = LoggerFactory.getLogger(AtsController.class);

    private final AtsScoreService atsScoreService;
    private final PdfGeneratorService pdfGeneratorService;
    private final ObjectMapper objectMapper;

    public AtsController(AtsScoreService atsScoreService, PdfGeneratorService pdfGeneratorService, ObjectMapper objectMapper) {
        this.atsScoreService = atsScoreService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.objectMapper = objectMapper;
    }

    /**
     * Calculate the ATS score for a resume against a job description.
     */
    @PostMapping("/calculate")
    public ResponseEntity<AtsReportDTO> calculateScore(@RequestBody Map<String, Object> request) {
        try {
            ResumeDTO resume = objectMapper.convertValue(request.get("resume"), ResumeDTO.class);
            JobDTO job = objectMapper.convertValue(request.get("job"), JobDTO.class);

            if (resume == null) {
                return ResponseEntity.badRequest().build();
            }

            if (job == null) {
                job = JobDTO.builder().build();
            }

            AtsReportDTO report = atsScoreService.calculate(resume, job);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("ATS calculation failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Generate and download an ATS-friendly resume PDF with chosen template.
     */
    @PostMapping("/pdf")
    public ResponseEntity<byte[]> generatePdf(@RequestBody Map<String, Object> request) {
        try {
            ResumeDTO resume = objectMapper.convertValue(request.get("resume"), ResumeDTO.class);
            SuggestionDTO suggestions = request.containsKey("suggestions")
                    ? objectMapper.convertValue(request.get("suggestions"), SuggestionDTO.class)
                    : null;
            String template = request.containsKey("template") && request.get("template") != null
                    ? request.get("template").toString() : "modern";

            if (resume == null) {
                return ResponseEntity.badRequest().build();
            }

            byte[] pdf = pdfGeneratorService.generate(resume, suggestions, template);

            String candidateName = (resume.getName() != null && !resume.getName().isBlank())
                    ? resume.getName().replaceAll("[^a-zA-Z0-9_-]", "_")
                    : "resume";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(candidateName + "_ats_" + template + ".pdf")
                    .build());
            headers.setContentLength(pdf.length);

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("PDF generation failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
