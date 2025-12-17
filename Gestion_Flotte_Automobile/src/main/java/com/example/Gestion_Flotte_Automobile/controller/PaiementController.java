package com.example.Gestion_Flotte_Automobile.controller;

import com.example.Gestion_Flotte_Automobile.entity.Paiement;
import com.example.Gestion_Flotte_Automobile.enums.StatutPaiement;
import com.example.Gestion_Flotte_Automobile.enums.TypePaiement;
import com.example.Gestion_Flotte_Automobile.service.ClientService;
import com.example.Gestion_Flotte_Automobile.service.PaiementService;
import com.example.Gestion_Flotte_Automobile.service.VoitureService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/paiements")
public class PaiementController {

    private final PaiementService paiementService;
    private final ClientService clientService;
    private final VoitureService voitureService;
    private final com.example.Gestion_Flotte_Automobile.service.ReservationService reservationService;
    private final com.example.Gestion_Flotte_Automobile.repository.UserRepository userRepository;

    @GetMapping
    public String listPaiements(Model model) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        boolean isEmploye = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYE"));

        if (isEmploye) {
            String email = auth.getName();
            com.example.Gestion_Flotte_Automobile.entity.User user = userRepository.findByEmail(email);
            if (user != null) {
                model.addAttribute("paiements", paiementService.findByEmploye(user.getId()));
            } else {
                model.addAttribute("paiements", java.util.Collections.emptyList());
            }
        } else {
            model.addAttribute("paiements", paiementService.findAll());
        }

        model.addAttribute("statuts", StatutPaiement.values());
        return "paiements/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("paiement", new Paiement());
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("voitures", voitureService.findAll());
        model.addAttribute("reservations", reservationService.findAll());
        model.addAttribute("types", TypePaiement.values());
        model.addAttribute("statuts", StatutPaiement.values());
        return "paiements/form";
    }

    @PostMapping
    public String savePaiement(@ModelAttribute("paiement") Paiement paiement, Model model) {
        try {
            paiementService.save(paiement);
            return "redirect:/paiements";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("clients", clientService.findAll());
            model.addAttribute("voitures", voitureService.findAll());
            model.addAttribute("reservations", reservationService.findAll());
            model.addAttribute("types", TypePaiement.values());
            model.addAttribute("statuts", StatutPaiement.values());
            return "paiements/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        java.util.Optional<Paiement> paiement = paiementService.findById(id);
        if (paiement.isPresent()) {
            model.addAttribute("paiement", paiement.get());
            model.addAttribute("clients", clientService.findAll());
            model.addAttribute("voitures", voitureService.findAll());
            model.addAttribute("reservations", reservationService.findAll());
            model.addAttribute("types", TypePaiement.values());
            model.addAttribute("statuts", StatutPaiement.values());
            return "paiements/form";
        }
        return "redirect:/paiements";
    }

    @PostMapping("/update/{id}")
    public String updatePaiement(@PathVariable Long id, @ModelAttribute("paiement") Paiement paiement, Model model) {
        try {
            paiementService.update(id, paiement);
            return "redirect:/paiements";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            model.addAttribute("clients", clientService.findAll());
            model.addAttribute("voitures", voitureService.findAll());
            model.addAttribute("reservations", reservationService.findAll());
            model.addAttribute("types", TypePaiement.values());
            model.addAttribute("statuts", StatutPaiement.values());
            return "paiements/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deletePaiement(@PathVariable Long id) {
        paiementService.deleteById(id);
        return "redirect:/paiements";
    }

    @GetMapping("/client/{id}")
    public String listPaiementsByClient(@PathVariable Long id, Model model) {
        model.addAttribute("paiements", paiementService.findByClient(id));
        return "paiements/list";
    }

    @GetMapping("/voiture/{id}")
    public String listPaiementsByVoiture(@PathVariable Long id, Model model) {
        model.addAttribute("paiements", paiementService.findByVoiture(id));
        return "paiements/list";
    }
}
