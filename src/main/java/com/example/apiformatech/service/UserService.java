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
    // Méthode pour lister l'ensemble des users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Méthode pour récupérer un utilisateur par ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id); // Utilise le repository pour trouver l'utilisateur par ID
    }

    // Méthode pour assigner un rôle à un utilisateur
    public User assignRoleToUser(String email, String roleTitle) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByTitle(roleTitle).orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);
        return userRepository.save(user);
    }

    // Méthode pour sauvegarder un utilisateur avec ses informations et ses info personnelles
    public User updateUserAndInfo(Long id, User updatedUser, UserInfo updatedInfo) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        // Mise à jour des informations de base de User
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());

        // Si les informations personnelles (UserInfo) sont présentes, on les met à jour
        if (updatedInfo != null) {
            UserInfo existingUserInfo = existingUser.getUserInfo();

            if (existingUserInfo != null) {

                // Met à jour les informations dans l'objet UserInfo existant
                existingUserInfo.setLastname(updatedInfo.getLastname());
                existingUserInfo.setFirstname(updatedInfo.getFirstname());
                existingUserInfo.setPhone(updatedInfo.getPhone());
                existingUserInfo.setBirthdate(updatedInfo.getBirthdate());

                userInfoRepository.save(existingUserInfo); // Sauvegarde les modifications de UserInfo
            } else {
                // Si aucun UserInfo n'existe, on assigne les nouvelles informations à l'utilisateur
                updatedInfo.setUser(existingUser);
                userInfoRepository.save(updatedInfo);
                existingUser.setUserInfo(updatedInfo); // Lien entre User et UserInfo
            }
        }

        // Sauvegarde l'utilisateur avec les nouvelles informations
        return userRepository.save(existingUser);
    }


    // Méthode pour supprimer un utilisateur
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


}