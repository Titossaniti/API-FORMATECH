package com.example.apiformatech.controller;

import com.example.apiformatech.model.StudentModuleComment;
import com.example.apiformatech.service.StudentModuleCommentService;
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
        return ResponseEntity.ok(savedComment);
    }

    // Récupérer tous les commentaires et notes
    @GetMapping
    public ResponseEntity<List<StudentModuleComment>> getAllComments() {
        return ResponseEntity.ok(studentModuleCommentService.getAllComments());
    }

    // Récupérer un commentaire par ID
    @GetMapping("/{id}")
    public ResponseEntity<StudentModuleComment> getCommentById(@PathVariable Long id) {
        return studentModuleCommentService.getCommentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Mettre à jour un commentaire
    @PutMapping("/{id}")
    public ResponseEntity<StudentModuleComment> updateComment(@PathVariable Long id, @RequestBody StudentModuleComment comment) {
        StudentModuleComment updatedComment = studentModuleCommentService.updateComment(id, comment);
        return ResponseEntity.ok(updatedComment);
    }

    // Supprimer un commentaire par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        studentModuleCommentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}

