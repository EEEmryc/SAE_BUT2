package sae.learnhub.learnhub.api.controller.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sae.learnhub.learnhub.api.dto.account.AccountRequestCreateRequest;
import sae.learnhub.learnhub.api.dto.account.AccountRequestResponse;
import sae.learnhub.learnhub.api.mapper.AccountRequestMapper;
import sae.learnhub.learnhub.application.account.AccountRequestService;

@RestController
@RequestMapping("/api/account-requests")
@RequiredArgsConstructor
public class AccountRequestController {

    private final AccountRequestService service;
    private final AccountRequestMapper mapper;

    @PostMapping
    public ResponseEntity<AccountRequestResponse> submit(
            @Valid @RequestBody AccountRequestCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(service.submit(mapper.toCommand(request))));
    }
}
