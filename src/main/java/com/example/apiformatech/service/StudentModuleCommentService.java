package com.example.apiformatech.service;

import com.example.apiformatech.model.StudentModuleComment;
import com.example.apiformatech.repository.StudentModuleCommentRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StudentModuleCommentService {

    private final StudentModuleCommentRepository studentModuleCommentRepository;

    // Injection des dépendances via le constructeur
    public StudentModuleCommentService(StudentModuleCommentRepository studentModuleCommentRepository) {
        this.studentModuleCommentRepository = studentModuleCommentRepository;
    }

    // Méthode pour sauvegarder un commentaire et une note
    public StudentModuleComment saveComment(StudentModuleComment comment) {
        comment.setCreatedAt(new Date());
        return studentModuleCommentRepository.save(comment);
    }

    // Méthode pour récupérer tous les commentaires et notes
    public List<StudentModuleComment> getAllComments() {
        return studentModuleCommentRepository.findAll();
    }

    // Méthode pour récupérer un commentaire par son ID
    public Optional<StudentModuleComment> getCommentById(Long id) {
        return studentModuleCommentRepository.findById(id);
    }

    // Méthode pour supprimer un commentaire par son ID
    public void deleteComment(Long id) {
        studentModuleCommentRepository.deleteById(id);
    }
}

