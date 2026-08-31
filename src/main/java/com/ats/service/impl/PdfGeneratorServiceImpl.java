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
 * Generates ATS-friendly resume PDFs using OpenPDF.
 * Single column, no tables, no icons, black text, professional spacing.
 */
@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(PdfGeneratorServiceImpl.class);

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLACK);
    private static final Font HEADING_FONT = new Font(Font.HELVETICA, 13, Font.BOLD, Color.BLACK);
    private static final Font SUBHEADING_FONT = new Font(Font.HELVETICA, 11, Font.BOLD, Color.BLACK);
    private static final Font BODY_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
    private static final Font BODY_ITALIC = new Font(Font.HELVETICA, 10, Font.ITALIC, new Color(80, 80, 80));
    private static final Font CONTACT_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(60, 60, 60));

    @Override
    public byte[] generate(ResumeDTO resume, SuggestionDTO suggestions) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 50, 50, 40, 40);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            if (resume.getName() != null) {
                Paragraph name = new Paragraph(resume.getName(), TITLE_FONT);
                name.setAlignment(Element.ALIGN_CENTER);
                doc.add(name);
            }

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
                Paragraph contactPara = new Paragraph(contact.toString(), CONTACT_FONT);
                contactPara.setAlignment(Element.ALIGN_CENTER);
                contactPara.setSpacingAfter(8);
                doc.add(contactPara);
            }

            addSeparator(doc);

            String summary = (suggestions != null && suggestions.getImprovedSummary() != null
                    && !suggestions.getImprovedSummary().startsWith("AI"))
                    ? suggestions.getImprovedSummary()
                    : resume.getSummary();
            if (summary != null && !summary.isBlank()) {
                addSectionHeading(doc, "PROFESSIONAL SUMMARY");
                Paragraph summaryPara = new Paragraph(summary, BODY_FONT);
                summaryPara.setSpacingAfter(8);
                doc.add(summaryPara);
            }

            if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
                addSectionHeading(doc, "TECHNICAL SKILLS");

                if (suggestions != null && suggestions.getCategorizedSkills() != null
                        && !suggestions.getCategorizedSkills().isEmpty()) {
                    for (Map.Entry<String, List<String>> entry : suggestions.getCategorizedSkills().entrySet()) {
                        Paragraph skillLine = new Paragraph();
                        skillLine.add(new Chunk(entry.getKey() + ": ", SUBHEADING_FONT));
                        skillLine.add(new Chunk(String.join(", ", entry.getValue()), BODY_FONT));
                        skillLine.setSpacingAfter(3);
                        doc.add(skillLine);
                    }
                } else {
                    Paragraph skillsPara = new Paragraph(String.join(", ", resume.getSkills()), BODY_FONT);
                    skillsPara.setSpacingAfter(8);
                    doc.add(skillsPara);
                }
                doc.add(new Paragraph(" "));
            }

            if (resume.getExperience() != null && !resume.getExperience().isEmpty()) {
                addSectionHeading(doc, "PROFESSIONAL EXPERIENCE");

                for (int i = 0; i < resume.getExperience().size(); i++) {
                    ResumeDTO.ExperienceDTO exp = resume.getExperience().get(i);

                    Paragraph titleLine = new Paragraph();
                    titleLine.add(new Chunk(exp.getTitle() != null ? exp.getTitle() : "", SUBHEADING_FONT));
                    if (exp.getCompany() != null && !exp.getCompany().isBlank()) {
                        titleLine.add(new Chunk(" — " + exp.getCompany(), BODY_ITALIC));
                    }
                    doc.add(titleLine);

                    if (exp.getDuration() != null && !exp.getDuration().isBlank()) {
                        Paragraph duration = new Paragraph(exp.getDuration(), BODY_ITALIC);
                        duration.setSpacingAfter(4);
                        doc.add(duration);
                    }

                    List<String> bullets = getBullets(exp, suggestions, i);
                    for (String bullet : bullets) {
                        Paragraph bulletPara = new Paragraph("• " + bullet, BODY_FONT);
                        bulletPara.setIndentationLeft(15);
                        bulletPara.setSpacingAfter(2);
                        doc.add(bulletPara);
                    }
                    doc.add(new Paragraph(" "));
                }
            }

            if (resume.getProjects() != null && !resume.getProjects().isEmpty()) {
                addSectionHeading(doc, "PROJECTS");

                for (int i = 0; i < resume.getProjects().size(); i++) {
                    ResumeDTO.ProjectDTO proj = resume.getProjects().get(i);

                    Paragraph projName = new Paragraph(proj.getName() != null ? proj.getName() : "", SUBHEADING_FONT);
                    doc.add(projName);

                    if (proj.getDescription() != null && !proj.getDescription().isBlank()) {
                        Paragraph desc = new Paragraph(proj.getDescription(), BODY_FONT);
                        desc.setSpacingAfter(3);
                        doc.add(desc);
                    }

                    List<String> bullets = getProjectBullets(proj, suggestions, i);
                    for (String bullet : bullets) {
                        Paragraph bulletPara = new Paragraph("• " + bullet, BODY_FONT);
                        bulletPara.setIndentationLeft(15);
                        bulletPara.setSpacingAfter(2);
                        doc.add(bulletPara);
                    }

                    if (proj.getTechnologies() != null && !proj.getTechnologies().isEmpty()) {
                        Paragraph techLine = new Paragraph();
                        techLine.add(new Chunk("Technologies: ", SUBHEADING_FONT));
                        techLine.add(new Chunk(String.join(", ", proj.getTechnologies()), BODY_FONT));
                        techLine.setSpacingAfter(5);
                        doc.add(techLine);
                    }
                    doc.add(new Paragraph(" "));
                }
            }

            if (resume.getEducation() != null && !resume.getEducation().isBlank()) {
                addSectionHeading(doc, "EDUCATION");
                Paragraph eduPara = new Paragraph(resume.getEducation(), BODY_FONT);
                eduPara.setSpacingAfter(8);
                doc.add(eduPara);
            }

            if (resume.getCertifications() != null && !resume.getCertifications().isEmpty()) {
                addSectionHeading(doc, "CERTIFICATIONS");
                for (String cert : resume.getCertifications()) {
                    Paragraph certPara = new Paragraph("• " + cert, BODY_FONT);
                    certPara.setIndentationLeft(15);
                    certPara.setSpacingAfter(2);
                    doc.add(certPara);
                }
            }

            doc.close();
            byte[] pdfBytes = baos.toByteArray();
            log.info("Generated ATS-friendly PDF: {} bytes", pdfBytes.length);
            return pdfBytes;

        } catch (Exception e) {
            log.error("Failed to generate PDF", e);
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    private void addSectionHeading(Document doc, String title) throws DocumentException {
        addSeparator(doc);
        Paragraph heading = new Paragraph(title, HEADING_FONT);
        heading.setSpacingBefore(8);
        heading.setSpacingAfter(6);
        doc.add(heading);
    }

    private void addSeparator(Document doc) throws DocumentException {
        LineSeparator line = new LineSeparator();
        line.setLineColor(new Color(180, 180, 180));
        line.setLineWidth(0.5f);
        doc.add(new Chunk(line));
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
