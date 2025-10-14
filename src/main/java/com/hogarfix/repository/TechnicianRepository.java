package com.hogarfix.repository;

import com.hogarfix.model.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    // Método de ejemplo para la métrica gerencial (Técnico más destacado)
    // Spring Data JPA lo implementa automáticamente basándose en el nombre
    List<Technician> findTop5ByOrderByCalificacionPromedioDesc();

    // Método para la gestión de técnicos
    List<Technician> findByDisponible(Boolean disponible);
}
