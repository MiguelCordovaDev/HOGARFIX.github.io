package com.hogarfix.service;

import com.hogarfix.model.Qualification;
import com.hogarfix.model.ServiceState;
import com.hogarfix.model.Technician;
import com.hogarfix.repository.QualificationRepository;
import com.hogarfix.repository.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QualificationService {
    private final QualificationRepository calificacionRepository;
    private final ServicioService servicioService;
    private final TechnicianRepository tecnicoRepository;

    /**
     * Registra una nueva calificación y actualiza el promedio del técnico.
     */
    @Transactional
    public Qualification registrarCalificacion(Long idServicio, Qualification calificacion) {
        com.hogarfix.model.Service servicio = servicioService.findById(idServicio)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado."));

        // Regla de Negocio: Debe estar en estado que permita calificación
        if (servicio.getEstado() != ServiceState.COMPLETADO) {
            throw new IllegalStateException("El servicio no ha finalizado correctamente o ya fue calificado.");
        }
        if (servicio.getCalificacion() != null) {
            throw new IllegalStateException("El servicio ya tiene una calificación registrada.");
        }

        Technician tecnico = servicio.getTecnico();
        if (tecnico == null) {
            throw new IllegalStateException("El servicio no tiene un técnico asignado para calificar.");
        }

        calificacion.setServicio(servicio);
        calificacion.setTecnico(tecnico);
        calificacion.setFechaCalificacion(LocalDateTime.now());

        Qualification savedCalificacion = calificacionRepository.save(calificacion);

        // Actualizar el promedio del técnico (Principio de Consistencia)
        updateTecnicoAverageRating(tecnico);

        return savedCalificacion;
    }

    /**
     * Lógica auxiliar para recalcular la calificación promedio de un técnico.
     * Se mantiene aquí porque está intrínsecamente ligada al proceso de calificación.
     */
    private void updateTecnicoAverageRating(Technician tecnico) {
        // En un entorno de producción, esto debería hacerse con una consulta optimizada.
        List<Qualification> calificaciones = calificacionRepository.findByTecnico(tecnico);
        double newAverage = calificaciones.stream()
                .mapToInt(Qualification::getPuntuacion)
                .average()
                .orElse(0.0);

        tecnico.setCalificacionPromedio(Math.round(newAverage * 10.0) / 10.0); // Redondeo a un decimal
        tecnicoRepository.save(tecnico);
    }
}
