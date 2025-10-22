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

   

    

}

