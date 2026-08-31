package com.ats.service.impl;

import com.ats.service.NoticeMatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deterministic notice period matcher.
 * Score: 0-5 points.
 */
@Service
public class NoticeMatcherServiceImpl implements NoticeMatcherService {

    private static final Logger log = LoggerFactory.getLogger(NoticeMatcherServiceImpl.class);

    @Override
    public int score(String resumeNotice, String jobNotice) {
        if (jobNotice == null || jobNotice.isBlank()) {
            return 5;
        }

        if (resumeNotice == null || resumeNotice.isBlank()) {
            return 0;
        }

        int resumeDays = parseDays(resumeNotice);
        int jobDays = parseDays(jobNotice);

        int score;
        if (resumeDays <= 0 && jobDays <= 0) {
            score = 5;
        } else if (resumeDays <= jobDays) {
            score = 5;
        } else if (resumeDays <= jobDays + 15) {
            score = 3;
        } else {
            score = 1;
        }

        log.info("Notice match: resume={} ({}d), job={} ({}d), score={}/5",
                resumeNotice, resumeDays, jobNotice, jobDays, score);
        return score;
    }

    private int parseDays(String notice) {
        if (notice == null) return 0;
        String lower = notice.toLowerCase().trim();

        if (lower.contains("immediate")) return 0;

        Matcher matcher = Pattern.compile("(\\d+)").matcher(lower);
        if (matcher.find()) {
            int number = Integer.parseInt(matcher.group(1));
            if (lower.contains("month")) {
                return number * 30;
            } else if (lower.contains("week")) {
                return number * 7;
            } else {
                return number;
            }
        }

        return 0;
    }
}
