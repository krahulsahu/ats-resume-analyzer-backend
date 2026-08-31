package com.ats.service.impl;

import com.ats.dto.ResumeDTO;
import com.ats.service.ResumeParserService;
import com.ats.util.RegexUtil;
import com.ats.util.SkillDictionary;
import com.ats.util.TextNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resume text parser that detects sections and extracts structured data.
 */
@Service
public class ResumeParserServiceImpl implements ResumeParserService {

    private static final Logger log = LoggerFactory.getLogger(ResumeParserServiceImpl.class);

    private static final Map<String, Pattern> SECTION_PATTERNS = new LinkedHashMap<>();

    static {
        SECTION_PATTERNS.put("summary", Pattern.compile(
                "(?i)^\\s*(professional\\s+summary|summary|profile|objective|about\\s+me|career\\s+objective|career\\s+summary)\\s*:?\\s*$",
                Pattern.MULTILINE));
        SECTION_PATTERNS.put("skills", Pattern.compile(
                "(?i)^\\s*(technical\\s+skills|skills|core\\s+competencies|technologies|tech\\s+stack|key\\s+skills|areas\\s+of\\s+expertise)\\s*:?\\s*$",
                Pattern.MULTILINE));
        SECTION_PATTERNS.put("experience", Pattern.compile(
                "(?i)^\\s*(work\\s+experience|experience|professional\\s+experience|employment\\s+history|work\\s+history|employment)\\s*:?\\s*$",
                Pattern.MULTILINE));
        SECTION_PATTERNS.put("projects", Pattern.compile(
                "(?i)^\\s*(projects|personal\\s+projects|key\\s+projects|academic\\s+projects|notable\\s+projects)\\s*:?\\s*$",
                Pattern.MULTILINE));
        SECTION_PATTERNS.put("education", Pattern.compile(
                "(?i)^\\s*(education|academic\\s+background|academics|qualifications|educational\\s+qualifications)\\s*:?\\s*$",
                Pattern.MULTILINE));
        SECTION_PATTERNS.put("certifications", Pattern.compile(
                "(?i)^\\s*(certifications|certificates|professional\\s+certifications|licenses|certifications\\s+&\\s+courses)\\s*:?\\s*$",
                Pattern.MULTILINE));
        SECTION_PATTERNS.put("achievements", Pattern.compile(
                "(?i)^\\s*(achievements|awards|honors|accomplishments|recognition)\\s*:?\\s*$",
                Pattern.MULTILINE));
    }

    @Override
    public ResumeDTO parse(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return ResumeDTO.builder().build();
        }

        log.info("Parsing resume text ({} chars)", rawText.length());

        Map<String, String> sections = detectSections(rawText);

        String name = RegexUtil.extractName(rawText);
        String email = RegexUtil.extractEmail(rawText);
        String phone = RegexUtil.extractPhone(rawText);
        String summary = extractSummary(sections);
        List<String> skills = extractSkills(sections, rawText);
        List<ResumeDTO.ExperienceDTO> experience = extractExperience(sections);
        List<ResumeDTO.ProjectDTO> projects = extractProjects(sections);
        String education = extractEducation(sections, rawText);
        String location = extractLocation(rawText);
        String noticePeriod = RegexUtil.extractNoticePeriod(rawText);
        List<String> certifications = extractCertifications(sections);

        ResumeDTO dto = ResumeDTO.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .summary(summary)
                .skills(skills)
                .experience(experience)
                .projects(projects)
                .education(education)
                .location(location)
                .noticePeriod(noticePeriod)
                .certifications(certifications)
                .build();

        log.info("Parsed resume: name={}, skills={}, experience={}, projects={}",
                name, skills.size(), experience.size(), projects.size());

