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

    @GetMapping
    public String listPaiements(Model model) {
        model.addAttribute("paiements", paiementService.findAll());
        return "paiements/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("paiement", new Paiement());
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("voitures", voitureService.findAll());
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
            model.addAttribute("types", TypePaiement.values());
            model.addAttribute("statuts", StatutPaiement.values());
            return "paiements/form";
        }
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
