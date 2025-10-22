package com.hogarfix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
// ... (otras importaciones)
import org.springframework.stereotype.Service;

import com.hogarfix.model.Cliente;
import com.hogarfix.model.Role; // Asegúrate de importar Role
import com.hogarfix.model.Tecnico;
import com.hogarfix.repository.ClienteRepository;
import com.hogarfix.repository.TecnicoRepository;


import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private TecnicoRepository tecnicoRepository;
    // ... (restos de la clase)

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // ... (lógica de búsqueda)

        // 1. Intentar buscar como Cliente
        if (clienteRepository.findByEmail(email).isPresent()) {
            Cliente cliente = clienteRepository.findByEmail(email).get();
            return new org.springframework.security.core.userdetails.User(
                    cliente.getEmail(), 
                    cliente.getPassword(), 
                    mapRolesToAuthorities(cliente.getRoles())); // << Usa el método actualizado
        }

        // 2. Intentar buscar como Técnico
        if (tecnicoRepository.findByEmail(email).isPresent()) {
            Tecnico tecnico = tecnicoRepository.findByEmail(email).get();
            return new org.springframework.security.core.userdetails.User(
                    tecnico.getEmail(), 
                    tecnico.getPassword(), 
                    mapRolesToAuthorities(tecnico.getRoles())); // << Usa el método actualizado
        }

        throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
    }

    // Método auxiliar para convertir Roles a GrantedAuthorities de Spring Security
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
            // Obtiene el nombre del Enum como String (Ej: "ROLE_CLIENTE")
            .map(role -> new SimpleGrantedAuthority(role.getName().name())) 
            .collect(Collectors.toList());
    }
}