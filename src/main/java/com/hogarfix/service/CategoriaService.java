package com.hogarfix.service;

import com.hogarfix.model.Categoria;
import com.hogarfix.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);
    private final CategoriaRepository categoriaRepository;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Guarda una nueva categoría si no existe ya una con el mismo nombre.
     */
    public Categoria guardarCategoria(Categoria categoria) {
        if (categoriaRepository.existsByNombre(categoria.getNombre())) {
            log.warn("Error: La categoría con nombre '{}' ya existe.", categoria.getNombre());
            return null;
        }
        return categoriaRepository.save(categoria);
    }

    /**
     * Obtiene todas las categorías disponibles.
     */
    public List<Categoria> buscarTodas() {
        return categoriaRepository.findAll();
    }
    
    /**
     * Busca una categoría por su ID.
     */
    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }
}