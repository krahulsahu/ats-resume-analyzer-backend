package com.ats.service.impl;

import com.ats.service.LocationMatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Deterministic location matcher.
 * Score: 0, 5, or 10 points.
 */
@Service
public class LocationMatcherServiceImpl implements LocationMatcherService {

    private static final Logger log = LoggerFactory.getLogger(LocationMatcherServiceImpl.class);

    @Override
    public int score(String resumeLocation, String jobLocation) {
        if (jobLocation == null || jobLocation.isBlank()) {
            return 10;
        }

        if (resumeLocation == null || resumeLocation.isBlank()) {
            return 0;
        }

        String resumeLower = resumeLocation.toLowerCase().trim();
        String jobLower = jobLocation.toLowerCase().trim();

        int score;
        if (jobLower.contains("remote") || resumeLower.contains("remote")) {
            score = 10;
        } else if (jobLower.contains(resumeLower) || resumeLower.contains(jobLower)) {
            score = 10;
        } else if (areSameCity(resumeLower, jobLower)) {
            score = 10;
        } else {
            score = 5;
        }

        log.info("Location match: resume={}, job={}, score={}/10", resumeLocation, jobLocation, score);
        return score;
    }

    private boolean areSameCity(String city1, String city2) {
        String[][] aliases = {
                {"bengaluru", "bangalore"},
                {"gurugram", "gurgaon"},
                {"mumbai", "bombay"},
                {"chennai", "madras"},
                {"kolkata", "calcutta"},
                {"thiruvananthapuram", "trivandrum"},
                {"kochi", "cochin"},
                {"varanasi", "banaras", "benares"},
                {"new york", "nyc"},
                {"san francisco", "sf", "bay area"}
        };

        for (String[] group : aliases) {
            boolean city1Match = false, city2Match = false;
            for (String alias : group) {
                if (city1.contains(alias)) city1Match = true;
                if (city2.contains(alias)) city2Match = true;
            }
            if (city1Match && city2Match) return true;
        }

        return false;
    }
}
