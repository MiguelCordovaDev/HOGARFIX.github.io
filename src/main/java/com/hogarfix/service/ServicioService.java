package com.hogarfix.service;

import com.hogarfix.model.Customer;
import com.hogarfix.model.ServiceState;
import com.hogarfix.model.Technician;
import com.hogarfix.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicioService {
    private final ServiceRepository servicioRepository;
    private final TechnicianService tecnicoService;
    private final CustomerService clienteService;

    // --- CRUD BÁSICO ---

    public List<com.hogarfix.model.Service> findAll() {
        return servicioRepository.findAll();
    }

    // Método auxiliar para obtener el Servicio o lanzar la excepción inmediatamente
    private com.hogarfix.model.Service getServicioOrThrow(Long idServicio) {
        return servicioRepository.findById(idServicio)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado: " + idServicio));
    }

    // --- FLUJO DE NEGOCIO ---

    /**
     * 1. Lógica para que un Cliente cree un nuevo servicio.
     */
    @Transactional
    public com.hogarfix.model.Service solicitarServicio(com.hogarfix.model.Service nuevoServicio, Long idCliente) {
        nuevoServicio.setFechaSolicitud(LocalDateTime.now());
        nuevoServicio.setEstado(ServiceState.SOLICITADO);

        // ** SOLUCIÓN AL TransientObjectException **
        // 1. Obtenemos el Cliente ya existente y persistente.
        // El uso de findById dentro de una transacción ya es la forma correcta.
        // Si falla, es porque el objeto devuelto no está siendo re-asociado.

        // Mejoramos la legibilidad y manejo del error:
        Customer clienteExistente = clienteService.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente con ID " + idCliente + " no encontrado."));

        // 2. Asignamos la referencia persistente
        nuevoServicio.setCliente(clienteExistente);

        // 3. Guardamos el servicio. Como el Cliente ya existe, no se intenta persistir.
        return servicioRepository.save(nuevoServicio);
    }

    /**
     * 2. Lógica para asignar un técnico a un servicio.
     */
    @Transactional
    public com.hogarfix.model.Service asignarTecnico(Long idServicio, Long idTecnico) {
        // USO DEL MÉTODO CORREGIDO
        com.hogarfix.model.Service servicio = getServicioOrThrow(idServicio);

        Technician tecnico = tecnicoService.findById(idTecnico)
                .orElseThrow(() -> new RuntimeException("Técnico no encontrado."));

        if (servicio.getEstado() != ServiceState.SOLICITADO) {
            throw new IllegalStateException("Solo se puede asignar un técnico a servicios SOLICITADOS.");
        }

        servicio.setTecnico(tecnico);
        servicio.setEstado(ServiceState.ASIGNADO);

        return servicioRepository.save(servicio);
    }

    /**
     * 3. Lógica para que el Técnico marque el servicio como completado.
     */
    @Transactional
    public com.hogarfix.model.Service completarServicio(Long idServicio) {
        // USO DEL MÉTODO CORREGIDO
        com.hogarfix.model.Service servicio = getServicioOrThrow(idServicio);

        if (servicio.getEstado() != ServiceState.ASIGNADO && servicio.getEstado() != ServiceState.EN_PROGRESO) {
            throw new IllegalStateException("El servicio debe estar asignado o en progreso para completarse.");
        }

        servicio.setEstado(ServiceState.COMPLETADO);
        servicio.setFechaRealizacion(LocalDateTime.now());

        return servicioRepository.save(servicio);
    }

    // Aquí iría la búsqueda de servicios por estado, por cliente, por técnico, etc.
    /*public List<com.hogarfix.model.Service> findServiciosByTecnico(Long idTecnico) {
        return servicioRepository.findByTecnicoIdTecnico(idTecnico);
    }*/
}
