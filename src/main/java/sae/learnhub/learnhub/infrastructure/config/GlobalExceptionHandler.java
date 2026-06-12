package sae.learnhub.learnhub.infrastructure.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sae.learnhub.learnhub.application.exception.AccessDeniedException;
import sae.learnhub.learnhub.application.exception.AuthenticationFailedException;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return ResponseEntity.badRequest().body(Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Données invalides",
                "messages", errors
        ));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRuleException(BusinessRuleException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationFailedException(AuthenticationFailedException ex) {
        return errorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return errorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause().getMessage();
        String errorDetails = "Données invalides ou contrainte violée";
        
        if (message != null) {
            if (message.contains("utilisateur_email_key")) errorDetails = "Email déjà utilisé";
            else if (message.contains("utilisateur_role_check")) errorDetails = "Rôle invalide";
        }

        return ResponseEntity.badRequest().body(Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", errorDetails
        ));
    }

    private ResponseEntity<Map<String, Object>> errorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "error", message
        ));
    }
}
