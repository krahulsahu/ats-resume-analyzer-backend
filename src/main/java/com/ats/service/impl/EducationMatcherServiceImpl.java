package com.ats.service.impl;

import com.ats.service.EducationMatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Deterministic education qualification matcher.
 * Score: 0, 5, or 10 points.
 */
@Service
public class EducationMatcherServiceImpl implements EducationMatcherService {

    private static final Logger log = LoggerFactory.getLogger(EducationMatcherServiceImpl.class);

    private static final Map<String, Integer> EDUCATION_LEVELS = Map.ofEntries(
            Map.entry("diploma", 1),
            Map.entry("associate", 2),
            Map.entry("bca", 3),
            Map.entry("b.com", 3),
            Map.entry("bcom", 3),
            Map.entry("b.sc", 3),
            Map.entry("bsc", 3),
            Map.entry("b.s", 3),
            Map.entry("bs", 3),
            Map.entry("bachelor", 4),
            Map.entry("b.tech", 4),
            Map.entry("btech", 4),
            Map.entry("b.e", 4),
            Map.entry("be", 4),
            Map.entry("mca", 5),
            Map.entry("mba", 5),
            Map.entry("m.com", 5),
            Map.entry("mcom", 5),
            Map.entry("m.sc", 5),
            Map.entry("msc", 5),
            Map.entry("m.s", 5),
            Map.entry("ms", 5),
            Map.entry("master", 6),
            Map.entry("m.tech", 6),
            Map.entry("mtech", 6),
            Map.entry("m.e", 6),
            Map.entry("me", 6),
            Map.entry("ph.d", 7),
            Map.entry("phd", 7),
            Map.entry("doctorate", 7)
    );

    @Override
    public int score(String resumeEducation, String requiredEducation) {
        if (requiredEducation == null || requiredEducation.isBlank()) {
            return 10;
        }

        if (resumeEducation == null || resumeEducation.isBlank()) {
            return 0;
        }

        int resumeLevel = getLevel(resumeEducation);
        int requiredLevel = getLevel(requiredEducation);

        int score;
        if (resumeLevel >= requiredLevel) {
            score = 10;
        } else if (resumeLevel >= requiredLevel - 1) {
            score = 5;
        } else {
            score = 0;
        }

        log.info("Education match: resume={} (level {}), required={} (level {}), score={}/10",
                resumeEducation, resumeLevel, requiredEducation, requiredLevel, score);
        return score;
    }

    private int getLevel(String education) {
        if (education == null) return 0;
        String normalized = education.toLowerCase().trim()
                .replaceAll("[^a-z.]", "");

        for (Map.Entry<String, Integer> entry : EDUCATION_LEVELS.entrySet()) {
            if (normalized.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return 0;
    }
}
