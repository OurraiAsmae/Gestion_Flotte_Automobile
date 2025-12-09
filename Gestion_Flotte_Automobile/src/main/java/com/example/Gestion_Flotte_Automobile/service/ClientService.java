package com.example.Gestion_Flotte_Automobile.service;

import com.example.Gestion_Flotte_Automobile.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    Client save(Client client);

    Client update(Long id, Client client);

    void deleteById(Long id);

    Optional<Client> findById(Long id);

    List<Client> findAll();

    boolean existsByCin(String cin);

    List<Client> searchByName(String nom);
}
