package com.example.Gestion_Flotte_Automobile.service.impl;

import com.example.Gestion_Flotte_Automobile.entity.Voiture;
import com.example.Gestion_Flotte_Automobile.enums.StatutVoiture;
import com.example.Gestion_Flotte_Automobile.repository.VoitureRepository;
import com.example.Gestion_Flotte_Automobile.service.VoitureService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VoitureServiceImpl implements VoitureService {

    private final VoitureRepository voitureRepository;

    public VoitureServiceImpl(VoitureRepository voitureRepository) {
        this.voitureRepository = voitureRepository;
    }

    @Override
    @Transactional
    public Voiture save(Voiture voiture) {
        return voitureRepository.save(voiture);
    }

    @Override
    @Transactional
    public Voiture update(Long id, Voiture voiture) {
        if (voitureRepository.existsById(id)) {
            voiture.setId(id);
            return voitureRepository.save(voiture);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        voitureRepository.deleteById(id);
    }

    @Override
    public Optional<Voiture> findById(Long id) {
        return voitureRepository.findById(id);
    }

    @Override
    public List<Voiture> findAll() {
        return voitureRepository.findAll();
    }

    @Override
    public List<Voiture> findByStatut(StatutVoiture statut) {
        return voitureRepository.findByStatut(statut);
    }

    @Override
    public boolean existsByImmatriculation(String immatriculation) {
        return voitureRepository.existsByImmatriculation(immatriculation);
    }

    @Override
    @Transactional
    public void mettreAJourStatutVoiture(Voiture voiture, StatutVoiture statut) {
        voiture.setStatut(statut);
        voitureRepository.save(voiture);
    }
}
