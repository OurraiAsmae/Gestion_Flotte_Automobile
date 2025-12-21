package com.example.Gestion_Flotte_Automobile.controller;

import com.example.Gestion_Flotte_Automobile.entity.User;
import com.example.Gestion_Flotte_Automobile.service.DashboardService;
import com.example.Gestion_Flotte_Automobile.service.NotificationService;
import com.example.Gestion_Flotte_Automobile.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping("/gerant/home")
    public String gerantHome(Model model) {
        // Common stats
        populateCommonStats(model);

        // Gerant specific stats (Financials & Maintenance)
        model.addAttribute("entretiensEnAttente", dashboardService.countEntretiensEnAttente());
        model.addAttribute("paiementsDuMois", dashboardService.sumPaiementsDuMois());

        double totalDepenses = dashboardService.sumCoutEntretiensPayes();
        double totalRevenus = dashboardService.sumTotalPaiements();
        double beneficeNet = totalRevenus - totalDepenses;

        model.addAttribute("totalEntretienPaye", totalDepenses);
        model.addAttribute("beneficeNet", beneficeNet);

        // Add notifications for Gerant as well since they are generated
        addNotificationsToModel(model);

        return "gerant/home";
    }

    @GetMapping("/employe/home")
    public String employeHome(Model model) {
        // Common stats
        populateCommonStats(model);

        // Employe specific stats (Clients)
        model.addAttribute("clientsCount", dashboardService.countClients());

        // Notifications
        addNotificationsToModel(model);

        return "employe/home";
    }

    private void populateCommonStats(Model model) {
        model.addAttribute("voituresDisponibles", dashboardService.countVoituresDisponibles());
        model.addAttribute("reservationsEnCours", dashboardService.countReservationsEnCours());
    }

    private void addNotificationsToModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            User user = userService.findByEmail(auth.getName());
            if (user != null) {
                model.addAttribute("unreadNotifications", notificationService.findNonLus(user.getId()));
            }
        }
    }
}
