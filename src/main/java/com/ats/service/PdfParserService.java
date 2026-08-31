package com.ats.service;

/**
 * Service for extracting text content from PDF files.
 */
public interface PdfParserService {

    /**
     * Extract raw text content from a PDF byte array.
     *
     * @param pdfBytes the PDF file content
     * @return extracted text
     */
    String extractText(byte[] pdfBytes);
}
