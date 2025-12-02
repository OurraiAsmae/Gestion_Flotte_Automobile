package com.example.Gestion_Flotte_Automobile.entity;

import com.example.Gestion_Flotte_Automobile.enums.TypePaiement;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "paiements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "voiture_id", nullable = false)
    @NotNull(message = "La voiture est obligatoire")
    private Voiture voiture;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @NotNull(message = "Le client est obligatoire")
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypePaiement typePaiement;

    @Min(value = 0, message = "Le montant doit être positif")
    @Column(nullable = false)
    private double montant;

    @NotNull(message = "La date de paiement est obligatoire")
    @Column(nullable = false)
    private LocalDate datePaiement;

    private LocalDate dateEcheance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private com.example.Gestion_Flotte_Automobile.enums.StatutPaiement statut;
}
