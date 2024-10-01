package com.example.apiformatech.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Mauvais identifiant ou mot de passe
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Identifiant ou mot de passe incorrect.");
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("erreur", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getDescription(false).substring(4));
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Accès refusé
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        String message = ex.getMessage().contains("Insufficient scope")
                ? "Droits insuffisants pour accéder à cette ressource."
                : "Accès refusé.";
        response.put("message", message);
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("erreur", HttpStatus.FORBIDDEN.getReasonPhrase());
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getDescription(false).substring(4));
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // Utilisateur non trouvé lors de l'authentification
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Utilisateur non trouvé.");
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("erreur", HttpStatus.NOT_FOUND.getReasonPhrase());
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getDescription(false).substring(4));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Erreurs de validation lors de la requête
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Erreur de validation des données.");
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("erreur", HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getDescription(false).substring(4));

        // Détails des erreurs de validation
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage()));
        response.put("erreurs", validationErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Erreurs liées aux paramètres illégaux
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Paramètre illégal : " + ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("erreur", HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getDescription(false).substring(4));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Donnée non trouvée
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("erreur", HttpStatus.NOT_FOUND.getReasonPhrase());
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getDescription(false).substring(4));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    // Gérer toutes les autres exceptions (erreurs inattendues)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        logger.error("Une erreur inattendue est survenue", ex);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Une erreur inattendue est survenue.");
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("erreur", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getDescription(false).substring(4));
        response.put("errorId", UUID.randomUUID().toString());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
