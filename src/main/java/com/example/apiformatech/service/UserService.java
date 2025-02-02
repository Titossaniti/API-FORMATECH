package com.example.apiformatech.service;

import com.example.apiformatech.exception.BadRequestException;
import com.example.apiformatech.exception.ResourceNotFoundException;
import com.example.apiformatech.model.Establishment;
import com.example.apiformatech.model.Role;
import com.example.apiformatech.model.User;
import com.example.apiformatech.model.UserInfo;
import com.example.apiformatech.repository.EstablishmentRepository;
import com.example.apiformatech.repository.RoleRepository;
import com.example.apiformatech.repository.UserInfoRepository;
import com.example.apiformatech.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.apiformatech.dto.UserDTO;
import com.example.apiformatech.dto.EstablishmentDTO;

@Service
public class UserService implements UserDetailsService {

    public UserDTO mapToUserDTO(User user) {
        EstablishmentDTO establishmentDTO = null;

        if (user.getEstablishment() != null) {
            List<UserDTO> adminDTOs = user.getEstablishment().getAdmins().stream()
                    .map(admin -> new UserDTO(admin.getId(), admin.getEmail(), admin.getRole().getTitle(), null))
                    .collect(Collectors.toList());

            establishmentDTO = new EstablishmentDTO(
                    user.getEstablishment().getId(),
                    user.getEstablishment().getName(),
                    user.getEstablishment().getCity(),
                    adminDTOs
            );
        }

        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getRole().getTitle(),
                establishmentDTO
        );
    }


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserInfoRepository userInfoRepository;
    private final EstablishmentRepository establishmentRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, UserInfoRepository userInfoRepository, EstablishmentRepository establishmentRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userInfoRepository = userInfoRepository;
        this.establishmentRepository = establishmentRepository;
    }

    // Implémentation de la méthode de UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found, email: " + email));

        // Créer un UserDetails à partir de l'utilisateur trouvé
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().getTitle())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    // Méthode pour sauvegarder un utilisateur
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé.");
        }

        if (user.getRole().getTitle().equals("SUPERADMIN") && user.getEstablishment() != null) {
            throw new BadRequestException("Un superadmin ne peut pas être rattaché à un établissement.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Méthode pour récupérer un utilisateur par email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailWithEstablishment(email);
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
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role role = roleRepository.findByTitle(roleTitle).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        user.setRole(role);
        return userRepository.save(user);
    }

    // Méthode pour sauvegarder un utilisateur avec ses informations et ses info personnelles
    public User updateUserAndInfo(Long id, User updatedUser, UserInfo updatedInfo) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

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
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur avec l'ID " + id + " n'existe pas");
        }
        userRepository.deleteById(id);
    }

    // Create Admin car il doit avoir un establishment lié obligatoirement
    public User createAdmin(User admin, Long establishmentId, UserDetails userDetails) {
        // Récupérer l'utilisateur connecté
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Établissement non trouvé"));

        // Vérification des permissions
        if (!currentUser.getRole().getTitle().equals("SUPERADMIN") &&
                (currentUser.getEstablishment() == null || !currentUser.getEstablishment().getId().equals(establishmentId))) {
            throw new RuntimeException("Vous ne pouvez créer un admin que pour votre propre établissement.");
        }

        // Création de l'Admin
        admin.setRole(roleRepository.findByTitle("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Rôle ADMIN introuvable")));
        admin.setEstablishment(establishment);
        admin.setPassword(passwordEncoder.encode(admin.getPassword())); // Hash du mot de passe

        return userRepository.save(admin);
    }

}