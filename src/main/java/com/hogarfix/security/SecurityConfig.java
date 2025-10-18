package com.hogarfix.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Clase de configuración para definir beans relacionados con la seguridad,
 * como el codificador de contraseñas (PasswordEncoder).
 */
@Configuration
public class SecurityConfig {

     @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilita CSRF (es común deshabilitarlo en APIs REST)
            .csrf(csrf -> csrf.disable()) 
            
            // 2. Define reglas de autorización de peticiones
            .authorizeHttpRequests(authorize -> authorize
                // Rutas Estáticas y de la Web (Públicas)
                .requestMatchers("/", "/home", "/css/**", "/js/**", "/images/**", "/index.html","/busqueda.html","/register","/login","/busqueda","/registro.html").permitAll() 
                
                // RUTAS DE API (TODO el que empiece con /api/** es público)
                // Esto incluye /api/tecnicos, /api/clientes, etc.
                .requestMatchers("/api/**").permitAll()
                
                // Cualquier otra petición que no coincida con las anteriores, debe estar autenticada.
                // Es buena práctica dejar esta como última línea de autorización
                .anyRequest().authenticated()
            )
            
            // 3. Configura el formulario de login 
            .formLogin(form -> form
                // Si tienes una página de login.html, puedes usar loginPage("/login.html")
                .loginPage("/login") 
                .permitAll()
            )
            
            // 4. Configura el logout
            .logout(logout -> logout
                .permitAll());
                
        return http.build();
    }
    
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
