package com.hogarfix.repository;

import com.hogarfix.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Buscar pagos por el servicio asociado
    List<Pago> findByIdServicio(Long idServicio);
    
    // Buscar si ya existe una transacción con una referencia específica (para evitar duplicados)
    boolean existsByReferenciaTransaccion(String referenciaTransaccion);
}
