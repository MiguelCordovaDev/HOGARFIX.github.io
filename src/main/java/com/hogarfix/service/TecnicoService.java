package com.hogarfix.service;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.hogarfix.model.Tecnico;
import com.hogarfix.model.Rol;
import com.hogarfix.repository.TecnicoRepository;
import com.hogarfix.repository.RolRepository;
import com.hogarfix.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    public Tecnico registrarTecnico(Tecnico tecnico) {
        try {
            if (tecnico.getUsuario() == null || tecnico.getUsuario().getEmail() == null) {
                log.error("Usuario o email inválido al intentar registrar técnico");
                throw new RuntimeException("Usuario o email inválido");
            }

            if (tecnicoRepository.existsByUsuario_Email(tecnico.getUsuario().getEmail())) {
                log.warn("Intento de registro con email duplicado: {}", tecnico.getUsuario().getEmail());
                throw new RuntimeException("El correo ya está registrado");
            }

            // Asignar rol TECNICO por defecto (crear si no existe)
            Rol rol = rolRepository.findByNombre("TECNICO")
                    .orElseGet(() -> rolRepository.save(Rol.builder()
                            .nombre("TECNICO")
                            .descripcion("Rol por defecto para técnicos")
                            .build()));

            tecnico.getUsuario().setRol(rol);
            tecnico.getUsuario().setPassword(passwordEncoder.encode(tecnico.getUsuario().getPassword()));

            // Guardar usuario primero para evitar TransientPropertyValueException
            var usuarioGuardado = usuarioRepository.save(tecnico.getUsuario());
            tecnico.setUsuario(usuarioGuardado);
            Tecnico saved = tecnicoRepository.save(tecnico);
            log.info("Técnico registrado exitosamente en BD: email={}, id={}", tecnico.getUsuario().getEmail(), saved.getIdTecnico());
            return saved;
        } catch (Exception e) {
            log.error("Error al registrar técnico en BD: email={}, causa={}", 
                tecnico.getUsuario() != null ? tecnico.getUsuario().getEmail() : "desconocido", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Tecnico> listarTecnicos() {
        try {
            var tecnicos = tecnicoRepository.findAll();
            // Inicializar asociaciones perezosas (usuario, categorias, direccion)
            for (Tecnico t : tecnicos) {
                if (t.getUsuario() != null) {
                    t.getUsuario().getEmail();
                }
                if (t.getCategorias() != null) {
                    t.getCategorias().size();
                }
                if (t.getDireccion() != null) {
                    t.getDireccion().getCiudad();
                }
            }
            log.debug("Listando {} técnicos de BD", tecnicos.size());
            return tecnicos;
        } catch (Exception e) {
            log.error("Error al listar técnicos de BD", e);
            throw e;
        }
    }

    public Optional<Tecnico> buscarPorId(Long id) {
        try {
            return tecnicoRepository.findById(id);
        } catch (Exception e) {
            log.error("Error al buscar técnico por id en BD: id={}", id, e);
            throw e;
        }
    }

    public List<Tecnico> buscarPorCategoria(String categoria) {
        try {
            log.debug("Buscando técnicos por categoría: {}", categoria);
            return tecnicoRepository.findTecnicosByCategoriaNombre(categoria);
        } catch (Exception e) {
            log.error("Error al buscar técnicos por categoría: {}", categoria, e);
            throw e;
        }
    }

    public Optional<Tecnico> autenticar(String email, String password) {
        try {
            var resultado = tecnicoRepository.findByUsuario_Email(email)
                    .filter(t -> passwordEncoder.matches(password, t.getUsuario().getPassword()));
            
            if (resultado.isPresent()) {
                log.info("Autenticación exitosa de técnico: {}", email);
            } else {
                log.warn("Fallo de autenticación para técnico: {}", email);
            }
            return resultado;
        } catch (Exception e) {
            log.error("Error durante autenticación de técnico: {}", email, e);
            throw e;
        }
    }

    public Tecnico obtenerPorEmail(String email) {
        try {
            return tecnicoRepository.findByUsuario_Email(email)
                    .orElseThrow(() -> new RuntimeException("No se encontró el técnico con email: " + email));
        } catch (Exception e) {
            log.error("Error al obtener técnico por email: {}", email, e);
            throw e;
        }
    }

}