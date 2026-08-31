package com.ats.service.impl;

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
 * AI-powered resume improvement service using Ollama (Llama 3.1 8B).
 * Used only for rewriting and suggestions — never for scoring.
 */
@Service
public class AiSuggestionServiceImpl implements AiSuggestionService {

    private static final Logger log = LoggerFactory.getLogger(AiSuggestionServiceImpl.class);

    private final OllamaConfig ollamaConfig;
    private final RestTemplate ollamaRestTemplate;
    private final ObjectMapper objectMapper;

    public AiSuggestionServiceImpl(OllamaConfig ollamaConfig, RestTemplate ollamaRestTemplate, ObjectMapper objectMapper) {
        this.ollamaConfig = ollamaConfig;
        this.ollamaRestTemplate = ollamaRestTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String improveSummary(ResumeDTO resume, JobDTO job) {
        String prompt = buildSummaryPrompt(resume, job);
        return callOllama(prompt);
    }

    @Override
    public SuggestionDTO generateSuggestions(ResumeDTO resume, JobDTO job) {
        SuggestionDTO.Builder builder = SuggestionDTO.builder();

        try {
            String improvedSummary = improveSummary(resume, job);
            builder.improvedSummary(improvedSummary);
        } catch (Exception e) {
            log.warn("Failed to generate improved summary: {}", e.getMessage());
            builder.improvedSummary("AI improvement unavailable. Ensure Ollama is running.");
        }

        try {
            List<SuggestionDTO.ImprovedExperienceDTO> improvedExperience = improveExperience(resume, job);
            builder.improvedExperience(improvedExperience);
        } catch (Exception e) {
            log.warn("Failed to improve experience: {}", e.getMessage());
        }

        try {
            List<SuggestionDTO.ImprovedProjectDTO> improvedProjects = improveProjects(resume, job);
            builder.improvedProjects(improvedProjects);
        } catch (Exception e) {
            log.warn("Failed to improve projects: {}", e.getMessage());
        }

        try {
            Map<String, List<String>> categorized = categorizeSkills(resume.getSkills());
            builder.categorizedSkills(categorized);
        } catch (Exception e) {
            log.warn("Failed to categorize skills: {}", e.getMessage());
        }

        builder.generalSuggestions(generateGeneralSuggestions(resume, job));

        return builder.build();
    }

    private List<SuggestionDTO.ImprovedExperienceDTO> improveExperience(ResumeDTO resume, JobDTO job) {
        List<SuggestionDTO.ImprovedExperienceDTO> result = new ArrayList<>();

        if (resume.getExperience() == null) return result;

        for (ResumeDTO.ExperienceDTO exp : resume.getExperience()) {
            String prompt = buildExperiencePrompt(exp, job);
            String response = callOllama(prompt);

            List<String> improvedBullets = Arrays.stream(response.split("\\r?\\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .filter(s -> s.startsWith("•") || s.startsWith("-") || s.startsWith("–") || s.length() > 20)
                    .map(s -> s.replaceFirst("^[•\\-–\\d.]+\\s*", ""))
                    .toList();

            result.add(SuggestionDTO.ImprovedExperienceDTO.builder()
                    .title(exp.getTitle())
                    .company(exp.getCompany())
                    .improvedBullets(improvedBullets.isEmpty() ? List.of(response) : improvedBullets)
                    .build());
        }

        return result;
    }

    private List<SuggestionDTO.ImprovedProjectDTO> improveProjects(ResumeDTO resume, JobDTO job) {
        List<SuggestionDTO.ImprovedProjectDTO> result = new ArrayList<>();

        if (resume.getProjects() == null) return result;

        for (ResumeDTO.ProjectDTO proj : resume.getProjects()) {
            String prompt = buildProjectPrompt(proj, job);
            String response = callOllama(prompt);

            List<String> improvedBullets = Arrays.stream(response.split("\\r?\\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty() && s.length() > 15)
                    .map(s -> s.replaceFirst("^[•\\-–\\d.]+\\s*", ""))
                    .toList();

            result.add(SuggestionDTO.ImprovedProjectDTO.builder()
                    .name(proj.getName())
                    .improvedBullets(improvedBullets.isEmpty() ? List.of(response) : improvedBullets)
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
            suggestions.add("Rewrite your professional summary to be ATS-optimized and include key skills.");
        }
        if (job.getRequiredSkills() != null) {
            suggestions.add("Ensure your resume contains these key skills: " +
                    String.join(", ", job.getRequiredSkills().stream().limit(5).toList()));
        }
        suggestions.add("Use action verbs at the start of each bullet point.");
        suggestions.add("Quantify achievements with numbers, percentages, and metrics where possible.");
        suggestions.add("Maintain a clean, single-column format for ATS readability.");

        return suggestions;
    }

    private String buildSummaryPrompt(ResumeDTO resume, JobDTO job) {
        return """
                You are an expert ATS resume writer. Write an improved professional summary for this candidate.

                CANDIDATE INFO:
                Name: %s
                Current Summary: %s
                Skills: %s
                Experience: %s years

                JOB TARGET:
                Title: %s
                Required Skills: %s

                RULES:
                - Write exactly 80 words
                - ATS optimized with relevant keywords
                - Do NOT fabricate experience or skills the candidate doesn't have
                - Use a professional tone
                - Mention key technical skills naturally
                - Start with years of experience and role
                - Return ONLY the summary text, nothing else
                """.formatted(
                resume.getName(),
                resume.getSummary() != null ? resume.getSummary() : "None",
                String.join(", ", resume.getSkills() != null ? resume.getSkills() : List.of()),
                resume.getExperience() != null ? resume.getExperience().stream()
                        .mapToDouble(ResumeDTO.ExperienceDTO::getYears).sum() : 0,
                job.getTitle(),
                String.join(", ", job.getRequiredSkills() != null ? job.getRequiredSkills() : List.of())
        );
    }

    private String buildExperiencePrompt(ResumeDTO.ExperienceDTO exp, JobDTO job) {
        return """
                You are an expert ATS resume writer. Rewrite these experience bullet points.

                ROLE: %s at %s

                CURRENT BULLETS:
                %s

                JOB TARGET SKILLS: %s

                RULES:
                - Use format: Action Verb + Technology + Business Impact
                - Each bullet must start with a strong action verb
                - Include specific technologies where appropriate
                - Add measurable business impact (percentages, numbers)
                - Do NOT fabricate achievements
                - Keep each bullet to 1-2 lines
                - Return only the improved bullets, one per line, prefixed with "•"
                """.formatted(
                exp.getTitle(),
                exp.getCompany() != null ? exp.getCompany() : "",
                exp.getBullets() != null ? String.join("\n", exp.getBullets()) : "No bullets provided",
                job.getRequiredSkills() != null ? String.join(", ", job.getRequiredSkills()) : ""
        );
    }

    private String buildProjectPrompt(ResumeDTO.ProjectDTO proj, JobDTO job) {
        return """
                You are an expert ATS resume writer. Rewrite this project description using the STAR format.

                PROJECT: %s
                Description: %s

                CURRENT BULLETS:
                %s

                Technologies: %s

                RULES:
                - Use STAR format: Situation, Task, Action, Result
                - Highlight technical complexity and business impact
                - Include specific technologies used
                - Keep concise (3-4 bullets maximum)
                - Do NOT fabricate features or results
                - Return only the improved bullets, one per line, prefixed with "•"
                """.formatted(
                proj.getName(),
                proj.getDescription() != null ? proj.getDescription() : "",
                proj.getBullets() != null ? String.join("\n", proj.getBullets()) : "No bullets provided",
                proj.getTechnologies() != null ? String.join(", ", proj.getTechnologies()) : ""
        );
    }

    private String callOllama(String prompt) {
        try {
            String url = ollamaConfig.getBaseUrl() + "/api/generate";

            Map<String, Object> request = new HashMap<>();
            request.put("model", ollamaConfig.getModel());
            request.put("prompt", prompt);
            request.put("stream", false);
            request.put("options", Map.of(
                    "temperature", 0.7,
                    "top_p", 0.9,
                    "num_predict", 500
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            log.debug("Calling Ollama: model={}, prompt length={}", ollamaConfig.getModel(), prompt.length());

            ResponseEntity<String> response = ollamaRestTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String responseText = jsonNode.path("response").asText("");
                log.debug("Ollama response: {} chars", responseText.length());
                return responseText.trim();
            }

            return "AI response unavailable.";
        } catch (Exception e) {
            log.error("Ollama API call failed: {}", e.getMessage());
            return "AI improvement unavailable. Ensure Ollama is running at " + ollamaConfig.getBaseUrl();
        }
    }
}
