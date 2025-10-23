package com.hogarfix.service;

import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.hogarfix.model.Tecnico;
import com.hogarfix.repository.TecnicoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final PasswordEncoder passwordEncoder;

    public Tecnico registrarTecnico(Tecnico tecnico) {
        if (tecnicoRepository.existsByUsuario_Email(tecnico.getUsuario().getEmail())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        tecnico.getUsuario().setPassword(passwordEncoder.encode(tecnico.getUsuario().getPassword()));
        return tecnicoRepository.save(tecnico);
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