package com.hogarfix.repository;

import com.hogarfix.model.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// Aplicación del principio DAO (Objeto de Acceso a Datos)
// Esta interfaz define las operaciones CRUD básicas sin necesidad de código SQL.

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {

    // Método personalizado para la búsqueda de técnicos por categoría (ej. Plomero)
    List<Tecnico> findByIdCategoria(Long idCategoria);

    // Método para verificar si un email ya existe (importante para el registro)
    boolean existsByEmail(String email);

    Optional<Tecnico> findByEmail(String email);
}
