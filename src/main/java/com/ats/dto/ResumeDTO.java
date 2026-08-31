package com.ats.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for resume data sent/received via API.
 */
public class ResumeDTO {
    private String name;
    private String email;
    private String phone;
    private String summary;
    private List<String> skills = new ArrayList<>();
    private List<ExperienceDTO> experience = new ArrayList<>();
    private List<ProjectDTO> projects = new ArrayList<>();
    private String education;
    private String location;
    private String noticePeriod;
    private List<String> certifications = new ArrayList<>();

    public ResumeDTO() {}

    public ResumeDTO(String name, String email, String phone, String summary,
                     List<String> skills, List<ExperienceDTO> experience,
                     List<ProjectDTO> projects, String education, String location,
                     String noticePeriod, List<String> certifications) {
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
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name;
        private String email;
        private String phone;
        private String summary;
        private List<String> skills = new ArrayList<>();
        private List<ExperienceDTO> experience = new ArrayList<>();
        private List<ProjectDTO> projects = new ArrayList<>();
        private String education;
        private String location;
        private String noticePeriod;
        private List<String> certifications = new ArrayList<>();

        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder summary(String summary) { this.summary = summary; return this; }
        public Builder skills(List<String> skills) { this.skills = skills; return this; }
        public Builder experience(List<ExperienceDTO> experience) { this.experience = experience; return this; }
        public Builder projects(List<ProjectDTO> projects) { this.projects = projects; return this; }
        public Builder education(String education) { this.education = education; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder noticePeriod(String noticePeriod) { this.noticePeriod = noticePeriod; return this; }
        public Builder certifications(List<String> certifications) { this.certifications = certifications; return this; }

        public ResumeDTO build() {
            return new ResumeDTO(name, email, phone, summary, skills, experience, projects, education, location, noticePeriod, certifications);
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
    public List<ExperienceDTO> getExperience() { return experience; }
    public void setExperience(List<ExperienceDTO> experience) { this.experience = experience; }
    public List<ProjectDTO> getProjects() { return projects; }
    public void setProjects(List<ProjectDTO> projects) { this.projects = projects; }
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getNoticePeriod() { return noticePeriod; }
    public void setNoticePeriod(String noticePeriod) { this.noticePeriod = noticePeriod; }
    public List<String> getCertifications() { return certifications; }
    public void setCertifications(List<String> certifications) { this.certifications = certifications; }

    public static class ExperienceDTO {
        private String title;
        private String company;
        private String duration;
        private double years;
        private List<String> bullets = new ArrayList<>();

        public ExperienceDTO() {}
        public ExperienceDTO(String title, String company, String duration, double years, List<String> bullets) {
            this.title = title;
            this.company = company;
            this.duration = duration;
            this.years = years;
            this.bullets = bullets != null ? bullets : new ArrayList<>();
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String title;
            private String company;
            private String duration;
            private double years;
            private List<String> bullets = new ArrayList<>();

            public Builder title(String title) { this.title = title; return this; }
            public Builder company(String company) { this.company = company; return this; }
            public Builder duration(String duration) { this.duration = duration; return this; }
            public Builder years(double years) { this.years = years; return this; }
            public Builder bullets(List<String> bullets) { this.bullets = bullets; return this; }
            public ExperienceDTO build() { return new ExperienceDTO(title, company, duration, years, bullets); }
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        public double getYears() { return years; }
        public void setYears(double years) { this.years = years; }
        public List<String> getBullets() { return bullets; }
        public void setBullets(List<String> bullets) { this.bullets = bullets; }
    }

    public static class ProjectDTO {
        private String name;
        private String description;
        private List<String> technologies = new ArrayList<>();
        private List<String> bullets = new ArrayList<>();

        public ProjectDTO() {}
        public ProjectDTO(String name, String description, List<String> technologies, List<String> bullets) {
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
            public ProjectDTO build() { return new ProjectDTO(name, description, technologies, bullets); }
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
}
