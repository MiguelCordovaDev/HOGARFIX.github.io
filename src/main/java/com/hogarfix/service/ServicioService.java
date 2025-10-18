package com.hogarfix.service;

import com.hogarfix.model.Servicio;
import com.hogarfix.repository.ServicioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

// Lógica de negocio para la gestión de solicitudes de servicios
@Service
public class ServicioService {

    private static final Logger log = LoggerFactory.getLogger(ServicioService.class);
    private final ServicioRepository servicioRepository;

    @Autowired
    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    /**
     * Crea una nueva solicitud de servicio por parte de un cliente.
     */
    public Servicio solicitarServicio(Servicio servicio) {
        log.info("Nueva solicitud de servicio para cliente ID: {} en categoría ID: {}", 
                 servicio.getIdCliente(), servicio.getIdCategoria());

        // TDD/Regla de Negocio: Asegurar que el estado inicial sea PENDIENTE
        servicio.setEstado("PENDIENTE");
        servicio.setFechaSolicitud(new Date());
        
        return servicioRepository.save(servicio);
    }
    
    /**
     * Asigna un técnico a una solicitud de servicio pendiente.
     * @return El Servicio actualizado o null si no se encuentra.
     */
    public Servicio asignarTecnico(Long servicioId, Long tecnicoId) {
        Optional<Servicio> optionalServicio = servicioRepository.findById(servicioId);
        
        if (optionalServicio.isEmpty()) {
            log.warn("Intento de asignar técnico a servicio ID {} no existente.", servicioId);
            return null;
        }
        
        Servicio servicio = optionalServicio.get();
        
        // Regla de negocio: Solo se puede asignar si está PENDIENTE
        if (!servicio.getEstado().equals("PENDIENTE")) {
            log.warn("El servicio ID {} no puede asignarse, estado actual: {}", servicioId, servicio.getEstado());
            return null;
        }
        
        // Actualizar datos
        servicio.setIdTecnico(tecnicoId);
        servicio.setEstado("ASIGNADO");
        servicio.setFechaAsignacion(new Date());
        
        log.info("Servicio ID {} asignado a técnico ID {}. Estado: ASIGNADO.", servicioId, tecnicoId);
        return servicioRepository.save(servicio);
    }
    
    /**
     * Obtiene el historial de servicios de un cliente.
     */
    public List<Servicio> obtenerHistorialPorCliente(Long idCliente) {
        return servicioRepository.findByIdClienteOrderByFechaSolicitudDesc(idCliente);
    }
}
