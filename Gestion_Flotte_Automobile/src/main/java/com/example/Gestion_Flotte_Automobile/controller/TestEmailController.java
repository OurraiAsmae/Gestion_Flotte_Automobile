package com.example.Gestion_Flotte_Automobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestEmailController {

    @GetMapping("/test-email-render")
    public String testEmailRender(Model model) {
        model.addAttribute("subject", "Test Email Subject");
        model.addAttribute("bodyContent", "This is a <strong>test</strong> message content.<br>It supports HTML.");
        model.addAttribute("actionLink", "http://localhost:8080/login");
        model.addAttribute("actionText", "Click Me");
        return "email/default-email";
    }
}
