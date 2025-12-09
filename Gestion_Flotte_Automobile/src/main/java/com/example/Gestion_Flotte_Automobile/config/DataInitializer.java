package com.example.Gestion_Flotte_Automobile.config;

import com.example.Gestion_Flotte_Automobile.entity.*;
import com.example.Gestion_Flotte_Automobile.enums.*;
import com.example.Gestion_Flotte_Automobile.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
            VoitureRepository voitureRepository,
            ClientRepository clientRepository,
            ReservationRepository reservationRepository,
            EntretienRepository entretienRepository,
            PaiementRepository paiementRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // --- Users ---
            User gerant = userRepository.findByEmail("gerant@test.com");
            if (gerant == null) {
                gerant = new User();
                gerant.setNom("Admin");
                gerant.setPrenom("Gerant");
                gerant.setEmail("gerant@test.com");
                gerant.setMotDePasse(passwordEncoder.encode("password123"));
                gerant.setRole(Role.GERANT);
                gerant.setTelephone("0600000000");
                gerant.setStatut(StatutUtilisateur.ACTIF);
                userRepository.save(gerant);
                System.out.println("GERANT user created: gerant@test.com / password123");
            }

            User employe = userRepository.findByEmail("employe@test.com");
            if (employe == null) {
                employe = new User();
                employe.setNom("User");
                employe.setPrenom("Employe");
                employe.setEmail("employe@test.com");
                employe.setMotDePasse(passwordEncoder.encode("password123"));
                employe.setRole(Role.EMPLOYE);
                employe.setTelephone("0611111111");
                employe.setStatut(StatutUtilisateur.ACTIF);
                userRepository.save(employe);
                System.out.println("EMPLOYE user created: employe@test.com / password123");
            }

            // --- Voitures ---
            if (voitureRepository.count() == 0) {
                Voiture v1 = new Voiture();
                v1.setImmatriculation("1234-A-50");
                v1.setMarque("Toyota");
                v1.setModele("Yaris");
                v1.setAnnee(2020);
                v1.setKilometrageActuel(50000);
                v1.setDateMiseEnService(LocalDate.of(2020, 1, 15));
                v1.setStatut(StatutVoiture.DISPONIBLE);
                v1.setDateProchaineVidange(LocalDate.of(2024, 6, 15));
                v1.setDateProchaineVisiteTechnique(LocalDate.of(2025, 1, 15));
                v1.setDateExpirationAssurance(LocalDate.of(2024, 12, 31));
                v1.setDateExpirationVignette(LocalDate.of(2024, 12, 31));
                voitureRepository.save(v1);

                Voiture v2 = new Voiture();
                v2.setImmatriculation("5678-B-60");
                v2.setMarque("Dacia");
                v2.setModele("Logan");
                v2.setAnnee(2022);
                v2.setKilometrageActuel(20000);
                v2.setDateMiseEnService(LocalDate.of(2022, 5, 20));
                v2.setStatut(StatutVoiture.EN_RESERVATION);
                v2.setDateProchaineVidange(LocalDate.of(2024, 8, 20));
                v2.setDateProchaineVisiteTechnique(LocalDate.of(2026, 5, 20));
                v2.setDateExpirationAssurance(LocalDate.of(2024, 12, 31));
                v2.setDateExpirationVignette(LocalDate.of(2024, 12, 31));
                voitureRepository.save(v2);

                System.out.println("Voitures seeded.");
            }

            // --- Clients ---
            if (clientRepository.count() == 0) {
                Client c1 = new Client();
                c1.setNom("Dupont");
                c1.setPrenom("Jean");
                c1.setCin("AB123456");
                c1.setTelephone("0622222222");
                c1.setEmail("jean.dupont@example.com");
                c1.setAdresse("123 Rue Principale, Casablanca");
                clientRepository.save(c1);

                Client c2 = new Client();
                c2.setNom("Martin");
                c2.setPrenom("Sophie");
                c2.setCin("CD789012");
                c2.setTelephone("0633333333");
                c2.setEmail("sophie.martin@example.com");
                c2.setAdresse("456 Avenue Mohamed V, Rabat");
                clientRepository.save(c2);

                System.out.println("Clients seeded.");
            }

            // --- Reservations ---
            if (reservationRepository.count() == 0) {
                Voiture v2 = voitureRepository.findAll().get(1); // Dacia Logan
                Client c1 = clientRepository.findAll().get(0); // Jean Dupont

                Reservation r1 = new Reservation();
                r1.setVoiture(v2);
                r1.setClient(c1);
                r1.setEmploye(employe); // Created/Managed by employe
                r1.setDateDebut(LocalDateTime.now().minusDays(2));
                r1.setDateFin(LocalDateTime.now().plusDays(5));
                r1.setMotif("Vacances");
                r1.setStatut(StatutReservation.CONFIRMEE);
                reservationRepository.save(r1);

                System.out.println("Reservations seeded.");
            }

            // --- Entretiens ---
            if (entretienRepository.count() == 0) {
                Voiture v1 = voitureRepository.findAll().get(0); // Toyota Yaris

                Entretien e1 = new Entretien();
                e1.setVoiture(v1);
                e1.setTypeEntretien(TypeEntretien.VIDANGE);
                e1.setDateEntretien(LocalDate.now().minusMonths(1));
                e1.setKilometrage(48000);
                e1.setCout(500.0);
                e1.setPrestataire("Garage AutoFix");
                e1.setCommentaires("Vidange standard et changement filtre à huile.");
                entretienRepository.save(e1);

                System.out.println("Entretiens seeded.");
            }

            // --- Paiements ---
            if (paiementRepository.count() == 0) {
                Voiture v2 = voitureRepository.findAll().get(1); // Dacia Logan
                Client c1 = clientRepository.findAll().get(0); // Jean Dupont

                Paiement p1 = new Paiement();
                p1.setVoiture(v2);
                p1.setClient(c1);
                p1.setTypePaiement(TypePaiement.ESPECES);
                p1.setMontant(1500.0);
                p1.setDatePaiement(LocalDate.now().minusDays(2));
                p1.setStatut(StatutPaiement.PAYE);
                paiementRepository.save(p1);

                System.out.println("Paiements seeded.");
            }
        };
    }
}
