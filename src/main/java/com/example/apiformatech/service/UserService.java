package com.example.apiformatech.service;

import com.example.apiformatech.model.Role;
import com.example.apiformatech.model.User;
import com.example.apiformatech.model.UserInfo;
import com.example.apiformatech.repository.RoleRepository;
import com.example.apiformatech.repository.UserInfoRepository;
import com.example.apiformatech.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserInfoRepository userInfoRepository;

    // Injection des dépendances via le constructeur
    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserInfoRepository userInfoRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userInfoRepository = userInfoRepository;
    }

    // Méthode pour sauvegarder un utilisateur
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Méthode pour récupérer un utilisateur par email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Méthode pour récupérer un rôle par son nom
    public Role getRoleByName(String roleTitle) {
        return roleRepository.findByTitle(roleTitle).orElseThrow(() -> new RuntimeException("Role not found"));
    }

    // Méthode pour assigner un rôle à un utilisateur
    public User assignRoleToUser(String email, String roleTitle) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByTitle(roleTitle).orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);
        return userRepository.save(user);
    }

    // Méthode pour sauvegarder un utilisateur avec ses informations personnelles
    public User saveUserWithInfo(User user, UserInfo userInfo) {
        user.setUserInfo(userInfo);
        userInfo.setUser(user);
        userInfoRepository.save(userInfo);
        return userRepository.save(user);
    }

    // Méthode pour mettre à jour les informations personnelles d'un utilisateur
    public User updateUserInfo(Long userId, UserInfo updatedInfo) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUserInfo(updatedInfo);
        updatedInfo.setUser(user);
                userInfoRepository.save(updatedInfo);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Méthode pour récupérer un utilisateur par ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id); // Utilise le repository pour trouver l'utilisateur par ID
    }


}