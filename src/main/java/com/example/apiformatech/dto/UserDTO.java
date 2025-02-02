package com.example.apiformatech.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class UserDTO {
    private final Long id;
    private final String email;
    private final String role; // On ne renvoie que le titre du rôle

    @JsonIgnoreProperties("admins")
    private final EstablishmentDTO establishment; // Référence simplifiée

    public UserDTO(Long id, String email, String role, EstablishmentDTO establishment) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.establishment = establishment;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public EstablishmentDTO getEstablishment() { return establishment; }
}

