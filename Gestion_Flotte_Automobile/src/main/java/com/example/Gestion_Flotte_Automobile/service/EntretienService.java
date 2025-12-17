package com.example.Gestion_Flotte_Automobile.service;

import com.example.Gestion_Flotte_Automobile.entity.Entretien;

import java.util.List;
import java.util.Optional;

public interface EntretienService {
    Entretien save(Entretien entretien);

    Entretien update(Long id, Entretien entretien);

    void deleteById(Long id);

    Optional<Entretien> findById(Long id);

    List<Entretien> findAll();

    List<Entretien> findByVoiture(Long voitureId);

    void terminerEntretien(Long id);

    List<Entretien> findByDateEntretienBetween(java.time.LocalDate startDate, java.time.LocalDate endDate);
}
