package com.hogarfix.repository;

import com.hogarfix.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Aplicación del principio DAO (Data Access Object) para la entidad Cliente
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Método de soporte para la lógica de negocio (ej. evitar registro con email duplicado)
    boolean existsByEmail(String email);
    
    // Método para la autenticación simple
     Optional<Cliente> findByEmail(String email);
}
