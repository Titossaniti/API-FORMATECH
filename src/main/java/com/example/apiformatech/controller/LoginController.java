package com.example.apiformatech.controller;

import com.example.apiformatech.dto.LoginRequest;
import com.example.apiformatech.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    // Endpoint pour se connecter et obtenir un token JWT
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        // Utilisation des données encapsulées dans loginRequest
        String token = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(token); // Renvoie le token JWT
    }

}

