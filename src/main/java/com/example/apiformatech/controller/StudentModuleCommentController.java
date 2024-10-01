package com.example.apiformatech.controller;

import com.example.apiformatech.model.StudentModuleComment;
import com.example.apiformatech.service.StudentModuleCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class StudentModuleCommentController {

    private final StudentModuleCommentService studentModuleCommentService;

    public StudentModuleCommentController(StudentModuleCommentService studentModuleCommentService) {
        this.studentModuleCommentService = studentModuleCommentService;
    }

    // Ajouter un commentaire et une note
    @PostMapping
    public ResponseEntity<StudentModuleComment> addComment(@RequestBody StudentModuleComment comment) {
        StudentModuleComment savedComment = studentModuleCommentService.saveComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }


    // Récupérer tous les commentaires et notes
    @GetMapping
    public ResponseEntity<List<StudentModuleComment>> getAllComments() {
        List<StudentModuleComment> comments = studentModuleCommentService.getAllComments();
        if (comments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(comments);
    }


    // Récupérer un commentaire par ID
    @GetMapping("/{id}")
    public ResponseEntity<StudentModuleComment> getCommentById(@PathVariable Long id) {
        return studentModuleCommentService.getCommentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    // Mettre à jour un commentaire
    @PutMapping("/{id}")
    public ResponseEntity<StudentModuleComment> updateComment(@PathVariable Long id, @RequestBody StudentModuleComment comment) {
        try {
            StudentModuleComment updatedComment = studentModuleCommentService.updateComment(id, comment);
            return ResponseEntity.ok(updatedComment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    // Supprimer un commentaire par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        try {
            studentModuleCommentService.deleteComment(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}

