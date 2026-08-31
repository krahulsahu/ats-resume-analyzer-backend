package com.ats.service;

import com.ats.dto.ResumeDTO;
import com.ats.dto.SuggestionDTO;

/**
 * Service for generating ATS-friendly resume PDFs.
 */
public interface PdfGeneratorService {

    /**
     * Generate an ATS-friendly resume PDF.
     *
     * @param resume the resume data
     * @param suggestions optional AI suggestions to incorporate
     * @return PDF content as byte array
     */
    byte[] generate(ResumeDTO resume, SuggestionDTO suggestions);
}
