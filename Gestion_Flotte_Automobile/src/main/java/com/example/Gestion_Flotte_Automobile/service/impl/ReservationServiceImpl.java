package com.example.Gestion_Flotte_Automobile.service.impl;

import com.example.Gestion_Flotte_Automobile.entity.Reservation;
import com.example.Gestion_Flotte_Automobile.enums.StatutReservation;
import com.example.Gestion_Flotte_Automobile.repository.ReservationRepository;
import com.example.Gestion_Flotte_Automobile.service.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final com.example.Gestion_Flotte_Automobile.service.VoitureService voitureService;
    private final com.example.Gestion_Flotte_Automobile.service.NotificationService notificationService;
    private final com.example.Gestion_Flotte_Automobile.repository.PaiementRepository paiementRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
            com.example.Gestion_Flotte_Automobile.service.VoitureService voitureService,
            com.example.Gestion_Flotte_Automobile.service.NotificationService notificationService,
            com.example.Gestion_Flotte_Automobile.repository.PaiementRepository paiementRepository) {
        this.reservationRepository = reservationRepository;
        this.voitureService = voitureService;
        this.notificationService = notificationService;
        this.paiementRepository = paiementRepository;
    }

    @Override
    @Transactional
    public Reservation save(Reservation reservation) {
        if (reservation.getDateDebut().isAfter(reservation.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin.");
        }
        if (reservation.getDateDebut().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("La date de début ne peut pas être dans le passé.");
        }

        if (!estVoitureDisponible(reservation.getVoiture().getId(), reservation.getDateDebut(),
                reservation.getDateFin())) {
            throw new IllegalStateException("La voiture n'est pas disponible pour cette période.");
        }

        reservation.setStatut(StatutReservation.CONFIRMEE);
        Reservation saved = reservationRepository.save(reservation);

        // Calculate Price & Create Payment
        Double storedPrice = saved.getVoiture().getPrixParJour();
        double prixParJour = (storedPrice != null) ? storedPrice : 300.0; // Default to 300 if not set

        long hours = java.time.temporal.ChronoUnit.HOURS.between(saved.getDateDebut(), saved.getDateFin());
        long days = (long) Math.ceil(hours / 24.0);

        if (days < 1)
            days = 1; // Minimum 1 day
        double totalAmount = days * prixParJour;

        com.example.Gestion_Flotte_Automobile.entity.Paiement paiement = new com.example.Gestion_Flotte_Automobile.entity.Paiement();
        paiement.setReservation(saved);
        paiement.setVoiture(saved.getVoiture());
        paiement.setClient(saved.getClient());
        paiement.setMontant(totalAmount);
        paiement.setMontant(totalAmount);
        paiement.setStatut(com.example.Gestion_Flotte_Automobile.enums.StatutPaiement.EN_ATTENTE);

        com.example.Gestion_Flotte_Automobile.enums.TypePaiement selectedType = saved.getTypePaiement();
        paiement.setTypePaiement(
                selectedType != null ? selectedType : com.example.Gestion_Flotte_Automobile.enums.TypePaiement.ESPECES);

        paiement.setDatePaiement(java.time.LocalDate.now()); // Set current date as placeholder or actual payment date?
                                                             // Usually 'datePaiement' is when it is paid. But field is
                                                             // NotNull. So set now.
        paiementRepository.save(paiement);

        voitureService.mettreAJourStatutVoiture(saved.getVoiture(),
                com.example.Gestion_Flotte_Automobile.enums.StatutVoiture.EN_RESERVATION);

        notificationService.envoyerNotification(saved.getEmploye(), "Nouvelle Réservation",
                "Une nouvelle réservation a été créée pour la voiture " + saved.getVoiture().getImmatriculation()
                        + ". Montant estimé: " + totalAmount + " MAD",
                com.example.Gestion_Flotte_Automobile.enums.TypeNotification.INFORMATION);

        notificationService.envoyerNotificationAuxGerants("Nouvelle Réservation",
                "Nouvelle réservation (ID: " + saved.getId() + ") pour le client " + saved.getClient().getNom() +
                        " " + saved.getClient().getPrenom() + ". Montant: " + totalAmount);

        return saved;
    }

    @Override
    @Transactional
    public Reservation update(Long id, Reservation reservation) {
        if (reservationRepository.existsById(id)) {
            // Strict Locking Rules
            java.util.List<com.example.Gestion_Flotte_Automobile.entity.Paiement> paiements = paiementRepository
                    .findByReservationId(id);
            boolean isPaid = paiements.stream()
                    .anyMatch(p -> p.getStatut() == com.example.Gestion_Flotte_Automobile.enums.StatutPaiement.PAYE);

            if (isPaid) {
                org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                        .getContext().getAuthentication();
                boolean isEmploye = auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYE"));
                if (isEmploye) {
                    throw new IllegalStateException(
                            "Modification interdite : La réservation est payée. Veuillez contacter un gérant.");
                }
            }
            if (reservation.getStatut() == StatutReservation.TERMINEE
                    || reservation.getStatut() == StatutReservation.ANNULEE) {
                // Fetch the actual car to ensure we have the entity
                Optional<Reservation> existingOpt = reservationRepository.findById(id);
                if (existingOpt.isPresent()) {
                    com.example.Gestion_Flotte_Automobile.entity.Voiture voiture = existingOpt.get().getVoiture();
                    System.out.println(
                            "Updating car status for reservation " + id + " to DISPONIBLE. Car ID: " + voiture.getId());
                    voitureService.mettreAJourStatutVoiture(voiture,
                            com.example.Gestion_Flotte_Automobile.enums.StatutVoiture.DISPONIBLE);

                    // Also update the incoming reservation's voiture to avoid overwriting with
                    // stale state
                    if (reservation.getVoiture() != null) {
                        reservation.getVoiture()
                                .setStatut(com.example.Gestion_Flotte_Automobile.enums.StatutVoiture.DISPONIBLE);
                    }
                }
            }

            reservation.setId(id);
            return reservationRepository.save(reservation);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public List<Reservation> findByClient(Long clientId) {
        return reservationRepository.findByClientId(clientId);
    }

    @Override
    public List<Reservation> findByVoiture(Long voitureId) {
        return reservationRepository.findByVoitureId(voitureId);
    }

    @Override
    public List<Reservation> findByEmploye(Long userId) {
        return reservationRepository.findByEmployeId(userId);
    }

    @Override
    public List<Reservation> findByStatut(StatutReservation statut) {
        return reservationRepository.findByStatut(statut);
    }

    @Override
    public boolean estVoitureDisponible(Long voitureId, java.time.LocalDateTime debut, java.time.LocalDateTime fin) {
        List<Reservation> overlapping = reservationRepository.findOverlappingReservations(voitureId, debut, fin);
        return overlapping.isEmpty();
    }

    @Override
    @Transactional
    public void annulerReservation(Long id) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(id);
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();

            if (reservation.getDateDebut().isBefore(java.time.LocalDateTime.now())) {
                throw new IllegalStateException("Impossible d'annuler une réservation déjà commencée ou passée.");
            }

            reservation.setStatut(StatutReservation.ANNULEE);
            reservationRepository.save(reservation);

            voitureService.mettreAJourStatutVoiture(reservation.getVoiture(),
                    com.example.Gestion_Flotte_Automobile.enums.StatutVoiture.DISPONIBLE);

            notificationService.envoyerNotification(reservation.getEmploye(), "Réservation Annulée",
                    "La réservation " + id + " a été annulée.",
                    com.example.Gestion_Flotte_Automobile.enums.TypeNotification.ALERTE);

            notificationService.envoyerNotificationAuxGerants("Réservation Annulée",
                    "La réservation " + id + " pour la voiture " + reservation.getVoiture().getImmatriculation()
                            + " a été annulée.");
        }
    }

    @Override
    public List<Reservation> findByDateDebutBetween(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return reservationRepository.findByDateDebutBetween(start, end);
    }
}
