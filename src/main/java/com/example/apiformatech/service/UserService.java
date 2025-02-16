package com.example.apiformatech.service;

import com.example.apiformatech.dto.CreateTrainerDTO;
import com.example.apiformatech.exception.BadRequestException;
import com.example.apiformatech.exception.ResourceNotFoundException;
import com.example.apiformatech.model.*;
import com.example.apiformatech.repository.*;
import jakarta.transaction.Transactional;
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

    private final SessionUserService sessionUserService;
    private final SessionRepository sessionRepository;
    private final SessionUserRepository sessionUserRepository;

    public UserDTO mapToUserDTO(User user) {
        EstablishmentDTO establishmentDTO = null;

        if (user.getEstablishment() != null) {
            List<UserDTO> adminDTOs = user.getEstablishment().getAdmins().stream()
                    .map(admin -> new UserDTO(admin.getId(), admin.getEmail(), admin.getRole().getTitle(), null, null))
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
                establishmentDTO,
                user.getUserInfo()
        );
    }

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserInfoRepository userInfoRepository;
    private final EstablishmentRepository establishmentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, UserInfoRepository userInfoRepository, EstablishmentRepository establishmentRepository, SessionUserService sessionUserService, SessionRepository sessionRepository, SessionUserRepository sessionUserRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userInfoRepository = userInfoRepository;
        this.establishmentRepository = establishmentRepository;
        this.sessionUserService = sessionUserService;
        this.sessionRepository = sessionRepository;
        this.sessionUserRepository = sessionUserRepository;
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

    // Lire ses infos personnelles
    public UserDTO getAuthenticatedUserProfile(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getRole().getTitle(),
                user.getEstablishment() != null ? new EstablishmentDTO(
                        user.getEstablishment().getId(),
                        user.getEstablishment().getName(),
                        user.getEstablishment().getCity(),
                        null
                ) : null,
                user.getUserInfo()
        );
    }

    // Méthode pour créer un utilisateur
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé.");
        }

        if (user.getRole().getTitle().equals("SUPERADMIN") && user.getEstablishment() != null) {
            throw new BadRequestException("Un superadmin ne peut pas être rattaché à un établissement.");
        }
        // Vérifier si l'utilisateur est un ADMIN et limite la création aux élèves et formateurs
        if (user.getRole().getTitle().equals("ADMIN")) {
            if (!user.getRole().getTitle().equals("STUDENT") && !user.getRole().getTitle().equals("TRAINER")) {
                throw new BadRequestException("Un admin ne peut créer que des élèves ou des formateurs.");
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Créer des élèves pour un établissement et les assigner à une session
    @Transactional
    public List<User> createStudents(List<User> students, Long sessionId, UserDetails userDetails) {

        // Vérifier que l'utilisateur qui fait la requête est un admin ou superadmin
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!currentUser.getRole().getTitle().equals("SUPERADMIN") && !currentUser.getRole().getTitle().equals("ADMIN")) {
            throw new BadRequestException("Seuls superadmin ou admin peuvent ajouter des étudiants.");
        }

        // Vérifier que la session existe
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session non trouvée"));

        // Si l'utilisateur est un admin, il ne peut ajouter des étudiants que dans son établissement
        if (currentUser.getRole().getTitle().equals("ADMIN") &&
                !currentUser.getEstablishment().equals(session.getEstablishment())) {
            throw new BadRequestException("Vous ne pouvez ajouter des étudiants que dans votre établissement.");
        }

        return students.stream().map(student -> {

            // Vérifier si l'étudiant existe déjà
            Optional<User> existingStudent = userRepository.findByEmail(student.getEmail());

            User studentToSave;
            if (existingStudent.isPresent()) {
                studentToSave = existingStudent.get();
            } else {
                // Créer un nouvel étudiant
                student.setRole(roleRepository.findByTitle("STUDENT")
                        .orElseThrow(() -> new ResourceNotFoundException("Rôle STUDENT introuvable")));
                student.setPassword(passwordEncoder.encode(student.getPassword()));

                // Enregistrer l'utilisateur
                studentToSave = userRepository.save(student);
            }

            // Vérifier si l'étudiant est déjà assigné à cette session
            if (!sessionUserRepository.existsByUserIdAndSessionId(studentToSave.getId(), sessionId)) {
                // Ajouter l'étudiant à la session
                SessionUser sessionUser = new SessionUser();
                sessionUser.setUser(studentToSave);
                sessionUser.setSession(session);
                sessionUserRepository.save(sessionUser);
            }

            return studentToSave;
        }).toList();
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
        return userRepository.findById(id);
    }

    // Méthode pour assigner un rôle à un utilisateur
    public User assignRoleToUser(String email, String roleTitle) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role role = roleRepository.findByTitle(roleTitle).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        user.setRole(role);
        return userRepository.save(user);
    }

    // Méthode pour mettre à jour un utilisateur et ses informations personnelles
    @Transactional
    public User updateUserAndInfo(Long id, User updatedUser, UserInfo updatedInfo, UserDetails userDetails) {

        // Récupérer l'utilisateur connecté
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Récupérer l'utilisateur à modifier
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        // Vérification des permissions
        if (currentUser.getRole().getTitle().equals("ADMIN")) {
            // Un ADMIN ne peut pas modifier un autre ADMIN ou un SUPERADMIN
            if (existingUser.getRole().getTitle().equals("ADMIN") || existingUser.getRole().getTitle().equals("SUPERADMIN")) {
                throw new BadRequestException("Un admin ne peut pas modifier un autre admin ou un superadmin.");
            }
        } else if (currentUser.getRole().getTitle().equals("STUDENT") || currentUser.getRole().getTitle().equals("TRAINER")) {
            // Un étudiant ou formateur ne peut modifier que son propre profil
            if (!currentUser.getId().equals(existingUser.getId())) {
                throw new BadRequestException("Vous ne pouvez modifier que votre propre profil.");
            }
        }

        // Mise à jour des informations de base de User
        existingUser.setEmail(updatedUser.getEmail());

        // Vérifier si un nouveau mot de passe a été fourni
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            if (!updatedUser.getPassword().equals(existingUser.getPassword())) {
                // Hacher le mot de passe avant de le sauvegarder
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
        }

        // Mise à jour ou création de UserInfo
        if (updatedInfo != null) {
            UserInfo existingUserInfo = existingUser.getUserInfo();
            if (existingUserInfo != null) {
                existingUserInfo.setLastname(updatedInfo.getLastname());
                existingUserInfo.setFirstname(updatedInfo.getFirstname());
                existingUserInfo.setPhone(updatedInfo.getPhone());
                existingUserInfo.setBirthdate(updatedInfo.getBirthdate());
            } else {
                updatedInfo.setUser(existingUser);
                existingUser.setUserInfo(updatedInfo);
            }
            userInfoRepository.save(existingUser.getUserInfo());
        }

        return userRepository.save(existingUser);
    }

    // Méthode pour supprimer un utilisateur
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur avec l'ID " + id + " n'existe pas");
        }
        userRepository.deleteById(id);
    }

    // Create pour l'Admin, car il doit avoir un establishment lié obligatoirement
    public User createAdmin(User admin, Long establishmentId, UserDetails userDetails) {
        // Vérifier si le mot de passe est null
        if (admin.getPassword() == null || admin.getPassword().isEmpty()) {
            throw new BadRequestException("Un mot de passe doit être enregistré.");
        }

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Établissement non trouvé"));

        if (!currentUser.getRole().getTitle().equals("SUPERADMIN") &&
                (currentUser.getEstablishment() == null || !currentUser.getEstablishment().getId().equals(establishmentId))) {
            throw new BadRequestException("Vous ne pouvez créer un admin que pour votre propre établissement.");
        }

        admin.setRole(roleRepository.findByTitle("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Rôle ADMIN introuvable")));
        admin.setEstablishment(establishment);

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        return userRepository.save(admin);
    }

    @Transactional
    public User createTrainer(CreateTrainerDTO trainerDTO, UserDetails userDetails) {
        User adminOrSuperadmin = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!adminOrSuperadmin.getRole().getTitle().equals("SUPERADMIN") &&
                !adminOrSuperadmin.getRole().getTitle().equals("ADMIN")) {
            throw new BadRequestException("Seuls les administrateurs et superadmin peuvent créer un formateur.");
        }

        if (userRepository.existsByEmail(trainerDTO.getEmail())) {
            throw new BadRequestException("Un utilisateur avec cet email existe déjà.");
        }

        User trainer = new User();
        trainer.setEmail(trainerDTO.getEmail());
        trainer.setPassword(passwordEncoder.encode(trainerDTO.getPassword()));
        trainer.setRole(roleRepository.findByTitle("TRAINER")
                .orElseThrow(() -> new ResourceNotFoundException("Rôle TRAINER introuvable")));

        userRepository.save(trainer);

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstname(trainerDTO.getUserInfo().getFirstname());
        userInfo.setLastname(trainerDTO.getUserInfo().getLastname());
        userInfo.setPhone(trainerDTO.getUserInfo().getPhone());
        userInfo.setBirthdate(trainerDTO.getUserInfo().getBirthdate());
        userInfo.setUser(trainer);

        userInfoRepository.save(userInfo);

        trainer.setUserInfo(userInfo);
        return userRepository.save(trainer);
    }



    public List<User> getUsersByRole(String roleTitle) {
        return userRepository.findByRoleTitle(roleTitle);
    }

}