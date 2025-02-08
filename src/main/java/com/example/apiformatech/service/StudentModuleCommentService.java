package com.example.apiformatech.service;

import com.example.apiformatech.exception.BadRequestException;
import com.example.apiformatech.exception.ResourceNotFoundException;
import com.example.apiformatech.model.SessionModule;
import com.example.apiformatech.model.StudentModuleComment;
import com.example.apiformatech.model.User;
import com.example.apiformatech.repository.SessionModuleRepository;
import com.example.apiformatech.repository.StudentModuleCommentRepository;
import com.example.apiformatech.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentModuleCommentService {

    private final StudentModuleCommentRepository studentModuleCommentRepository;
    private final UserRepository userRepository;
    private final SessionModuleRepository sessionModuleRepository;

    // Injection des dépendances via le constructeur
    public StudentModuleCommentService(StudentModuleCommentRepository studentModuleCommentRepository, UserRepository userRepository, SessionModuleRepository sessionModuleRepository) {
        this.studentModuleCommentRepository = studentModuleCommentRepository;
        this.userRepository = userRepository;
        this.sessionModuleRepository = sessionModuleRepository;
    }

    // Méthode pour sauvegarder un commentaire et une note
    public StudentModuleComment saveComment(StudentModuleComment comment) {
        if(studentModuleCommentRepository.existsByStudentAndTrainerAndSessionModule(comment.getStudent(), comment.getTrainer(), comment.getSessionModule())){
            throw new ResourceNotFoundException("Le commentaire pour cet élève sur ce module existe déjà.");
        }
        comment.setCreatedAt(new Date());
        return studentModuleCommentRepository.save(comment);
    }

    public List<StudentModuleComment> getCommentsByRole(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String role = user.getRole().getTitle();

        if ("SUPERADMIN".equals(role)) {
            return studentModuleCommentRepository.findAll();
        }

        if ("ADMIN".equals(role)) {
            if (user.getEstablishment() == null) {
                throw new RuntimeException("L'admin n'est rattaché à aucun établissement.");
            }

            return studentModuleCommentRepository.findAll().stream()
                    .filter(comment -> comment.getSessionModule().getSession().getEstablishment().equals(user.getEstablishment()))
                    .collect(Collectors.toList());
        }

        if ("TRAINER".equals(role)) {
            List<SessionModule> trainerModules = sessionModuleRepository.findByTrainer(user);

            return studentModuleCommentRepository.findAll().stream()
                    .filter(comment -> trainerModules.contains(comment.getSessionModule()) ||
                            trainerModules.stream().anyMatch(sessionModule ->
                                    sessionModule.getSession().equals(comment.getSessionModule().getSession())))
                    .collect(Collectors.toList());
        }

        if ("STUDENT".equals(role)) {
            return studentModuleCommentRepository.findAll().stream()
                    .filter(comment -> comment.getStudent().equals(user))
                    .collect(Collectors.toList());
        }

        throw new BadRequestException("Accès refusé");
    }

    // Méthode pour récupérer tous les commentaires et notes
//    public List<StudentModuleComment> getAllComments() {
//        return studentModuleCommentRepository.findAll();
//    }

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

