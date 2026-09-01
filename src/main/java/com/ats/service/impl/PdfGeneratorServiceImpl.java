package com.ats.service.impl;

import com.ats.dto.ResumeDTO;
import com.ats.dto.SuggestionDTO;
import com.ats.service.PdfGeneratorService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * Generates ATS-friendly resume PDFs supporting 5 specialized ATS templates using OpenPDF.
 * Single column, no tables, no icons, black text, professional spacing.
 */
@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(PdfGeneratorServiceImpl.class);

    @Override
    public byte[] generate(ResumeDTO resume, SuggestionDTO suggestions) {
        return generate(resume, suggestions, "modern");
    }

    @Override
    public byte[] generate(ResumeDTO resume, SuggestionDTO suggestions, String templateId) {
        String tpl = (templateId != null && !templateId.isBlank()) ? templateId.toLowerCase().trim() : "modern";

        return switch (tpl) {
            case "latex", "minimalist" -> generateLatexStyle(resume, suggestions);
            case "ivy", "academic" -> generateIvyStyle(resume, suggestions);
            case "executive" -> generateExecutiveStyle(resume, suggestions);
            case "compact" -> generateCompactStyle(resume, suggestions);
            default -> generateModernStyle(resume, suggestions);
        };
    }

    // ==========================================
    // 1. MODERN WORKDAY STYLE (Default)
    // ==========================================
    private byte[] generateModernStyle(ResumeDTO resume, SuggestionDTO suggestions) {
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLACK);
        Font headingFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(26, 86, 160)); // Accent Blue
        Font subHeadingFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);
        Font bodyFont = new Font(Font.HELVETICA, 9.5f, Font.NORMAL, new Color(30, 30, 30));
        Font italicFont = new Font(Font.HELVETICA, 9.5f, Font.ITALIC, new Color(70, 70, 70));
        Font contactFont = new Font(Font.HELVETICA, 9.5f, Font.NORMAL, new Color(60, 60, 60));

        return buildStandardPdf(resume, suggestions, titleFont, headingFont, subHeadingFont, bodyFont, italicFont, contactFont, 40, 40, 35, 35, true);
    }

    // ==========================================
    // 2. TECH MINIMALIST (Matching User's LaTeX)
    // ==========================================
    private byte[] generateLatexStyle(ResumeDTO resume, SuggestionDTO suggestions) {
        Font titleFont = new Font(Font.HELVETICA, 17, Font.BOLD, Color.BLACK);
        Font headingFont = new Font(Font.HELVETICA, 11.5f, Font.BOLD, new Color(26, 86, 160)); // LaTeX accent #1A56A0
        Font subHeadingFont = new Font(Font.HELVETICA, 9.5f, Font.BOLD, Color.BLACK);
        Font bodyFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.BLACK);
        Font italicFont = new Font(Font.HELVETICA, 9, Font.ITALIC, new Color(50, 50, 50));
        Font contactFont = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(50, 50, 50));

        return buildStandardPdf(resume, suggestions, titleFont, headingFont, subHeadingFont, bodyFont, italicFont, contactFont, 28, 28, 20, 20, true);
    }

    // ==========================================
    // 3. IVY LEAGUE CLASSIC (Serif / Times)
    // ==========================================
    private byte[] generateIvyStyle(ResumeDTO resume, SuggestionDTO suggestions) {
        Font titleFont = new Font(Font.TIMES_ROMAN, 19, Font.BOLD, Color.BLACK);
        Font headingFont = new Font(Font.TIMES_ROMAN, 12, Font.BOLD, Color.BLACK);
        Font subHeadingFont = new Font(Font.TIMES_ROMAN, 10.5f, Font.BOLD, Color.BLACK);
        Font bodyFont = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL, Color.BLACK);
        Font italicFont = new Font(Font.TIMES_ROMAN, 10, Font.ITALIC, new Color(50, 50, 50));
        Font contactFont = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL, new Color(60, 60, 60));

        return buildStandardPdf(resume, suggestions, titleFont, headingFont, subHeadingFont, bodyFont, italicFont, contactFont, 45, 45, 40, 40, false);
    }

    // ==========================================
    // 4. EXECUTIVE BOLD (Slate Dividers)
    // ==========================================
    private byte[] generateExecutiveStyle(ResumeDTO resume, SuggestionDTO suggestions) {
        Font titleFont = new Font(Font.HELVETICA, 19, Font.BOLD, new Color(15, 23, 42)); // Slate 900
        Font headingFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(30, 41, 59));
        Font subHeadingFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);
        Font bodyFont = new Font(Font.HELVETICA, 9.5f, Font.NORMAL, new Color(30, 41, 59));
        Font italicFont = new Font(Font.HELVETICA, 9.5f, Font.ITALIC, new Color(71, 85, 105));
        Font contactFont = new Font(Font.HELVETICA, 9.5f, Font.NORMAL, new Color(71, 85, 105));

        return buildStandardPdf(resume, suggestions, titleFont, headingFont, subHeadingFont, bodyFont, italicFont, contactFont, 36, 36, 30, 30, true);
    }

    // ==========================================
    // 5. COMPACT HIGH-DENSITY (Maximum Content)
    // ==========================================
    private byte[] generateCompactStyle(ResumeDTO resume, SuggestionDTO suggestions) {
        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, Color.BLACK);
        Font headingFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.BLACK);
        Font subHeadingFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.BLACK);
        Font bodyFont = new Font(Font.HELVETICA, 8.5f, Font.NORMAL, Color.BLACK);
        Font italicFont = new Font(Font.HELVETICA, 8.5f, Font.ITALIC, new Color(60, 60, 60));
        Font contactFont = new Font(Font.HELVETICA, 8.5f, Font.NORMAL, new Color(60, 60, 60));

        return buildStandardPdf(resume, suggestions, titleFont, headingFont, subHeadingFont, bodyFont, italicFont, contactFont, 24, 24, 18, 18, true);
    }

    // ==========================================
    // Core PDF Generator Engine
    // ==========================================
    private byte[] buildStandardPdf(ResumeDTO resume, SuggestionDTO suggestions,
                                    Font titleFont, Font headingFont, Font subHeadingFont,
                                    Font bodyFont, Font italicFont, Font contactFont,
                                    float marginLeft, float marginRight, float marginTop, float marginBottom,
                                    boolean hasRule) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, marginLeft, marginRight, marginTop, marginBottom);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // Header Name
            if (resume.getName() != null && !resume.getName().isBlank()) {
                Paragraph name = new Paragraph(resume.getName(), titleFont);
                name.setAlignment(Element.ALIGN_CENTER);
                name.setSpacingAfter(2);
                doc.add(name);
            }

            // Contact Bar
            StringBuilder contact = new StringBuilder();
            if (resume.getEmail() != null) contact.append(resume.getEmail());
            if (resume.getPhone() != null) {
                if (!contact.isEmpty()) contact.append(" | ");
                contact.append(resume.getPhone());
            }
            if (resume.getLocation() != null) {
                if (!contact.isEmpty()) contact.append(" | ");
                contact.append(resume.getLocation());
            }
            if (!contact.isEmpty()) {
                Paragraph contactPara = new Paragraph(contact.toString(), contactFont);
                contactPara.setAlignment(Element.ALIGN_CENTER);
                contactPara.setSpacingAfter(6);
                doc.add(contactPara);
            }

            // Professional Summary
            String summary = (suggestions != null && suggestions.getImprovedSummary() != null
                    && !suggestions.getImprovedSummary().startsWith("AI"))
                    ? suggestions.getImprovedSummary()
                    : resume.getSummary();
            if (summary != null && !summary.isBlank()) {
                addSectionHeading(doc, "PROFESSIONAL SUMMARY", headingFont, hasRule);
                Paragraph summaryPara = new Paragraph(summary, bodyFont);
                summaryPara.setSpacingAfter(6);
                doc.add(summaryPara);
            }

            // Technical Skills
            if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
                addSectionHeading(doc, "TECHNICAL SKILLS", headingFont, hasRule);
                if (suggestions != null && suggestions.getCategorizedSkills() != null
                        && !suggestions.getCategorizedSkills().isEmpty()) {
                    for (Map.Entry<String, List<String>> entry : suggestions.getCategorizedSkills().entrySet()) {
                        Paragraph skillLine = new Paragraph();
                        skillLine.add(new Chunk(entry.getKey() + ": ", subHeadingFont));
                        skillLine.add(new Chunk(String.join(", ", entry.getValue()), bodyFont));
                        skillLine.setSpacingAfter(2);
                        doc.add(skillLine);
                    }
                } else {
                    Paragraph skillsPara = new Paragraph(String.join(", ", resume.getSkills()), bodyFont);
                    skillsPara.setSpacingAfter(6);
                    doc.add(skillsPara);
                }
            }

            // Work Experience
            if (resume.getExperience() != null && !resume.getExperience().isEmpty()) {
                addSectionHeading(doc, "WORK EXPERIENCE", headingFont, hasRule);

                for (int i = 0; i < resume.getExperience().size(); i++) {
                    ResumeDTO.ExperienceDTO exp = resume.getExperience().get(i);

                    Paragraph titleLine = new Paragraph();
                    titleLine.add(new Chunk(exp.getTitle() != null ? exp.getTitle() : "", subHeadingFont));
                    if (exp.getCompany() != null && !exp.getCompany().isBlank()) {
                        titleLine.add(new Chunk(" — " + exp.getCompany(), italicFont));
                    }
                    if (exp.getDuration() != null && !exp.getDuration().isBlank()) {
                        titleLine.add(new Chunk(" (" + exp.getDuration() + ")", italicFont));
                    }
                    titleLine.setSpacingAfter(2);
                    doc.add(titleLine);

                    List<String> bullets = getBullets(exp, suggestions, i);
                    for (String bullet : bullets) {
                        Paragraph bulletPara = new Paragraph("• " + bullet, bodyFont);
                        bulletPara.setIndentationLeft(12);
                        bulletPara.setSpacingAfter(1.5f);
                        doc.add(bulletPara);
                    }
                }
            }

            // Projects
            if (resume.getProjects() != null && !resume.getProjects().isEmpty()) {
                addSectionHeading(doc, "KEY PROJECTS", headingFont, hasRule);

                for (int i = 0; i < resume.getProjects().size(); i++) {
                    ResumeDTO.ProjectDTO proj = resume.getProjects().get(i);

                    Paragraph projName = new Paragraph();
                    projName.add(new Chunk(proj.getName() != null ? proj.getName() : "", subHeadingFont));
                    if (proj.getTechnologies() != null && !proj.getTechnologies().isEmpty()) {
                        projName.add(new Chunk(" (" + String.join(", ", proj.getTechnologies()) + ")", italicFont));
                    }
                    projName.setSpacingAfter(2);
                    doc.add(projName);

                    if (proj.getDescription() != null && !proj.getDescription().isBlank()) {
                        Paragraph desc = new Paragraph(proj.getDescription(), bodyFont);
                        desc.setSpacingAfter(2);
                        doc.add(desc);
                    }

                    List<String> bullets = getProjectBullets(proj, suggestions, i);
                    for (String bullet : bullets) {
                        Paragraph bulletPara = new Paragraph("• " + bullet, bodyFont);
                        bulletPara.setIndentationLeft(12);
                        bulletPara.setSpacingAfter(1.5f);
                        doc.add(bulletPara);
                    }
                }
            }

            // Education
            if (resume.getEducation() != null && !resume.getEducation().isBlank()) {
                addSectionHeading(doc, "EDUCATION", headingFont, hasRule);
                Paragraph eduPara = new Paragraph(resume.getEducation(), bodyFont);
                eduPara.setSpacingAfter(6);
                doc.add(eduPara);
            }

            // Certifications
            if (resume.getCertifications() != null && !resume.getCertifications().isEmpty()) {
                addSectionHeading(doc, "CERTIFICATIONS", headingFont, hasRule);
                for (String cert : resume.getCertifications()) {
                    Paragraph certPara = new Paragraph("• " + cert, bodyFont);
                    certPara.setIndentationLeft(12);
                    certPara.setSpacingAfter(1.5f);
                    doc.add(certPara);
                }
            }

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF template", e);
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    private void addSectionHeading(Document doc, String title, Font font, boolean hasRule) throws DocumentException {
        Paragraph heading = new Paragraph(title, font);
        heading.setSpacingBefore(6);
        heading.setSpacingAfter(3);
        doc.add(heading);

        if (hasRule) {
            LineSeparator line = new LineSeparator();
            line.setLineColor(new Color(200, 200, 200));
            line.setLineWidth(0.5f);
            doc.add(new Chunk(line));
        }
    }

    private List<String> getBullets(ResumeDTO.ExperienceDTO exp, SuggestionDTO suggestions, int index) {
        if (suggestions != null && suggestions.getImprovedExperience() != null
                && index < suggestions.getImprovedExperience().size()) {
            List<String> improved = suggestions.getImprovedExperience().get(index).getImprovedBullets();
            if (improved != null && !improved.isEmpty()) return improved;
        }
        return exp.getBullets() != null ? exp.getBullets() : List.of();
    }

    private List<String> getProjectBullets(ResumeDTO.ProjectDTO proj, SuggestionDTO suggestions, int index) {
        if (suggestions != null && suggestions.getImprovedProjects() != null
                && index < suggestions.getImprovedProjects().size()) {
            List<String> improved = suggestions.getImprovedProjects().get(index).getImprovedBullets();
            if (improved != null && !improved.isEmpty()) return improved;
        }
        return proj.getBullets() != null ? proj.getBullets() : List.of();
    }
}
