package com.hogarfix.service;

import com.hogarfix.model.Pay;
import com.hogarfix.model.ServiceState;
import com.hogarfix.repository.PayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PayService {
    private final PayRepository pagoRepository;
    private final ServicioService servicioService;

    /**
     * Procesa y registra un pago asociado a un servicio.
     */
    @Transactional
    public Pay registrarPago(Long idServicio, Pay pago) {
        com.hogarfix.model.Service servicio = servicioService.findById(idServicio)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado."));

        // Regla de Negocio: Solo se paga si está COMPLETADO y no tiene pago registrado
        if (servicio.getEstado() != ServiceState.COMPLETADO) {
            throw new IllegalStateException("El servicio no está en estado COMPLETADO.");
        }
        if (servicio.getPago() != null) {
            throw new IllegalStateException("El servicio ya tiene un pago registrado.");
        }

        pago.setServicio(servicio);
        pago.setFechaPago(LocalDateTime.now());
        pago.setEstado("Completado"); // Asumimos éxito por defecto

        servicio.setEstado(ServiceState.CALIFICADO); // Cambiamos el estado para permitir la calificación

        // Guardar el pago (y actualizar el servicio por cascada o guardar explícitamente)
        return pagoRepository.save(pago);
    }
}
