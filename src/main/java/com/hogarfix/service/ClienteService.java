package com.hogarfix.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hogarfix.dto.ERole;
import com.hogarfix.model.Cliente;
import com.hogarfix.model.Role;
import com.hogarfix.repository.ClienteRepository;
import com.hogarfix.repository.RoleRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder; 

    @Autowired
    private RoleRepository roleRepository;

    /**
     * 1. Verifica si el email ya existe.
     * 2. Encripta la contraseña antes de guardar.
     * 3. Guarda el nuevo cliente en la BD.
     */
    public Cliente saveCliente(Cliente cliente) {
        // Encriptar la contraseña (¡Obligatorio para Spring Security!)

        Role roleCliente = roleRepository.findByName(ERole.ROLE_CLIENTE);
        if (roleCliente == null) {
            throw new RuntimeException("Error: El rol ROLE_CLIENTE no existe en la BD. Ejecuta la inicialización de roles.");
        }
        // Asigna el rol al cliente
        cliente.setRoles(Collections.singletonList(roleCliente));
        
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