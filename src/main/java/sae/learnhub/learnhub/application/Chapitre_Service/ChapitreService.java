package sae.learnhub.learnhub.application.Chapitre_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import sae.learnhub.learnhub.application.exception.AccessDeniedException;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.domain.model.Chapitre;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.repository.IChapitreRepository;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapitreService {

    private final IChapitreRepository chapitreRepository;
    private final ICoursRepository coursRepository;
    private final IInscriptionRepository inscriptionRepository;

    // --- Structures de données internes au Service ---
    public record ChapitreCommand(String titre, String contenu, Integer ordre) {}
    
    public record ChapitreResult(Long id, String titre, String contenu, Integer ordre, 
                                 LocalDateTime dateCreation, Long coursId, String coursTitre) {}

    public ChapitreResult create(Long coursId, ChapitreCommand command, String email) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));

        if (cours.getProf() == null || !cours.getProf().getEmail().equals(email)) {
            throw new AccessDeniedException("Seul le professeur responsable peut ajouter des chapitres");
        }

        Chapitre chapitre = new Chapitre();
        chapitre.setTitre(command.titre());
        chapitre.setContenu(command.contenu());
        chapitre.setOrdre(command.ordre());
        chapitre.setDateCreation(LocalDateTime.now());
        chapitre.setCours(cours);

        Chapitre savedChapitre = chapitreRepository.save(chapitre);
        return toResult(savedChapitre);
    }

    public List<ChapitreResult> findByCoursId(Long coursId, String profEmail, String eleveEmail) {
        if (profEmail != null) {
            Cours cours = coursRepository.findById(coursId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));
            if (cours.getProf() == null || !cours.getProf().getEmail().equals(profEmail)) {
                throw new AccessDeniedException("Accès refusé : ce cours ne vous appartient pas");
            }
        }
        if (eleveEmail != null && !inscriptionRepository.existsByEleveEmailAndCoursId(eleveEmail, coursId)) {
            throw new AccessDeniedException("Accès refusé : vous n'êtes pas inscrit à ce cours");
        }
        
        List<Chapitre> chapitres = chapitreRepository.findByCoursIdOrderByOrdreAsc(coursId);
        return chapitres.stream().map(this::toResult).toList();
    }

    public ChapitreResult update(Long coursId, Long chapitreId, ChapitreCommand command, String email) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapitre introuvable"));

        if (!chapitre.getCours().getId().equals(coursId)) {
            throw new BusinessRuleException("Ce chapitre n'appartient pas à ce cours");
        }

        if (chapitre.getCours().getProf() == null || !chapitre.getCours().getProf().getEmail().equals(email)) {
            throw new AccessDeniedException("Seul le professeur responsable peut modifier les chapitres");
        }

        chapitre.setTitre(command.titre());
        chapitre.setContenu(command.contenu());
        chapitre.setOrdre(command.ordre());

        Chapitre updatedChapitre = chapitreRepository.save(chapitre);
        return toResult(updatedChapitre);
    }

    public void delete(Long coursId, Long chapitreId, String email) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapitre introuvable"));

        if (!chapitre.getCours().getId().equals(coursId)) {
            throw new BusinessRuleException("Ce chapitre n'appartient pas à ce cours");
        }

        if (chapitre.getCours().getProf() == null || !chapitre.getCours().getProf().getEmail().equals(email)) {
            throw new AccessDeniedException("Seul le professeur responsable peut supprimer les chapitres");
        }

        chapitreRepository.deleteById(chapitreId);
    }

    private ChapitreResult toResult(Chapitre chapitre) {
        return new ChapitreResult(
                chapitre.getId(),
                chapitre.getTitre(),
                chapitre.getContenu(),
                chapitre.getOrdre(),
                chapitre.getDateCreation(),
                chapitre.getCours().getId(),
                chapitre.getCours().getTitre()
        );
    }
}
