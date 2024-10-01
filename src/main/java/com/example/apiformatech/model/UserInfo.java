package com.example.apiformatech.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Date;

@Entity
@Table(name = "user_info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Le prénom est obligatoire")
    @Size(min = 1, max = 50, message = "Le prénom doit contenir entre 1 et 50 caractères")
    private String firstname;

    @NotNull(message = "Le nom est obligatoire")
    @Size(min = 1, max = 50, message = "Le nom doit contenir entre 1 et 50 caractères")
    private String lastname;

    @NotNull(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Le numéro de téléphone est invalide")
    private String phone;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private Date birthdate;



    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public Date getBirthdate() {
        return birthdate;
    }
    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }
}
