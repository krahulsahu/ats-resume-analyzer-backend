package com.ats.service;

/**
 * Service for matching education qualifications between resume and JD.
 */
public interface EducationMatcherService {

    /**
     * Score the education match.
     *
     * @param resumeEducation education from resume
     * @param requiredEducation education required by JD
     * @return education score out of 10
     */
    int score(String resumeEducation, String requiredEducation);
}
