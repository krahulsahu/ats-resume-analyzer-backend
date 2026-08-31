package com.ats.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for AI-generated improvement suggestions.
 */
public class SuggestionDTO {
    private String improvedSummary;
    private List<ImprovedExperienceDTO> improvedExperience = new ArrayList<>();
    private List<ImprovedProjectDTO> improvedProjects = new ArrayList<>();
    private Map<String, List<String>> categorizedSkills = new HashMap<>();
    private List<String> generalSuggestions = new ArrayList<>();

    public SuggestionDTO() {}

    public SuggestionDTO(String improvedSummary, List<ImprovedExperienceDTO> improvedExperience,
                         List<ImprovedProjectDTO> improvedProjects,
                         Map<String, List<String>> categorizedSkills,
                         List<String> generalSuggestions) {
        this.improvedSummary = improvedSummary;
        this.improvedExperience = improvedExperience != null ? improvedExperience : new ArrayList<>();
        this.improvedProjects = improvedProjects != null ? improvedProjects : new ArrayList<>();
        this.categorizedSkills = categorizedSkills != null ? categorizedSkills : new HashMap<>();
        this.generalSuggestions = generalSuggestions != null ? generalSuggestions : new ArrayList<>();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String improvedSummary;
        private List<ImprovedExperienceDTO> improvedExperience = new ArrayList<>();
        private List<ImprovedProjectDTO> improvedProjects = new ArrayList<>();
        private Map<String, List<String>> categorizedSkills = new HashMap<>();
        private List<String> generalSuggestions = new ArrayList<>();

        public Builder improvedSummary(String improvedSummary) { this.improvedSummary = improvedSummary; return this; }
        public Builder improvedExperience(List<ImprovedExperienceDTO> improvedExperience) { this.improvedExperience = improvedExperience; return this; }
        public Builder improvedProjects(List<ImprovedProjectDTO> improvedProjects) { this.improvedProjects = improvedProjects; return this; }
        public Builder categorizedSkills(Map<String, List<String>> categorizedSkills) { this.categorizedSkills = categorizedSkills; return this; }
        public Builder generalSuggestions(List<String> generalSuggestions) { this.generalSuggestions = generalSuggestions; return this; }

        public SuggestionDTO build() {
            return new SuggestionDTO(improvedSummary, improvedExperience, improvedProjects, categorizedSkills, generalSuggestions);
        }
    }

    public String getImprovedSummary() { return improvedSummary; }
    public void setImprovedSummary(String improvedSummary) { this.improvedSummary = improvedSummary; }
    public List<ImprovedExperienceDTO> getImprovedExperience() { return improvedExperience; }
    public void setImprovedExperience(List<ImprovedExperienceDTO> improvedExperience) { this.improvedExperience = improvedExperience; }
    public List<ImprovedProjectDTO> getImprovedProjects() { return improvedProjects; }
    public void setImprovedProjects(List<ImprovedProjectDTO> improvedProjects) { this.improvedProjects = improvedProjects; }
    public Map<String, List<String>> getCategorizedSkills() { return categorizedSkills; }
    public void setCategorizedSkills(Map<String, List<String>> categorizedSkills) { this.categorizedSkills = categorizedSkills; }
    public List<String> getGeneralSuggestions() { return generalSuggestions; }
    public void setGeneralSuggestions(List<String> generalSuggestions) { this.generalSuggestions = generalSuggestions; }

    public static class ImprovedExperienceDTO {
        private String title;
        private String company;
        private List<String> improvedBullets = new ArrayList<>();

        public ImprovedExperienceDTO() {}
        public ImprovedExperienceDTO(String title, String company, List<String> improvedBullets) {
            this.title = title;
            this.company = company;
            this.improvedBullets = improvedBullets != null ? improvedBullets : new ArrayList<>();
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String title;
            private String company;
            private List<String> improvedBullets = new ArrayList<>();

            public Builder title(String title) { this.title = title; return this; }
            public Builder company(String company) { this.company = company; return this; }
            public Builder bullets(List<String> bullets) { this.improvedBullets = bullets; return this; }
            public Builder improvedBullets(List<String> bullets) { this.improvedBullets = bullets; return this; }

            public ImprovedExperienceDTO build() {
                return new ImprovedExperienceDTO(title, company, improvedBullets);
            }
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        public List<String> getImprovedBullets() { return improvedBullets; }
        public void setImprovedBullets(List<String> improvedBullets) { this.improvedBullets = improvedBullets; }
    }

    public static class ImprovedProjectDTO {
        private String name;
        private List<String> improvedBullets = new ArrayList<>();

        public ImprovedProjectDTO() {}
        public ImprovedProjectDTO(String name, List<String> improvedBullets) {
            this.name = name;
            this.improvedBullets = improvedBullets != null ? improvedBullets : new ArrayList<>();
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String name;
            private List<String> improvedBullets = new ArrayList<>();

            public Builder name(String name) { this.name = name; return this; }
            public Builder bullets(List<String> bullets) { this.improvedBullets = bullets; return this; }
            public Builder improvedBullets(List<String> bullets) { this.improvedBullets = bullets; return this; }

            public ImprovedProjectDTO build() {
                return new ImprovedProjectDTO(name, improvedBullets);
            }
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<String> getImprovedBullets() { return improvedBullets; }
        public void setImprovedBullets(List<String> improvedBullets) { this.improvedBullets = improvedBullets; }
    }
}
