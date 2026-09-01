package com.ats.controller;

import com.ats.dto.JobDTO;
import com.ats.dto.ResumeDTO;
import com.ats.dto.SuggestionDTO;
import com.ats.service.AiSuggestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for AI-powered resume improvements using Google Gemini 1.5 Flash.
 * Authentication and API keys are strictly managed on the backend.
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private static final Logger log = LoggerFactory.getLogger(AiController.class);

    private final AiSuggestionService aiSuggestionService;
    private final ObjectMapper objectMapper;

    public AiController(AiSuggestionService aiSuggestionService, ObjectMapper objectMapper) {
        this.aiSuggestionService = aiSuggestionService;
        this.objectMapper = objectMapper;
    }

    /**
     * Generate an AI-improved professional summary.
     */
    @PostMapping("/summary")
    public ResponseEntity<Map<String, String>> improveSummary(@RequestBody Map<String, Object> request) {
        try {
            ResumeDTO resume = objectMapper.convertValue(request.get("resume"), ResumeDTO.class);
            JobDTO job = objectMapper.convertValue(request.get("job"), JobDTO.class);

            if (resume == null) {
                return ResponseEntity.badRequest().build();
            }
            if (job == null) {
                job = JobDTO.builder().build();
            }

            String improvedSummary = aiSuggestionService.improveSummary(resume, job);
            return ResponseEntity.ok(Map.of("summary", improvedSummary));
        } catch (Exception e) {
            log.error("AI summary generation error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Generate full AI improvement suggestions (summary, experience, projects, skills).
     */
    @PostMapping("/improve")
    public ResponseEntity<SuggestionDTO> generateSuggestions(@RequestBody Map<String, Object> request) {
        try {
            ResumeDTO resume = objectMapper.convertValue(request.get("resume"), ResumeDTO.class);
            JobDTO job = objectMapper.convertValue(request.get("job"), JobDTO.class);

            if (resume == null) {
                return ResponseEntity.badRequest().build();
            }
            if (job == null) {
                job = JobDTO.builder().build();
            }

            SuggestionDTO suggestions = aiSuggestionService.generateSuggestions(resume, job);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            log.error("AI suggestion generation error", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
