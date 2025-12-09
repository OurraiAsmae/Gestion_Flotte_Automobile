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

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
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
        Notification notification = new Notification();
        notification.setDestinataire(destinataire);
        notification.setTitre(titre);
        notification.setMessage(message);
        notification.setTypeNotification(com.example.Gestion_Flotte_Automobile.enums.TypeNotification.INFO);

        notification.setDateEnvoi(java.time.LocalDateTime.now());
        notification.setLu(false);
        notificationRepository.save(notification);
    }
}
