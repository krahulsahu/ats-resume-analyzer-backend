package com.ats.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing a parsed resume.
 */
public class Resume {
    private String name;
    private String email;
    private String phone;
    private String summary;
    private List<String> skills = new ArrayList<>();
    private List<Experience> experience = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();
    private String education;
    private String location;
    private String noticePeriod;
    private List<String> certifications = new ArrayList<>();
    private String rawText;

    public Resume() {}

    public Resume(String name, String email, String phone, String summary,
                  List<String> skills, List<Experience> experience,
                  List<Project> projects, String education, String location,
                  String noticePeriod, List<String> certifications, String rawText) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.summary = summary;
        this.skills = skills != null ? skills : new ArrayList<>();
        this.experience = experience != null ? experience : new ArrayList<>();
        this.projects = projects != null ? projects : new ArrayList<>();
        this.education = education;
        this.location = location;
        this.noticePeriod = noticePeriod;
        this.certifications = certifications != null ? certifications : new ArrayList<>();
        this.rawText = rawText;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String email;
        private String phone;
        private String summary;
        private List<String> skills = new ArrayList<>();
        private List<Experience> experience = new ArrayList<>();
        private List<Project> projects = new ArrayList<>();
        private String education;
        private String location;
        private String noticePeriod;
        private List<String> certifications = new ArrayList<>();
        private String rawText;

        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder summary(String summary) { this.summary = summary; return this; }
        public Builder skills(List<String> skills) { this.skills = skills; return this; }
        public Builder experience(List<Experience> experience) { this.experience = experience; return this; }
        public Builder projects(List<Project> projects) { this.projects = projects; return this; }
        public Builder education(String education) { this.education = education; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder noticePeriod(String noticePeriod) { this.noticePeriod = noticePeriod; return this; }
        public Builder certifications(List<String> certifications) { this.certifications = certifications; return this; }
        public Builder rawText(String rawText) { this.rawText = rawText; return this; }

        public Resume build() {
            return new Resume(name, email, phone, summary, skills, experience, projects, education, location, noticePeriod, certifications, rawText);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public List<Experience> getExperience() { return experience; }
    public void setExperience(List<Experience> experience) { this.experience = experience; }
    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getNoticePeriod() { return noticePeriod; }
    public void setNoticePeriod(String noticePeriod) { this.noticePeriod = noticePeriod; }
    public List<String> getCertifications() { return certifications; }
    public void setCertifications(List<String> certifications) { this.certifications = certifications; }
    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }
}
