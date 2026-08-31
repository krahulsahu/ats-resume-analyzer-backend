package com.ats.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing a work experience entry.
 */
public class Experience {
    private String title;
    private String company;
    private String duration;
    private double years;
    private List<String> bullets = new ArrayList<>();

    public Experience() {}

    public Experience(String title, String company, String duration, double years, List<String> bullets) {
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
        public Experience build() { return new Experience(title, company, duration, years, bullets); }
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
