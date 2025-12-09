package com.example.Gestion_Flotte_Automobile.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(nullable = false)
    private String prenom;

    @NotBlank(message = "Le CIN est obligatoire")
    @Column(nullable = false, unique = true)
    private String cin;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Le numéro de téléphone est invalide")
    @Column(nullable = false)
    private String telephone;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(columnDefinition = "TEXT")
    private String documents; // Pour stocker des liens ou noms de fichiers

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @lombok.ToString.Exclude
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @lombok.ToString.Exclude
    private List<Paiement> paiements;
}
