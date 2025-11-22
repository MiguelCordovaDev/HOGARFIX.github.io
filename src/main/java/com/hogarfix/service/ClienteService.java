package com.hogarfix.service;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.hogarfix.model.Cliente;
import com.hogarfix.model.Rol;
import com.hogarfix.repository.ClienteRepository;
import com.hogarfix.repository.RolRepository;
import com.hogarfix.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    // Registro con validación de email duplicado
    public Cliente registrarCliente(Cliente cliente) {
        if (clienteRepository.existsByUsuario_Email(cliente.getUsuario().getEmail())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Asignar rol CLIENTE por defecto (crear si no existe)
        Rol rol = rolRepository.findByNombre("CLIENTE")
                .orElseGet(() -> rolRepository.save(Rol.builder()
                        .nombre("CLIENTE")
                        .descripcion("Rol por defecto para clientes")
                        .build()));

        cliente.getUsuario().setRol(rol);
        cliente.getUsuario().setPassword(passwordEncoder.encode(cliente.getUsuario().getPassword()));

        // Guardar el usuario primero para evitar TransientPropertyValueException
        var usuarioGuardado = usuarioRepository.save(cliente.getUsuario());
        cliente.setUsuario(usuarioGuardado);
        return clienteRepository.save(cliente);
    }

    // Autenticación simple
    public Optional<Cliente> autenticar(String email, String password) {
        return clienteRepository.findByUsuario_Email(email)
                .filter(c -> passwordEncoder.matches(password, c.getUsuario().getPassword()));
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarClientes() {
        var clientes = clienteRepository.findAll();
        // Inicializar asociaciones perezosas dentro de la transacción del servicio
        for (Cliente c : clientes) {
            if (c.getUsuario() != null) {
                // tocar una propiedad para forzar carga
                c.getUsuario().getEmail();
            }
            if (c.getDireccion() != null) {
                c.getDireccion().getCiudad();
            }
        }
        return clientes;
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByUsuario_Email(email);
    }

    public Optional<Cliente> buscarPorUsername(String username) {
        return clienteRepository.findByUsuario_Username(username);
    }

    public Cliente obtenerPorEmail(String email) {
        return clienteRepository.findByUsuario_Email(email)
                .orElseThrow(() -> new RuntimeException("No se encontró el cliente con email: " + email));
    }

    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
}