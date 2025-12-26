package com.example.Gestion_Flotte_Automobile.service;

import com.example.Gestion_Flotte_Automobile.entity.Notification;
import com.example.Gestion_Flotte_Automobile.entity.User;
import com.example.Gestion_Flotte_Automobile.entity.Voiture;
import com.example.Gestion_Flotte_Automobile.enums.Role;
import com.example.Gestion_Flotte_Automobile.enums.TypeNotification;
import com.example.Gestion_Flotte_Automobile.repository.NotificationRepository;
import com.example.Gestion_Flotte_Automobile.repository.UserRepository;
import com.example.Gestion_Flotte_Automobile.repository.VoitureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class MaintenanceNotificationScheduler {

    @Autowired
    private VoitureRepository voitureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 9 * * ?")
    public void checkMaintenanceSchedule() {
        System.out.println("----- Checking Maintenance Schedules -----");
        List<Voiture> voitures = voitureRepository.findAll();
        List<User> managers = userRepository.findByRole(Role.GERANT);
        LocalDate today = LocalDate.now();

        for (Voiture v : voitures) {
            checkAndNotify(v, v.getDateProchaineVidange(), "Vidange", today, managers);
            checkAndNotify(v, v.getDateProchaineVisiteTechnique(), "Visite Technique", today, managers);
        }
    }

    private void checkAndNotify(Voiture voiture, LocalDate scheduledDate, String type, LocalDate today,
            List<User> managers) {
        if (scheduledDate == null)
            return;

        long daysUntil = ChronoUnit.DAYS.between(today, scheduledDate);

        if (daysUntil == 7 || daysUntil == 3 || daysUntil == 1) {
            String message = "Rappel: " + type + " prévue dans " + daysUntil + " jours pour " + voiture.getMarque()
                    + " " + voiture.getModele() + " (" + voiture.getImmatriculation() + ").";

            for (User manager : managers) {
                Notification notification = new Notification();
                notification.setDestinataire(manager);
                notification.setTitre("Rappel " + type);
                notification.setMessage(message);
                notification.setTypeNotification(TypeNotification.RAPPEL);
                notification.setLu(false);

                notificationRepository.save(notification);
            }
            System.out.println("Notification sent for " + voiture.getImmatriculation());
        }
    }
}
