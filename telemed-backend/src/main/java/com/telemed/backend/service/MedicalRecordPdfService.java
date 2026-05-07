package com.telemed.backend.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.telemed.backend.entity.MedicalRecord;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Locale;

@Service
public class MedicalRecordPdfService {

    public byte[] generateSingleMedicalRecordPdf(MedicalRecord record) {
        try {
            DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy", Locale.ENGLISH);
            DateTimeFormatter dobFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
            String visitDate = "";
            if (record.getCreatedAt() != null) {
                visitDate = record.getCreatedAt()
                        .atZone(ZoneId.systemDefault())
                        .format(customFormatter);
            }

            Document document = new Document(PageSize.A4, 36, 36, 36, 60);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new FooterPageEvent());
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

            Paragraph header = new Paragraph("TELEMED HUMLA - MEDICAL RECORD", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            document.add(new Paragraph(" "));
            document.add(new LineSeparator());
            document.add(new Paragraph(" "));

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidths(new float[]{1.5f, 3f});
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(10f);

            addTableCell(infoTable, "Patient Name:", record.getPatient().getFirstName() + " " +
                    record.getPatient().getLastName(), labelFont, textFont);
            addTableCell(infoTable, "Age:", String.valueOf(record.getPatient().getAge()), labelFont, textFont);
            addTableCell(infoTable, "Date of Birth:", record.getPatient().getDateOfBirth().format(dobFormatter), labelFont, textFont);
            addTableCell(infoTable, "Visit date:", visitDate, labelFont, textFont);
            addTableCell(infoTable, "Attending Provider:", record.getCreator().getFullName(), labelFont, textFont);

            document.add(infoTable);
            document.add(new LineSeparator());
            document.add(new Paragraph(" "));

            document.add(new Paragraph("CLINICAL EVALUATION", subTitleFont));
            document.add(new Paragraph(" "));

            for (Map.Entry<String, String> entry : record.getPatientHistory().entrySet()) {
                Paragraph keyPara = new Paragraph(entry.getKey().toUpperCase(), labelFont);
                keyPara.setSpacingBefore(10f);
                document.add(keyPara);

                Paragraph valuePara = new Paragraph(entry.getValue(), textFont);
                valuePara.setIndentationLeft(20f);
                document.add(valuePara);
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private void addTableCell(PdfPTable table, String label, String value,
                              Font labelFont, Font textFont) {

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingBottom(5f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(
                new Phrase(value != null ? value : "", textFont)
        );
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingBottom(5f);
        table.addCell(valueCell);
    }
}