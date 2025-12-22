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
    public String showCreateForm(@RequestParam(required = false) Long voitureId, Model model) {
        Entretien entretien = new Entretien();
        if (voitureId != null) {
            voitureService.findById(voitureId).ifPresent(entretien::setVoiture);
        }
        model.addAttribute("entretien", entretien);
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
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            boolean isGerant = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_GERANT"));

            if (!isGerant) {
                // For Employees: Only update 'paye' status.
                java.util.Optional<Entretien> existingOpt = entretienService.findById(id);
                if (existingOpt.isPresent()) {
                    Entretien existing = existingOpt.get();
                    existing.setPaye(entretien.isPaye());
                    // Preserve other fields (redundant if using ORM save on attached entity but
                    // good for safety)
                    entretienService.save(existing);
                }
            } else {
                // For Gerant: Update all
                entretienService.update(id, entretien);
            }
            return "redirect:/entretiens";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            model.addAttribute("voitures", voitureService.findAll());
            model.addAttribute("types", TypeEntretien.values());
            return "entretiens/form";
        }
    }

    @GetMapping("/delete/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('GERANT')")
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
