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

        // Auto-create maintenance for new cars if dates are set
        // Use the transient cost if provided, otherwise default to 0.0
        Double coutVidange = voiture.getCoutVidange() != null ? voiture.getCoutVidange() : 0.0;
        Double coutVisite = voiture.getCoutVisiteTechnique() != null ? voiture.getCoutVisiteTechnique() : 0.0;

        createAutoEntretien(saved, saved.getDateProchaineVidange(),
                com.example.Gestion_Flotte_Automobile.enums.TypeEntretien.VIDANGE,
                "Vidange planifiée lors de la création.", coutVidange);
        createAutoEntretien(saved, saved.getDateProchaineVisiteTechnique(),
                com.example.Gestion_Flotte_Automobile.enums.TypeEntretien.VISITE_TECHNIQUE,
                "Visite Technique planifiée lors de la création.", coutVisite);

        return saved;
    }

    @Override
    @Transactional
    public Voiture update(Long id, Voiture voiture) {
        if (voitureRepository.existsById(id)) {
            // Fetch existing car to compare dates
            Optional<Voiture> existingOpt = voitureRepository.findById(id);
            if (existingOpt.isPresent()) {
                Voiture existing = existingOpt.get();

                Double coutVidange = voiture.getCoutVidange() != null ? voiture.getCoutVidange() : 0.0;
                Double coutVisite = voiture.getCoutVisiteTechnique() != null ? voiture.getCoutVisiteTechnique() : 0.0;

                // Check for Vidange date change
                if (voiture.getDateProchaineVidange() != null &&
                        !voiture.getDateProchaineVidange().equals(existing.getDateProchaineVidange())) {
                    createAutoEntretien(voiture, voiture.getDateProchaineVidange(),
                            com.example.Gestion_Flotte_Automobile.enums.TypeEntretien.VIDANGE,
                            "Vidange planifiée automatiquement via mise à jour voiture.", coutVidange);
                }

                // Check for Visite Technique date change
                if (voiture.getDateProchaineVisiteTechnique() != null &&
                        !voiture.getDateProchaineVisiteTechnique().equals(existing.getDateProchaineVisiteTechnique())) {
                    createAutoEntretien(voiture, voiture.getDateProchaineVisiteTechnique(),
                            com.example.Gestion_Flotte_Automobile.enums.TypeEntretien.VISITE_TECHNIQUE,
                            "Visite Technique planifiée automatiquement via mise à jour voiture.", coutVisite);
                }
            }

            voiture.setId(id);
            Voiture saved = voitureRepository.save(voiture);

            // Re-run the checks to save Entretien using the SAVED voiture to avoid
            // transient instance errors
            // (Note: The robust way is to rely on 'createAutoEntretien' which should handle
            // the entity relationship.
            // In the 'save' block above, we used 'saved'. In this 'update' block, we pass
            // 'voiture' but it has the ID.
            // For safety, let's trust the logic above or rely on simpler flow.
            // The previous double-check block was a bit redundant if we trust 'voiture' has
            // ID.)

            return saved;
        }
        return null;
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
            // Check if there is an active reservation
            // Note: We use the repository directly to avoid circular dependency with
            // ReservationService
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
