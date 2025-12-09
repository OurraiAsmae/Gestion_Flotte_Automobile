package com.example.Gestion_Flotte_Automobile.repository;

import com.example.Gestion_Flotte_Automobile.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    java.util.List<Client> findByNomContainingIgnoreCase(String nom);

    boolean existsByCin(String cin);
}
