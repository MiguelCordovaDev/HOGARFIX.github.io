package com.hogarfix.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 🔸 Deshabilitamos CSRF solo si estás trabajando con APIs REST.
            .csrf(csrf -> csrf.disable())

            // 🔸 Reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // Recursos públicos (CSS, JS, imágenes, páginas principales)
                .requestMatchers(
                    "/", "/home", "/css/**", "/js/**", "/images/**",
                    "/registro_tecnicos", "/login_tecnicos", "/index.html",
                    "/pagos", "/register", "/login", "/registro.html",
                    "/tecnicos", "/cliente/registro"
                ).permitAll()

                // 🔒 Solo usuarios autenticados pueden acceder a /usuario y otros recursos
                .requestMatchers("/usuario/**").authenticated()

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )

            // 🔸 Configuración del login
            .formLogin(form -> form
                .loginPage("/login")              // Página de login personalizada
                .defaultSuccessUrl("/", true)     // Redirige al index tras iniciar sesión
                .permitAll()
            )

            // 🔸 Configuración del logout
            .logout(logout -> logout
    .logoutUrl("/logout")
    .logoutSuccessUrl("/")       // redirige al index
    .invalidateHttpSession(true)
    .deleteCookies("JSESSIONID")
    .permitAll()
);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}