package com.hogarfix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hogarfix.model.Cliente;
import com.hogarfix.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder; // Necesario para encriptar la contraseña

    /**
     * 1. Verifica si el email ya existe.
     * 2. Encripta la contraseña antes de guardar.
     * 3. Guarda el nuevo cliente en la BD.
     */
    public Cliente saveCliente(Cliente cliente) {
        // Encriptar la contraseña (¡Obligatorio para Spring Security!)
        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        
        // Guardar en la base de datos
        return clienteRepository.save(cliente);
    }
    
    /**
     * Verifica si un email ya existe antes de registrarlo.
     */
    public boolean existsByEmail(String email) {
        return clienteRepository.findByEmail(email).isPresent();
    }
}