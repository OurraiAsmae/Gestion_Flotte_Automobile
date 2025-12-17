package com.example.Gestion_Flotte_Automobile.controller;

import com.example.Gestion_Flotte_Automobile.entity.Client;
import com.example.Gestion_Flotte_Automobile.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    private final com.example.Gestion_Flotte_Automobile.service.ReservationService reservationService;
    private final com.example.Gestion_Flotte_Automobile.service.PaiementService paiementService;

    @GetMapping
    public String listClients(Model model) {
        model.addAttribute("clients", clientService.findAll());
        return "clients/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("client", new Client());
        return "clients/form";
    }

    @PostMapping
    public String saveClient(@ModelAttribute("client") Client client) {
        clientService.save(client);
        return "redirect:/clients";
    }

    @GetMapping("/edit/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('GERANT')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Client> client = clientService.findById(id);
        if (client.isPresent()) {
            model.addAttribute("client", client.get());
            return "clients/form";
        }
        return "redirect:/clients";
    }

    @PostMapping("/update/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('GERANT')")
    public String updateClient(@PathVariable Long id, @ModelAttribute("client") Client client) {
        clientService.update(id, client);
        return "redirect:/clients";
    }

    @GetMapping("/delete/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('GERANT')")
    public String deleteClient(@PathVariable Long id) {
        clientService.deleteById(id);
        return "redirect:/clients";
    }

    @GetMapping("/{id}")
    public String clientDetails(@PathVariable Long id, Model model) {
        Optional<Client> client = clientService.findById(id);
        if (client.isPresent()) {
            model.addAttribute("client", client.get());
            model.addAttribute("reservations", reservationService.findByClient(id));
            model.addAttribute("paiements", paiementService.findByClient(id));
            return "clients/details";
        }
        return "redirect:/clients";
    }

    @GetMapping("/search")
    public String searchClients(@RequestParam("nom") String nom, Model model) {
        model.addAttribute("clients", clientService.searchByName(nom));
        return "clients/list";
    }
}
