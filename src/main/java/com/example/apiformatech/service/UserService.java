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

    // Implément methode of UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found, email: " + email));

        // Create UserDetails from user found
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

    // Read userInfo
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

    // Create user method
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé.");
        }

        if (user.getRole().getTitle().equals("SUPERADMIN") && user.getEstablishment() != null) {
            throw new BadRequestException("Un superadmin ne peut pas être rattaché à un établissement.");
        }
        // Verify if user is ADMIN
        if (user.getRole().getTitle().equals("ADMIN")) {
            if (!user.getRole().getTitle().equals("STUDENT") && !user.getRole().getTitle().equals("TRAINER")) {
                throw new BadRequestException("Un admin ne peut créer que des élèves ou des formateurs.");
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Create students and assign them to a session
    @Transactional
    public List<User> createStudents(List<User> students, Long sessionId, UserDetails userDetails) {

        // Verify if user is admin or superadmin
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!currentUser.getRole().getTitle().equals("SUPERADMIN") && !currentUser.getRole().getTitle().equals("ADMIN")) {
            throw new BadRequestException("Seuls superadmin ou admin peuvent ajouter des étudiants.");
        }

        // Verify is the session exists
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session non trouvée"));

        // If user is admin, he can add student to its own establishment only
        if (currentUser.getRole().getTitle().equals("ADMIN") &&
                !currentUser.getEstablishment().equals(session.getEstablishment())) {
            throw new BadRequestException("Vous ne pouvez ajouter des étudiants que dans votre établissement.");
        }

        // Get the student role
        Role studentRole = roleRepository.findByTitle("STUDENT")
                .orElseThrow(() -> new ResourceNotFoundException("Rôle STUDENT introuvable"));

        return students.stream().map(student -> {

            // Verify is the user already exists
            Optional<User> existingStudent = userRepository.findByEmail(student.getEmail());

            User studentToSave;
            if (existingStudent.isPresent()) {
                studentToSave = existingStudent.get();
            } else {
                // Give student role to the added user
                student.setRole(studentRole);
                student.setPassword(passwordEncoder.encode(student.getPassword()));

                // Save user
                studentToSave = userRepository.save(student);
            }

            if (!sessionUserRepository.existsByUserIdAndSessionId(studentToSave.getId(), sessionId)) {
                SessionUser sessionUser = new SessionUser();
                sessionUser.setUser(studentToSave);
                sessionUser.setSession(session);
                sessionUserRepository.save(sessionUser);
            }

            return studentToSave;
        }).toList();
    }

    // Get user from its mail
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailWithEstablishment(email);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by its id
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Assign a role to an user
    public User assignRoleToUser(String email, String roleTitle) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role role = roleRepository.findByTitle(roleTitle).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        user.setRole(role);
        return userRepository.save(user);
    }

    // Method to update user and userInfo
    @Transactional
    public User updateUserAndInfo(Long id, User updatedUser, UserInfo updatedInfo, UserDetails userDetails) {

        // Get user currently connected
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Get the selected user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        // Verify permissions
        if (currentUser.getRole().getTitle().equals("ADMIN")) {
            // ADMIN can't modify another ADMIN or a SUPERADMIN
            if (existingUser.getRole().getTitle().equals("ADMIN") || existingUser.getRole().getTitle().equals("SUPERADMIN")) {
                throw new BadRequestException("Un admin ne peut pas modifier un autre admin ou un superadmin.");
            }
        } else if (currentUser.getRole().getTitle().equals("STUDENT") || currentUser.getRole().getTitle().equals("TRAINER")) {
            // Student and trainer can only modify their own profile
            if (!currentUser.getId().equals(existingUser.getId())) {
                throw new BadRequestException("Vous ne pouvez modifier que votre propre profil.");
            }
        }

        // update user
        existingUser.setEmail(updatedUser.getEmail());

        // Check if new password has been set
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            if (!updatedUser.getPassword().equals(existingUser.getPassword())) {
                // Hashing password
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
        }

        // Update UserInfo or create it if it doesn't exist
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

    // Delete an user
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur avec l'ID " + id + " n'existe pas");
        }
        userRepository.deleteById(id);
    }

    // Special method to create an admin, with admin role and the mandatory establishment
    public User createAdmin(User admin, Long establishmentId, UserDetails userDetails) {
        // Check if there is a password
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

    // Special method to create a trainer (only admins and super admins can do this)
    @Transactional
    public User createTrainer(CreateTrainerDTO trainerDTO, UserDetails userDetails) {
        // Check if current user exists
        User adminOrSuperadmin = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        // Check current user role
        if (!adminOrSuperadmin.getRole().getTitle().equals("SUPERADMIN") &&
                !adminOrSuperadmin.getRole().getTitle().equals("ADMIN")) {
            throw new BadRequestException("Seuls les admins et superadmins peuvent créer un formateur.");
        }
        // Check if the new added user is unique
        if (userRepository.existsByEmail(trainerDTO.getEmail())) {
            throw new BadRequestException("Un utilisateur avec cet email existe déjà.");
        }
        // Create user
        User trainer = new User();
        trainer.setEmail(trainerDTO.getEmail());
        // Hashing password
        trainer.setPassword(passwordEncoder.encode(trainerDTO.getPassword()));
        // TRAINER role added to the user if the role in find in database
        trainer.setRole(roleRepository.findByTitle("TRAINER")
                .orElseThrow(() -> new ResourceNotFoundException("Rôle TRAINER introuvable")));

        // Save the user
        userRepository.save(trainer);

        // Creating userInfo for the user
        UserInfo userInfo = new UserInfo();
        userInfo.setFirstname(trainerDTO.getUserInfo().getFirstname());
        userInfo.setLastname(trainerDTO.getUserInfo().getLastname());
        userInfo.setPhone(trainerDTO.getUserInfo().getPhone());
        userInfo.setBirthdate(trainerDTO.getUserInfo().getBirthdate());
        userInfo.setUser(trainer);
        // Saving userInfo
        userInfoRepository.save(userInfo);

        // Link user with userInfo and save the user
        trainer.setUserInfo(userInfo);
        return userRepository.save(trainer);
    }



    public List<User> getUsersByRole(String roleTitle) {
        return userRepository.findByRoleTitle(roleTitle);
    }

}