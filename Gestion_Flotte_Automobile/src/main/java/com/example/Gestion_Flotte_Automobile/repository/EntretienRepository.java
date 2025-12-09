package com.example.Gestion_Flotte_Automobile.repository;

import com.example.Gestion_Flotte_Automobile.entity.Entretien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntretienRepository extends JpaRepository<Entretien, Long> {
    List<Entretien> findByVoitureId(Long voitureId);
}
