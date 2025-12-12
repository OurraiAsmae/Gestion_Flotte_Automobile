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

    private final com.example.Gestion_Flotte_Automobile.repository.ReservationRepository reservationRepository;

    // Run every day at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkExpirations() {
        List<Voiture> voitures = voitureService.findAll();
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);

        for (Voiture voiture : voitures) {
            // Check Assurance
            if (voiture.getDateExpirationAssurance() != null &&
                    !voiture.getDateExpirationAssurance().isBefore(today) &&
                    voiture.getDateExpirationAssurance().isBefore(sevenDaysLater)) {
                notificationService.envoyerNotificationAuxGerants("Expiration Assurance",
                        "L'assurance de la voiture " + voiture.getImmatriculation() + " expire bientôt ("
                                + voiture.getDateExpirationAssurance() + ").");
            }

            // Check Vignette
            if (voiture.getDateExpirationVignette() != null &&
                    !voiture.getDateExpirationVignette().isBefore(today) &&
                    voiture.getDateExpirationVignette().isBefore(sevenDaysLater)) {
                notificationService.envoyerNotificationAuxGerants("Expiration Vignette",
                        "La vignette de la voiture " + voiture.getImmatriculation() + " expire bientôt ("
                                + voiture.getDateExpirationVignette() + ").");
            }
        }

        checkUpcomingReturns(today);
    }

    private void checkUpcomingReturns(LocalDate today) {
        LocalDate tomorrow = today.plusDays(1);
        // We will fetch all active reservations and check those ending tomorrow
        // Optimized: We could add a custom query in repository, but for now we filter
        // in memory or fetch all.
        // Let's use findAll for simplicity as per instructions to not add new queries
        // unless necessary,
        // BUT the user asked for efficiency. ReservationRepository has findByStatut.
        List<com.example.Gestion_Flotte_Automobile.entity.Reservation> reservations = reservationRepository
                .findByStatut(com.example.Gestion_Flotte_Automobile.enums.StatutReservation.CONFIRMEE);

        for (com.example.Gestion_Flotte_Automobile.entity.Reservation res : reservations) {
            if (res.getDateFin().toLocalDate().isEqual(tomorrow)) {
                notificationService.envoyerNotificationAuxGerants("Retour Prévu Demain",
                        "La voiture " + res.getVoiture().getImmatriculation() + " doit être retournée demain par "
                                + res.getClient().getNom());
            }
        }
    }
}
