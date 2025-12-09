package com.example.Gestion_Flotte_Automobile.service;

import com.example.Gestion_Flotte_Automobile.entity.Reservation;
import com.example.Gestion_Flotte_Automobile.enums.StatutReservation;

import java.util.List;
import java.util.Optional;

public interface ReservationService {
    Reservation save(Reservation reservation);

    Reservation update(Long id, Reservation reservation);

    void deleteById(Long id);

    Optional<Reservation> findById(Long id);

    List<Reservation> findAll();

    List<Reservation> findByClient(Long clientId);

    List<Reservation> findByVoiture(Long voitureId);

    List<Reservation> findByEmploye(Long userId);

    List<Reservation> findByStatut(StatutReservation statut);

    boolean estVoitureDisponible(Long voitureId, java.time.LocalDateTime debut, java.time.LocalDateTime fin);

    void annulerReservation(Long id);
}
