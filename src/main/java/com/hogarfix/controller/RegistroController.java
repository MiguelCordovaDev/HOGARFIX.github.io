package com.hogarfix.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hogarfix.model.Cliente;
import com.hogarfix.service.ClienteService;

@Controller
public class RegistroController {

 @Autowired
    private ClienteService clienteService;

   
    
    // Muestra la vista de Registro (registro.html) y crea el objeto para el formulario
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Objeto necesario para th:object="${cliente}" en el formulario
        model.addAttribute("cliente", new Cliente()); 
        return "registro"; 
    }

    // Procesa el formulario POST desde registro.html (th:action="@{/cliente/registro}")
    @PostMapping("/cliente/registro")
    public String registerUser(@ModelAttribute("cliente") Cliente cliente, RedirectAttributes redirectAttributes) {
        
        // 1. VERIFICACIÓN DE EXISTENCIA EN BD
        if (clienteService.existsByEmail(cliente.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "El email ya está registrado. Intenta iniciar sesión.");
            return "redirect:/register"; 
        }
        
        try {
            // 2. GUARDAR EN BD (incluye la encriptación de contraseña)
            clienteService.saveCliente(cliente);
            redirectAttributes.addFlashAttribute("success", "¡Registro exitoso! Ya puedes iniciar sesión.");
            
            // 3. Redirigir al login
            return "redirect:/login"; 
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al registrar. Inténtalo de nuevo.");
            return "redirect:/register";
        }
    }
}