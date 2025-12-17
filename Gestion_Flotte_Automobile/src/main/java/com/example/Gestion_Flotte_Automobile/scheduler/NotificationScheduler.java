package com.example.Gestion_Flotte_Automobile.scheduler;

import com.example.Gestion_Flotte_Automobile.entity.User;
import com.example.Gestion_Flotte_Automobile.entity.Voiture;
import com.example.Gestion_Flotte_Automobile.enums.Role;
import com.example.Gestion_Flotte_Automobile.enums.TypeNotification;
import com.example.Gestion_Flotte_Automobile.repository.NotificationRepository;
import com.example.Gestion_Flotte_Automobile.repository.UserRepository;
import com.example.Gestion_Flotte_Automobile.repository.VoitureRepository;
import com.example.Gestion_Flotte_Automobile.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

        private final VoitureRepository voitureRepository;
        private final UserRepository userRepository;
        private final NotificationService notificationService;
        private final NotificationRepository notificationRepository;

        @Scheduled(cron = "0 0 9 * * ?") // Daily at 9 AM
        public void checkDeadlines() {
                List<Voiture> voitures = voitureRepository.findAll();
                List<User> gerants = userRepository.findByRole(Role.GERANT);
                List<User> employes = userRepository.findByRole(Role.EMPLOYE);

                LocalDate today = LocalDate.now();
                LocalDate warningDate = today.plusDays(7);

                for (Voiture voiture : voitures) {
                        // Insurance Check
                        if (voiture.getDateExpirationAssurance() != null &&
                                        !voiture.getDateExpirationAssurance().isAfter(warningDate) &&
                                        voiture.getDateExpirationAssurance().isAfter(today.minusDays(1))) {
                                String titre = "Rappel Assurance: " + voiture.getImmatriculation();
                                String msg = "L'assurance de la voiture " + voiture.getImmatriculation() + " expire le "
                                                + voiture.getDateExpirationAssurance();

                                // Notify BOTH Gerants and Employes
                                notifyUsers(gerants, titre, msg, TypeNotification.RAPPEL);
                                notifyUsers(employes, titre, msg, TypeNotification.RAPPEL);
                        }

                        // Vignette Check
                        if (voiture.getDateExpirationVignette() != null &&
                                        !voiture.getDateExpirationVignette().isAfter(warningDate) &&
                                        voiture.getDateExpirationVignette().isAfter(today.minusDays(1))) {
                                String titre = "Rappel Vignette: " + voiture.getImmatriculation();
                                String msg = "La vignette de la voiture " + voiture.getImmatriculation() + " expire le "
                                                + voiture.getDateExpirationVignette();

                                notifyUsers(gerants, titre, msg, TypeNotification.RAPPEL);
                                notifyUsers(employes, titre, msg, TypeNotification.RAPPEL);
                        }

                        // Maintenance Check (Vidange)
                        if (voiture.getDateProchaineVidange() != null &&
                                        !voiture.getDateProchaineVidange().isAfter(warningDate) &&
                                        voiture.getDateProchaineVidange().isAfter(today.minusDays(1))) {
                                String titre = "Rappel Vidange: " + voiture.getImmatriculation();
                                String msg = "Vidange à prévoir pour " + voiture.getImmatriculation() + " avant le "
                                                + voiture.getDateProchaineVidange();

                                notifyUsers(gerants, titre, msg, TypeNotification.ALERTE);
                                notifyUsers(employes, titre, msg, TypeNotification.ALERTE);
                        }

                        // Maintenance Check (Visite Technique)
                        if (voiture.getDateProchaineVisiteTechnique() != null &&
                                        !voiture.getDateProchaineVisiteTechnique().isAfter(warningDate) &&
                                        voiture.getDateProchaineVisiteTechnique().isAfter(today.minusDays(1))) {
                                String titre = "Rappel Visite Technique: " + voiture.getImmatriculation();
                                String msg = "Visite technique à prévoir pour " + voiture.getImmatriculation()
                                                + " avant le "
                                                + voiture.getDateProchaineVisiteTechnique();

                                notifyUsers(gerants, titre, msg, TypeNotification.ALERTE);
                                notifyUsers(employes, titre, msg, TypeNotification.ALERTE);
                        }
                }
        }

        private void notifyUsers(List<User> users, String titre, String message, TypeNotification type) {
                LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
                for (User user : users) {
                        // Prevent duplicates: Check if notification exists for this user, title, since
                        // start of today
                        boolean exists = notificationRepository.existsByDestinataireIdAndTitreAndDateEnvoiAfter(
                                        user.getId(), titre, startOfDay);

                        if (!exists) {
                                notificationService.envoyerNotification(user, titre, message, type);
                        }
                }
        }
}
