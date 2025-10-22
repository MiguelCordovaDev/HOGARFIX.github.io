package com.hogarfix.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hogarfix.model.Role;
import com.hogarfix.dto.ERole;
import com.hogarfix.repository.RoleRepository;

import java.util.Arrays;

@Configuration
public class RoleInitializationConfig {

    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            // Itera sobre todos los valores del enum ERole y los guarda si no existen
            Arrays.stream(ERole.values()).forEach(roleName -> {
                if (roleRepository.findByName(roleName) == null) {
                    Role role = new Role();
                    role.setName(roleName);
                    roleRepository.save(role);
                    System.out.println("Role inicializado: " + roleName);
                }
            });
        };
    }
}