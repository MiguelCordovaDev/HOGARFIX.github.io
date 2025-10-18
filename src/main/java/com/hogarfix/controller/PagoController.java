package com.hogarfix.controller;

import com.hogarfix.model.Pago;
import com.hogarfix.service.PagoService;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controlador para gestionar las transacciones de pago (MVC)
@RestController
@RequestMapping("/api")
public class PagoController {

    private final PagoService pagoService;

    @Autowired
    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

@GetMapping("/pagos")
public List<Pago> listarPagos() {
    // Lógica para devolver todos los pagos
    return Collections.emptyList();
}
    /**
     * POST /api/pagos - Procesa un pago por un servicio.
     * @param pago Incluye idServicio, monto, metodoPago y referenciaTransaccion.
     */
    @PostMapping
    public ResponseEntity<Pago> procesarPago(@RequestBody Pago pago) {
        // Validación básica de datos esenciales
        if (pago.getIdServicio() == null || pago.getMonto() == null || pago.getReferenciaTransaccion() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400
        }
        
        Pago pagoProcesado = pagoService.procesarPago(pago);

        if (pagoProcesado == null) {
            // El servicio no existe o la transacción es duplicada (409 Conflict o 404 Not Found)
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Usamos 409 para transacciones fallidas/duplicadas
        }
        
        // 201 Created
        return new ResponseEntity<>(pagoProcesado, HttpStatus.CREATED); 
    }
}