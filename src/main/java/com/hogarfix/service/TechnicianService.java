package com.hogarfix.service;

import com.hogarfix.model.Technician;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TechnicianService {
    private final TechnicianService tecnicoRepository;

    // CRUD Básico
    public List<Technician> findAll() {
        return tecnicoRepository.findAll();
    }

    public Optional<Technician> findById(Long id) {
        return tecnicoRepository.findById(id);
    }

    public Technician save(Technician tecnico) {
        if (tecnico.getUsuario() == null || tecnico.getUsuario().getNombres().isEmpty()) {
            throw new IllegalArgumentException("El nombre del técnico no puede estar vacío.");
        }
        return tecnicoRepository.save(tecnico);
    }

    // Lógica para el Técnico más destacado
    public List<Technician> findTopTecnicosByValoracion() {
        return tecnicoRepository.findTop5ByOrderByCalificacionPromedioDesc();
    }

    // Lógica de gestión: Desactivar un técnico
    public void deactivateTecnico(Long idTecnico) {
        Technician tecnico = tecnicoRepository.findById(idTecnico)
                .orElseThrow(() -> new RuntimeException("Técnico no encontrado"));
        tecnico.setDisponible(false);
        tecnicoRepository.save(tecnico);
    }
}
