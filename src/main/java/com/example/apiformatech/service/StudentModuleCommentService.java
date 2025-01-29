package com.example.apiformatech.service;

import com.example.apiformatech.exception.ResourceNotFoundException;
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
        if(studentModuleCommentRepository.existsByStudentAndTrainerAndSessionModule(comment.getStudent(), comment.getTrainer(), comment.getSessionModule())){
            throw new ResourceNotFoundException("Le commentaire pour cet élève sur ce module existe déjà.");
        }
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

    // Méthode pour mettre à jour un commentaire et une note
    public StudentModuleComment updateComment(Long id, StudentModuleComment updatedComment) {
        StudentModuleComment existingComment = studentModuleCommentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        // Met à jour les informations du commentaire
        existingComment.setGrade(updatedComment.getGrade());
        existingComment.setComment(updatedComment.getComment());
        existingComment.setUpdatedAt(new Date()); // Met à jour la date de mise à jour

        return studentModuleCommentRepository.save(existingComment);
    }


    // Méthode pour supprimer un commentaire par son ID
    public void deleteComment(Long id) {
        if (!studentModuleCommentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Commentaire avec l'ID" + id + " n'existe pas");
        }
        studentModuleCommentRepository.deleteById(id);
    }
}

