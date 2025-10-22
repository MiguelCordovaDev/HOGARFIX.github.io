package com.hogarfix.service;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.hogarfix.model.Cliente;
import com.hogarfix.repository.ClienteRepository;

@Service
public class ClienteDetailsService implements UserDetailsService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("Cliente no encontrado con email: " + email));

        // Retorna un objeto UserDetails (Spring Security) usando el email y la contraseña encriptada.
        return new org.springframework.security.core.userdetails.User(
                cliente.getEmail(), 
                cliente.getPassword(), 
                Collections.emptyList() // Lista de Roles/Autoridades
        );
    }
}