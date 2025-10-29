package com.hogarfix.service;

import com.hogarfix.model.Pago;
import com.hogarfix.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;

    public Pago registrarPago(Pago pago) {
        return pagoRepository.save(pago);
    }

    public List<Pago> listarPagos() {
        return pagoRepository.findAll();
    }

    public List<Pago> listarPorCliente(com.hogarfix.model.Cliente cliente) {
        return pagoRepository.findByCliente(cliente);
    }

    public java.util.Optional<Pago> marcarPagado(Long id) {
        return pagoRepository.findById(id).map(p -> {
            p.setEstado("PAGADO");
            p.setFechaPago(java.time.LocalDateTime.now());
            // opcional: actualizar servicio si hace falta
            try {
                if (p.getServicio() != null) {
                    var s = p.getServicio();
                    s.setMonto(p.getMonto()); // asegurar monto
                    // no cambiar estado del servicio aquí
                }
            } catch (Exception ignored) {}
            return pagoRepository.save(p);
        });
    }
}