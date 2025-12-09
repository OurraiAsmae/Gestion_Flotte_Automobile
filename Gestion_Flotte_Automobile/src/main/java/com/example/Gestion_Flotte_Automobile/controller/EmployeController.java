package com.example.Gestion_Flotte_Automobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employe")
public class EmployeController {

    private final com.example.Gestion_Flotte_Automobile.service.DashboardService dashboardService;

    public EmployeController(com.example.Gestion_Flotte_Automobile.service.DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/home")
    public String home(org.springframework.ui.Model model) {
        model.addAttribute("voituresDisponibles", dashboardService.countVoituresDisponibles());
        model.addAttribute("reservationsEnCours", dashboardService.countReservationsEnCours());
        model.addAttribute("clientsCount", dashboardService.countClients());
        model.addAttribute("paiementsDuMois", dashboardService.sumPaiementsDuMois());
        return "employe/home";
    }
}
