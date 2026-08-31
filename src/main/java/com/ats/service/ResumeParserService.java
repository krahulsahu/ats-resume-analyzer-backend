package com.ats.service;

import com.ats.dto.ResumeDTO;

/**
 * Service for parsing raw resume text into a structured ResumeDTO.
 */
public interface ResumeParserService {

    /**
     * Parse raw resume text into a structured DTO.
     *
     * @param rawText the raw text extracted from resume
     * @return structured resume DTO
     */
    ResumeDTO parse(String rawText);
}
