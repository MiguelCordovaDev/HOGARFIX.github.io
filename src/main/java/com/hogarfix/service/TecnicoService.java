package com.hogarfix.service;

import com.hogarfix.model.Tecnico;
import com.hogarfix.repository.TecnicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TecnicoService {

    // Uso de Logback para Logging (Mejora de eficiencia y funcionalidad)
    private static final Logger log = LoggerFactory.getLogger(TecnicoService.class);

    private final TecnicoRepository tecnicoRepository;

    @Autowired
    public TecnicoService(TecnicoRepository tecnicoRepository) {
        this.tecnicoRepository = tecnicoRepository;
    }

   
    public Tecnico guardarTecnico(Tecnico tecnico) {
        log.info("Intentando guardar nuevo técnico: {}", tecnico.getEmail());

        // Regla de negocio TDD: Evitar duplicados por email
        if (tecnicoRepository.existsByEmail(tecnico.getEmail())) {
            log.warn("Error de negocio: El email {} ya está registrado.", tecnico.getEmail());
            return null;
        }

        // Regla de negocio: Asegurar que esté activo por defecto
        if (tecnico.getActive() == null) {
            tecnico.setActive(true);
        }

        return tecnicoRepository.save(tecnico);
    }

    /**
     * Busca todos los técnicos.
     */
    public List<Tecnico> buscarTodos() {
        return tecnicoRepository.findAll();
    }
    
    /**
     * Busca técnicos filtrados por su categoría (ej. Plomero).
     */
    public List<Tecnico> buscarPorCategoria(Long idCategoria) {
        log.debug("Buscando técnicos para la categoría ID: {}", idCategoria);
        return tecnicoRepository.findByIdCategoria(idCategoria);
    }
}