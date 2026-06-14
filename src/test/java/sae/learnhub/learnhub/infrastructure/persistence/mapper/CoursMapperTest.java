package sae.learnhub.learnhub.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.infrastructure.persistence.entity.ChapitreJpaEntity;
import sae.learnhub.learnhub.infrastructure.persistence.entity.CoursJpaEntity;
import sae.learnhub.learnhub.infrastructure.persistence.entity.InscriptionJpaEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class CoursMapperTest {

    private final CoursMapper mapper = new CoursMapper(new UserMapper());

    @Test
    void updateEntity_preserveLesRelationsExistantes() {
        ChapitreJpaEntity chapitre = new ChapitreJpaEntity();
        InscriptionJpaEntity inscription = new InscriptionJpaEntity();
        List<ChapitreJpaEntity> chapitres = new ArrayList<>(List.of(chapitre));
        List<InscriptionJpaEntity> inscriptions = new ArrayList<>(List.of(inscription));

        CoursJpaEntity entity = new CoursJpaEntity();
        entity.setChapitres(chapitres);
        entity.setInscriptions(inscriptions);

        Cours cours = new Cours();
        cours.setId(42L);
        cours.setTitre("Cours mis a jour");
        cours.setDescription("Description");
        cours.setStatut("PUBLISHED");
        cours.setFichierPrincipalNom("cours.pdf");

        mapper.updateEntity(cours, entity);

        assertEquals("Cours mis a jour", entity.getTitre());
        assertEquals("cours.pdf", entity.getFichierPrincipalNom());
        assertSame(chapitres, entity.getChapitres());
        assertSame(inscriptions, entity.getInscriptions());
    }
}
