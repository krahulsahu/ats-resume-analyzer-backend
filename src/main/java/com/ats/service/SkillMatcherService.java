package com.ats.service;

import java.util.List;
import java.util.Map;

/**
 * Service for matching resume skills against job description requirements.
 */
public interface SkillMatcherService {

    /**
     * Result of skill matching analysis.
     */
    record SkillMatchResult(
            List<String> matched,
            List<String> missing,
            int score
    ) {}

    /**
     * Match resume skills against required JD skills.
     *
     * @param resumeSkills skills from the resume
     * @param requiredSkills required skills from the JD
     * @param resumeText full resume text for additional skill extraction
     * @return skill match result with score out of 40
     */
    SkillMatchResult match(List<String> resumeSkills, List<String> requiredSkills, String resumeText);
}
