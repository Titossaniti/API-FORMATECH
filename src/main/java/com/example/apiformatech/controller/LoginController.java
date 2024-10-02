package com.example.apiformatech.controller;

import com.example.apiformatech.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    // Endpoint pour se connecter et obtenir un token JWT
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        String token = loginService.login(email, password);
        return ResponseEntity.ok(token); // Renvoie le token JWT dans la réponse
    }
}

