package com.example.apiformatech.controller;

import com.example.apiformatech.dto.CreateTrainerDTO;
import com.example.apiformatech.dto.UpdateUserDTO;
import com.example.apiformatech.dto.UserDTO;
import com.example.apiformatech.exception.BadRequestException;
import com.example.apiformatech.exception.ResourceNotFoundException;
import com.example.apiformatech.model.Session;
import com.example.apiformatech.model.User;
import com.example.apiformatech.service.SessionModuleService;
import com.example.apiformatech.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final SessionModuleService sessionModuleService;

    public UserController(UserService userService, SessionModuleService sessionModuleService) {
        this.userService = userService;
        this.sessionModuleService = sessionModuleService;
    }

    // Créer un utilisateur
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        if (!user.getRole().getTitle().equals("ADMIN") && user.getEstablishment() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // Créer un admin pour un établissement
    @PostMapping("/admin")
    public ResponseEntity<?> createAdmin(@RequestBody User user,
                                         @RequestParam Long establishmentId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User createdAdmin = userService.createAdmin(user, establishmentId, userDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAdmin);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Create TRAINER (only admin and super admin can do it)
    @PostMapping("/trainer")
    public ResponseEntity<?> createTrainer(
            @RequestBody CreateTrainerDTO trainerDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User trainer = userService.createTrainer(trainerDTO, userDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(trainer);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Créer des élèves pour une session
    @PostMapping("/students/{sessionId}")
    public ResponseEntity<?> createStudent(
            @RequestBody List<User> students, // Accepte une liste d'étudiants
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<User> createdStudents = userService.createStudents(students, sessionId, userDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStudents);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Récupérer ses infos personnelles
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserDTO userProfile = userService.getAuthenticatedUserProfile(userDetails);
        return ResponseEntity.ok(userProfile);
    }

    // Récupérer un utilisateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return ResponseEntity.ok(userService.mapToUserDTO(user));
    }

    // Récupérer un utilisateur par email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Récupérer tous les utilisateurs / filtrage par rôle possible
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false) String role) {
        List<User> users;

        if (role != null) {
            users = userService.getUsersByRole(role);
        } else {
            users = userService.getAllUsers();
        }
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(users);
    }

    // Récupérer les sessions d'un formateur
    @GetMapping("/{trainerId}/sessions")
    public ResponseEntity<List<Session>> getTrainerSessions(@PathVariable Long trainerId) {
        List<Session> sessions = sessionModuleService.getSessionsByTrainer(trainerId);

        if (sessions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(sessions);
    }

    // Assigner un rôle à un utilisateur
    @PutMapping("/{email}/role/{role}")
    public ResponseEntity<User> assignRoleToUser(@PathVariable String email, @PathVariable String role) {
        try {
            User updatedUser = userService.assignRoleToUser(email, role);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            // Par exemple, si le rôle est invalide
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException e) {
            // Si l'utilisateur n'est pas trouvé
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    // Mettre à jour les informations de login et les infos personnelles d'un utilisateur
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUserAndInfo(
            @PathVariable Long id,
            @RequestBody UpdateUserDTO updateUserDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            User updated = userService.updateUserAndInfo(
                    id,
                    updateUserDTO.getUser(),
                    updateUserDTO.getUserInfo(),
                    userDetails
            );
            return ResponseEntity.ok(updated);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    // Supprimer un utilisateur par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}