package com.hogarfix.repository;

import com.hogarfix.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByDni(String dni);

    // Consulta personalizada para la métrica gerencial: "Usuario que solicita más servicios"
    @Query(value = "SELECT c.*, COUNT(s.id_servicio) as total_servicios " +
            "FROM tbl_cliente c JOIN tbl_servicio s ON c.id_cliente = s.id_cliente " +
            "GROUP BY c.id_cliente ORDER BY total_servicios DESC LIMIT 1",
            nativeQuery = true)
    Customer findTopSolicitante();
}
