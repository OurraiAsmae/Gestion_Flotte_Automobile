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
    private final com.example.Gestion_Flotte_Automobile.repository.ReservationRepository reservationRepository;

    public PaiementServiceImpl(PaiementRepository paiementRepository,
            com.example.Gestion_Flotte_Automobile.repository.ReservationRepository reservationRepository) {
        this.paiementRepository = paiementRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional
    public Paiement save(Paiement paiement) {

        if (paiement.getClient() == null) {
            throw new IllegalArgumentException("Le paiement doit être associé à un client.");
        }
        List<com.example.Gestion_Flotte_Automobile.entity.Reservation> reservations = reservationRepository
                .findByClientId(paiement.getClient().getId());
        if (reservations.isEmpty()) {
            throw new IllegalStateException("Impossible d'enregistrer un paiement : ce client n'a aucune réservation.");
        }
        return paiementRepository.save(paiement);
    }

    @Override
    @Transactional
    public Paiement update(Long id, Paiement paiement) {
        if (paiementRepository.existsById(id)) {
            paiement.setId(id);
            return paiementRepository.save(paiement);
        }
        return null;
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
}
