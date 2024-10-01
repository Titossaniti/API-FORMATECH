package com.example.apiformatech.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "establishments")
public class Establishment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le nom de l'établissement est obligatoire")
    @Size(min = 1, max = 100, message = "Le nom doit contenir entre 1 et 100 caractères")
    private String name;

    @NotNull(message = "La commune est obligatoire")
    @Size(min = 1, max = 50, message = "La ville doit contenir entre 1 et 50 caractères")
    private String city;

    @NotNull(message = "Le code postal est obligatoire")
    private String postalCode;

    @NotNull(message = "L'adresse est obligatoire")
    @Size(min = 1, max = 255, message = "L'adresse doit contenir entre 1 et 255 caractères")
    private String address;

    @NotNull(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Le numéro de téléphone est invalide")
    private String phone;

    @NotNull(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

