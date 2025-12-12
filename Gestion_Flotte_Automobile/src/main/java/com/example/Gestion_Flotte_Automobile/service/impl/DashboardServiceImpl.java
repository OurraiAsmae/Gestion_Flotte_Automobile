package com.example.Gestion_Flotte_Automobile.service.impl;

import com.example.Gestion_Flotte_Automobile.enums.StatutReservation;
import com.example.Gestion_Flotte_Automobile.enums.StatutVoiture;
import com.example.Gestion_Flotte_Automobile.repository.*;
import com.example.Gestion_Flotte_Automobile.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final VoitureRepository voitureRepository;
    private final ReservationRepository reservationRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PaiementRepository paiementRepository;
    private final EntretienRepository entretienRepository;

    public DashboardServiceImpl(VoitureRepository voitureRepository,
            ReservationRepository reservationRepository,
            ClientRepository clientRepository,
            UserRepository userRepository,
            PaiementRepository paiementRepository,
            EntretienRepository entretienRepository) {
        this.voitureRepository = voitureRepository;
        this.reservationRepository = reservationRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.paiementRepository = paiementRepository;
        this.entretienRepository = entretienRepository;
    }

    @Override
    public long countVoituresDisponibles() {
        return voitureRepository.findAll().stream()
                .filter(v -> v.getStatut() == StatutVoiture.DISPONIBLE)
                .count();
    }

    @Override
    public long countVoituresEnEntretien() {
        return voitureRepository.findAll().stream()
                .filter(v -> v.getStatut() == StatutVoiture.EN_ENTRETIEN)
                .count();
    }

    @Override
    public long countReservationsEnCours() {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getStatut() == StatutReservation.CONFIRMEE)
                .count();
    }

    @Override
    public long countReservationsAujourdHui() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        return reservationRepository.findAll().stream()
                .filter(r -> r.getDateCreation().isAfter(startOfDay) && r.getDateCreation().isBefore(endOfDay))
                .count();
    }

    @Override
    public long countClients() {
        return clientRepository.count();
    }

    @Override
    public long countEmployes() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == com.example.Gestion_Flotte_Automobile.enums.Role.EMPLOYE)
                .count();
    }

    @Override
    public double sumPaiementsDuMois() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        return paiementRepository.sumMontantByStatutAndDatePaiementGreaterThanEqual(
                com.example.Gestion_Flotte_Automobile.enums.StatutPaiement.PAYE,
                startOfMonth);
    }

    @Override
    public long countEntretiensEnAttente() {
        return entretienRepository.count();
    }
}
