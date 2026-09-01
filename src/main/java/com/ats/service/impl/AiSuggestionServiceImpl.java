package com.ats.service.impl;

import com.ats.config.GeminiConfig;
import com.ats.dto.JobDTO;
import com.ats.dto.ResumeDTO;
import com.ats.dto.SuggestionDTO;
import com.ats.service.AiSuggestionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * AI-powered resume improvement service using Google Gemini 1.5 Flash.
 * All prompts enforce structured JSON responses with no markdown wrappers.
 */
@Service
public class AiSuggestionServiceImpl implements AiSuggestionService {

    private static final Logger log = LoggerFactory.getLogger(AiSuggestionServiceImpl.class);

    private final GeminiConfig geminiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AiSuggestionServiceImpl(GeminiConfig geminiConfig, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.geminiConfig = geminiConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String improveSummary(ResumeDTO resume, JobDTO job) {
        String prompt = buildSummaryPrompt(resume, job);
        String response = callAi(prompt);
        if (response != null && !response.isBlank()) {
            try {
                String cleanJson = extractJson(response);
                JsonNode node = objectMapper.readTree(cleanJson);
                if (node.has("summary") && !node.path("summary").asText().isBlank()) {
                    return node.path("summary").asText().trim();
                }
            } catch (Exception e) {
                log.warn("Summary JSON parse failed, using raw response: {}", e.getMessage());
                return response.replaceAll("(?s)^```json\\s*", "").replaceAll("```$", "").trim();
            }
        }
        return generateFallbackSummary(resume, job);
    }

    @Override
    public SuggestionDTO generateSuggestions(ResumeDTO resume, JobDTO job) {
        SuggestionDTO.Builder builder = SuggestionDTO.builder();

        // 1. Summary
        try {
            String summary = improveSummary(resume, job);
            builder.improvedSummary(summary);
        } catch (Exception e) {
            log.warn("Summary generation error: {}", e.getMessage());
            builder.improvedSummary(generateFallbackSummary(resume, job));
        }

        // 2. Experience Bullets
        try {
            List<SuggestionDTO.ImprovedExperienceDTO> improvedExperience = improveExperience(resume, job);
            builder.improvedExperience(improvedExperience);
        } catch (Exception e) {
            log.warn("Experience generation error: {}", e.getMessage());
            builder.improvedExperience(generateFallbackExperience(resume));
        }

        // 3. Projects
        try {
            List<SuggestionDTO.ImprovedProjectDTO> improvedProjects = improveProjects(resume, job);
            builder.improvedProjects(improvedProjects);
        } catch (Exception e) {
            log.warn("Project generation error: {}", e.getMessage());
            builder.improvedProjects(generateFallbackProjects(resume));
        }

        // 4. Categorized Skills
        try {
            Map<String, List<String>> categorized = categorizeSkillsWithAi(resume.getSkills());
            builder.categorizedSkills(categorized);
        } catch (Exception e) {
            log.warn("Skill categorization error: {}", e.getMessage());
            builder.categorizedSkills(categorizeSkillsHeuristic(resume.getSkills()));
        }

        // 5. General Tips
        builder.generalSuggestions(generateGeneralSuggestions(resume, job));

        return builder.build();
    }

    private List<SuggestionDTO.ImprovedExperienceDTO> improveExperience(ResumeDTO resume, JobDTO job) {
        List<SuggestionDTO.ImprovedExperienceDTO> result = new ArrayList<>();
        if (resume.getExperience() == null || resume.getExperience().isEmpty()) {
            return result;
        }

        for (ResumeDTO.ExperienceDTO exp : resume.getExperience()) {
            String prompt = buildExperiencePrompt(exp, job);
            String response = callAi(prompt);

            List<String> improvedBullets = new ArrayList<>();
            if (response != null && !response.isBlank()) {
                try {
                    String cleanJson = extractJson(response);
                    JsonNode node = objectMapper.readTree(cleanJson);
                    if (node.has("bullets") && node.path("bullets").isArray()) {
                        for (JsonNode b : node.path("bullets")) {
                            if (!b.asText().isBlank()) {
                                improvedBullets.add(b.asText().replaceFirst("^[•\\-*\\d.]+\\s*", "").trim());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Experience JSON parse fallback: {}", e.getMessage());
                }
            }

            if (improvedBullets.isEmpty()) {
                improvedBullets = exp.getBullets() != null ? exp.getBullets().stream()
                        .map(b -> "Architected and delivered " + b + ", improving system throughput and workflow automation.")
                        .toList() : List.of();
            }

            result.add(SuggestionDTO.ImprovedExperienceDTO.builder()
                    .title(exp.getTitle())
                    .company(exp.getCompany())
                    .improvedBullets(improvedBullets)
                    .build());
        }

        return result;
    }

    private List<SuggestionDTO.ImprovedProjectDTO> improveProjects(ResumeDTO resume, JobDTO job) {
        List<SuggestionDTO.ImprovedProjectDTO> result = new ArrayList<>();
        if (resume.getProjects() == null || resume.getProjects().isEmpty()) {
            return result;
        }

        for (ResumeDTO.ProjectDTO proj : resume.getProjects()) {
            String prompt = buildProjectPrompt(proj, job);
            String response = callAi(prompt);

            List<String> improvedBullets = new ArrayList<>();
            if (response != null && !response.isBlank()) {
                try {
                    String cleanJson = extractJson(response);
                    JsonNode node = objectMapper.readTree(cleanJson);
                    if (node.has("bullets") && node.path("bullets").isArray()) {
                        for (JsonNode b : node.path("bullets")) {
                            if (!b.asText().isBlank()) {
                                improvedBullets.add(b.asText().replaceFirst("^[•\\-*\\d.]+\\s*", "").trim());
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }

            if (improvedBullets.isEmpty()) {
                improvedBullets = proj.getBullets() != null ? proj.getBullets() : List.of();
            }

            result.add(SuggestionDTO.ImprovedProjectDTO.builder()
                    .name(proj.getName())
                    .improvedBullets(improvedBullets)
                    .build());
        }

        return result;
    }

    private Map<String, List<String>> categorizeSkillsWithAi(List<String> skills) {
        if (skills == null || skills.isEmpty()) return new LinkedHashMap<>();

        String prompt = """
                SYSTEM:
                Categorize these technical skills into standard ATS resume categories. Return JSON only with no surrounding text.

                SKILLS:
                %s

                OUTPUT JSON FORMAT:
                {
                  "Languages": [],
                  "Backend": [],
                  "Frontend": [],
                  "Databases": [],
                  "Cloud & DevOps": [],
                  "Developer Tools": []
                }
                """.formatted(String.join(", ", skills));

        String response = callAi(prompt);
        if (response != null && !response.isBlank()) {
            try {
                String cleanJson = extractJson(response);
                JsonNode node = objectMapper.readTree(cleanJson);
                Map<String, List<String>> result = new LinkedHashMap<>();
                node.fieldNames().forEachRemaining(key -> {
                    List<String> list = new ArrayList<>();
                    node.path(key).forEach(item -> list.add(item.asText()));
                    if (!list.isEmpty()) result.put(key, list);
                });
                if (!result.isEmpty()) return result;
            } catch (Exception ignored) {}
        }
        return categorizeSkillsHeuristic(skills);
    }

    private Map<String, List<String>> categorizeSkillsHeuristic(List<String> skills) {
        Map<String, List<String>> categorized = new LinkedHashMap<>();
        if (skills == null) return categorized;

        for (String skill : skills) {
            String category = com.ats.util.SkillDictionary.getCategory(skill);
            categorized.computeIfAbsent(category, k -> new ArrayList<>()).add(skill);
        }

        return categorized;
    }

    private List<String> generateGeneralSuggestions(ResumeDTO resume, JobDTO job) {
        List<String> suggestions = new ArrayList<>();
        if (resume.getSummary() == null || resume.getSummary().length() < 50) {
            suggestions.add("Add a strong professional summary highlighting your key achievements and years of experience.");
        }
        if (job.getRequiredSkills() != null && !job.getRequiredSkills().isEmpty()) {
            suggestions.add("Explicitly incorporate required technologies (" +
                    String.join(", ", job.getRequiredSkills().stream().limit(4).toList()) + ") into work experience bullets.");
        }
        suggestions.add("Quantify bullet points with tangible metrics (e.g., 'reduced latency by 35%', 'handled 50K+ requests/day').");
        suggestions.add("Use strong action verbs (Architected, Spearheaded, Optimized, Engineered) to begin every bullet point.");
        suggestions.add("Keep resume single-column and table-free for 100% ATS readability.");
        return suggestions;
    }

    // --- Gemini 1.5 Flash Dispatcher ---

    private String callAi(String prompt) {
        if (!geminiConfig.hasApiKey()) return null;

        try {
            String url = geminiConfig.getBaseUrl() + "/" + geminiConfig.getModel() + ":generateContent?key=" + geminiConfig.getApiKey();

            Map<String, Object> textPart = Map.of("text", prompt);
            Map<String, Object> content = Map.of("parts", List.of(textPart));
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(content),
                    "generationConfig", Map.of(
                            "temperature", 0.4,
                            "maxOutputTokens", 1000,
                            "responseMimeType", "application/json"
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", geminiConfig.getApiKey());
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && candidates.size() > 0) {
                    JsonNode textNode = candidates.get(0).path("content").path("parts").get(0).path("text");
                    return textNode.asText("").trim();
                }
            }
        } catch (Exception e) {
            log.warn("Gemini API call failed: {}", e.getMessage());
        }

        return null;
    }

    private String extractJson(String text) {
        if (text == null) return "{}";
        String trimmed = text.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.trim();
    }

    // --- Fallbacks ---

    private String generateFallbackSummary(ResumeDTO resume, JobDTO job) {
        double years = resume.getExperience() != null ? resume.getExperience().stream()
                .mapToDouble(ResumeDTO.ExperienceDTO::getYears).sum() : 2.0;
        String skillStr = resume.getSkills() != null && !resume.getSkills().isEmpty()
                ? String.join(", ", resume.getSkills().stream().limit(6).toList())
                : "Java, Spring Boot, REST APIs, Microservices";
        String targetTitle = (job != null && job.getTitle() != null && !job.getTitle().isBlank())
                ? job.getTitle() : "Software Engineer";

        return "Results-driven " + targetTitle + " with " + (years > 0 ? (int) Math.ceil(years) : 2) + "+ years of hands-on experience designing and deploying scalable, high-throughput enterprise applications. Proficient in " + skillStr + ". Proven track record of optimizing system performance, streamlining CI/CD delivery pipelines, and architecting robust RESTful microservices that deliver measurable business impact across the full SDLC.";
    }

    private List<SuggestionDTO.ImprovedExperienceDTO> generateFallbackExperience(ResumeDTO resume) {
        List<SuggestionDTO.ImprovedExperienceDTO> list = new ArrayList<>();
        if (resume.getExperience() == null) return list;

        for (var exp : resume.getExperience()) {
            List<String> bullets = exp.getBullets() != null && !exp.getBullets().isEmpty()
                    ? exp.getBullets().stream().map(b -> "Architected and delivered " + b + ", improving workflow automation and reducing system latency.").toList()
                    : List.of("Designed and implemented scalable backend microservices and REST APIs, optimizing query performance by 35%.");
            list.add(SuggestionDTO.ImprovedExperienceDTO.builder()
                    .title(exp.getTitle())
                    .company(exp.getCompany())
                    .improvedBullets(bullets)
                    .build());
        }
        return list;
    }

    private List<SuggestionDTO.ImprovedProjectDTO> generateFallbackProjects(ResumeDTO resume) {
        List<SuggestionDTO.ImprovedProjectDTO> list = new ArrayList<>();
        if (resume.getProjects() == null) return list;

        for (var proj : resume.getProjects()) {
            list.add(SuggestionDTO.ImprovedProjectDTO.builder()
                    .name(proj.getName())
                    .improvedBullets(proj.getBullets() != null ? proj.getBullets() : List.of("Architected full-stack solution with secure authentication, cloud containerization, and responsive UI."))
                    .build());
        }
        return list;
    }

    // --- JSON Prompts ---

    private String buildSummaryPrompt(ResumeDTO resume, JobDTO job) {
        return """
                SYSTEM:
                You are an expert ATS Resume Writer.

                TASK:
                Rewrite only the Professional Summary.

                RULES:
                - Maximum 60-75 words
                - No fake experience or exaggerated titles
                - Use keywords from the target job naturally
                - Keep 100%% factual accuracy based on candidate background
                - Return JSON ONLY (no markdown code blocks, no backticks)

                CANDIDATE:
                Name: %s
                Skills: %s
                Target Role: %s
                Required Skills: %s

                OUTPUT JSON FORMAT:
                {
                  "summary": "...",
                  "keywordsAdded": ["skill1", "skill2"]
                }
                """.formatted(
                resume.getName(),
                String.join(", ", resume.getSkills() != null ? resume.getSkills() : List.of()),
                job != null ? job.getTitle() : "Software Engineer",
                job != null && job.getRequiredSkills() != null ? String.join(", ", job.getRequiredSkills()) : "Java, Spring Boot"
        );
    }

    private String buildExperiencePrompt(ResumeDTO.ExperienceDTO exp, JobDTO job) {
        return """
                SYSTEM:
                You are an expert ATS Resume Writer. Rewrite each bullet using:
                Action Verb + Technology + Measurable Business Impact

                RULES:
                - 22 to 32 words per bullet
                - Preserve factual information
                - Incorporate target job skills where authentic
                - Return JSON ONLY (no markdown code blocks, no backticks)

                ROLE: %s at %s
                CURRENT BULLETS:
                %s

                TARGET JOB SKILLS: %s

                OUTPUT JSON FORMAT:
                {
                  "bullets": [
                    "...",
                    "..."
                  ]
                }
                """.formatted(
                exp.getTitle(),
                exp.getCompany() != null ? exp.getCompany() : "",
                exp.getBullets() != null ? String.join("\n", exp.getBullets()) : "",
                job != null && job.getRequiredSkills() != null ? String.join(", ", job.getRequiredSkills()) : ""
        );
    }

    private String buildProjectPrompt(ResumeDTO.ProjectDTO proj, JobDTO job) {
        return """
                SYSTEM:
                You are an expert ATS Resume Writer. Rewrite project bullets using STAR methodology (Situation, Task, Action, Result).

                PROJECT: %s
                CURRENT BULLETS:
                %s

                RULES:
                - 2 to 3 powerful bullets
                - 20 to 30 words each
                - Return JSON ONLY

                OUTPUT JSON FORMAT:
                {
                  "bullets": [
                    "...",
                    "..."
                  ]
                }
                """.formatted(
                proj.getName(),
                proj.getBullets() != null ? String.join("\n", proj.getBullets()) : ""
        );
    }
}
