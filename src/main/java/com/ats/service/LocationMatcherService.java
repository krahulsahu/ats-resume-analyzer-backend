package com.ats.service;

/**
 * Service for matching location requirements between resume and JD.
 */
public interface LocationMatcherService {

    /**
     * Score the location match.
     *
     * @param resumeLocation location from resume
     * @param jobLocation location from JD
     * @return location score out of 10
     */
    int score(String resumeLocation, String jobLocation);
}
