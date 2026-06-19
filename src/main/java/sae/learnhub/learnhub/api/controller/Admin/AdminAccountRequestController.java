package sae.learnhub.learnhub.api.controller.Admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sae.learnhub.learnhub.api.dto.account.AccountRequestResponse;
import sae.learnhub.learnhub.api.dto.account.AccountRequestStatusRequest;
import sae.learnhub.learnhub.api.mapper.AccountRequestMapper;
import sae.learnhub.learnhub.application.account.AccountRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/account-requests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAccountRequestController {

    private final AccountRequestService service;
    private final AccountRequestMapper mapper;

    @GetMapping
    public ResponseEntity<List<AccountRequestResponse>> findAll(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(
                service.findAll(status).stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountRequestResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(service.findById(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AccountRequestResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AccountRequestStatusRequest request) {
        return ResponseEntity.ok(
                mapper.toResponse(service.updateStatus(id, request.statut())));
    }
}
