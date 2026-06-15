package sae.learnhub.learnhub.api.mapper;

import sae.learnhub.learnhub.api.dto.signalement.SignalementRequest;
import sae.learnhub.learnhub.api.dto.signalement.SignalementResponse;
import sae.learnhub.learnhub.application.signalement.SignalementService;

public final class SignalementMapper {

    private SignalementMapper() {
    }

    public static SignalementService.SignalementCommand toCommand(SignalementRequest request) {
        return new SignalementService.SignalementCommand(
                request.sujet(),
                request.description(),
                request.categorie(),
                request.pieceJointeNom(),
                request.pieceJointeUrl());
    }

    public static SignalementResponse toResponse(SignalementService.SignalementResult result) {
        return new SignalementResponse(
                result.id(),
                result.sujet(),
                result.description(),
                result.categorie(),
                result.statut(),
                result.dateEnvoi(),
                result.pieceJointeNom(),
                result.pieceJointeUrl(),
                result.auteurId(),
                result.auteurNom(),
                result.auteurPrenom(),
                result.auteurEmail(),
                result.auteurRole());
    }
}
