package com.ats.service;

import com.ats.dto.ResumeDTO;
import com.ats.dto.SuggestionDTO;

/**
 * Service for generating ATS-friendly resume PDFs supporting multiple templates.
 */
public interface PdfGeneratorService {

    /**
     * Generate an ATS-friendly resume PDF using default template.
     */
    byte[] generate(ResumeDTO resume, SuggestionDTO suggestions);

    /**
     * Generate an ATS-friendly resume PDF using a specific template style:
     * - "modern": Modern Workday standard
     * - "latex": Tech Minimalist (matching single-page LaTeX format)
     * - "ivy": Ivy League Classic Serif
     * - "executive": Executive Bold
     * - "compact": Compact High-Density
     */
    byte[] generate(ResumeDTO resume, SuggestionDTO suggestions, String templateId);
}
