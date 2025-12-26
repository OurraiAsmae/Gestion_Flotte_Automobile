package com.example.Gestion_Flotte_Automobile.controller;

import com.example.Gestion_Flotte_Automobile.dto.CalendarEventDTO;

import com.example.Gestion_Flotte_Automobile.entity.Reservation;
import com.example.Gestion_Flotte_Automobile.entity.User;
import com.example.Gestion_Flotte_Automobile.entity.Voiture;

import com.example.Gestion_Flotte_Automobile.repository.ReservationRepository;
import com.example.Gestion_Flotte_Automobile.repository.UserRepository;
import com.example.Gestion_Flotte_Automobile.repository.VoitureRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private VoitureRepository voitureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.example.Gestion_Flotte_Automobile.repository.EntretienRepository entretienRepository;

    @GetMapping
    public String showCalendar() {
        return "calendar";
    }

    @GetMapping("/events")
    @ResponseBody
    public List<CalendarEventDTO> getEvents(Principal principal) {
        List<CalendarEventDTO> events = new ArrayList<>();
        String currentUserEmail = (principal != null) ? principal.getName() : "";

        List<Reservation> reservations = reservationRepository.findAll();
        List<Voiture> voitures = voitureRepository.findAll();

        for (Reservation res : reservations) {
            if (res.getStatut() == com.example.Gestion_Flotte_Automobile.enums.StatutReservation.ANNULEE) {
                continue;
            }

            String carInfo = res.getVoiture().getMarque() + " " + res.getVoiture().getModele() + " ("
                    + res.getVoiture().getImmatriculation() + ")";

            boolean isTerminee = res
                    .getStatut() == com.example.Gestion_Flotte_Automobile.enums.StatutReservation.TERMINEE;
            String color = isTerminee ? "gray" : "blue";

            if (res.getEmploye() != null && res.getEmploye().getEmail().equals(currentUserEmail)) {
                events.add(new CalendarEventDTO(
                        "Réservation - " + res.getVoiture().getImmatriculation()
                                + (isTerminee ? " (Terminée)" : ""),
                        res.getDateDebut().toString(),
                        res.getDateFin().toString(),
                        color,
                        false,
                        "PERSONAL",
                        "Réservation pour: " + carInfo + "\nMotif: " + res.getMotif()));
            }

            if (!isTerminee) {
                events.add(new CalendarEventDTO(
                        "Retour: " + res.getVoiture().getImmatriculation(),
                        res.getDateFin().toString(),
                        res.getDateFin().toString(),
                        "red",
                        false,
                        "RETURN",
                        "Le véhicule " + carInfo + " doit être retourné."));
            }
        }

        for (Voiture v : voitures) {
            String carInfo = v.getMarque() + " " + v.getModele() + " (" + v.getImmatriculation() + ")";

            if (v.getDateProchaineVidange() != null) {
                events.add(new CalendarEventDTO(
                        "Vidange: " + v.getImmatriculation(),
                        v.getDateProchaineVidange().toString(),
                        v.getDateProchaineVidange().toString(),
                        "orange",
                        true,
                        "MAINTENANCE",
                        "Vidange prévue pour: " + carInfo));
            }
            if (v.getDateProchaineVisiteTechnique() != null) {
                events.add(new CalendarEventDTO(
                        "Visite Tech: " + v.getImmatriculation(),
                        v.getDateProchaineVisiteTechnique().toString(),
                        v.getDateProchaineVisiteTechnique().toString(),
                        "orange",
                        true,
                        "MAINTENANCE",
                        "Visite technique prévue pour: " + carInfo));
            }
            if (v.getDateExpirationAssurance() != null) {
                events.add(new CalendarEventDTO(
                        "Assurance: " + v.getImmatriculation(),
                        v.getDateExpirationAssurance().toString(),
                        v.getDateExpirationAssurance().toString(),
                        "purple",
                        true,
                        "ASSURANCE",
                        "Expiration Assurance pour: " + carInfo));
            }
        }

        List<com.example.Gestion_Flotte_Automobile.entity.Entretien> entretiens = entretienRepository.findAll();
        for (com.example.Gestion_Flotte_Automobile.entity.Entretien ent : entretiens) {
            String carInfo = ent.getVoiture().getMarque() + " " + ent.getVoiture().getModele() + " ("
                    + ent.getVoiture().getImmatriculation() + ")";
            events.add(new CalendarEventDTO(
                    "Entretien: " + ent.getTypeEntretien(),
                    ent.getDateEntretien().toString(),
                    ent.getDateEntretien().toString(),
                    "orange",
                    true,
                    "MAINTENANCE",
                    "Entretien (" + ent.getTypeEntretien() + ") pour: " + carInfo + "\nPrestataire: "
                            + ent.getPrestataire()));
        }

        return events;
    }
}