        return dto;
    }

    private Map<String, String> detectSections(String text) {
        Map<String, String> sections = new LinkedHashMap<>();
        List<int[]> sectionPositions = new ArrayList<>();
        List<String> sectionNames = new ArrayList<>();

        for (Map.Entry<String, Pattern> entry : SECTION_PATTERNS.entrySet()) {
            Matcher matcher = entry.getValue().matcher(text);
            while (matcher.find()) {
                sectionPositions.add(new int[]{matcher.end(), text.length()});
                sectionNames.add(entry.getKey());
                break;
            }
        }

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < sectionPositions.size(); i++) indices.add(i);
        indices.sort(Comparator.comparingInt(i -> sectionPositions.get(i)[0]));

        for (int i = 0; i < indices.size(); i++) {
            int idx = indices.get(i);
            int start = sectionPositions.get(idx)[0];
            int end = (i + 1 < indices.size())
                    ? sectionPositions.get(indices.get(i + 1))[0] - 50
                    : text.length();
            end = Math.max(start, Math.min(end, text.length()));
            String sectionText = text.substring(start, end).trim();
            sections.put(sectionNames.get(idx), sectionText);
        }

        log.debug("Detected sections: {}", sections.keySet());
        return sections;
    }

    private String extractSummary(Map<String, String> sections) {
        String summary = sections.get("summary");
        if (summary != null && !summary.isBlank()) {
            String[] paragraphs = summary.split("\\n\\s*\\n");
            return TextNormalizer.cleanSectionText(paragraphs[0]);
        }
        return null;
    }

    private List<String> extractSkills(Map<String, String> sections, String fullText) {
        String skillsText = sections.getOrDefault("skills", "");
        List<String> skills = SkillDictionary.extractSkills(skillsText + " " + fullText);
        return skills.stream().distinct().toList();
    }

    private List<ResumeDTO.ExperienceDTO> extractExperience(Map<String, String> sections) {
        String expText = sections.get("experience");
        if (expText == null || expText.isBlank()) return List.of();

        List<ResumeDTO.ExperienceDTO> experiences = new ArrayList<>();
        String[] entries = expText.split("(?m)(?=^[A-Z][A-Za-z\\s]+(?:–|-|\\|)\\s*[A-Z])");

        for (String entry : entries) {
            entry = entry.trim();
            if (entry.isEmpty() || entry.length() < 20) continue;

            String[] lines = entry.split("\\r?\\n");
            String title = "";
            String company = "";
            String duration = "";
            List<String> bullets = new ArrayList<>();

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                if (i == 0) {
                    String[] parts = line.split("\\s*(?:–|-|\\||at|@)\\s*", 2);
                    title = parts[0].trim();
                    if (parts.length > 1) company = parts[1].trim();
                } else if (line.matches(".*\\d{4}.*") && duration.isEmpty()) {
                    duration = line;
                } else if (line.startsWith("•") || line.startsWith("-") || line.startsWith("●") || line.startsWith("▪")) {
                    bullets.add(line.replaceFirst("^[•\\-●▪]\\s*", "").trim());
                } else if (!line.isEmpty() && bullets.isEmpty() && company.isEmpty()) {
                    company = line;
                } else if (!line.isEmpty()) {
                    bullets.add(line);
                }
            }

            double years = RegexUtil.calculateYearsFromDurations(entry);
            if (years == 0) {
                years = RegexUtil.extractExperienceYears(entry);
            }

            if (!title.isEmpty()) {
                experiences.add(ResumeDTO.ExperienceDTO.builder()
                        .title(title)
                        .company(company)
                        .duration(duration)
                        .years(years)
                        .bullets(bullets)
                        .build());
            }
        }

        if (experiences.isEmpty()) {
            String[] blocks = expText.split("\\n\\s*\\n");
            for (String block : blocks) {
                block = block.trim();
                if (block.length() < 20) continue;
                String[] lines = block.split("\\r?\\n");
                List<String> bullets = new ArrayList<>();
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty()) bullets.add(line.replaceFirst("^[•\\-●▪]\\s*", ""));
                }
                if (!bullets.isEmpty()) {
                    String firstLine = bullets.remove(0);
                    experiences.add(ResumeDTO.ExperienceDTO.builder()
                            .title(firstLine)
                            .bullets(bullets)
                            .years(RegexUtil.calculateYearsFromDurations(block))
                            .build());
                }
            }
        }

        return experiences;
    }

    private List<ResumeDTO.ProjectDTO> extractProjects(Map<String, String> sections) {
        String projText = sections.get("projects");
        if (projText == null || projText.isBlank()) return List.of();

        List<ResumeDTO.ProjectDTO> projects = new ArrayList<>();
        String[] blocks = projText.split("\\n\\s*\\n");

        for (String block : blocks) {
            block = block.trim();
            if (block.length() < 15) continue;

            String[] lines = block.split("\\r?\\n");
            String name = "";
            String description = "";
            List<String> bullets = new ArrayList<>();
            List<String> technologies = new ArrayList<>();

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                if (i == 0) {
                    name = line.replaceFirst("^[•\\-●▪]\\s*", "");
                } else if (line.toLowerCase().startsWith("tech") || line.toLowerCase().contains("technologies used")) {
                    String techStr = line.replaceFirst("(?i)^.*?:\\s*", "");
                    technologies.addAll(Arrays.asList(techStr.split("\\s*[,|]\\s*")));
                } else if (line.startsWith("•") || line.startsWith("-") || line.startsWith("●")) {
                    bullets.add(line.replaceFirst("^[•\\-●▪]\\s*", ""));
                } else if (description.isEmpty()) {
                    description = line;
                } else {
                    bullets.add(line);
                }
            }

            if (technologies.isEmpty()) {
                technologies = SkillDictionary.extractSkills(block);
            }

            if (!name.isEmpty()) {
                projects.add(ResumeDTO.ProjectDTO.builder()
                        .name(name)
                        .description(description)
                        .technologies(technologies)
                        .bullets(bullets)
                        .build());
            }
        }

        return projects;
    }

    private String extractEducation(Map<String, String> sections, String fullText) {
        String eduText = sections.getOrDefault("education", fullText);
        return RegexUtil.extractEducation(eduText);
    }

    private String extractLocation(String text) {
        if (text == null) return null;

        List<String> cities = List.of(
                "Bengaluru", "Bangalore", "Mumbai", "Delhi", "Hyderabad", "Chennai",
                "Kolkata", "Pune", "Ahmedabad", "Jaipur", "Lucknow", "Noida",
                "Gurugram", "Gurgaon", "Chandigarh", "Indore", "Kochi", "Coimbatore",
                "Thiruvananthapuram", "Bhopal", "Nagpur", "Visakhapatnam",
                "New York", "San Francisco", "London", "Berlin", "Toronto", "Singapore",
                "Sydney", "Dubai", "Tokyo", "Remote"
        );

        String textLower = text.toLowerCase();
        for (String city : cities) {
            if (textLower.contains(city.toLowerCase())) {
                return city;
            }
        }

        return null;
    }

    private List<String> extractCertifications(Map<String, String> sections) {
        String certText = sections.get("certifications");
        if (certText == null || certText.isBlank()) return List.of();

        return Arrays.stream(certText.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.replaceFirst("^[•\\-●▪\\d.]+\\s*", ""))
                .filter(s -> s.length() > 3)
                .toList();
    }
}
