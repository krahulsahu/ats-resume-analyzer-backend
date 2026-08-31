package com.ats.service;

import com.ats.dto.JobDTO;

/**
 * Service for parsing raw job description text into a structured JobDTO.
 */
public interface JobParserService {

    /**
     * Parse raw JD text into a structured DTO.
     *
     * @param rawText the raw job description text
     * @return structured job DTO
     */
    JobDTO parse(String rawText);
}
