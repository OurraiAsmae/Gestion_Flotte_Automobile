package com.example.Gestion_Flotte_Automobile.scheduler;

import com.example.Gestion_Flotte_Automobile.entity.Voiture;
import com.example.Gestion_Flotte_Automobile.service.NotificationService;
import com.example.Gestion_Flotte_Automobile.service.VoitureService;
import com.example.Gestion_Flotte_Automobile.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final VoitureService voitureService;
    private final NotificationService notificationService;
    private final UserService userService;

    // Run every day at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkExpirations() {
        List<Voiture> voitures = voitureService.findAll();
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);

        // We need a recipient. I'll pick the first admin/manager found or just iterate
        // all admins.
        // Since I don't have a getAdmins method, I'll fetch all users and filter by
        // role if possible,
        // or just send to the first user for now as a placeholder.
        // Ideally, we should have a specific user or a system admin.
        // I will try to find a user with role GERANT.
        com.example.Gestion_Flotte_Automobile.entity.User admin = userService.findAll().stream()
                .filter(u -> u.getRole() == com.example.Gestion_Flotte_Automobile.enums.Role.GERANT)
                .findFirst()
                .orElse(null);

        if (admin == null)
            return; // No admin to notify

        for (Voiture voiture : voitures) {
            // Check Assurance
            if (voiture.getDateExpirationAssurance() != null &&
                    !voiture.getDateExpirationAssurance().isBefore(today) &&
                    voiture.getDateExpirationAssurance().isBefore(sevenDaysLater)) {
                notificationService.envoyerNotification(admin, "Expiration Assurance",
                        "L'assurance de la voiture " + voiture.getImmatriculation() + " expire bientôt ("
                                + voiture.getDateExpirationAssurance() + ").");
            }

            // Check Vignette
            if (voiture.getDateExpirationVignette() != null &&
                    !voiture.getDateExpirationVignette().isBefore(today) &&
                    voiture.getDateExpirationVignette().isBefore(sevenDaysLater)) {
                notificationService.envoyerNotification(admin, "Expiration Vignette",
                        "La vignette de la voiture " + voiture.getImmatriculation() + " expire bientôt ("
                                + voiture.getDateExpirationVignette() + ").");
            }
        }
    }
}
