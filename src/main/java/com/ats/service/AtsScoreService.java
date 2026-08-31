package com.ats.service;

import com.ats.dto.AtsReportDTO;
import com.ats.dto.ResumeDTO;
import com.ats.dto.JobDTO;

/**
 * Orchestrator service for computing the full ATS score.
 * Uses deterministic weighted scoring — no AI involved.
 */
public interface AtsScoreService {

    /**
     * Calculate the complete ATS report.
     *
     * @param resume parsed resume DTO
     * @param job parsed job DTO
     * @return complete ATS report with all category scores
     */
    AtsReportDTO calculate(ResumeDTO resume, JobDTO job);
}
