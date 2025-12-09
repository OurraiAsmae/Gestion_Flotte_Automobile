package com.example.Gestion_Flotte_Automobile.controller;

import com.example.Gestion_Flotte_Automobile.entity.User;
import com.example.Gestion_Flotte_Automobile.repository.UserRepository;
import com.example.Gestion_Flotte_Automobile.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String viewProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        return "profile/profile";
    }

    @GetMapping("/edit")
    public String editProfileForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        return "profile/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam String telephone,
            RedirectAttributes redirectAttributes) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setTelephone(telephone);
        userRepository.save(user);

        // Update session details if necessary, but for now just redirect
        redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès.");
        return "redirect:/profile";
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "profile/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        User user = userRepository.findByEmail(userDetails.getUsername());

        if (!passwordEncoder.matches(oldPassword, user.getMotDePasse())) {
            redirectAttributes.addFlashAttribute("error", "L'ancien mot de passe est incorrect.");
            return "redirect:/profile/change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Les nouveaux mots de passe ne correspondent pas.");
            return "redirect:/profile/change-password";
        }

        if (newPassword.length() < 8) {
            redirectAttributes.addFlashAttribute("error", "Le mot de passe doit contenir au moins 8 caractères.");
            return "redirect:/profile/change-password";
        }

        user.setMotDePasse(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Mot de passe modifié avec succès.");
        return "redirect:/profile";
    }
}
