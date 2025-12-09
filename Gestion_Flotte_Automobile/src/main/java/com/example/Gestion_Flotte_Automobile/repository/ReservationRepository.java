package com.example.Gestion_Flotte_Automobile.repository;

import com.example.Gestion_Flotte_Automobile.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    java.util.List<Reservation> findByClientId(Long clientId);

    java.util.List<Reservation> findByVoitureId(Long voitureId);

    java.util.List<Reservation> findByEmployeId(Long employeId);

    java.util.List<Reservation> findByStatut(com.example.Gestion_Flotte_Automobile.enums.StatutReservation statut);

    @org.springframework.data.jpa.repository.Query("SELECT r FROM Reservation r WHERE r.voiture.id = :voitureId " +
            "AND r.statut <> 'ANNULEE' " +
            "AND ((r.dateDebut <= :fin) AND (r.dateFin >= :debut))")
    java.util.List<Reservation> findOverlappingReservations(
            @org.springframework.data.repository.query.Param("voitureId") Long voitureId,
            @org.springframework.data.repository.query.Param("debut") java.time.LocalDateTime debut,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalDateTime fin);
}
