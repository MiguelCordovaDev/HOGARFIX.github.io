package com.hogarfix.repository;

import com.hogarfix.model.Pago;
import com.hogarfix.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByServicio(Servicio servicio);
    java.util.List<Pago> findByCliente(com.hogarfix.model.Cliente cliente);
}
