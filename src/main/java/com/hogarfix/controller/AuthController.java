package com.hogarfix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import com.hogarfix.model.Tecnico;

@Controller
public class AuthController {
    
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/pagos")
    public String FormPagos() {
        return "pagos"; // 
    }
    
    @GetMapping("/tecnicos")
    public String showtecnicos() {
        return "tecnicos"; // 
    }

    @GetMapping("/registro_tecnicos")
    public String registerPage(Model model) {
        
        model.addAttribute("tecnicos", new Tecnico());
        return "registro_tecnicos"; 
    }

     @GetMapping("/login_tecnicos")
    public String LogintecnicoPage(Model model) {
        
        model.addAttribute("tecnicos", new Tecnico());
        return "login_tecnicos"; 
    }
}

