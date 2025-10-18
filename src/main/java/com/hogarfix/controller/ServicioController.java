package com.hogarfix.controller;

import com.hogarfix.model.Servicio;
import com.hogarfix.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Controlador para gestionar las solicitudes de servicio (MVC)
@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    @Autowired
    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    /**
     * POST /api/servicios - Crea una nueva solicitud de servicio por parte de un cliente.
     */
    @PostMapping
    public ResponseEntity<Servicio> solicitarServicio(@RequestBody Servicio servicio) {
        // Validación básica: asegura que idCliente y idCategoria estén presentes
        if (servicio.getIdCliente() == null || servicio.getIdCategoria() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        Servicio nuevaSolicitud = servicioService.solicitarServicio(servicio);
        return new ResponseEntity<>(nuevaSolicitud, HttpStatus.CREATED);
    }
    
    /**
     * PATCH /api/servicios/{servicioId}/asignar - Asigna un técnico a un servicio.
     * (Usado por un administrador o sistema de asignación)
     */
    @PatchMapping("/{servicioId}/asignar")
    public ResponseEntity<Servicio> asignarTecnico(
            @PathVariable Long servicioId,
            @RequestBody Map<String, Long> payload) {
        
        Long tecnicoId = payload.get("idTecnico");
        
        if (tecnicoId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Servicio servicioActualizado = servicioService.asignarTecnico(servicioId, tecnicoId);

        if (servicioActualizado == null) {
             // 404 si el servicio no existe, o 409 si el estado no permite la asignación
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
        }
        return new ResponseEntity<>(servicioActualizado, HttpStatus.OK);
    }

    /**
     * GET /api/servicios/cliente/{idCliente} - Obtiene el historial de servicios de un cliente.
     */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Servicio>> obtenerHistorialPorCliente(@PathVariable Long idCliente) {
        List<Servicio> historial = servicioService.obtenerHistorialPorCliente(idCliente);
        
        if (historial.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(historial, HttpStatus.OK);
    }
}