package com.example.apiformatech.controller;

import com.example.apiformatech.dto.StudentModuleCommentDTO;
import com.example.apiformatech.model.StudentModuleComment;
import com.example.apiformatech.service.StudentModuleCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class StudentModuleCommentController {

    private final StudentModuleCommentService commentService;

    public StudentModuleCommentController(StudentModuleCommentService commentService) {
        this.commentService = commentService;
    }

    // Récupérer les commentaires d'un module et d'une session en fonction du rôle
    @GetMapping("/module/{moduleId}/session/{sessionId}")
    public ResponseEntity<?> getComments(
            @PathVariable Long moduleId,
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<StudentModuleCommentDTO> comments =
                    commentService.getComments(moduleId, sessionId, userDetails);
            return ResponseEntity.ok(comments);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Ajouter plusieurs notes et commentaires pour un module et une session
    @PostMapping("/module/{moduleId}/session/{sessionId}")
    public ResponseEntity<?> addComments(
            @PathVariable Long moduleId,
            @PathVariable Long sessionId,
            @RequestBody List<StudentModuleComment> comments,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<StudentModuleCommentDTO> result =
                    commentService.addComments(moduleId, sessionId, comments, userDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Modifier plusieurs notes et commentaires pour un module et une session
    @PutMapping("/module/{moduleId}/session/{sessionId}")
    public ResponseEntity<?> updateComments(
            @PathVariable Long moduleId,
            @PathVariable Long sessionId,
            @RequestBody List<StudentModuleComment> comments,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<StudentModuleCommentDTO> updatedComments =
                    commentService.updateComments(moduleId, sessionId, comments, userDetails);
            return ResponseEntity.ok(updatedComments);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Supprimer un commentaire
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            commentService.deleteComment(id, userDetails);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
