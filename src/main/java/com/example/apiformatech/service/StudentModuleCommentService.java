package com.example.apiformatech.service;

import com.example.apiformatech.exception.BadRequestException;
import com.example.apiformatech.exception.ResourceNotFoundException;
import com.example.apiformatech.model.SessionModule;
import com.example.apiformatech.model.StudentModuleComment;
import com.example.apiformatech.model.User;
import com.example.apiformatech.repository.SessionModuleRepository;
import com.example.apiformatech.repository.SessionUserRepository;
import com.example.apiformatech.repository.StudentModuleCommentRepository;
import com.example.apiformatech.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StudentModuleCommentService {

    private final StudentModuleCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final SessionModuleRepository sessionModuleRepository;
    private final SessionUserRepository sessionUserRepository;

    public StudentModuleCommentService(StudentModuleCommentRepository commentRepository,
                                       UserRepository userRepository,
                                       SessionModuleRepository sessionModuleRepository,
                                       SessionUserRepository sessionUserRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.sessionModuleRepository = sessionModuleRepository;
        this.sessionUserRepository = sessionUserRepository;
    }

    // Ajouter plusieurs notes et commentaires pour un module et une session
    @Transactional
    public List<StudentModuleComment> addComments(Long moduleId, Long sessionId,
                                                  List<StudentModuleComment> comments,
                                                  UserDetails userDetails) {
        User trainer = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        SessionModule sessionModule = (SessionModule) sessionModuleRepository.findByModule_IdAndSession_Id(moduleId, sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Module non lié à cette session."));

        if (!sessionModule.getTrainer().equals(trainer)) {
            throw new BadRequestException("Vous ne pouvez noter que les élèves de vos modules.");
        }

        return comments.stream().map(comment -> {
            if (!sessionUserRepository.existsByUserIdAndSessionId(comment.getStudent().getId(), sessionId)) {
                throw new BadRequestException("L'élève " + comment.getStudent().getId() + " n'est pas inscrit à cette session.");
            }

            comment.setTrainer(trainer);
            comment.setSessionModule(sessionModule);
            comment.setCreatedAt(new Date());

            return commentRepository.save(comment);
        }).toList();
    }

    // Modifier plusieurs notes et commentaires pour un module et une session
    @Transactional
    public List<StudentModuleComment> updateComments(Long moduleId, Long sessionId,
                                                     List<StudentModuleComment> comments,
                                                     UserDetails userDetails) {
        User trainer = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        return comments.stream().map(comment -> {
            StudentModuleComment existingComment = commentRepository.findById(comment.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Commentaire introuvable"));

            if (!existingComment.getTrainer().equals(trainer)) {
                throw new BadRequestException("Vous ne pouvez modifier que vos propres évaluations.");
            }

            existingComment.setGrade(comment.getGrade());
            existingComment.setComment(comment.getComment());
            existingComment.setUpdatedAt(new Date());

            return commentRepository.save(existingComment);
        }).toList();
    }

    // Récupérer les commentaires d'un module et d'une session en fonction du rôle
    public List<StudentModuleComment> getComments(Long moduleId, Long sessionId, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Vérification que le module est bien lié à la session
        SessionModule sessionModule = (SessionModule) sessionModuleRepository.findByModule_IdAndSession_Id(moduleId, sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Module non lié à cette session."));

        return switch (user.getRole().getTitle()) {
            case "SUPERADMIN" -> commentRepository.findByModuleAndSession(moduleId, sessionId);
            case "ADMIN" -> commentRepository.findByEstablishment(moduleId, sessionId, user.getEstablishment());
            case "TRAINER" -> commentRepository.findBySessionForTrainer(sessionId, user);
            case "STUDENT" -> commentRepository.findByStudentAndSessionModule(user, sessionModule);
            default -> throw new BadRequestException("Accès interdit.");
        };
    }


    // Supprimer un commentaire (uniquement si le formateur est l’auteur)
    public void deleteComment(Long id, UserDetails userDetails) {
        User trainer = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        StudentModuleComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire introuvable"));

        if (!comment.getTrainer().equals(trainer)) {
            throw new BadRequestException("Vous ne pouvez supprimer que vos propres évaluations.");
        }

        commentRepository.deleteById(id);
    }
}
