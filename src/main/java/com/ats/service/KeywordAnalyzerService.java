package com.ats.service;

import java.util.Map;

/**
 * Service for analyzing keyword frequency/density in resume text.
 */
public interface KeywordAnalyzerService {

    /**
     * Result of keyword analysis.
     */
    record KeywordResult(
            Map<String, Integer> keywordFrequency,
            int score
    ) {}

    /**
     * Analyze keyword density of resume against JD.
     *
     * @param resumeText full resume text
     * @param jobText full job description text
     * @return keyword analysis result with score out of 15
     */
    KeywordResult analyze(String resumeText, String jobText);
}
