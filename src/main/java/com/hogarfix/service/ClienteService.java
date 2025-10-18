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

    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder; // <-- INYECTADO

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) { // <-- INYECTADO EN EL CONSTRUCTOR
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    
    public Cliente registrarCliente(Cliente cliente) {
        log.info("Intentando registrar nuevo cliente: {}", cliente.getEmail());

        
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            log.warn("Error de negocio: El email {} ya está registrado para un cliente.", cliente.getEmail());
            return null;
        }
        
        // Cifrar la contraseña antes de guardarla (BCryptPasswordEncoder)
        String hashedPassword = passwordEncoder.encode(cliente.getPassword());
        cliente.setPassword(hashedPassword); // <-- SE ALMACENA EL HASH

        return clienteRepository.save(cliente);
    }
    
    /**
     * Autentica un cliente verificando el hash de la contraseña.
     */
    public Cliente autenticarCliente(String email, String rawPassword) {
        
        Optional<Cliente> clienteOptional = clienteRepository.findByEmail(email);

        if (clienteOptional.isEmpty()) {
            return null; // Usuario no encontrado
        }

        Cliente cliente = clienteOptional.get();
        
        // Verificar la contraseña proporcionada (rawPassword) contra la hasheada (cliente.getPassword())
        if (passwordEncoder.matches(rawPassword, cliente.getPassword())) { // <-- VERIFICACIÓN DEL HASH
            return cliente; // Autenticación exitosa
        }

        return null; // Contraseña incorrecta
    }
}