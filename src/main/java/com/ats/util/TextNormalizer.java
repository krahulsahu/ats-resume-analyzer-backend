package com.ats.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Text normalization utilities for consistent text comparison.
 */
public final class TextNormalizer {

    private TextNormalizer() {
        // Utility class
    }

    /**
     * Normalize text: lowercase, trim, collapse whitespace.
     */
    public static String normalize(String text) {
        if (text == null) return "";
        return text.toLowerCase().trim().replaceAll("\\s+", " ");
    }

    /**
     * Remove all punctuation from text.
     */
    public static String removePunctuation(String text) {
        if (text == null) return "";
        return text.replaceAll("[^a-zA-Z0-9\\s.#+]", " ").replaceAll("\\s+", " ").trim();
    }

    /**
     * Tokenize text into words.
     */
    public static List<String> tokenize(String text) {
        if (text == null || text.isBlank()) return List.of();
        return Arrays.stream(normalize(text).split("\\s+"))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }

    /**
     * Remove common stop words for keyword analysis.
     */
    public static List<String> removeStopWords(List<String> tokens) {
        List<String> stopWords = List.of(
                "the", "a", "an", "is", "are", "was", "were", "be", "been", "being",
                "have", "has", "had", "do", "does", "did", "will", "would", "could",
                "should", "may", "might", "shall", "can", "need", "dare", "ought",
                "and", "but", "or", "nor", "not", "so", "yet", "both", "either",
                "neither", "each", "every", "all", "any", "few", "more", "most",
                "other", "some", "such", "no", "only", "own", "same", "than",
                "too", "very", "just", "because", "as", "until", "while", "of",
                "at", "by", "for", "with", "about", "against", "between", "through",
                "during", "before", "after", "above", "below", "to", "from", "up",
                "down", "in", "out", "on", "off", "over", "under", "again", "further",
                "then", "once", "here", "there", "when", "where", "why", "how",
                "this", "that", "these", "those", "i", "me", "my", "myself", "we",
                "our", "ours", "you", "your", "he", "him", "his", "she", "her",
                "it", "its", "they", "them", "their", "what", "which", "who", "whom"
        );
        return tokens.stream()
                .filter(t -> !stopWords.contains(t.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Clean section text by removing common header artifacts.
     */
    public static String cleanSectionText(String text) {
        if (text == null) return "";
        // Remove lines that are just dashes, equals, or underscores
        return text.replaceAll("(?m)^[-=_]{3,}$", "")
                .replaceAll("(?m)^\\s*$", "")
                .trim();
    }
}
