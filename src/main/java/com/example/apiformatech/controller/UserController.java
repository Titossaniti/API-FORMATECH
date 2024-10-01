package com.example.apiformatech.controller;

import com.example.apiformatech.model.User;
import com.example.apiformatech.model.UserInfo;
import com.example.apiformatech.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Créer un utilisateur
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }


    // Récupérer un utilisateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    // Récupérer un utilisateur par email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Récupérer tous les utilisateurs
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(users);
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
            @RequestBody User updatedUser,
            @RequestBody UserInfo updatedInfo) {

        try {
            User user = userService.updateUserAndInfo(id, updatedUser, updatedInfo);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
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