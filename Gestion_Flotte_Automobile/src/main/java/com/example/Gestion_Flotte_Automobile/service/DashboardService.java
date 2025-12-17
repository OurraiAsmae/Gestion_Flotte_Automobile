package com.example.Gestion_Flotte_Automobile.service;

public interface DashboardService {
    long countVoituresDisponibles();

    long countVoituresEnEntretien();

    long countReservationsEnCours();

    long countReservationsAujourdHui();

    long countClients();

    long countEmployes();

    double sumPaiementsDuMois();

    long countEntretiensEnAttente();

    double sumCoutEntretiensPayes();

    double sumTotalPaiements();

    double sumCoutEntretiensPayes(java.time.LocalDate startDate, java.time.LocalDate endDate);

    double sumTotalPaiements(java.time.LocalDate startDate, java.time.LocalDate endDate);
}
