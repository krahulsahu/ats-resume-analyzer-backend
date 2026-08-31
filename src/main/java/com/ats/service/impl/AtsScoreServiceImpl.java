package com.ats.service.impl;

import com.ats.dto.AtsReportDTO;
import com.ats.dto.JobDTO;
import com.ats.dto.ResumeDTO;
import com.ats.service.*;
import com.ats.util.RegexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrator service that combines all matcher results into a final ATS report.
 * All scoring is deterministic — no AI involved.
 */
@Service
public class AtsScoreServiceImpl implements AtsScoreService {

    private static final Logger log = LoggerFactory.getLogger(AtsScoreServiceImpl.class);

    private final SkillMatcherService skillMatcher;
    private final ExperienceMatcherService experienceMatcher;
    private final KeywordAnalyzerService keywordAnalyzer;
    private final EducationMatcherService educationMatcher;
    private final LocationMatcherService locationMatcher;
    private final NoticeMatcherService noticeMatcher;

    public AtsScoreServiceImpl(SkillMatcherService skillMatcher,
                               ExperienceMatcherService experienceMatcher,
                               KeywordAnalyzerService keywordAnalyzer,
                               EducationMatcherService educationMatcher,
                               LocationMatcherService locationMatcher,
                               NoticeMatcherService noticeMatcher) {
        this.skillMatcher = skillMatcher;
        this.experienceMatcher = experienceMatcher;
        this.keywordAnalyzer = keywordAnalyzer;
        this.educationMatcher = educationMatcher;
        this.locationMatcher = locationMatcher;
        this.noticeMatcher = noticeMatcher;
    }

    @Override
    public AtsReportDTO calculate(ResumeDTO resume, JobDTO job) {
        log.info("Calculating ATS score for resume: {}", resume.getName());

        SkillMatcherService.SkillMatchResult skillResult = skillMatcher.match(
                resume.getSkills(),
                job.getRequiredSkills(),
                String.join(" ", resume.getSkills())
        );

        double resumeYears = calculateTotalExperience(resume);
        int experienceScore = experienceMatcher.score(resumeYears, job.getExperienceYears());

        String resumeText = buildResumeText(resume);
        KeywordAnalyzerService.KeywordResult keywordResult = keywordAnalyzer.analyze(resumeText, job.getRawText());

        int educationScore = educationMatcher.score(resume.getEducation(), job.getEducation());
        int locationScore = locationMatcher.score(resume.getLocation(), job.getLocation());
        int noticeScore = noticeMatcher.score(resume.getNoticePeriod(), job.getNoticePeriod());

        int overallScore = skillResult.score() + experienceScore + keywordResult.score()
                + educationScore + locationScore + noticeScore;

        String grade = calculateGrade(overallScore);

        List<String> suggestions = generateSuggestions(skillResult, experienceScore,
                keywordResult.score(), educationScore, locationScore, resume, job);

        AtsReportDTO report = AtsReportDTO.builder()
                .overallScore(overallScore)
                .skillScore(skillResult.score())
                .experienceScore(experienceScore)
                .keywordScore(keywordResult.score())
                .educationScore(educationScore)
                .locationScore(locationScore)
                .noticeScore(noticeScore)
                .matchedSkills(skillResult.matched())
                .missingSkills(skillResult.missing())
                .keywordDensity(keywordResult.keywordFrequency())
                .suggestions(suggestions)
                .grade(grade)
                .resume(resume)
                .job(job)
                .build();

        log.info("ATS Report: overall={}, grade={}, skills={}/40, exp={}/20, kw={}/15, edu={}/10, loc={}/10, notice={}/5",
                overallScore, grade, skillResult.score(), experienceScore, keywordResult.score(),
                educationScore, locationScore, noticeScore);

        return report;
    }

    private double calculateTotalExperience(ResumeDTO resume) {
        if (resume.getExperience() == null || resume.getExperience().isEmpty()) {
            return 0;
        }
        double totalYears = resume.getExperience().stream()
                .mapToDouble(ResumeDTO.ExperienceDTO::getYears)
                .sum();

        if (totalYears <= 0 && resume.getSummary() != null) {
            totalYears = RegexUtil.extractExperienceYears(resume.getSummary());
        }

        return totalYears;
    }

    private String buildResumeText(ResumeDTO resume) {
        StringBuilder sb = new StringBuilder();
        if (resume.getSummary() != null) sb.append(resume.getSummary()).append(" ");
        if (resume.getSkills() != null) sb.append(String.join(" ", resume.getSkills())).append(" ");
        if (resume.getExperience() != null) {
            resume.getExperience().forEach(exp -> {
                if (exp.getTitle() != null) sb.append(exp.getTitle()).append(" ");
                if (exp.getBullets() != null) exp.getBullets().forEach(b -> sb.append(b).append(" "));
            });
        }
        if (resume.getProjects() != null) {
            resume.getProjects().forEach(proj -> {
                if (proj.getName() != null) sb.append(proj.getName()).append(" ");
                if (proj.getDescription() != null) sb.append(proj.getDescription()).append(" ");
                if (proj.getBullets() != null) proj.getBullets().forEach(b -> sb.append(b).append(" "));
            });
        }
        return sb.toString();
    }

    private String calculateGrade(int score) {
        if (score >= 90) return "A+";
        if (score >= 80) return "A";
        if (score >= 70) return "B+";
        if (score >= 60) return "B";
        if (score >= 50) return "C";
        if (score >= 40) return "D";
        return "F";
    }

    private List<String> generateSuggestions(SkillMatcherService.SkillMatchResult skillResult,
                                              int expScore, int kwScore, int eduScore,
                                              int locScore, ResumeDTO resume, JobDTO job) {
        List<String> suggestions = new ArrayList<>();

        if (!skillResult.missing().isEmpty()) {
            suggestions.add("Add missing skills to your resume: " + String.join(", ", skillResult.missing()));
        }
        if (skillResult.score() < 30) {
            suggestions.add("Your skills match is low. Consider gaining experience in the required technologies.");
        }

        if (expScore < 15) {
            suggestions.add("Highlight more relevant work experience and quantify your achievements.");
        }
        if (expScore <= 8) {
            suggestions.add("Your experience level is below the requirement. Emphasize transferable skills and projects.");
        }

        if (kwScore < 10) {
            suggestions.add("Use more industry-relevant keywords throughout your resume.");
        }

        if (eduScore < 10) {
            suggestions.add("Consider adding relevant certifications to strengthen your educational qualifications.");
        }

        if (resume.getSummary() == null || resume.getSummary().length() < 50) {
            suggestions.add("Add a strong professional summary that highlights your key skills and experience.");
        }

        if (resume.getSkills() == null || resume.getSkills().size() < 5) {
            suggestions.add("List more technical skills explicitly in a dedicated Skills section.");
        }

        if (resume.getExperience() != null) {
            for (var exp : resume.getExperience()) {
                if (exp.getBullets() == null || exp.getBullets().size() < 2) {
                    suggestions.add("Add more bullet points to your experience at " +
                            (exp.getCompany() != null ? exp.getCompany() : exp.getTitle()));
                    break;
                }
            }
        }

        return suggestions;
    }
}
