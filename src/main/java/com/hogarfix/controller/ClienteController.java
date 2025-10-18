package com.hogarfix.controller;

import com.hogarfix.model.Cliente;
import com.hogarfix.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Controlador para gestionar las amas de casa (Clientes) (MVC)
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * POST /api/clientes/registro - Permite a las amas de casa registrarse.
     */
    @PostMapping("/registro")
    public ResponseEntity<Cliente> registrarCliente(@RequestBody Cliente cliente) {
        Cliente nuevoCliente = clienteService.registrarCliente(cliente);

        // Regla de negocio TDD: Conflicto por email duplicado
        if (nuevoCliente == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409
        }
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED); // 201
    }

    /**
     * POST /api/clientes/login - Autenticación simple de cliente.
     */
    @PostMapping("/login")
    public ResponseEntity<Cliente> autenticarCliente(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        if (email == null || password == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400
        }
        
        Cliente cliente = clienteService.autenticarCliente(email, password);

        if (cliente == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401
        }
        return new ResponseEntity<>(cliente, HttpStatus.OK); // 200
    }
}
