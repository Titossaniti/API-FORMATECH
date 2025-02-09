package com.example.apiformatech.dto;

import com.example.apiformatech.model.UserInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

    public class UserDTO {
        private final Long id;
        private final String email;
        private final String role;

        @JsonIgnoreProperties("admins")
        private final EstablishmentDTO establishment;

        private final UserInfo userInfo;

        public UserDTO(Long id, String email, String role, EstablishmentDTO establishment, UserInfo userInfo) {
            this.id = id;
            this.email = email;
            this.role = role;
            this.establishment = establishment;
            this.userInfo = userInfo;
        }

        public Long getId() { return id; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public EstablishmentDTO getEstablishment() { return establishment; }
        public UserInfo getUserInfo() { return userInfo; }
    }
