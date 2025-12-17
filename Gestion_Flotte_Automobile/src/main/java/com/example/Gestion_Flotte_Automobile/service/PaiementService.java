package com.example.Gestion_Flotte_Automobile.service;

import com.example.Gestion_Flotte_Automobile.entity.Paiement;
import com.example.Gestion_Flotte_Automobile.enums.StatutPaiement;

import java.util.List;
import java.util.Optional;

public interface PaiementService {
    Paiement save(Paiement paiement);

    Paiement update(Long id, Paiement paiement);

    void deleteById(Long id);

    Optional<Paiement> findById(Long id);

    List<Paiement> findAll();

    List<Paiement> findByClient(Long clientId);

    List<Paiement> findByVoiture(Long voitureId);

    List<Paiement> findByStatut(StatutPaiement statut);

    List<Paiement> findByEmploye(Long employeId);
}
