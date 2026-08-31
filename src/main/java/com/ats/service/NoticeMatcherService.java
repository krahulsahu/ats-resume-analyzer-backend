package com.ats.service;

/**
 * Service for matching notice period requirements.
 */
public interface NoticeMatcherService {

    /**
     * Score the notice period match.
     *
     * @param resumeNotice notice period from resume
     * @param jobNotice notice period from JD
     * @return notice score out of 5
     */
    int score(String resumeNotice, String jobNotice);
}
