package com.example.apiformatech.repository;

import com.example.apiformatech.model.Role;
import com.example.apiformatech.model.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.establishment WHERE u.email = :email")
    Optional<User> findByEmailWithEstablishment(@Param("email") String email);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRoleTitle(String roleTitle);

    String role(@NotNull(message = "Le rôle est obligatoire") Role role);
}