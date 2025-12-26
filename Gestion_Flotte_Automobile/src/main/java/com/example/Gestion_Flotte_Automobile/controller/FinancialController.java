package com.example.Gestion_Flotte_Automobile.controller;

import com.example.Gestion_Flotte_Automobile.repository.EntretienRepository;
import com.example.Gestion_Flotte_Automobile.repository.PaiementRepository;
import com.example.Gestion_Flotte_Automobile.enums.StatutPaiement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/gerant")
@RequiredArgsConstructor
public class FinancialController {

    private final PaiementRepository paiementRepository;
    private final EntretienRepository entretienRepository;

    @GetMapping("/financial-summary")
    public String showFinancialSummary(@RequestParam(required = false) Integer year, Model model) {
        if (year == null)
            year = LocalDate.now().getYear();
        final int finalYear = year;
        LocalDate startOfYear = LocalDate.of(year, 1, 1);

        Double totalRevenue = paiementRepository.sumMontantByStatutAndDatePaiementGreaterThanEqual(StatutPaiement.PAYE,
                startOfYear);
        if (totalRevenue == null)
            totalRevenue = 0.0;

        Double totalMaintenance = entretienRepository.findAll().stream()
                .filter(e -> e.getDateEntretien().getYear() == finalYear)
                .mapToDouble(e -> e.getCout())
                .sum();

        model.addAttribute("year", year);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalMaintenance", totalMaintenance);
        model.addAttribute("netIncome", totalRevenue - totalMaintenance);

        return "gerant/financial-summary";
    }

    @GetMapping("/financial-summary/export")
    public void exportFinancialSummary(@RequestParam(required = false) Integer year,
            jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        if (year == null)
            year = LocalDate.now().getYear();
        final int finalYear = year;

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"financial_summary_" + year + ".csv\"");

        java.io.PrintWriter writer = response.getWriter();
        writer.println("Date,Type,Description,Montant,Status");

        java.util.List<com.example.Gestion_Flotte_Automobile.entity.Paiement> paiements = paiementRepository
                .findByStatut(StatutPaiement.PAYE);
        for (com.example.Gestion_Flotte_Automobile.entity.Paiement p : paiements) {
            if (p.getDatePaiement().getYear() == finalYear) {
                writer.println(p.getDatePaiement() + ",REVENUE,Payment Res #" + p.getReservation().getId() + ","
                        + p.getMontant() + "," + p.getStatut());
            }
        }

        java.util.List<com.example.Gestion_Flotte_Automobile.entity.Entretien> entretiens = entretienRepository
                .findAll();
        for (com.example.Gestion_Flotte_Automobile.entity.Entretien e : entretiens) {
            if (e.getDateEntretien().getYear() == finalYear) {
                writer.println(e.getDateEntretien() + ",EXPENSE,Entretien " + e.getTypeEntretien() + " Car "
                        + e.getVoiture().getImmatriculation() + "," + e.getCout() + ",DONE");
            }
        }
    }
}
