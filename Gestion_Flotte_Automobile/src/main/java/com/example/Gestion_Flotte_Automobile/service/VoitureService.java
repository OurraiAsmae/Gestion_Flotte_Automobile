package com.example.Gestion_Flotte_Automobile.service;

import com.example.Gestion_Flotte_Automobile.entity.Voiture;
import com.example.Gestion_Flotte_Automobile.enums.StatutVoiture;

import java.util.List;
import java.util.Optional;

public interface VoitureService {
    Voiture save(Voiture voiture);

    Voiture update(Long id, Voiture voiture);

    void deleteById(Long id);

    Optional<Voiture> findById(Long id);

    List<Voiture> findAll();

    List<Voiture> findByStatut(StatutVoiture statut);

    boolean existsByImmatriculation(String immatriculation);

    void mettreAJourStatutVoiture(Voiture voiture, StatutVoiture statut);
}
