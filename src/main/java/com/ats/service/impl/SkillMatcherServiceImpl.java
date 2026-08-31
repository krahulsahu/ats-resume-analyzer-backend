package com.ats.service.impl;

import com.ats.service.SkillMatcherService;
import com.ats.util.SkillDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Deterministic skill matching using exact match + synonym mapping.
 * Score: 0-40 points.
 */
@Service
public class SkillMatcherServiceImpl implements SkillMatcherService {

    private static final Logger log = LoggerFactory.getLogger(SkillMatcherServiceImpl.class);
    private static final int MAX_SCORE = 40;

    @Override
    public SkillMatchResult match(List<String> resumeSkills, List<String> requiredSkills, String resumeText) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return new SkillMatchResult(List.of(), List.of(), MAX_SCORE);
        }

        Set<String> normalizedResumeSkills = new LinkedHashSet<>();
        if (resumeSkills != null) {
            for (String skill : resumeSkills) {
                String canonical = SkillDictionary.findSkill(skill);
                normalizedResumeSkills.add(canonical != null ? canonical : skill);
            }
        }

        if (resumeText != null) {
            List<String> textSkills = SkillDictionary.extractSkills(resumeText);
            normalizedResumeSkills.addAll(textSkills);
        }

        Set<String> resumeSkillsLower = new HashSet<>();
        for (String s : normalizedResumeSkills) {
            resumeSkillsLower.add(s.toLowerCase());
        }

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String required : requiredSkills) {
            String normalizedRequired = required.toLowerCase().trim();
            String canonical = SkillDictionary.findSkill(required);
            String canonicalLower = canonical != null ? canonical.toLowerCase() : normalizedRequired;

            if (resumeSkillsLower.contains(normalizedRequired) || resumeSkillsLower.contains(canonicalLower)) {
                matched.add(canonical != null ? canonical : required);
            } else {
                boolean found = false;
                for (String resumeSkill : normalizedResumeSkills) {
                    String resumeCanonical = SkillDictionary.findSkill(resumeSkill);
                    if (resumeCanonical != null && resumeCanonical.equalsIgnoreCase(canonical != null ? canonical : required)) {
                        matched.add(canonical != null ? canonical : required);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    missing.add(canonical != null ? canonical : required);
                }
            }
        }

        matched = matched.stream().distinct().toList();
        missing = missing.stream().distinct().toList();

        int totalRequired = matched.size() + missing.size();
        int score = totalRequired > 0 ? (int) Math.round((double) matched.size() / totalRequired * MAX_SCORE) : MAX_SCORE;

        log.info("Skill match: {}/{} matched, score={}/{}", matched.size(), totalRequired, score, MAX_SCORE);
        return new SkillMatchResult(matched, missing, score);
    }
}
