package com.hogarfix.service;

import com.hogarfix.model.Cliente;
import com.hogarfix.repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Lógica de negocio para los clientes (amas de casa)
@Service
public class ClienteService {

  @Autowired
    private ClienteRepository clienteRepository;

    // Inyección de PasswordEncoder para hashear la contraseña
    @Autowired
    private PasswordEncoder passwordEncoder;

   
    public Cliente guardarCliente(Cliente cliente) {
        
        // 1. Verificar si el email ya existe
        Optional<Cliente> existingClient = clienteRepository.findByEmail(cliente.getEmail());
        if (existingClient.isPresent()) {
            // Si ya existe, lanzamos una excepción para que el controlador la maneje y muestre un error.
            throw new IllegalStateException("El correo electrónico ya se encuentra registrado.");
        }
        
        // 2. Hashear la contraseña antes de guardar
        String contrasenaHash = passwordEncoder.encode(cliente.getPassword());
        cliente.setPassword(contrasenaHash);
        
        // 3. Guardar el cliente
        return clienteRepository.save(cliente);
    }
}