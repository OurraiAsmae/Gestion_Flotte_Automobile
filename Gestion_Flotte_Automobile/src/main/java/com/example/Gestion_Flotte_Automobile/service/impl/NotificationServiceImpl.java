package com.example.Gestion_Flotte_Automobile.service.impl;

import com.example.Gestion_Flotte_Automobile.entity.Notification;
import com.example.Gestion_Flotte_Automobile.repository.NotificationRepository;
import com.example.Gestion_Flotte_Automobile.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final com.example.Gestion_Flotte_Automobile.repository.UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
            com.example.Gestion_Flotte_Automobile.repository.UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    // ... existing methods ...

    @Override
    @Transactional
    public void envoyerNotificationAuxGerants(String titre, String message) {
        List<com.example.Gestion_Flotte_Automobile.entity.User> gerants = userRepository
                .findByRole(com.example.Gestion_Flotte_Automobile.enums.Role.GERANT);
        for (com.example.Gestion_Flotte_Automobile.entity.User gerant : gerants) {
            envoyerNotification(gerant, titre, message,
                    com.example.Gestion_Flotte_Automobile.enums.TypeNotification.INFORMATION);
        }
    }

    @Override
    @Transactional
    public void envoyerNotificationAuxEmployes(String titre, String message) {
        List<com.example.Gestion_Flotte_Automobile.entity.User> employes = userRepository
                .findByRole(com.example.Gestion_Flotte_Automobile.enums.Role.EMPLOYE);
        for (com.example.Gestion_Flotte_Automobile.entity.User employe : employes) {
            envoyerNotification(employe, titre, message,
                    com.example.Gestion_Flotte_Automobile.enums.TypeNotification.INFORMATION);
        }
    }

    @Override
    @Transactional
    public void envoyerNotification(com.example.Gestion_Flotte_Automobile.entity.User destinataire, String titre,
            String message, com.example.Gestion_Flotte_Automobile.enums.TypeNotification type) {
        Notification notification = new Notification();
        notification.setDestinataire(destinataire);
        notification.setTitre(titre);
        notification.setMessage(message);
        notification.setTypeNotification(type);

        notification.setDateEnvoi(java.time.LocalDateTime.now());
        notification.setLu(false);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id);
    }

    @Override
    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> findByDestinataire(Long userId) {
        return notificationRepository.findByDestinataireId(userId);
    }

    @Override
    public List<Notification> findNonLus(Long userId) {
        return notificationRepository.findByDestinataireIdAndLuFalse(userId);
    }

    @Override
    @Transactional
    public void marquerCommeLu(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setLu(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional
    public void envoyerNotification(com.example.Gestion_Flotte_Automobile.entity.User destinataire, String titre,
            String message) {
        envoyerNotification(destinataire, titre, message,
                com.example.Gestion_Flotte_Automobile.enums.TypeNotification.INFORMATION);
    }

}
