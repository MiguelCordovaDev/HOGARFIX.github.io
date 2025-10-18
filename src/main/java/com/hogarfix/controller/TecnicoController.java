package com.hogarfix.controller;

import com.hogarfix.model.Tecnico;
import com.hogarfix.service.TecnicoService;
import com.google.common.collect.ImmutableList; // Librería Google Guava
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Aplicación del principio MVC (C: Controlador)
@RestController
@RequestMapping("/api/tecnicos")
public class TecnicoController {

    private final TecnicoService tecnicoService;

    @Autowired
    public TecnicoController(TecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    /**
     * POST /api/tecnicos - Registrar un nuevo técnico
     */
    @PostMapping
    public ResponseEntity<Tecnico> registrarTecnico(@RequestBody Tecnico tecnico) {
        Tecnico nuevoTecnico = tecnicoService.guardarTecnico(tecnico);

        // Manejo de la regla de negocio definida en TDD (email duplicado)
        if (nuevoTecnico == null) {
            // Retorna un error 409 Conflict si el recurso ya existe
            return new ResponseEntity<>(HttpStatus.CONFLICT); 
        }
        return new ResponseEntity<>(nuevoTecnico, HttpStatus.CREATED);
    }

    /**
     * GET /api/tecnicos - Obtener todos los técnicos (uso de Guava)
     */
    @GetMapping
    public ResponseEntity<List<Tecnico>> obtenerTodosLosTecnicos() {
        List<Tecnico> tecnicos = tecnicoService.buscarTodos();
        
        // Uso de Google Guava: ImmutableList.copyOf (mejora la seguridad y eficiencia 
        // al evitar modificaciones accidentales de la lista)
        List<Tecnico> tecnicosInmutables = ImmutableList.copyOf(tecnicos); 
        
        if (tecnicosInmutables.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tecnicosInmutables, HttpStatus.OK);
    }

    /**
     * GET /api/tecnicos/categoria/{id} - Obtener técnicos por especialidad
     */
    @GetMapping("/categoria/{id}")
    public ResponseEntity<List<Tecnico>> obtenerTecnicosPorCategoria(@PathVariable("id") Long idCategoria) {
        List<Tecnico> tecnicos = tecnicoService.buscarPorCategoria(idCategoria);
        
        if (tecnicos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(tecnicos, HttpStatus.OK);
    }
}