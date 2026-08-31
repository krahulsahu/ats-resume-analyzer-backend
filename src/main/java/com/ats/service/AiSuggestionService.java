package com.ats.service;

import com.ats.dto.ResumeDTO;
import com.ats.dto.JobDTO;
import com.ats.dto.SuggestionDTO;

/**
 * Service for generating AI-powered resume improvement suggestions using Ollama.
 */
public interface AiSuggestionService {

    /**
     * Generate an improved professional summary.
     *
     * @param resume the parsed resume
     * @param job the parsed JD
     * @return improved summary text
     */
    String improveSummary(ResumeDTO resume, JobDTO job);

    /**
     * Generate all AI improvement suggestions.
     *
     * @param resume the parsed resume
     * @param job the parsed JD
     * @return full suggestion DTO with improved content
     */
    SuggestionDTO generateSuggestions(ResumeDTO resume, JobDTO job);
}
