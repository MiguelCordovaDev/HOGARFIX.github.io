package com.hogarfix.repository;



import com.hogarfix.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Aplicación del principio DAO para Categoria
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    // Método para evitar categorías duplicadas
    boolean existsByNombre(String nombre);
}
