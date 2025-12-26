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

    private final com.example.Gestion_Flotte_Automobile.repository.EntretienRepository entretienRepository;
    private final com.example.Gestion_Flotte_Automobile.repository.ReservationRepository reservationRepository;

    public VoitureServiceImpl(VoitureRepository voitureRepository,
            com.example.Gestion_Flotte_Automobile.repository.EntretienRepository entretienRepository,
            com.example.Gestion_Flotte_Automobile.repository.ReservationRepository reservationRepository) {
        this.voitureRepository = voitureRepository;
        this.entretienRepository = entretienRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional
    public Voiture save(Voiture voiture) {
        Voiture saved = voitureRepository.save(voiture);

        Double coutVidange = voiture.getCoutVidange() != null ? voiture.getCoutVidange() : 0.0;
        Double coutVisite = voiture.getCoutVisiteTechnique() != null ? voiture.getCoutVisiteTechnique() : 0.0;
        Double coutAssurance = voiture.getCoutAssurance() != null ? voiture.getCoutAssurance() : 0.0;
        Double coutVignette = voiture.getCoutVignette() != null ? voiture.getCoutVignette() : 0.0;

        createAutoEntretien(saved, saved.getDateProchaineVidange(),
                com.example.Gestion_Flotte_Automobile.enums.TypeEntretien.VIDANGE,
                "Vidange planifiée lors de la création.", coutVidange);
        createAutoEntretien(saved, saved.getDateProchaineVisiteTechnique(),
                com.example.Gestion_Flotte_Automobile.enums.TypeEntretien.VISITE_TECHNIQUE,
                "Visite Technique planifiée lors de la création.", coutVisite);
        createAutoEntretien(saved, saved.getDateExpirationAssurance(),
                com.example.Gestion_Flotte_Automobile.enums.TypeEntretien.ASSURANCE,
                "Assurance enregistrée lors de la création.", coutAssurance);
        createAutoEntretien(saved, saved.getDateExpirationVignette(),
                com.example.Gestion_Flotte_Automobile.enums.TypeEntretien.VIGNETTE,
                "Vignette enregistrée lors de la création.", coutVignette);

        return saved;
    }

    @Override
    @Transactional
    public Voiture update(Long id, Voiture voiture) {
        if (voitureRepository.existsById(id)) {
            Optional<Voiture> existingOpt = voitureRepository.findById(id);
            if (existingOpt.isPresent()) {
                Voiture existing = existingOpt.get();

                createHistoryIfChanged(existing, voiture);

                existing.setImmatriculation(voiture.getImmatriculation());
                existing.setMarque(voiture.getMarque());
                existing.setModele(voiture.getModele());
                existing.setAnnee(voiture.getAnnee());
                existing.setKilometrageActuel(voiture.getKilometrageActuel());
                existing.setDateMiseEnService(voiture.getDateMiseEnService());
                existing.setStatut(voiture.getStatut());
                existing.setPrixParJour(voiture.getPrixParJour());

                existing.setDateProchaineVidange(voiture.getDateProchaineVidange());
                existing.setDateProchaineVisiteTechnique(voiture.getDateProchaineVisiteTechnique());
                existing.setDateExpirationAssurance(voiture.getDateExpirationAssurance());
                existing.setDateExpirationVignette(voiture.getDateExpirationVignette());

                existing.setCoutVidange(voiture.getCoutVidange());
                existing.setCoutVisiteTechnique(voiture.getCoutVisiteTechnique());
                existing.setCoutAssurance(voiture.getCoutAssurance());
                existing.setCoutVignette(voiture.getCoutVignette());

                System.out.println("DEBUG SERVICE SAVING: " + existing);
                System.out.println("DEBUG SERVICE SAVING COUTS: Vidange=" + existing.getCoutVidange());

                return voitureRepository.save(existing);
            }
        }
        return null;
    }

    private void createHistoryIfChanged(Voiture existing, Voiture newVoiture) {
        Double coutVidange = newVoiture.getCoutVidange() != null ? newVoiture.getCoutVidange() : 0.0;
        Double coutVisite = newVoiture.getCoutVisiteTechnique() != null ? newVoiture.getCoutVisiteTechnique() : 0.0;
        Double coutAssurance = newVoiture.getCoutAssurance() != null ? newVoiture.getCoutAssurance() : 0.0;
        Double coutVignette = newVoiture.getCoutVignette() != null ? newVoiture.getCoutVignette() : 0.0;

        if (newVoiture.getDateProchaineVidange() != null &&
                !newVoiture.getDateProchaineVidange().equals(existing.getDateProchaineVidange())) {
            createAutoEntretien(existing, newVoiture.getDateProchaineVidange(),
                    com.example.Gestion_Flotte_Automobile.enums.TypeEntretien.VIDANGE,
                    "Vidange planifiée automatiquement via mise à jour voiture.", coutVidange);
        }

        if (newVoiture.getDateProchaineVisiteTechnique() != null &&
                !newVoiture.getDateProchaineVisiteTechnique().equals(existing.getDateProchaineVisiteTechnique())) {
            createAutoEntretien(existing, newVoiture.getDateProchaineVisiteTechnique(),
                    com.example.Gestion_Flotte_Automobile.enums.TypeEntretien.VISITE_TECHNIQUE,
                    "Visite Technique planifiée automatiquement via mise à jour voiture.", coutVisite);
        }

        if (newVoiture.getDateExpirationAssurance() != null &&
                !newVoiture.getDateExpirationAssurance().equals(existing.getDateExpirationAssurance())) {
            createAutoEntretien(existing, newVoiture.getDateExpirationAssurance(),
                    com.example.Gestion_Flotte_Automobile.enums.TypeEntretien.ASSURANCE,
                    "Assurance mise à jour via formulaire voiture.", coutAssurance);
        }

        if (newVoiture.getDateExpirationVignette() != null &&
                !newVoiture.getDateExpirationVignette().equals(existing.getDateExpirationVignette())) {
            createAutoEntretien(existing, newVoiture.getDateExpirationVignette(),
                    com.example.Gestion_Flotte_Automobile.enums.TypeEntretien.VIGNETTE,
                    "Vignette mise à jour via formulaire voiture.", coutVignette);
        }
    }

    private void createAutoEntretien(Voiture voiture, java.time.LocalDate date,
            com.example.Gestion_Flotte_Automobile.enums.TypeEntretien type, String comment, Double cout) {
        if (date != null) {
            com.example.Gestion_Flotte_Automobile.entity.Entretien entretien = new com.example.Gestion_Flotte_Automobile.entity.Entretien();
            entretien.setVoiture(voiture);
            entretien.setTypeEntretien(type);
            entretien.setDateEntretien(date);
            entretien.setKilometrage(voiture.getKilometrageActuel());
            entretien.setCout(cout);
            entretien.setPrestataire("Auto (Planifié)");
            entretien.setCommentaires(comment);
            entretienRepository.save(entretien);
        }
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
        if (statut == StatutVoiture.DISPONIBLE && voiture.getStatut() != StatutVoiture.DISPONIBLE) {
            java.util.Optional<com.example.Gestion_Flotte_Automobile.entity.Reservation> activeRes = reservationRepository
                    .findByVoitureIdAndStatut(voiture.getId(),
                            com.example.Gestion_Flotte_Automobile.enums.StatutReservation.CONFIRMEE);
            if (activeRes.isPresent()) {
                com.example.Gestion_Flotte_Automobile.entity.Reservation res = activeRes.get();
                res.setStatut(com.example.Gestion_Flotte_Automobile.enums.StatutReservation.TERMINEE);
                reservationRepository.save(res);
            }
        }
        voiture.setStatut(statut);
        voitureRepository.saveAndFlush(voiture);
    }
}
