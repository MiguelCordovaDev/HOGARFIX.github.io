package com.hogarfix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import com.hogarfix.model.Cliente;

@Controller
public class AuthController {
    
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html
    }

    @GetMapping("/geo")
    public String showgeo() {
        return "geo"; // templates/login.html
    }
    

    @GetMapping("/register")
    public String registerPage(Model model) {
        // Inicializa un nuevo objeto 'cliente' para que el formulario lo llene (th:object="${cliente}")
        model.addAttribute("cliente", new Cliente());
        return "registro"; 
    }
}
