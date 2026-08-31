package com.ats.service.impl;

import com.ats.service.ExperienceMatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Deterministic experience year matcher.
 * Score: 0-20 points (tiered scoring).
 */
@Service
public class ExperienceMatcherServiceImpl implements ExperienceMatcherService {

    private static final Logger log = LoggerFactory.getLogger(ExperienceMatcherServiceImpl.class);

    @Override
    public int score(double resumeYears, double requiredYears) {
        if (requiredYears <= 0) {
            return 20;
        }

        if (resumeYears <= 0) {
            return 2;
        }

        double diff = resumeYears - requiredYears;

        int score;
        if (diff >= 0) {
            score = 20;
        } else if (diff >= -1) {
            score = 15;
        } else if (diff >= -2) {
            score = 8;
        } else {
            score = 2;
        }

        log.info("Experience match: resume={}yrs, required={}yrs, diff={}, score={}/20",
                resumeYears, requiredYears, diff, score);
        return score;
    }
}
