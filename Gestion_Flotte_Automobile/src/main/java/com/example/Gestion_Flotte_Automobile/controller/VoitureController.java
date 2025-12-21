package com.example.Gestion_Flotte_Automobile.controller;

import com.example.Gestion_Flotte_Automobile.entity.Voiture;
import com.example.Gestion_Flotte_Automobile.enums.StatutVoiture;
import com.example.Gestion_Flotte_Automobile.service.VoitureService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/voitures")
public class VoitureController {

    private final VoitureService voitureService;

    @GetMapping
    public String listVoitures(Model model) {
        model.addAttribute("voitures", voitureService.findAll());
        return "voitures/list";
    }

    @GetMapping("/new")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('GERANT')")
    public String showCreateForm(Model model) {
        model.addAttribute("voiture", new Voiture());
        model.addAttribute("statuts", StatutVoiture.values());
        return "voitures/form";
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('GERANT')")
    public String saveVoiture(@jakarta.validation.Valid @ModelAttribute("voiture") Voiture voiture,
            org.springframework.validation.BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("statuts", StatutVoiture.values());
            return "voitures/form";
        }
        voitureService.save(voiture);
        return "redirect:/voitures";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Voiture> voiture = voitureService.findById(id);
        if (voiture.isPresent()) {
            System.out.println("DEBUG LOAD VOITURE " + id + ": " + voiture.get());
            System.out.println("DEBUG LOAD COUTS: Vidange=" + voiture.get().getCoutVidange());

            model.addAttribute("voiture", voiture.get());
            model.addAttribute("statuts", StatutVoiture.values());
            return "voitures/form";
        }
        return "redirect:/voitures";
    }

    @PostMapping("/update/{id}")
    public String updateVoiture(@PathVariable Long id,
            @jakarta.validation.Valid @ModelAttribute("voiture") Voiture voiture,
            org.springframework.validation.BindingResult result, Model model) {

        System.out.println("DEBUG UPDATE VOITURE: " + voiture);
        System.out.println("DEBUG COUTS: Vidange=" + voiture.getCoutVidange()
                + ", Visite=" + voiture.getCoutVisiteTechnique()
                + ", Assurance=" + voiture.getCoutAssurance()
                + ", Vignette=" + voiture.getCoutVignette());

        if (result.hasErrors()) {
            model.addAttribute("statuts", StatutVoiture.values());
            return "voitures/form";
        }
        voitureService.update(id, voiture);
        return "redirect:/voitures";
    }

    @GetMapping("/delete/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('GERANT')")
    public String deleteVoiture(@PathVariable Long id) {
        voitureService.deleteById(id);
        return "redirect:/voitures";
    }

    @GetMapping("/{id}")
    public String voitureDetails(@PathVariable Long id, Model model) {
        Optional<Voiture> voiture = voitureService.findById(id);
        if (voiture.isPresent()) {
            model.addAttribute("voiture", voiture.get());
            return "voitures/details";
        }
        return "redirect:/voitures";
    }
}
