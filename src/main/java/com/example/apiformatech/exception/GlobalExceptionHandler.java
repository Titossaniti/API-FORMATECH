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

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Gère les erreurs d'authentification incorrecte (mauvais identifiant ou mot de passe)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        return buildErrorResponse("Identifiant ou mot de passe incorrect.", HttpStatus.UNAUTHORIZED, request);
    }

    // Gère les accès refusés à une ressource protégée
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.warn("Accès refusé : {}", request.getDescription(false));
        return buildErrorResponse("Accès refusé. Vous n'avez pas les droits nécessaires.", HttpStatus.FORBIDDEN, request);
    }

    // Gère les cas où un utilisateur n'est pas trouvé lors de l'authentification
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        return buildErrorResponse("Utilisateur non trouvé.", HttpStatus.NOT_FOUND, request);
    }

    // Gère les erreurs de validation des données envoyées via `@RequestBody`
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> response = buildBaseResponse("Erreur de validation des données.", HttpStatus.BAD_REQUEST, request);

        // Ajoute les erreurs spécifiques aux champs
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage()));
        response.put("erreurs", validationErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Gère les erreurs de validation des paramètres `@RequestParam` et `@PathVariable`
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        Map<String, Object> response = buildBaseResponse("Erreur de validation des paramètres.", HttpStatus.BAD_REQUEST, request);

        // Ajoute les erreurs spécifiques
        Map<String, String> validationErrors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                validationErrors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        response.put("erreurs", validationErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    // Gère les erreurs métiers personnalisées (ex: email déjà utilisé)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
        logger.warn("Erreur métier : {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    // Gère les ressources non trouvées
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.warn("Ressource non trouvée : {}", request.getDescription(false));
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    // Gère les erreurs de paramètres illégaux
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return buildErrorResponse("Paramètre illégal : " + ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }


    // Gère toutes les autres exceptions non prévues (erreurs internes)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        logger.error("Une erreur inattendue est survenue", ex);

        Map<String, Object> response = buildBaseResponse("Une erreur inattendue est survenue.", HttpStatus.INTERNAL_SERVER_ERROR, request);
        response.put("errorId", UUID.randomUUID().toString()); // Ajout d'un identifiant unique pour le suivi

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ============================== MÉTHODES UTILITAIRES ============================== //

    // Construit une réponse d'erreur standardisée
    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(buildBaseResponse(message, status, request), status);
    }


    // Crée une structure de base pour toutes les réponses d'erreur
    private Map<String, Object> buildBaseResponse(String message, HttpStatus status, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", status.value());
        response.put("erreur", status.getReasonPhrase());
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getDescription(false).substring(4));
        return response;
    }
}