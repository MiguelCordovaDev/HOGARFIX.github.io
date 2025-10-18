package com.hogarfix.repository;

import com.hogarfix.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Aplicación del principio DAO para la entidad Servicio
@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    // Buscar servicios pendientes por cliente (funcionalidad de historial)
    List<Servicio> findByIdClienteOrderByFechaSolicitudDesc(Long idCliente);
    
    // Buscar servicios asignados a un técnico
    List<Servicio> findByIdTecnicoAndEstado(Long idTecnico, String estado);
}
