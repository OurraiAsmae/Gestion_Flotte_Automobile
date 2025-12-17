package com.example.Gestion_Flotte_Automobile.service;

import com.example.Gestion_Flotte_Automobile.entity.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
        Notification save(Notification notification);

        void deleteById(Long id);

        Optional<Notification> findById(Long id);

        List<Notification> findAll();

        List<Notification> findByDestinataire(Long userId);

        List<Notification> findNonLus(Long userId);

        void marquerCommeLu(Long notificationId);

        void envoyerNotification(com.example.Gestion_Flotte_Automobile.entity.User destinataire, String titre,
                        String message);

        void envoyerNotification(com.example.Gestion_Flotte_Automobile.entity.User destinataire, String titre,
                        String message, com.example.Gestion_Flotte_Automobile.enums.TypeNotification type);

        void envoyerNotificationAuxGerants(String titre, String message);

        void envoyerNotificationAuxEmployes(String titre, String message);
}
