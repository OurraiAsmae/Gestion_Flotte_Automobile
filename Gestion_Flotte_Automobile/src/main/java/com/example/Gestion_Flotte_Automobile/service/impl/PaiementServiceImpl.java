package com.example.Gestion_Flotte_Automobile.service.impl;

import com.example.Gestion_Flotte_Automobile.entity.Paiement;
import com.example.Gestion_Flotte_Automobile.enums.StatutPaiement;
import com.example.Gestion_Flotte_Automobile.repository.PaiementRepository;
import com.example.Gestion_Flotte_Automobile.service.PaiementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;
    private final com.example.Gestion_Flotte_Automobile.service.NotificationService notificationService;

    public PaiementServiceImpl(PaiementRepository paiementRepository,
            com.example.Gestion_Flotte_Automobile.service.NotificationService notificationService) {
        this.paiementRepository = paiementRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Paiement save(Paiement paiement) {

        if (paiement.getReservation() == null) {
            throw new IllegalArgumentException("Le paiement doit être lié à une réservation existante.");
        }

        if (paiement.getClient() == null) {
            paiement.setClient(paiement.getReservation().getClient());
        }
        if (paiement.getVoiture() == null) {
            paiement.setVoiture(paiement.getReservation().getVoiture());
        }

        if (!paiement.getReservation().getClient().getId().equals(paiement.getClient().getId())) {
            throw new IllegalArgumentException("Le client du paiement ne correspond pas au client de la réservation.");
        }

        Paiement saved = paiementRepository.save(paiement);

        if (saved.getReservation().getEmploye() != null) {
            notificationService.envoyerNotification(saved.getReservation().getEmploye(),
                    "Nouveau Paiement",
                    "Un paiement de " + saved.getMontant() + " MAD a été reçu pour la réservation #"
                            + saved.getReservation().getId(),
                    com.example.Gestion_Flotte_Automobile.enums.TypeNotification.CONFIRMATION);
        }

        notificationService.envoyerNotificationAuxGerants("Paiement Reçu",
                "Un paiement de " + saved.getMontant() + " MAD a été enregistré pour la réservation #"
                        + saved.getReservation().getId());

        return saved;
    }

    @Override
    @Transactional
    public Paiement update(Long id, Paiement paiement) {
        return paiementRepository.findById(id).map(existingPaiement -> {
            if (paiement.getStatut() == StatutPaiement.PAYE && existingPaiement.getStatut() != StatutPaiement.PAYE) {
                if (existingPaiement
                        .getTypePaiement() != com.example.Gestion_Flotte_Automobile.enums.TypePaiement.ESPECES) {

                    org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                            .getContext().getAuthentication();
                    boolean isGerant = auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_GERANT"));
                    if (!isGerant) {
                        throw new IllegalStateException(
                                "Seul un Gérant peut valider un paiement non-espèces (Virement/Chèque/Carte).");
                    }
                }
            }
            existingPaiement.setStatut(paiement.getStatut());
            return paiementRepository.save(existingPaiement);
        }).orElse(null);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        paiementRepository.deleteById(id);
    }

    @Override
    public Optional<Paiement> findById(Long id) {
        return paiementRepository.findById(id);
    }

    @Override
    public List<Paiement> findAll() {
        return paiementRepository.findAll();
    }

    @Override
    public List<Paiement> findByClient(Long clientId) {
        return paiementRepository.findByClientId(clientId);
    }

    @Override
    public List<Paiement> findByVoiture(Long voitureId) {
        return paiementRepository.findByVoitureId(voitureId);
    }

    @Override
    public List<Paiement> findByStatut(StatutPaiement statut) {
        return paiementRepository.findByStatut(statut);
    }

    @Override
    public List<Paiement> findByEmploye(Long employeId) {
        return paiementRepository.findByReservationEmployeId(employeId);
    }
}
