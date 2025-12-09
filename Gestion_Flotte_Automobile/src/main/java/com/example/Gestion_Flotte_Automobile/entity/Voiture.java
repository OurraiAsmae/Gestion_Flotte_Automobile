package com.example.Gestion_Flotte_Automobile.entity;

import com.example.Gestion_Flotte_Automobile.enums.StatutVoiture;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "voitures")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voiture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'immatriculation est obligatoire")
    @Column(nullable = false, unique = true)
    private String immatriculation;

    @NotBlank(message = "La marque est obligatoire")
    @Column(nullable = false)
    private String marque;

    @NotBlank(message = "Le modèle est obligatoire")
    @Column(nullable = false)
    private String modele;

    @Min(value = 1900, message = "L'année doit être valide")
    @Column(nullable = false)
    private int annee;

    @Min(value = 0, message = "Le kilométrage ne peut pas être négatif")
    @Column(nullable = false)
    private double kilometrageActuel;

    @jakarta.validation.constraints.PastOrPresent(message = "La date de mise en service ne peut pas être dans le futur")
    @Column(nullable = false)
    private LocalDate dateMiseEnService;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutVoiture statut;

    private LocalDate dateProchaineVidange;
    private LocalDate dateProchaineVisiteTechnique;
    private LocalDate dateExpirationAssurance;
    private LocalDate dateExpirationVignette;

    @OneToMany(mappedBy = "voiture", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @lombok.ToString.Exclude
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "voiture", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @lombok.ToString.Exclude
    private List<Entretien> entretiens;

    @OneToMany(mappedBy = "voiture", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @lombok.ToString.Exclude
    private List<Paiement> paiements;
}
