package com.hogarfix.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hogarfix.dto.ERole;
import com.hogarfix.model.Role;
import com.hogarfix.model.Tecnico;
import com.hogarfix.repository.RoleRepository;
import com.hogarfix.repository.TecnicoRepository;

@Service
public class TecnicoService {

    @Autowired
    private TecnicoRepository tecnicoRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder; 

    @Autowired
    private RoleRepository roleRepository;

    public Tecnico saveTecnico(Tecnico tecnico) {
        if (existsByEmail(tecnico.getEmail())) {
            throw new RuntimeException("El email ya está registrado como técnico.");
        }
        
        // 1. BUSCAR Y ASIGNAR EL ROL: ROLE_TECNICO
        Role roleTecnico = roleRepository.findByName(ERole.ROLE_TECNICO);
        if (roleTecnico == null) {
            throw new RuntimeException("Error: El rol ROLE_TECNICO no existe en la BD. Ejecuta la inicialización de roles.");
        }
        // Asigna el rol al técnico
        tecnico.setRoles(Collections.singletonList(roleTecnico));
        
        tecnico.setPassword(passwordEncoder.encode(tecnico.getPassword()));
        
        // 2. Guardar en la base de datos
        return tecnicoRepository.save(tecnico);
    }
    
    public boolean existsByEmail(String email) {
        return tecnicoRepository.findByEmail(email).isPresent();
    }
}