package com.ats.service.impl;

import com.ats.service.KeywordAnalyzerService;
import com.ats.util.SkillDictionary;
import com.ats.util.TextNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Keyword frequency/density analyzer using TF-like scoring.
 * Score: 0-15 points.
 */
@Service
public class KeywordAnalyzerServiceImpl implements KeywordAnalyzerService {

    private static final Logger log = LoggerFactory.getLogger(KeywordAnalyzerServiceImpl.class);
    private static final int MAX_SCORE = 15;

    @Override
    public KeywordResult analyze(String resumeText, String jobText) {
        if (resumeText == null || resumeText.isBlank()) {
            return new KeywordResult(Map.of(), 0);
        }

        String normalizedResume = TextNormalizer.normalize(resumeText);
        List<String> importantKeywords = SkillDictionary.getImportantKeywords();

        Set<String> jdKeywords = new LinkedHashSet<>(importantKeywords);
        if (jobText != null && !jobText.isBlank()) {
            List<String> jdTokens = TextNormalizer.tokenize(jobText);
            jdTokens = TextNormalizer.removeStopWords(jdTokens);
            Map<String, Integer> jdFreq = new LinkedHashMap<>();
            for (String token : jdTokens) {
                if (token.length() > 2) {
                    jdFreq.merge(token, 1, Integer::sum);
                }
            }
            jdFreq.entrySet().stream()
                    .filter(e -> e.getValue() >= 2)
                    .map(Map.Entry::getKey)
                    .forEach(jdKeywords::add);
        }

        Map<String, Integer> keywordFrequency = new LinkedHashMap<>();
        for (String keyword : jdKeywords) {
            int count = countOccurrences(normalizedResume, keyword.toLowerCase());
            if (count > 0) {
                keywordFrequency.put(keyword, count);
            }
        }

        int matchedKeywords = keywordFrequency.size();
        int totalKeywords = jdKeywords.size();
        double coverage = totalKeywords > 0 ? (double) matchedKeywords / totalKeywords : 0;

        int totalFrequency = keywordFrequency.values().stream().mapToInt(Integer::intValue).sum();
        double densityBonus = Math.min(1.0, totalFrequency / 30.0);

        double rawScore = (coverage * 0.7 + densityBonus * 0.3) * MAX_SCORE;
        int score = (int) Math.min(MAX_SCORE, Math.round(rawScore));

        log.info("Keyword analysis: {}/{} keywords found, density={}, score={}/{}",
                matchedKeywords, totalKeywords, totalFrequency, score, MAX_SCORE);

        return new KeywordResult(keywordFrequency, score);
    }

    private int countOccurrences(String text, String keyword) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(keyword, idx)) != -1) {
            count++;
            idx += keyword.length();
        }
        return count;
    }
}
