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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    // Registro con validación de email duplicado
    public Cliente registrarCliente(Cliente cliente) {
        try {
            if (clienteRepository.existsByUsuario_Email(cliente.getUsuario().getEmail())) {
                logger.warn("Intento de registro con email duplicado: {}", cliente.getUsuario().getEmail());
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
            Cliente clienteGuardado = clienteRepository.save(cliente);
            
            logger.info("Cliente registrado exitosamente en BD: email={}, id={}", cliente.getUsuario().getEmail(), clienteGuardado.getIdCliente());
            return clienteGuardado;
        } catch (Exception e) {
            logger.error("Error al registrar cliente en BD: email={}, causa={}", 
                cliente.getUsuario().getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    // Autenticación simple
    public Optional<Cliente> autenticar(String email, String password) {
        try {
            var resultado = clienteRepository.findByUsuario_Email(email)
                    .filter(c -> passwordEncoder.matches(password, c.getUsuario().getPassword()));
            
            if (resultado.isPresent()) {
                logger.info("Autenticación exitosa de cliente: {}", email);
            } else {
                logger.warn("Fallo de autenticación para cliente: {}", email);
            }
            return resultado;
        } catch (Exception e) {
            logger.error("Error durante autenticación de cliente: {}", email, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarClientes() {
        try {
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
            logger.debug("Listando {} clientes de BD", clientes.size());
            return clientes;
        } catch (Exception e) {
            logger.error("Error al listar clientes de BD", e);
            throw e;
        }
    }

    public Optional<Cliente> buscarPorId(Long id) {
        try {
            return clienteRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error al buscar cliente por id en BD: id={}", id, e);
            throw e;
        }
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        try {
            return clienteRepository.findByUsuario_Email(email);
        } catch (Exception e) {
            logger.error("Error al buscar cliente por email en BD: email={}", email, e);
            throw e;
        }
    }

    public Optional<Cliente> buscarPorUsername(String username) {
        try {
            return clienteRepository.findByUsuario_Username(username);
        } catch (Exception e) {
            logger.error("Error al buscar cliente por username en BD: username={}", username, e);
            throw e;
        }
    }

    public Cliente obtenerPorEmail(String email) {
        try {
            return clienteRepository.findByUsuario_Email(email)
                    .orElseThrow(() -> new RuntimeException("No se encontró el cliente con email: " + email));
        } catch (Exception e) {
            logger.error("Error al obtener cliente por email: {}", email, e);
            throw e;
        }
    }

    public void eliminarCliente(Long id) {
        try {
            clienteRepository.deleteById(id);
            logger.info("Cliente eliminado de BD: id={}", id);
        } catch (Exception e) {
            logger.error("Error al eliminar cliente de BD: id={}", id, e);
            throw e;
        }
    }
}