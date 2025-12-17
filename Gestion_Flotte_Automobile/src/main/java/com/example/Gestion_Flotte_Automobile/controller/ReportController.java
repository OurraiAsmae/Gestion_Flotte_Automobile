package com.example.Gestion_Flotte_Automobile.controller;

import com.example.Gestion_Flotte_Automobile.service.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

@Controller
@RequestMapping("/reports")
@AllArgsConstructor
public class ReportController {

    private final ReservationService reservationService;
    private final VoitureService voitureService;
    private final EntretienService entretienService;
    private final DashboardService dashboardService;

    @GetMapping("/export")
    public void exportReport(HttpServletResponse response,
            @RequestParam("type") String type,
            @RequestParam("format") String format,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr)
            throws IOException, DocumentException {

        LocalDate startDate = (startDateStr != null && !startDateStr.isEmpty()) ? LocalDate.parse(startDateStr) : null;
        LocalDate endDate = (endDateStr != null && !endDateStr.isEmpty()) ? LocalDate.parse(endDateStr) : null;

        if ("PDF".equalsIgnoreCase(format)) {
            exportPdf(response, type, startDate, endDate);
        } else {
            exportCsv(response, type, startDate, endDate);
        }
    }

    private void exportCsv(HttpServletResponse response, String type, LocalDate startDate, LocalDate endDate)
            throws IOException {
        response.setContentType("text/csv");
        String filename = "report_" + type.toLowerCase() + "_" + LocalDate.now() + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (PrintWriter writer = response.getWriter()) {
            switch (type) {
                case "FINANCIAL":
                    writer.println("Indicateur,Montant (DH)");
                    writer.println("Recettes Totales Payees," + dashboardService.sumTotalPaiements(startDate, endDate));
                    writer.println(
                            "Depenses Totales Payees," + dashboardService.sumCoutEntretiensPayes(startDate, endDate));
                    writer.println("Benefice Net," + (dashboardService.sumTotalPaiements(startDate, endDate)
                            - dashboardService.sumCoutEntretiensPayes(startDate, endDate)));
                    if (startDate != null && endDate != null) {
                        writer.println("Periode," + startDate + " au " + endDate);
                    }
                    break;
                case "RESERVATIONS":
                    writer.println("ID,Client,Voiture,Date Debut,Date Fin,Statut");
                    java.util.List<com.example.Gestion_Flotte_Automobile.entity.Reservation> reservations;
                    if (startDate != null && endDate != null) {
                        reservations = reservationService.findByDateDebutBetween(startDate.atStartOfDay(),
                                endDate.atTime(23, 59, 59));
                    } else {
                        reservations = reservationService.findAll();
                    }

                    reservations.forEach(r -> {
                        writer.printf("%d,%s,%s,%s,%s,%s%n",
                                r.getId(),
                                r.getClient().getNom() + " " + r.getClient().getPrenom(),
                                r.getVoiture().getImmatriculation(),
                                r.getDateDebut(),
                                r.getDateFin(),
                                r.getStatut());
                    });
                    break;
                case "VOITURES":
                    writer.println("ID,Marque,Modele,Immatriculation,Statut,Prix/Jour");
                    voitureService.findAll().forEach(v -> {
                        writer.printf("%d,%s,%s,%s,%s,%.2f%n",
                                v.getId(),
                                v.getMarque(),
                                v.getModele(),
                                v.getImmatriculation(),
                                v.getStatut(),
                                v.getPrixParJour() != null ? v.getPrixParJour() : 0.0);
                    });
                    break;
                case "ENTRETIENS":
                    writer.println("ID,Voiture,Type,Date,Cout,Paye");
                    java.util.List<com.example.Gestion_Flotte_Automobile.entity.Entretien> entretiens;
                    if (startDate != null && endDate != null) {
                        entretiens = entretienService.findByDateEntretienBetween(startDate, endDate);
                    } else {
                        entretiens = entretienService.findAll();
                    }
                    entretiens.forEach(e -> {
                        writer.printf("%d,%s,%s,%s,%.2f,%s%n",
                                e.getId(),
                                e.getVoiture().getImmatriculation(),
                                e.getTypeEntretien(),
                                e.getDateEntretien(),
                                e.getCout(),
                                e.isPaye() ? "Oui" : "Non");
                    });
                    break;
            }
        }
    }

