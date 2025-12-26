package com.example.Gestion_Flotte_Automobile.service.impl;

import com.example.Gestion_Flotte_Automobile.entity.Entretien;
import com.example.Gestion_Flotte_Automobile.repository.EntretienRepository;
import com.example.Gestion_Flotte_Automobile.service.EntretienService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EntretienServiceImpl implements EntretienService {

    private final EntretienRepository entretienRepository;
    private final com.example.Gestion_Flotte_Automobile.service.VoitureService voitureService;
    private final com.example.Gestion_Flotte_Automobile.service.NotificationService notificationService;

    public EntretienServiceImpl(EntretienRepository entretienRepository,
            com.example.Gestion_Flotte_Automobile.service.VoitureService voitureService,
            com.example.Gestion_Flotte_Automobile.service.NotificationService notificationService) {
        this.entretienRepository = entretienRepository;
        this.voitureService = voitureService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Entretien save(Entretien entretien) {

        com.example.Gestion_Flotte_Automobile.entity.Voiture voiture = voitureService
                .findById(entretien.getVoiture().getId())
                .orElseThrow(() -> new IllegalArgumentException("Voiture introuvable"));

        if (voiture.getStatut() == com.example.Gestion_Flotte_Automobile.enums.StatutVoiture.EN_RESERVATION) {
            throw new IllegalStateException(
                    "Impossible d'ajouter un entretien : la voiture est actuellement réservée.");
        }

        // Automatic Renewal Logic
        LocalDate today = LocalDate.now();
        switch (entretien.getTypeEntretien()) {
            case ASSURANCE:
                // Add 1 year to current expiration, or from today if null/past
                LocalDate nextAssurance = (voiture.getDateExpirationAssurance() != null
                        && voiture.getDateExpirationAssurance().isAfter(today))
                                ? voiture.getDateExpirationAssurance().plusYears(1)
                                : today.plusYears(1);
                voiture.setDateExpirationAssurance(nextAssurance);
                break;
            case VIGNETTE:
                LocalDate nextVignette = (voiture.getDateExpirationVignette() != null
                        && voiture.getDateExpirationVignette().isAfter(today))
                                ? voiture.getDateExpirationVignette().plusYears(1)
                                : today.plusYears(1);
                voiture.setDateExpirationVignette(nextVignette);
                break;
            case VISITE_TECHNIQUE:
                LocalDate nextVisite = (voiture.getDateProchaineVisiteTechnique() != null
                        && voiture.getDateProchaineVisiteTechnique().isAfter(today))
                                ? voiture.getDateProchaineVisiteTechnique().plusYears(5)
                                : today.plusYears(5);
                voiture.setDateProchaineVisiteTechnique(nextVisite);
                break;
            case VIDANGE:
                break;
            default:
                break;
        }

        Entretien saved = entretienRepository.save(entretien);

        voitureService.mettreAJourStatutVoiture(voiture,
                com.example.Gestion_Flotte_Automobile.enums.StatutVoiture.EN_ENTRETIEN);

        String titre = "Nouvel Entretien";
        String message = "Un nouvel entretien a été planifié pour la voiture " + voiture.getImmatriculation()
                + ". Statut: EN_ENTRETIEN";

        notificationService.envoyerNotificationAuxGerants(titre, message);
        notificationService.envoyerNotificationAuxEmployes(titre, message);

        return saved;
    }

    @Override
    @Transactional
    public Entretien update(Long id, Entretien entretien) {
        if (entretienRepository.existsById(id)) {
            entretien.setId(id);
            return entretienRepository.save(entretien);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        entretienRepository.deleteById(id);
    }

    @Override
    public Optional<Entretien> findById(Long id) {
        return entretienRepository.findById(id);
    }

    @Override
    public List<Entretien> findAll() {
        return entretienRepository.findAll();
    }

    @Override
    public List<Entretien> findByVoiture(Long voitureId) {
        return entretienRepository.findByVoitureId(voitureId);
    }

    @Override
    @Transactional
    public void terminerEntretien(Long id) {
        Optional<Entretien> entretienOpt = entretienRepository.findById(id);
        if (entretienOpt.isPresent()) {
            Entretien entretien = entretienOpt.get();
            voitureService.mettreAJourStatutVoiture(entretien.getVoiture(),
                    com.example.Gestion_Flotte_Automobile.enums.StatutVoiture.DISPONIBLE);

            String titre = "Entretien Terminé";
            String message = "L'entretien pour la voiture " + entretien.getVoiture().getImmatriculation()
                    + " est terminé. La voiture est maintenant disponible.";

            notificationService.envoyerNotificationAuxGerants(titre, message);
            notificationService.envoyerNotificationAuxEmployes(titre, message);
        }
    }

    @Override
    public List<Entretien> findByDateEntretienBetween(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        return entretienRepository.findByDateEntretienBetween(startDate, endDate);
    }
}
