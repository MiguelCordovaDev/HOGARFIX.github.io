package com.hogarfix.service;

import java.util.List;
import java.util.Optional;
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
        if (tecnico.getUsuario() == null || tecnico.getUsuario().getEmail() == null) {
            throw new RuntimeException("Usuario o email inválido");
        }

        if (tecnicoRepository.existsByUsuario_Email(tecnico.getUsuario().getEmail())) {
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

        try {
            // Guardar usuario primero para evitar TransientPropertyValueException
            var usuarioGuardado = usuarioRepository.save(tecnico.getUsuario());
            tecnico.setUsuario(usuarioGuardado);
            Tecnico saved = tecnicoRepository.save(tecnico);
            log.info("Tecnico guardado con id={}", saved.getIdTecnico());
            return saved;
        } catch (Exception e) {
            log.error("Error persistiendo tecnico", e);
            throw e;
        }
    }

    public List<Tecnico> listarTecnicos() {
        return tecnicoRepository.findAll();
    }

    public Optional<Tecnico> buscarPorId(Long id) {
        return tecnicoRepository.findById(id);
    }

    public List<Tecnico> buscarPorCategoria(String categoria) {
        return tecnicoRepository.findTecnicosByCategoriaNombre(categoria);
    }

    public Optional<Tecnico> autenticar(String email, String password) {
        return tecnicoRepository.findByUsuario_Email(email)
                .filter(t -> passwordEncoder.matches(password, t.getUsuario().getPassword()));
    }

    public Tecnico obtenerPorEmail(String email) {
        return tecnicoRepository.findByUsuario_Email(email)
                .orElseThrow(() -> new RuntimeException("No se encontró el técnico con email: " + email));
    }

}