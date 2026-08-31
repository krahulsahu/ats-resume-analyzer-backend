package com.ats.service.impl;

import com.ats.service.PdfParserService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * PDF text extraction implementation using Apache PDFBox.
 */
@Service
public class PdfParserServiceImpl implements PdfParserService {

    private static final Logger log = LoggerFactory.getLogger(PdfParserServiceImpl.class);

    @Override
    public String extractText(byte[] pdfBytes) {
        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new IllegalArgumentException("PDF content is empty");
        }

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);
            log.debug("Extracted {} characters from PDF ({} pages)", text.length(), document.getNumberOfPages());
            return text;
        } catch (IOException e) {
            log.error("Failed to parse PDF document", e);
            throw new RuntimeException("Failed to parse PDF: " + e.getMessage(), e);
        }
    }
}
