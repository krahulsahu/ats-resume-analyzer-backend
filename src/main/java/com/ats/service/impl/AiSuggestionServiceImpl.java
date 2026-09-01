package com.ats.service.impl;

import com.ats.config.GeminiConfig;
import com.ats.config.OllamaConfig;
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
 * AI-powered resume improvement service using Google Gemini 1.5 Flash (with Ollama fallback).
 * Used strictly for rewriting and suggestions — never for deterministic scoring.
 */
@Service
public class AiSuggestionServiceImpl implements AiSuggestionService {

    private static final Logger log = LoggerFactory.getLogger(AiSuggestionServiceImpl.class);

    private final GeminiConfig geminiConfig;
    private final OllamaConfig ollamaConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AiSuggestionServiceImpl(GeminiConfig geminiConfig, OllamaConfig ollamaConfig,
                                   RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.geminiConfig = geminiConfig;
        this.ollamaConfig = ollamaConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String improveSummary(ResumeDTO resume, JobDTO job) {
        String prompt = buildSummaryPrompt(resume, job);
        String response = callAi(prompt);
        if (response != null && !response.isBlank()) {
            return response;
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
            Map<String, List<String>> categorized = categorizeSkills(resume.getSkills());
            builder.categorizedSkills(categorized);
        } catch (Exception e) {
            log.warn("Skill categorization error: {}", e.getMessage());
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
                improvedBullets = Arrays.stream(response.split("\\r?\\n"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty() && (s.startsWith("•") || s.startsWith("-") || s.startsWith("*") || s.length() > 20))
                        .map(s -> s.replaceFirst("^[•\\-*\\d.]+\\s*", ""))
                        .toList();
            }

            if (improvedBullets.isEmpty()) {
                improvedBullets = exp.getBullets() != null ? exp.getBullets().stream()
                        .map(b -> "Architected and delivered " + b + ", improving workflow automation and system reliability.")
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
                improvedBullets = Arrays.stream(response.split("\\r?\\n"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty() && s.length() > 15)
                        .map(s -> s.replaceFirst("^[•\\-*\\d.]+\\s*", ""))
                        .toList();
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

    private Map<String, List<String>> categorizeSkills(List<String> skills) {
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

    // --- AI Dispatcher: Gemini 1.5 Flash -> Ollama -> Fallback ---

    private String callAi(String prompt) {
        // Priority 1: Google Gemini 1.5 Flash
        if (geminiConfig.hasApiKey()) {
            try {
                String geminiResponse = callGemini(prompt);
                if (geminiResponse != null && !geminiResponse.isBlank()) {
                    return geminiResponse;
                }
            } catch (Exception e) {
                log.warn("Gemini API call failed: {}", e.getMessage());
            }
        }

        // Priority 2: Local Ollama
        try {
            String ollamaResponse = callOllama(prompt);
            if (ollamaResponse != null && !ollamaResponse.isBlank()) {
                return ollamaResponse;
            }
        } catch (Exception e) {
            log.debug("Ollama unavailable: {}", e.getMessage());
        }

        return null;
    }

    private String callGemini(String prompt) {
        String url = geminiConfig.getBaseUrl() + "/" + geminiConfig.getModel() + ":generateContent?key=" + geminiConfig.getApiKey();

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(textPart));
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(content),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "maxOutputTokens", 800
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        log.info("Calling Google Gemini 1.5 Flash API...");
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && candidates.size() > 0) {
                    JsonNode textNode = candidates.get(0).path("content").path("parts").get(0).path("text");
                    String text = textNode.asText("");
                    log.info("Gemini 1.5 Flash generated {} characters", text.length());
                    return text.trim();
                }
            } catch (Exception e) {
                log.error("Failed to parse Gemini response", e);
            }
        }
        return null;
    }

    private String callOllama(String prompt) {
        String url = ollamaConfig.getBaseUrl() + "/api/generate";

        Map<String, Object> request = new HashMap<>();
        request.put("model", ollamaConfig.getModel());
        request.put("prompt", prompt);
        request.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.path("response").asText("").trim();
            } catch (Exception ignored) {}
        }
        return null;
    }

    // --- Fallback Generation (Smart Heuristics) ---

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

    // --- Prompt Builders ---

    private String buildSummaryPrompt(ResumeDTO resume, JobDTO job) {
        return """
                You are an expert ATS resume writer. Write an ATS-optimized professional summary for this candidate targeting this job.

                CANDIDATE:
                Name: %s
                Skills: %s
                Target Role: %s
                Required Skills: %s

                RULES:
                - Output exactly 75-85 words.
                - Start with years of experience and role.
                - Incorporate key technical skills naturally.
                - Highlight business impact and scalability.
                - Return ONLY the summary paragraph, no intro or markdown title.
                """.formatted(
                resume.getName(),
                String.join(", ", resume.getSkills() != null ? resume.getSkills() : List.of()),
                job != null ? job.getTitle() : "Software Engineer",
                job != null && job.getRequiredSkills() != null ? String.join(", ", job.getRequiredSkills()) : "Java, Spring Boot"
        );
    }

    private String buildExperiencePrompt(ResumeDTO.ExperienceDTO exp, JobDTO job) {
        return """
                You are an expert ATS resume writer. Rewrite these experience bullet points.

                ROLE: %s at %s
                CURRENT BULLETS:
                %s

                TARGET JOB SKILLS: %s

                RULES:
                - Use the formula: Action Verb + Technology + Measurable Business Impact.
                - Rewrite each bullet point to be powerful and ATS keyword-rich.
                - Return 2 to 4 bullet points, one per line, prefixed with "•".
                - Return ONLY the bullet points, no commentary.
                """.formatted(
                exp.getTitle(),
                exp.getCompany() != null ? exp.getCompany() : "",
                exp.getBullets() != null ? String.join("\n", exp.getBullets()) : "",
                job != null && job.getRequiredSkills() != null ? String.join(", ", job.getRequiredSkills()) : ""
        );
    }

    private String buildProjectPrompt(ResumeDTO.ProjectDTO proj, JobDTO job) {
        return """
                You are an expert ATS resume writer. Rewrite this project description using STAR methodology (Situation, Task, Action, Result).

                PROJECT: %s
                CURRENT BULLETS:
                %s

                RULES:
                - Output 2 to 3 bullet points, each starting with "•".
                - Emphasize technical architecture, performance improvements, and tech stack.
                - Return ONLY the bullet points.
                """.formatted(
                proj.getName(),
                proj.getBullets() != null ? String.join("\n", proj.getBullets()) : ""
        );
    }
}
