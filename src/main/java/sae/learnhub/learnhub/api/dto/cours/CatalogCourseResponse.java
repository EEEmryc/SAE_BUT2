package sae.learnhub.learnhub.api.dto.cours;

public record CatalogCourseResponse(
        Long id,
        String titre,
        String description,
        String statut,
        String profNom,
        String profPrenom,
        String profEmail,
        int nombreChapitres,
        long nombreRessources,
        String statutInscription
) {}
