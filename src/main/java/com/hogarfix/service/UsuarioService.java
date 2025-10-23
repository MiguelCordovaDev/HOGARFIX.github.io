package com.hogarfix.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.hogarfix.model.Usuario;
import com.hogarfix.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Registrar usuario con encriptación
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setCreatedAt(LocalDateTime.now());
        usuario.setIsActivo(true);

        return usuarioRepository.save(usuario);
    }

    // Autenticación simple
    public Optional<Usuario> autenticar(String email, String password) {
        return usuarioRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()));
    }

    // Soft delete (marcar como eliminado)
    public void eliminarUsuario(Long id, Integer usuarioModificacion) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            usuario.setIsActivo(false);
            usuario.setDeletedAt(LocalDateTime.now());
            usuario.setUsuarioModificacion(usuarioModificacion);
            usuario.setUpdatedAt(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        return usuarioRepository.findById(id)
                .map(u -> {
                    u.setUsername(usuarioActualizado.getUsername());
                    u.setEmail(usuarioActualizado.getEmail());
                    u.setUsuarioModificacion(usuarioActualizado.getUsuarioModificacion());
                    u.setDeletedAt(LocalDateTime.now());
                    return usuarioRepository.save(u);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}