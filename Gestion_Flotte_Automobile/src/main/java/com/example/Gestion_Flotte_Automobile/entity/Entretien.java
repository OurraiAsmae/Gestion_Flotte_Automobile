package com.example.Gestion_Flotte_Automobile.entity;

import com.example.Gestion_Flotte_Automobile.enums.TypeEntretien;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "entretiens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entretien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "voiture_id", nullable = false)
    @NotNull(message = "La voiture est obligatoire")
    private Voiture voiture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEntretien typeEntretien;

    @NotNull(message = "La date d'entretien est obligatoire")
    @Column(nullable = false)
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEntretien;

    @Min(value = 0, message = "Le kilométrage doit être positif")
    @Column(nullable = false)
    private double kilometrage;

    @Min(value = 0, message = "Le coût doit être positif")
    @Column(nullable = false)
    private double cout;

    private String prestataire;

    @Column(columnDefinition = "TEXT")
    private String commentaires;

    @Column(nullable = false)
    private boolean paye = false;
}
