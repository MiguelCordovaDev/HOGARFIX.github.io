package com.hogarfix.repository;

import com.hogarfix.model.Service;
import com.hogarfix.model.ServiceState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByEstado(ServiceState estado);

    // Consulta para la métrica gerencial: "Servicio más destacado (más ingresos)"
    // Asume que el Pago está asociado al Servicio
    @Query(value = "SELECT c.nombre, SUM(p.monto) as total_ingresos " +
            "FROM tbl_servicio s JOIN tbl_categoria c ON s.id_categoria = c.id_categoria " +
            "JOIN tbl_pago p ON s.id_servicio = p.id_servicio " +
            "GROUP BY c.nombre ORDER BY total_ingresos DESC LIMIT 1",
            nativeQuery = true)
    List<Object[]> findTopCategoriaByIngresos();
}
