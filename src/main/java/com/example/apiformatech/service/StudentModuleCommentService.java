package com.example.apiformatech.service;

import com.example.apiformatech.dto.StudentModuleCommentDTO;
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
import java.util.stream.Collectors;

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
    public List<StudentModuleCommentDTO> addComments(Long moduleId, Long sessionId,
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

            // Vérifier qu'un commentaire n'existe pas déjà pour cet élève
            boolean commentExists = commentRepository.existsByStudentAndSessionModule(comment.getStudent(), sessionModule);
            if (commentExists) {
                throw new BadRequestException("Un commentaire existe déjà pour l'élève " + comment.getStudent().getId() + " dans ce module et cette session.");
            }

            comment.setTrainer(trainer);
            comment.setSessionModule(sessionModule);
            comment.setCreatedAt(new Date());

            StudentModuleComment savedComment = commentRepository.save(comment);
            return new StudentModuleCommentDTO(
                    savedComment.getId(),
                    savedComment.getStudent().getId(),
                    savedComment.getGrade(),
                    savedComment.getComment(),
                    savedComment.getSessionModule().getModule().getId(),
                    savedComment.getSessionModule().getSession().getId()
            );
        }).collect(Collectors.toList());
    }


    // Modifier plusieurs notes et commentaires pour un module et une session
    @Transactional
    public List<StudentModuleCommentDTO> updateComments(Long moduleId, Long sessionId,
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

            StudentModuleComment updatedComment = commentRepository.save(existingComment);
            return new StudentModuleCommentDTO(
                    updatedComment.getId(),
                    updatedComment.getStudent().getId(),
                    updatedComment.getGrade(),
                    updatedComment.getComment(),
                    updatedComment.getSessionModule().getModule().getId(),
                    updatedComment.getSessionModule().getSession().getId()
            );
        }).collect(Collectors.toList());
    }

    // Récupérer les commentaires d'un module et d'une session en fonction du rôle
    public List<StudentModuleCommentDTO> getComments(Long moduleId, Long sessionId, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        SessionModule sessionModule = (SessionModule) sessionModuleRepository.findByModule_IdAndSession_Id(moduleId, sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Module non lié à cette session."));

        List<StudentModuleComment> comments;
        switch (user.getRole().getTitle()) {
            case "SUPERADMIN":
                comments = commentRepository.findByModuleAndSessionFiltered(moduleId, sessionId);
                break;
            case "ADMIN":
                comments = commentRepository.findByEstablishmentAndModule(moduleId, sessionId, user.getEstablishment());
                break;
            case "TRAINER":
                comments = commentRepository.findByModuleSessionForTrainer(moduleId, sessionId, user);
                break;
            case "STUDENT":
                comments = commentRepository.findByStudentAndSessionModule(user, sessionModule);
                break;
            default:
                throw new BadRequestException("Accès interdit.");
        }

        return comments.stream()
                .map(comment -> new StudentModuleCommentDTO(
                        comment.getId(),
                        comment.getStudent().getId(),
                        comment.getGrade(),
                        comment.getComment(),
                        comment.getSessionModule().getModule().getId(),
                        comment.getSessionModule().getSession().getId()
                ))
                .collect(Collectors.toList());
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
