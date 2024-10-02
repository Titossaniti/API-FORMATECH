package com.example.apiformatech.config;

import com.example.apiformatech.model.Role;
import com.example.apiformatech.model.User;
import com.example.apiformatech.model.UserInfo;
import com.example.apiformatech.repository.RoleRepository;
import com.example.apiformatech.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Création des rôles
            if (roleRepository.findByTitle("SUPERADMIN").isEmpty()) {
                Role superadminRole = new Role();
                superadminRole.setTitle("SUPERADMIN");
                roleRepository.save(superadminRole);
            }

            if (roleRepository.findByTitle("ADMIN").isEmpty()) {
                Role adminRole = new Role();
                adminRole.setTitle("ADMIN");
                roleRepository.save(adminRole);
            }

            if (roleRepository.findByTitle("TRAINER").isEmpty()) {
                Role trainerRole = new Role();
                trainerRole.setTitle("TRAINER");
                roleRepository.save(trainerRole);
            }

            if (roleRepository.findByTitle("STUDENT").isEmpty()) {
                Role studentRole = new Role();
                studentRole.setTitle("STUDENT");
                roleRepository.save(studentRole);
            }

            // Création du superadmin
            if (userRepository.findByEmail("superadmin@mail.com").isEmpty()) {
                Role superadminRole = roleRepository.findByTitle("SUPERADMIN").get();

                User superadmin = new User();
                superadmin.setEmail("superadmin@mail.com");
                superadmin.setPassword(passwordEncoder.encode("123abc"));
                superadmin.setRole(superadminRole);

                UserInfo userInfo = new UserInfo();
                userInfo.setFirstname("super");
                userInfo.setLastname("admin");
                userInfo.setPhone("+0633333333");
                userInfo.setBirthdate(new Date());
                userInfo.setUser(superadmin);

                superadmin.setUserInfo(userInfo);
                userRepository.save(superadmin);

            }
        };
    }
}