    private void exportPdf(HttpServletResponse response, String type, LocalDate startDate, LocalDate endDate)
            throws IOException, DocumentException {
        response.setContentType("application/pdf");
        String filename = "report_" + type.toLowerCase() + "_" + LocalDate.now() + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Font fontBody = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

        document.add(new Paragraph("Rapport: " + type, fontHeader));
        document.add(new Paragraph("Date Generation: " + LocalDate.now(), fontBody));
        if (startDate != null && endDate != null) {
            document.add(new Paragraph("Periode: " + startDate + " au " + endDate, fontBody));
        }
        document.add(Chunk.NEWLINE);

        PdfPTable table;
        switch (type) {
            case "FINANCIAL":
                table = new PdfPTable(2);
                table.addCell("Indicateur");
                table.addCell("Montant (DH)");
                table.addCell("Recettes Totales");
                table.addCell(String.valueOf(dashboardService.sumTotalPaiements(startDate, endDate)));
                table.addCell("Depenses Totales");
                table.addCell(String.valueOf(dashboardService.sumCoutEntretiensPayes(startDate, endDate)));
                table.addCell("Benefice Net");
                table.addCell(String.valueOf(dashboardService.sumTotalPaiements(startDate, endDate)
                        - dashboardService.sumCoutEntretiensPayes(startDate, endDate)));
                document.add(table);
                break;
            case "RESERVATIONS":
                table = new PdfPTable(6);
                table.setWidthPercentage(100);
                table.addCell("ID");
                table.addCell("Client");
                table.addCell("Voiture");
                table.addCell("Debut");
                table.addCell("Fin");
                table.addCell("Statut");

                java.util.List<com.example.Gestion_Flotte_Automobile.entity.Reservation> reservations;
                if (startDate != null && endDate != null) {
                    reservations = reservationService.findByDateDebutBetween(startDate.atStartOfDay(),
                            endDate.atTime(23, 59, 59));
                } else {
                    reservations = reservationService.findAll();
                }

                for (com.example.Gestion_Flotte_Automobile.entity.Reservation r : reservations) {
                    table.addCell(String.valueOf(r.getId()));
                    table.addCell(r.getClient().getNom() + " " + r.getClient().getPrenom());
                    table.addCell(r.getVoiture().getImmatriculation());
                    table.addCell(r.getDateDebut().toString());
                    table.addCell(r.getDateFin().toString());
                    table.addCell(String.valueOf(r.getStatut()));
                }
                document.add(table);
                break;

            case "VOITURES":
                table = new PdfPTable(6);
                table.setWidthPercentage(100);
                table.addCell("ID");
                table.addCell("Marque");
                table.addCell("Modele");
                table.addCell("Immat.");
                table.addCell("Statut");
                table.addCell("Prix/J");

                for (com.example.Gestion_Flotte_Automobile.entity.Voiture v : voitureService.findAll()) {
                    table.addCell(String.valueOf(v.getId()));
                    table.addCell(v.getMarque());
                    table.addCell(v.getModele());
                    table.addCell(v.getImmatriculation());
                    table.addCell(String.valueOf(v.getStatut()));
                    table.addCell(String.valueOf(v.getPrixParJour()));
                }
                document.add(table);
                break;

            case "ENTRETIENS":
                table = new PdfPTable(6);
                table.setWidthPercentage(100);
                table.addCell("ID");
                table.addCell("Voiture");
                table.addCell("Type");
                table.addCell("Date");
                table.addCell("Cout");
                table.addCell("Paye");

                java.util.List<com.example.Gestion_Flotte_Automobile.entity.Entretien> entretiens;
                if (startDate != null && endDate != null) {
                    entretiens = entretienService.findByDateEntretienBetween(startDate, endDate);
                } else {
                    entretiens = entretienService.findAll();
                }

                for (com.example.Gestion_Flotte_Automobile.entity.Entretien e : entretiens) {
                    table.addCell(String.valueOf(e.getId()));
                    table.addCell(e.getVoiture().getImmatriculation());
                    table.addCell(String.valueOf(e.getTypeEntretien()));
                    table.addCell(e.getDateEntretien().toString());
                    table.addCell(String.valueOf(e.getCout()));
                    table.addCell(e.isPaye() ? "Oui" : "Non");
                }
                document.add(table);
                break;

            default:
                document.add(new Paragraph(
                        "Type de rapport inconnu: " + type,
                        fontBody));
        }

        document.close();
    }
}
