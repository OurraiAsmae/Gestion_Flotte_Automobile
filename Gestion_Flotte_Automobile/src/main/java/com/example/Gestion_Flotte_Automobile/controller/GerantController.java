package com.example.Gestion_Flotte_Automobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gerant")
public class GerantController {

    private final com.example.Gestion_Flotte_Automobile.service.DashboardService dashboardService;

    public GerantController(com.example.Gestion_Flotte_Automobile.service.DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/home")
    public String home(org.springframework.ui.Model model) {
        model.addAttribute("voituresDisponibles", dashboardService.countVoituresDisponibles());
        model.addAttribute("reservationsEnCours", dashboardService.countReservationsEnCours());
        model.addAttribute("entretiensEnAttente", dashboardService.countEntretiensEnAttente());
        model.addAttribute("paiementsDuMois", dashboardService.sumPaiementsDuMois());
        return "gerant/home";
    }
}
