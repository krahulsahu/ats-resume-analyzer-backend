package com.ats.service.impl;

import com.ats.dto.JobDTO;
import com.ats.service.JobParserService;
import com.ats.util.RegexUtil;
import com.ats.util.SkillDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Job description parser that extracts structured data from JD text.
 */
@Service
public class JobParserServiceImpl implements JobParserService {

    private static final Logger log = LoggerFactory.getLogger(JobParserServiceImpl.class);

    private static final Pattern TITLE_PATTERN = Pattern.compile(
            "(?i)(?:job\\s+title|position|role)\\s*:?\\s*(.+?)(?:\\n|$)");

    private static final Pattern LOCATION_PATTERN = Pattern.compile(
            "(?i)(?:location|office|workplace|based in)\\s*:?\\s*(.+?)(?:\\n|$)");

    @Override
    public JobDTO parse(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return JobDTO.builder().build();
        }

        log.info("Parsing job description ({} chars)", rawText.length());

        String title = extractTitle(rawText);
        List<String> allSkills = SkillDictionary.extractSkills(rawText);
        double experienceYears = RegexUtil.extractExperienceYears(rawText);
        String education = RegexUtil.extractEducation(rawText);
        String location = extractJobLocation(rawText);
        String noticePeriod = RegexUtil.extractNoticePeriod(rawText);
        List<String> responsibilities = extractResponsibilities(rawText);
        List<String> tools = extractTools(rawText);

        List<String> requiredSkills = new ArrayList<>();
        List<String> preferredSkills = new ArrayList<>();
        categorizeSkills(rawText, allSkills, requiredSkills, preferredSkills);

        JobDTO dto = JobDTO.builder()
                .title(title)
                .requiredSkills(requiredSkills)
                .preferredSkills(preferredSkills)
                .experience(experienceYears > 0 ? experienceYears + " years" : null)
                .experienceYears(experienceYears)
                .education(education)
                .location(location)
                .noticePeriod(noticePeriod)
                .responsibilities(responsibilities)
                .tools(tools)
                .rawText(rawText)
                .build();

        log.info("Parsed JD: title={}, required={}, preferred={}, exp={}yrs",
                title, requiredSkills.size(), preferredSkills.size(), experienceYears);

        return dto;
    }

    private String extractTitle(String text) {
        Matcher matcher = TITLE_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        for (String line : text.split("\\r?\\n")) {
            line = line.trim();
            if (!line.isEmpty() && line.length() < 80 && !line.contains("@") && !line.matches(".*\\d{3,}.*")) {
                return line;
            }
        }
        return "Unknown Position";
    }

    private String extractJobLocation(String text) {
        Matcher matcher = LOCATION_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        List<String> cities = List.of(
                "Bengaluru", "Bangalore", "Mumbai", "Delhi", "Hyderabad", "Chennai",
                "Pune", "Noida", "Gurugram", "Gurgaon", "Remote",
                "New York", "San Francisco", "London", "Singapore"
        );
        String lower = text.toLowerCase();
        for (String city : cities) {
            if (lower.contains(city.toLowerCase())) return city;
        }
        return null;
    }

    private void categorizeSkills(String text, List<String> allSkills,
                                   List<String> required, List<String> preferred) {
        String lower = text.toLowerCase();

        int preferredStart = findSection(lower,
                "preferred", "nice to have", "good to have", "bonus", "plus", "optional");

        for (String skill : allSkills) {
            String skillLower = skill.toLowerCase();
            if (preferredStart > 0 && lower.indexOf(skillLower, preferredStart) >= 0
                    && lower.indexOf(skillLower) >= preferredStart) {
                preferred.add(skill);
            } else {
                required.add(skill);
            }
        }

        if (required.isEmpty() && !preferred.isEmpty()) {
            required.addAll(preferred);
            preferred.clear();
        }
    }

    private int findSection(String text, String... keywords) {
        int minPos = -1;
        for (String keyword : keywords) {
            int pos = text.indexOf(keyword);
            if (pos >= 0 && (minPos < 0 || pos < minPos)) {
                minPos = pos;
            }
        }
        return minPos;
    }

    private List<String> extractResponsibilities(String text) {
        List<String> responsibilities = new ArrayList<>();
        Pattern respPattern = Pattern.compile(
                "(?i)(?:responsibilities|duties|what you.?ll do|key responsibilities|role overview)\\s*:?");
        Matcher matcher = respPattern.matcher(text);

        if (matcher.find()) {
            String section = text.substring(matcher.end());
            String[] lines = section.split("\\r?\\n");
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.matches("(?i)^(requirements|qualifications|skills|education|experience|about|benefits).*")) break;
                if (line.startsWith("•") || line.startsWith("-") || line.startsWith("●") || line.matches("^\\d+\\.\\s.*")) {
                    responsibilities.add(line.replaceFirst("^[•\\-●▪\\d.]+\\s*", "").trim());
                }
            }
        }

        return responsibilities;
    }

    private List<String> extractTools(String text) {
        return SkillDictionary.extractSkills(text).stream()
                .filter(s -> {
                    String cat = SkillDictionary.getCategory(s);
                    return "Tools".equals(cat) || "DevOps".equals(cat);
                })
                .toList();
    }
}
