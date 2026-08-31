package com.ats.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing a parsed job description.
 */
public class JobDescription {
    private String title;
    private List<String> requiredSkills = new ArrayList<>();
    private List<String> preferredSkills = new ArrayList<>();
    private String experience;
    private double experienceYears;
    private String education;
    private String location;
    private String noticePeriod;
    private List<String> responsibilities = new ArrayList<>();
    private List<String> tools = new ArrayList<>();
    private String rawText;

    public JobDescription() {}

    public JobDescription(String title, List<String> requiredSkills, List<String> preferredSkills,
                          String experience, double experienceYears, String education, String location,
                          String noticePeriod, List<String> responsibilities, List<String> tools, String rawText) {
        this.title = title;
        this.requiredSkills = requiredSkills != null ? requiredSkills : new ArrayList<>();
        this.preferredSkills = preferredSkills != null ? preferredSkills : new ArrayList<>();
        this.experience = experience;
        this.experienceYears = experienceYears;
        this.education = education;
        this.location = location;
        this.noticePeriod = noticePeriod;
        this.responsibilities = responsibilities != null ? responsibilities : new ArrayList<>();
        this.tools = tools != null ? tools : new ArrayList<>();
        this.rawText = rawText;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String title;
        private List<String> requiredSkills = new ArrayList<>();
        private List<String> preferredSkills = new ArrayList<>();
        private String experience;
        private double experienceYears;
        private String education;
        private String location;
        private String noticePeriod;
        private List<String> responsibilities = new ArrayList<>();
        private List<String> tools = new ArrayList<>();
        private String rawText;

        public Builder title(String title) { this.title = title; return this; }
        public Builder requiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; return this; }
        public Builder preferredSkills(List<String> preferredSkills) { this.preferredSkills = preferredSkills; return this; }
        public Builder experience(String experience) { this.experience = experience; return this; }
        public Builder experienceYears(double experienceYears) { this.experienceYears = experienceYears; return this; }
        public Builder education(String education) { this.education = education; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder noticePeriod(String noticePeriod) { this.noticePeriod = noticePeriod; return this; }
        public Builder responsibilities(List<String> responsibilities) { this.responsibilities = responsibilities; return this; }
        public Builder tools(List<String> tools) { this.tools = tools; return this; }
        public Builder rawText(String rawText) { this.rawText = rawText; return this; }

        public JobDescription build() {
            return new JobDescription(title, requiredSkills, preferredSkills, experience, experienceYears, education, location, noticePeriod, responsibilities, tools, rawText);
        }
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }
    public List<String> getPreferredSkills() { return preferredSkills; }
    public void setPreferredSkills(List<String> preferredSkills) { this.preferredSkills = preferredSkills; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public double getExperienceYears() { return experienceYears; }
    public void setExperienceYears(double experienceYears) { this.experienceYears = experienceYears; }
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getNoticePeriod() { return noticePeriod; }
    public void setNoticePeriod(String noticePeriod) { this.noticePeriod = noticePeriod; }
    public List<String> getResponsibilities() { return responsibilities; }
    public void setResponsibilities(List<String> responsibilities) { this.responsibilities = responsibilities; }
    public List<String> getTools() { return tools; }
    public void setTools(List<String> tools) { this.tools = tools; }
    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }
}
