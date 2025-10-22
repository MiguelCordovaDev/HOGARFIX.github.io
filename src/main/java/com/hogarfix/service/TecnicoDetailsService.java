package com.hogarfix.service;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hogarfix.model.Tecnico;
import com.hogarfix.repository.TecnicoRepository;

@Service
public class TecnicoDetailsService implements UserDetailsService {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        Tecnico tecnico = tecnicoRepository.findByEmail(email)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("Técnico no encontrado con email: " + email));

        // Retorna un objeto UserDetails para Spring Security
        return new org.springframework.security.core.userdetails.User(
                tecnico.getEmail(), 
                tecnico.getPassword(), 
                // *** IMPORTANTE: Aquí se deberían añadir roles específicos para Técnicos,
                // como un rol ROLE_TECNICO, para distinguirlos de los clientes en la autorización.
                Collections.emptyList() 
        );
    }
}