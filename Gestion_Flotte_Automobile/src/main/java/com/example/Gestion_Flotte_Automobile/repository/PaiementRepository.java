package com.example.Gestion_Flotte_Automobile.repository;

import com.example.Gestion_Flotte_Automobile.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    java.util.List<Paiement> findByClientId(Long clientId);

    java.util.List<Paiement> findByVoitureId(Long voitureId);

    java.util.List<Paiement> findByStatut(com.example.Gestion_Flotte_Automobile.enums.StatutPaiement statut);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p WHERE p.statut = :statut AND p.datePaiement >= :date")
    Double sumMontantByStatutAndDatePaiementGreaterThanEqual(
            @org.springframework.data.repository.query.Param("statut") com.example.Gestion_Flotte_Automobile.enums.StatutPaiement statut,
            @org.springframework.data.repository.query.Param("date") java.time.LocalDate date);
}
