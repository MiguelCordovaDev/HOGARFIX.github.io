package com.hogarfix.service;

import com.hogarfix.model.Customer;
import com.hogarfix.model.Technician;
import com.hogarfix.repository.CustomerRepository;
import com.hogarfix.repository.ServiceRepository;
import com.hogarfix.repository.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagementService {
    private final CustomerRepository clienteRepository;
    private final ServiceRepository servicioRepository;
    private final TechnicianRepository tecnicoRepository;

    // 1. Métrica: Cliente más solicitante
    public Optional<Customer> getClienteMasSolicitante() {
        return Optional.ofNullable(clienteRepository.findTopSolicitante());
    }

    // 2. Métrica: Servicio más rentable (por categoría)
    public Object[] getServicioMasRentable() {
        // Retorna Nombre de Categoría y Suma de Ingresos
        List<Object[]> results = servicioRepository.findTopCategoriaByIngresos();
        return results.isEmpty() ? null : results.get(0);
    }

    // 3. Métrica: Técnico con mejor valoración (Ya implementado en TecnicoService, pero se puede centralizar)
    public Optional<Technician> getTecnicoMejorValorado() {
        List<Technician> topTecnicos = tecnicoRepository.findTop5ByOrderByCalificacionPromedioDesc();
        return topTecnicos.isEmpty() ? Optional.empty() : Optional.of(topTecnicos.get(0));
    }
}
