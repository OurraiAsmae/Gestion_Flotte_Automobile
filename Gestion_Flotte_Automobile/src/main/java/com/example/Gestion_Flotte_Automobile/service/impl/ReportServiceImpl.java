package com.example.Gestion_Flotte_Automobile.service.impl;

import com.example.Gestion_Flotte_Automobile.entity.Reservation;
import com.example.Gestion_Flotte_Automobile.repository.ReservationRepository;
import com.example.Gestion_Flotte_Automobile.service.ReportService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReservationRepository reservationRepository;

    public ReportServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public ByteArrayInputStream generateReservationReportPdf() {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            com.itextpdf.text.Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            com.itextpdf.text.Font fontRow = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            Paragraph title = new Paragraph("Rapport des Réservations", fontHeader);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            Stream.of("ID", "Client", "Voiture", "Début", "Fin", "Statut")
                    .forEach(headerTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setPhrase(new Phrase(headerTitle, fontHeader));
                        table.addCell(header);
                    });

            List<Reservation> reservations = reservationRepository.findAll();

            for (Reservation res : reservations) {
                table.addCell(String.valueOf(res.getId()));
                table.addCell(res.getClient() != null ? res.getClient().getNom() : "Inconnu");
                table.addCell(res.getVoiture() != null ? res.getVoiture().getImmatriculation() : "Inconnu");
                table.addCell(res.getDateDebut().toString());
                table.addCell(res.getDateFin().toString());
                table.addCell(res.getStatut().toString());
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public ByteArrayInputStream generateReservationReportExcel() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Réservations");

            Row headerRow = sheet.createRow(0);
            String[] headers = { "ID", "Client", "Voiture", "Début", "Fin", "Statut" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            List<Reservation> reservations = reservationRepository.findAll();
            int rowIdx = 1;
            for (Reservation res : reservations) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(res.getId());
                row.createCell(1).setCellValue(res.getClient() != null ? res.getClient().getNom() : "");
                row.createCell(2).setCellValue(res.getVoiture() != null ? res.getVoiture().getImmatriculation() : "");
                row.createCell(3).setCellValue(res.getDateDebut().toString());
                row.createCell(4).setCellValue(res.getDateFin().toString());
                row.createCell(5).setCellValue(res.getStatut().toString());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du fichier Excel", e);
        }
    }
}
