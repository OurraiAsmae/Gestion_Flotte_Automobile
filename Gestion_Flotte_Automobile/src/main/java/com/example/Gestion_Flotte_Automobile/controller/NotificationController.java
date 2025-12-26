package com.example.Gestion_Flotte_Automobile.controller;

import com.example.Gestion_Flotte_Automobile.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public String listNotifications(@RequestParam(required = false) Long userId, Model model) {
        if (userId != null) {
            model.addAttribute("notifications", notificationService.findByDestinataire(userId));
        } else {
            model.addAttribute("notifications", notificationService.findAll());
        }
        return "notifications/list";
    }

    @GetMapping("/read/{id}")
    public String markAsRead(@PathVariable Long id) {
        notificationService.marquerCommeLu(id);
        return "redirect:/notifications";
    }
}
