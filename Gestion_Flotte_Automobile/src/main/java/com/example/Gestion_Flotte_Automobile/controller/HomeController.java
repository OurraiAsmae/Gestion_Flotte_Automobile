package com.example.Gestion_Flotte_Automobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@lombok.RequiredArgsConstructor
public class HomeController {

    private final com.example.Gestion_Flotte_Automobile.service.DashboardService dashboardService;

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(org.springframework.ui.Model model) {
        model.addAttribute("voituresDisponibles", dashboardService.countVoituresDisponibles());
        model.addAttribute("reservationsEnCours", dashboardService.countReservationsEnCours());
        model.addAttribute("entretiensEnAttente", dashboardService.countEntretiensEnAttente());
        model.addAttribute("clients", dashboardService.countClients());
        model.addAttribute("paiementsDuMois", dashboardService.sumPaiementsDuMois());
        return "home";
    }
}
