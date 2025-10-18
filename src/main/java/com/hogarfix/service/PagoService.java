package com.hogarfix.service;

import com.hogarfix.model.Pago;
import com.hogarfix.model.Servicio;
import com.hogarfix.repository.PagoRepository;
import com.hogarfix.repository.ServicioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);
    private final PagoRepository pagoRepository;
    private final ServicioRepository servicioRepository; // Necesario para actualizar el estado del servicio

    @Autowired
    public PagoService(PagoRepository pagoRepository, ServicioRepository servicioRepository) {
        this.pagoRepository = pagoRepository;
        this.servicioRepository = servicioRepository;
    }

    /**
     * Procesa un nuevo pago, validando el servicio y evitando duplicados.
     * También actualiza el estado del servicio asociado (e.g., a 'PAGADO').
     */
    public Pago procesarPago(Pago pago) {
        // 1. Validar el Servicio asociado
        Optional<Servicio> optionalServicio = servicioRepository.findById(pago.getIdServicio());
        if (optionalServicio.isEmpty()) {
            log.warn("Pago rechazado: Servicio ID {} no encontrado.", pago.getIdServicio());
            return null;
        }
        
        // 2. TDD/Regla de Negocio: Evitar transacciones duplicadas
        if (pagoRepository.existsByReferenciaTransaccion(pago.getReferenciaTransaccion())) {
            log.warn("Pago rechazado: Referencia de transacción {} duplicada.", pago.getReferenciaTransaccion());
            return null;
        }

        // 3. Establecer estado y fecha
        pago.setEstado("COMPLETADO");
        
        // 4. Guardar el pago
        Pago pagoGuardado = pagoRepository.save(pago);
        
        // 5. Actualizar el estado del Servicio (Lógica transaccional)
        Servicio servicio = optionalServicio.get();
        servicio.setEstado("PAGADO"); // Nuevo estado después de recibir el pago
        servicioRepository.save(servicio);
        
        log.info("Pago exitoso registrado. Servicio ID {} actualizado a PAGADO.", servicio.getId());
        
        return pagoGuardado;
    }
}