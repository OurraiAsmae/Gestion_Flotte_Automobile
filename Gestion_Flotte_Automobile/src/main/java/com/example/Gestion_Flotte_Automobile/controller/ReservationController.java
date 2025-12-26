package com.example.Gestion_Flotte_Automobile.controller;

import com.example.Gestion_Flotte_Automobile.entity.Reservation;
import com.example.Gestion_Flotte_Automobile.enums.StatutReservation;
import com.example.Gestion_Flotte_Automobile.service.ClientService;
import com.example.Gestion_Flotte_Automobile.service.ReservationService;
import com.example.Gestion_Flotte_Automobile.service.UserService;
import com.example.Gestion_Flotte_Automobile.service.VoitureService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ClientService clientService;
    private final VoitureService voitureService;
    private final UserService userService;

    @GetMapping
    public String listReservations(Model model) {
        model.addAttribute("reservations", reservationService.findAll());
        model.addAttribute("statuts", StatutReservation.values());
        return "reservations/list";
    }

    @PostMapping("/update-status/{id}")
    public String updateReservationStatus(@PathVariable Long id, @RequestParam("statut") StatutReservation statut) {
        Optional<Reservation> reservationOpt = reservationService.findById(id);
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            reservation.setStatut(statut);
            reservationService.update(id, reservation);
        }
        return "redirect:/reservations";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, java.security.Principal principal) {
        Reservation reservation = new Reservation();
        if (principal != null) {
            com.example.Gestion_Flotte_Automobile.entity.User currentUser = userService
                    .findByEmail(principal.getName());
            if (currentUser != null) {
                reservation.setEmploye(currentUser);
            }
        }

        model.addAttribute("reservation", reservation);
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("voitures",
                voitureService.findByStatut(com.example.Gestion_Flotte_Automobile.enums.StatutVoiture.DISPONIBLE));
        model.addAttribute("employes", userService.findAll());
        model.addAttribute("statuts", StatutReservation.values());
        model.addAttribute("typesPaiement", com.example.Gestion_Flotte_Automobile.enums.TypePaiement.values());
        return "reservations/form";
    }

    @PostMapping
    public String saveReservation(@ModelAttribute("reservation") Reservation reservation, Model model,
            java.security.Principal principal) {
        try {
            if (reservation.getEmploye() == null && principal != null) {
                com.example.Gestion_Flotte_Automobile.entity.User currentUser = userService
                        .findByEmail(principal.getName());
                reservation.setEmploye(currentUser);
            }
            reservationService.save(reservation);
            return "redirect:/reservations";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("clients", clientService.findAll());
            model.addAttribute("voitures",
                    voitureService.findByStatut(com.example.Gestion_Flotte_Automobile.enums.StatutVoiture.DISPONIBLE));
            model.addAttribute("employes", userService.findAll());
            model.addAttribute("statuts", StatutReservation.values());
            model.addAttribute("typesPaiement", com.example.Gestion_Flotte_Automobile.enums.TypePaiement.values());
            return "reservations/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Reservation> reservation = reservationService.findById(id);
        if (reservation.isPresent()) {
            model.addAttribute("reservation", reservation.get());
            model.addAttribute("clients", clientService.findAll());
            model.addAttribute("voitures", voitureService.findAll());
            model.addAttribute("employes", userService.findAll());
            model.addAttribute("statuts", StatutReservation.values());
            return "reservations/form";
        }
        return "redirect:/reservations";
    }

    @PostMapping("/update/{id}")
    public String updateReservation(@PathVariable Long id, @ModelAttribute("reservation") Reservation reservation) {
        reservationService.update(id, reservation);
        return "redirect:/reservations";
    }

    @GetMapping("/cancel/{id}")
    public String cancelReservation(@PathVariable Long id,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            reservationService.annulerReservation(id);
            redirectAttributes.addFlashAttribute("success", "Réservation annulée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'annulation : " + e.getMessage());
        }
        return "redirect:/reservations";
    }

    @GetMapping("/{id}")
    public String reservationDetails(@PathVariable Long id, Model model) {
        Optional<Reservation> reservation = reservationService.findById(id);
        if (reservation.isPresent()) {
            model.addAttribute("reservation", reservation.get());
            return "reservations/details";
        }
        return "redirect:/reservations";
    }
}
