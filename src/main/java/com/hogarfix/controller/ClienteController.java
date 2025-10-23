package com.hogarfix.controller;

import com.hogarfix.model.Cliente;
import com.hogarfix.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Muestra el perfil del usuario autenticado.
     */
    @GetMapping("/usuario")
    public String verPerfil(Model model, Authentication authentication) {

        // Obtener el email del usuario autenticado (Spring Security)
        String email = authentication.getName();

        // Buscar los datos del cliente
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Pasar datos al HTML
        model.addAttribute("cliente", cliente);

        // Cargar la plantilla usuario.html (usa Thymeleaf)
        return "usuario";
    }
}
