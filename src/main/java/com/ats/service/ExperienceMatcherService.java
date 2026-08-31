package com.ats.service;

/**
 * Service for matching experience requirements between resume and JD.
 */
public interface ExperienceMatcherService {

    /**
     * Score the experience match between resume and JD.
     *
     * @param resumeYears years of experience from resume
     * @param requiredYears years of experience required by JD
     * @return experience score out of 20
     */
    int score(double resumeYears, double requiredYears);
}
