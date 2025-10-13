package com.hogarfix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html
    }

    @PostMapping("/index")
    public String loginSubmit() {
        // Aquí luego puedes validar usuario y contraseña
        return "index"; // templates/index.html
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // templates/register.html
    }
}
