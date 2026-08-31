package com.ats.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for the full ATS analysis report.
 */
public class AtsReportDTO {
    private int overallScore;
    private int skillScore;
    private int experienceScore;
    private int keywordScore;
    private int educationScore;
    private int locationScore;
    private int noticeScore;

    private int skillMaxScore = 40;
    private int experienceMaxScore = 20;
    private int keywordMaxScore = 15;
    private int educationMaxScore = 10;
    private int locationMaxScore = 10;
    private int noticeMaxScore = 5;

    private List<String> matchedSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();
    private Map<String, Integer> keywordDensity = new HashMap<>();
    private List<String> suggestions = new ArrayList<>();
    private String grade;
    private ResumeDTO resume;
    private JobDTO job;

    public AtsReportDTO() {}

    public AtsReportDTO(int overallScore, int skillScore, int experienceScore,
                        int keywordScore, int educationScore, int locationScore,
                        int noticeScore, int skillMaxScore, int experienceMaxScore,
                        int keywordMaxScore, int educationMaxScore, int locationMaxScore,
                        int noticeMaxScore, List<String> matchedSkills,
                        List<String> missingSkills, Map<String, Integer> keywordDensity,
                        List<String> suggestions, String grade, ResumeDTO resume, JobDTO job) {
        this.overallScore = overallScore;
        this.skillScore = skillScore;
        this.experienceScore = experienceScore;
        this.keywordScore = keywordScore;
        this.educationScore = educationScore;
        this.locationScore = locationScore;
        this.noticeScore = noticeScore;
        this.skillMaxScore = skillMaxScore;
        this.experienceMaxScore = experienceMaxScore;
        this.keywordMaxScore = keywordMaxScore;
        this.educationMaxScore = educationMaxScore;
        this.locationMaxScore = locationMaxScore;
        this.noticeMaxScore = noticeMaxScore;
        this.matchedSkills = matchedSkills != null ? matchedSkills : new ArrayList<>();
        this.missingSkills = missingSkills != null ? missingSkills : new ArrayList<>();
        this.keywordDensity = keywordDensity != null ? keywordDensity : new HashMap<>();
        this.suggestions = suggestions != null ? suggestions : new ArrayList<>();
        this.grade = grade;
        this.resume = resume;
        this.job = job;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private int overallScore;
        private int skillScore;
        private int experienceScore;
        private int keywordScore;
        private int educationScore;
        private int locationScore;
        private int noticeScore;
        private int skillMaxScore = 40;
        private int experienceMaxScore = 20;
        private int keywordMaxScore = 15;
        private int educationMaxScore = 10;
        private int locationMaxScore = 10;
        private int noticeMaxScore = 5;
        private List<String> matchedSkills = new ArrayList<>();
        private List<String> missingSkills = new ArrayList<>();
        private Map<String, Integer> keywordDensity = new HashMap<>();
        private List<String> suggestions = new ArrayList<>();
        private String grade;
        private ResumeDTO resume;
        private JobDTO job;

        public Builder overallScore(int overallScore) { this.overallScore = overallScore; return this; }
        public Builder skillScore(int skillScore) { this.skillScore = skillScore; return this; }
        public Builder experienceScore(int experienceScore) { this.experienceScore = experienceScore; return this; }
        public Builder keywordScore(int keywordScore) { this.keywordScore = keywordScore; return this; }
        public Builder educationScore(int educationScore) { this.educationScore = educationScore; return this; }
        public Builder locationScore(int locationScore) { this.locationScore = locationScore; return this; }
        public Builder noticeScore(int noticeScore) { this.noticeScore = noticeScore; return this; }
        public Builder skillMaxScore(int skillMaxScore) { this.skillMaxScore = skillMaxScore; return this; }
        public Builder experienceMaxScore(int experienceMaxScore) { this.experienceMaxScore = experienceMaxScore; return this; }
        public Builder keywordMaxScore(int keywordMaxScore) { this.keywordMaxScore = keywordMaxScore; return this; }
        public Builder educationMaxScore(int educationMaxScore) { this.educationMaxScore = educationMaxScore; return this; }
        public Builder locationMaxScore(int locationMaxScore) { this.locationMaxScore = locationMaxScore; return this; }
        public Builder noticeMaxScore(int noticeMaxScore) { this.noticeMaxScore = noticeMaxScore; return this; }
        public Builder matchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; return this; }
        public Builder missingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; return this; }
        public Builder keywordDensity(Map<String, Integer> keywordDensity) { this.keywordDensity = keywordDensity; return this; }
        public Builder suggestions(List<String> suggestions) { this.suggestions = suggestions; return this; }
        public Builder grade(String grade) { this.grade = grade; return this; }
        public Builder resume(ResumeDTO resume) { this.resume = resume; return this; }
        public Builder job(JobDTO job) { this.job = job; return this; }

        public AtsReportDTO build() {
            return new AtsReportDTO(overallScore, skillScore, experienceScore, keywordScore, educationScore, locationScore, noticeScore,
                    skillMaxScore, experienceMaxScore, keywordMaxScore, educationMaxScore, locationMaxScore, noticeMaxScore,
                    matchedSkills, missingSkills, keywordDensity, suggestions, grade, resume, job);
        }
    }

    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
    public int getSkillScore() { return skillScore; }
    public void setSkillScore(int skillScore) { this.skillScore = skillScore; }
    public int getExperienceScore() { return experienceScore; }
    public void setExperienceScore(int experienceScore) { this.experienceScore = experienceScore; }
    public int getKeywordScore() { return keywordScore; }
    public void setKeywordScore(int keywordScore) { this.keywordScore = keywordScore; }
    public int getEducationScore() { return educationScore; }
    public void setEducationScore(int educationScore) { this.educationScore = educationScore; }
    public int getLocationScore() { return locationScore; }
    public void setLocationScore(int locationScore) { this.locationScore = locationScore; }
    public int getNoticeScore() { return noticeScore; }
    public void setNoticeScore(int noticeScore) { this.noticeScore = noticeScore; }
    public int getSkillMaxScore() { return skillMaxScore; }
    public void setSkillMaxScore(int skillMaxScore) { this.skillMaxScore = skillMaxScore; }
    public int getExperienceMaxScore() { return experienceMaxScore; }
    public void setExperienceMaxScore(int experienceMaxScore) { this.experienceMaxScore = experienceMaxScore; }
    public int getKeywordMaxScore() { return keywordMaxScore; }
    public void setKeywordMaxScore(int keywordMaxScore) { this.keywordMaxScore = keywordMaxScore; }
    public int getEducationMaxScore() { return educationMaxScore; }
    public void setEducationMaxScore(int educationMaxScore) { this.educationMaxScore = educationMaxScore; }
    public int getLocationMaxScore() { return locationMaxScore; }
    public void setLocationMaxScore(int locationMaxScore) { this.locationMaxScore = locationMaxScore; }
    public int getNoticeMaxScore() { return noticeMaxScore; }
    public void setNoticeMaxScore(int noticeMaxScore) { this.noticeMaxScore = noticeMaxScore; }
    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }
    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }
    public Map<String, Integer> getKeywordDensity() { return keywordDensity; }
    public void setKeywordDensity(Map<String, Integer> keywordDensity) { this.keywordDensity = keywordDensity; }
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public ResumeDTO getResume() { return resume; }
    public void setResume(ResumeDTO resume) { this.resume = resume; }
    public JobDTO getJob() { return job; }
    public void setJob(JobDTO job) { this.job = job; }
}
