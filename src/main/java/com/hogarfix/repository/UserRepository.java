package com.hogarfix.repository;

import com.hogarfix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Método clave para Spring Security
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
