package sae.learnhub.learnhub.application.ressource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sae.learnhub.learnhub.application.exception.AccessDeniedException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.repository.IChapitreRepository;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;
import sae.learnhub.learnhub.domain.repository.IRessourceRepository;

@Service
@RequiredArgsConstructor
public class ResourceFileAccessService {

    private static final String RESOURCE_URL_PREFIX = "/api/files/resources/";

    private final ICoursRepository coursRepository;
    private final IChapitreRepository chapitreRepository;
    private final IRessourceRepository ressourceRepository;
    private final IInscriptionRepository inscriptionRepository;

    public void verifyAccess(
            String key,
            String email,
            boolean isProfessor,
            boolean isStudent,
            boolean isAdmin) {
        String url = RESOURCE_URL_PREFIX + key;
        Cours cours = findCourse(url);

        if (isAdmin) {
            return;
        }
        if (isProfessor
                && cours.getProf() != null
                && email.equals(cours.getProf().getEmail())) {
            return;
        }
        if (isStudent
                && inscriptionRepository.existsByEleveEmailAndCoursIdAndStatut(
                        email,
                        cours.getId(),
                        "VALIDE")) {
            return;
        }
        throw new AccessDeniedException("Vous n'avez pas accès à ce fichier");
    }

    private Cours findCourse(String url) {
        return coursRepository.findByFichierPrincipalUrl(url)
                .or(() -> chapitreRepository.findByFichierPrincipalUrl(url)
                        .map(chapitre -> chapitre.getCours()))
                .or(() -> ressourceRepository.findByUrl(url)
                        .map(ressource -> ressource.getChapitre().getCours()))
                .orElseThrow(() -> new ResourceNotFoundException("Fichier introuvable"));
    }
}
