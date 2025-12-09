package com.example.Gestion_Flotte_Automobile.controller;

import com.example.Gestion_Flotte_Automobile.entity.Entretien;
import com.example.Gestion_Flotte_Automobile.enums.TypeEntretien;
import com.example.Gestion_Flotte_Automobile.service.EntretienService;
import com.example.Gestion_Flotte_Automobile.service.VoitureService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/entretiens")
public class EntretienController {

    private final EntretienService entretienService;
    private final VoitureService voitureService;

    @GetMapping
    public String listEntretiens(Model model) {
        model.addAttribute("entretiens", entretienService.findAll());
        return "entretiens/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("entretien", new Entretien());
        model.addAttribute("voitures", voitureService.findAll());
        model.addAttribute("types", TypeEntretien.values());
        return "entretiens/form";
    }

    @PostMapping
    public String saveEntretien(@ModelAttribute("entretien") Entretien entretien, Model model) {
        try {
            entretienService.save(entretien);
            return "redirect:/entretiens";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("voitures", voitureService.findAll());
            model.addAttribute("types", TypeEntretien.values());
            return "entretiens/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        java.util.Optional<Entretien> entretien = entretienService.findById(id);
        if (entretien.isPresent()) {
            model.addAttribute("entretien", entretien.get());
            model.addAttribute("voitures", voitureService.findAll());
            model.addAttribute("types", TypeEntretien.values());
            return "entretiens/form";
        }
        return "redirect:/entretiens";
    }

    @PostMapping("/update/{id}")
    public String updateEntretien(@PathVariable Long id, @ModelAttribute("entretien") Entretien entretien,
            Model model) {
        try {
            entretienService.update(id, entretien);
            return "redirect:/entretiens";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            model.addAttribute("voitures", voitureService.findAll());
            model.addAttribute("types", TypeEntretien.values());
            return "entretiens/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteEntretien(@PathVariable Long id) {
        entretienService.deleteById(id);
        return "redirect:/entretiens";
    }

    @GetMapping("/voiture/{id}")
    public String listEntretiensByVoiture(@PathVariable Long id, Model model) {
        model.addAttribute("entretiens", entretienService.findByVoiture(id));
        return "entretiens/list";
    }
}
