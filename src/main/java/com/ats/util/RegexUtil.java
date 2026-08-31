package com.ats.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regex utility for extracting structured data from resume/JD text.
 */
public final class RegexUtil {

    private RegexUtil() {
        // Utility class
    }

    // --- Experience Year Patterns ---
    private static final List<Pattern> EXPERIENCE_PATTERNS = List.of(
            Pattern.compile("(\\d+\\.?\\d*)\\s*\\+?\\s*(?:years?|yrs?)\\s*(?:of)?\\s*(?:experience|exp)?", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?:experience|exp)\\s*(?:of)?\\s*(\\d+\\.?\\d*)\\s*\\+?\\s*(?:years?|yrs?)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(\\d+\\.?\\d*)\\s*(?:to|-)\\s*\\d+\\.?\\d*\\s*(?:years?|yrs?)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?:over|more than|at least|minimum)\\s*(\\d+\\.?\\d*)\\s*(?:years?|yrs?)", Pattern.CASE_INSENSITIVE)
    );

    private static final Pattern WORD_NUMBER_PATTERN = Pattern.compile(
            "(?:one|two|three|four|five|six|seven|eight|nine|ten)\\s*(?:years?|yrs?)",
            Pattern.CASE_INSENSITIVE
    );

    // Duration patterns like "Jan 2020 - Present" or "2019 - 2022"
    private static final Pattern DURATION_PATTERN = Pattern.compile(
            "(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec|January|February|March|April|May|June|July|August|September|October|November|December)?\\s*(\\d{4})\\s*(?:-|–|to)\\s*(?:(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec|January|February|March|April|May|June|July|August|September|October|November|December)?\\s*(\\d{4})|[Pp]resent|[Cc]urrent|[Nn]ow)",
            Pattern.CASE_INSENSITIVE
    );

    // --- Education Patterns ---
    private static final Pattern EDUCATION_PATTERN = Pattern.compile(
            "(?:B\\.?\\s*Tech|B\\.?E\\.?|B\\.?Sc|B\\.?S\\.?|M\\.?Tech|M\\.?E\\.?|M\\.?S\\.?|M\\.?Sc|MCA|BCA|MBA|Ph\\.?D|Diploma|Bachelor|Master|Associate|B\\.?Com|M\\.?Com)",
            Pattern.CASE_INSENSITIVE
    );

    // --- Email Pattern ---
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
    );

    // --- Phone Pattern ---
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?:\\+\\d{1,3}[-.\\s]?)?\\(?\\d{2,4}\\)?[-.\\s]?\\d{3,4}[-.\\s]?\\d{3,4}"
    );

    // --- Notice Period Patterns ---
    private static final Pattern NOTICE_PATTERN = Pattern.compile(
            "(immediate(?:ly)?|\\d+\\s*(?:days?|months?|weeks?)\\s*(?:notice)?|(?:notice\\s*(?:period)?\\s*:?\\s*)(\\d+\\s*(?:days?|months?|weeks?)))",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Extract total years of experience from text.
     */
    public static double extractExperienceYears(String text) {
        if (text == null || text.isBlank()) return 0;

        // Try numeric patterns
        for (Pattern pattern : EXPERIENCE_PATTERNS) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                try {
                    return Double.parseDouble(matcher.group(1));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        // Try word-based pattern
        Matcher wordMatcher = WORD_NUMBER_PATTERN.matcher(text);
        if (wordMatcher.find()) {
            return convertWordToNumber(wordMatcher.group().toLowerCase());
        }

        // Try to calculate from date ranges
        return calculateYearsFromDurations(text);
    }

    /**
     * Calculate total years from duration patterns (e.g., "2019 - 2022").
     */
    public static double calculateYearsFromDurations(String text) {
        Matcher matcher = DURATION_PATTERN.matcher(text);
        double totalYears = 0;
        while (matcher.find()) {
            try {
                int startYear = Integer.parseInt(matcher.group(1));
                int endYear = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 2026;
                totalYears += Math.max(0, endYear - startYear);
            } catch (NumberFormatException ignored) {
            }
        }
        return totalYears;
    }

    /**
     * Extract education qualification from text.
     */
    public static String extractEducation(String text) {
        if (text == null || text.isBlank()) return null;
        Matcher matcher = EDUCATION_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group().trim();
        }
        return null;
    }

    /**
     * Extract all education qualifications from text.
     */
    public static List<String> extractAllEducation(String text) {
        List<String> results = new ArrayList<>();
        if (text == null || text.isBlank()) return results;
        Matcher matcher = EDUCATION_PATTERN.matcher(text);
        while (matcher.find()) {
            results.add(matcher.group().trim());
        }
        return results;
    }

    /**
     * Extract email address from text.
     */
    public static String extractEmail(String text) {
        if (text == null) return null;
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        return matcher.find() ? matcher.group() : null;
    }

    /**
     * Extract phone number from text.
     */
    public static String extractPhone(String text) {
        if (text == null) return null;
        Matcher matcher = PHONE_PATTERN.matcher(text);
        return matcher.find() ? matcher.group() : null;
    }

    /**
     * Extract notice period from text.
     */
    public static String extractNoticePeriod(String text) {
        if (text == null || text.isBlank()) return null;
        Matcher matcher = NOTICE_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group().trim();
        }
        // Check for keywords
        String lower = text.toLowerCase();
        if (lower.contains("immediate")) return "Immediate";
        if (lower.contains("15 day")) return "15 Days";
        if (lower.contains("30 day") || lower.contains("1 month")) return "30 Days";
        if (lower.contains("60 day") || lower.contains("2 month")) return "60 Days";
        if (lower.contains("90 day") || lower.contains("3 month")) return "90 Days";
        return null;
    }

    /**
     * Convert word number to numeric value.
     */
    private static double convertWordToNumber(String text) {
        if (text.contains("one")) return 1;
        if (text.contains("two")) return 2;
        if (text.contains("three")) return 3;
        if (text.contains("four")) return 4;
        if (text.contains("five")) return 5;
        if (text.contains("six")) return 6;
        if (text.contains("seven")) return 7;
        if (text.contains("eight")) return 8;
        if (text.contains("nine")) return 9;
        if (text.contains("ten")) return 10;
        return 0;
    }

    /**
     * Extract name from first few lines of resume text.
     */
    public static String extractName(String text) {
        if (text == null || text.isBlank()) return null;
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            // Skip lines that look like section headers, email, phone
            if (line.length() > 50) continue;
            if (EMAIL_PATTERN.matcher(line).find()) continue;
            if (PHONE_PATTERN.matcher(line).find()) continue;
            if (line.matches("(?i)(resume|curriculum|cv|objective|summary|profile|about).*")) continue;
            // First non-trivial line is likely the name
            if (line.matches("^[A-Za-z][A-Za-z .'-]+$") && line.split("\\s+").length >= 2) {
                return line;
            }
        }
        return null;
    }
}
