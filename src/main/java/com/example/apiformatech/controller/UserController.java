package com.example.apiformatech.controller;

import com.example.apiformatech.model.User;
import com.example.apiformatech.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Injection par constructeur
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Crée un nouvel utilisateur.
     * @param user L'objet utilisateur à enregistrer, qui sera fourni dans le corps de la requête HTTP POST.
     * @return L'utilisateur enregistré.
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    /**
     * Récupère un utilisateur par email.
     * @param email L'email de l'utilisateur (passé dans l'URL).
     * @return L'utilisateur correspondant à l'email ou une réponse HTTP 404 si l'utilisateur n'est pas trouvé.
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère un utilisateur par ID.
     * @param id L'ID de l'utilisateur (passé dans l'URL).
     * @return L'utilisateur correspondant à l'ID ou une réponse HTTP 404 si l'utilisateur n'est pas trouvé.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère tous les utilisateurs.
     * @return La liste de tous les utilisateurs.
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}