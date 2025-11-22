package com.hogarfix.service;

import com.hogarfix.model.Pago;
import com.hogarfix.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(readOnly = true)
    public List<Pago> listarPorCliente(com.hogarfix.model.Cliente cliente) {
        List<Pago> pagos = pagoRepository.findByCliente(cliente);
        // Inicializar todas las relaciones lazy dentro de la transacción
        for (Pago p : pagos) {
            if (p.getServicio() != null) {
                // Tocar los campos lazy de Servicio para inicializarlos
                p.getServicio().getIdServicio();
                p.getServicio().getDescripcion();
                p.getServicio().getMonto();
                
                // Inicializar Tecnico si existe
                if (p.getServicio().getTecnico() != null) {
                    p.getServicio().getTecnico().getNombres();
                    p.getServicio().getTecnico().getApellidoPaterno();
                    p.getServicio().getTecnico().getApellidoMaterno();
                }
                
                // Inicializar Categoria si existe
                if (p.getServicio().getCategoria() != null) {
                    p.getServicio().getCategoria().getNombre();
                }
            }
        }
        return pagos;
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