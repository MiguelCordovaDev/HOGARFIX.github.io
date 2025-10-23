package com.hogarfix.service;

import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.hogarfix.model.Cliente;
import com.hogarfix.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    // Registro con validación de email duplicado
    public Cliente registrarCliente(Cliente cliente) {
        if (clienteRepository.existsByUsuario_Email(cliente.getUsuario().getEmail())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        cliente.getUsuario().setPassword(passwordEncoder.encode(cliente.getUsuario().getPassword()));
        return clienteRepository.save(cliente);
    }

    // Autenticación simple
    public Optional<Cliente> autenticar(String email, String password) {
        return clienteRepository.findByUsuario_Email(email)
                .filter(c -> passwordEncoder.matches(password, c.getUsuario().getPassword()));
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
}