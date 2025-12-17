package com.example.Gestion_Flotte_Automobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employe")
public class EmployeController {

    private final com.example.Gestion_Flotte_Automobile.service.DashboardService dashboardService;
    private final com.example.Gestion_Flotte_Automobile.service.NotificationService notificationService;
    private final com.example.Gestion_Flotte_Automobile.service.UserService userService;

    public EmployeController(com.example.Gestion_Flotte_Automobile.service.DashboardService dashboardService,
            com.example.Gestion_Flotte_Automobile.service.NotificationService notificationService,
            com.example.Gestion_Flotte_Automobile.service.UserService userService) {
        this.dashboardService = dashboardService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/home")
    public String home(org.springframework.ui.Model model) {
        model.addAttribute("voituresDisponibles", dashboardService.countVoituresDisponibles());
        model.addAttribute("reservationsEnCours", dashboardService.countReservationsEnCours());
        model.addAttribute("clientsCount", dashboardService.countClients());

        // Fetch unread notifications for the current user
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            com.example.Gestion_Flotte_Automobile.entity.User user = userService.findByEmail(auth.getName());
            if (user != null) {
                model.addAttribute("unreadNotifications", notificationService.findNonLus(user.getId()));
            }
        }

        return "employe/home";
    }
}
