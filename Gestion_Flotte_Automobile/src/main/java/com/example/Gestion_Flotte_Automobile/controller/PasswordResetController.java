package com.example.Gestion_Flotte_Automobile.controller;

import com.example.Gestion_Flotte_Automobile.entity.User;
import com.example.Gestion_Flotte_Automobile.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes,
            Model model) {
        try {
            boolean sent = passwordResetService.createPasswordResetTokenForUser(email);
            if (!sent) {
                model.addAttribute("error", "Aucun utilisateur trouvé avec cet e-mail.");
                return "forgot-password";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors de l'envoi de l'email. Vérifiez la configuration.");
            return "forgot-password";
        }

        redirectAttributes.addFlashAttribute("message", "Un code de réinitialisation a été envoyé à votre e-mail.");
        return "redirect:/reset-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam(required = false) String token, Model model) {
        if (token != null) {
            String result = passwordResetService.validatePasswordResetToken(token);
            if (result != null) {
                model.addAttribute("error", "Le jeton est invalide ou expiré.");
            } else {
                model.addAttribute("token", token);
            }
        }
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model, RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        String result = passwordResetService.validatePasswordResetToken(token);
        if (result != null) {
            model.addAttribute("error", "Jeton invalide.");
            return "reset-password";
        }

        User user = passwordResetService.getUserByToken(token);
        passwordResetService.changeUserPassword(user, password);

        redirectAttributes.addFlashAttribute("message", "Votre mot de passe a été réinitialisé avec succès.");
        return "redirect:/login";
    }
}
