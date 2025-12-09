package com.example.Gestion_Flotte_Automobile.repository;

import com.example.Gestion_Flotte_Automobile.entity.Voiture;
import com.example.Gestion_Flotte_Automobile.enums.StatutVoiture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoitureRepository extends JpaRepository<Voiture, Long> {
    boolean existsByImmatriculation(String immatriculation);

    List<Voiture> findByStatut(StatutVoiture statut);
}
