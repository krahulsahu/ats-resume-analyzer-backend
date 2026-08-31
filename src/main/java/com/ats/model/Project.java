package com.ats.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing a project entry.
 */
public class Project {
    private String name;
    private String description;
    private List<String> technologies = new ArrayList<>();
    private List<String> bullets = new ArrayList<>();

    public Project() {}

    public Project(String name, String description, List<String> technologies, List<String> bullets) {
        this.name = name;
        this.description = description;
        this.technologies = technologies != null ? technologies : new ArrayList<>();
        this.bullets = bullets != null ? bullets : new ArrayList<>();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name;
        private String description;
        private List<String> technologies = new ArrayList<>();
        private List<String> bullets = new ArrayList<>();

        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder technologies(List<String> technologies) { this.technologies = technologies; return this; }
        public Builder bullets(List<String> bullets) { this.bullets = bullets; return this; }
        public Project build() { return new Project(name, description, technologies, bullets); }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getTechnologies() { return technologies; }
    public void setTechnologies(List<String> technologies) { this.technologies = technologies; }
    public List<String> getBullets() { return bullets; }
    public void setBullets(List<String> bullets) { this.bullets = bullets; }
}
