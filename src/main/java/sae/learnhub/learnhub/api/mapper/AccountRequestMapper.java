package sae.learnhub.learnhub.api.mapper;

import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.api.dto.account.AccountRequestCreateRequest;
import sae.learnhub.learnhub.api.dto.account.AccountRequestResponse;
import sae.learnhub.learnhub.application.account.AccountRequestService;

@Component
public class AccountRequestMapper {

    public AccountRequestService.SubmitCommand toCommand(
            AccountRequestCreateRequest request) {
        return new AccountRequestService.SubmitCommand(
                request.nom(),
                request.prenom(),
                request.email(),
                request.formation(),
                request.requestedRole(),
                request.commentaire());
    }

    public AccountRequestResponse toResponse(
            AccountRequestService.RequestResult result) {
        return new AccountRequestResponse(
                result.id(),
                result.nom(),
                result.prenom(),
                result.email(),
                result.formation(),
                result.requestedRole(),
                result.commentaire(),
                result.statut(),
                result.dateCreation(),
                result.dateTraitement(),
                result.confirmationEmailSent());
    }
}
