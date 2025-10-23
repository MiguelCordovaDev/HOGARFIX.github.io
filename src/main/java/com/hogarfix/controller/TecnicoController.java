package com.hogarfix.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hogarfix.model.Categoria;
import com.hogarfix.model.Tecnico;
import com.hogarfix.repository.CategoriaRepository;
import com.hogarfix.service.TecnicoService;

@Controller
public class TecnicoController {

    @Autowired
    private TecnicoService tecnicoService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // 1. Muestra el formulario de registro de técnicos (ej: registro_tecnicos.html)
    @GetMapping("/registro_tecnicos")
    public String showRegistrationForm(Model model) {
        model.addAttribute("tecnico", new Tecnico());
        List<Categoria> categorias = categoriaRepository.findAll();
        
        // 2. Pasar la lista a la vista para el <select>
        model.addAttribute("categorias", categorias);   // 2. Pasar la lista a la vista para el <select>
        model.addAttribute("categorias", categorias); 
        return "registro_tecnicos"; // Asume que la vista se llama registro_tecnicos.html
    }

    // 2. Procesa el formulario POST
    @PostMapping("/tecnico/registro") // Se puede usar una URL específica
    public String registerTecnico(@ModelAttribute("tecnico") Tecnico tecnico, RedirectAttributes redirectAttributes) {
        
        if (tecnicoService.existsByEmail(tecnico.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "El email ya está registrado como técnico.");
            return "redirect:/registro_tecnicos"; 
        }
        
        try {
            tecnicoService.saveTecnico(tecnico);
            redirectAttributes.addFlashAttribute("success", "Registro de técnico exitoso. ¡Ahora puedes iniciar sesión!");
            return "redirect:/login_tecnicos"; // Redirigir al login de técnicos
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al registrar el técnico.");
            return "redirect:/registro_tecnicos";
        }
    }
    
    // 3. Muestra el formulario de Login de Técnicos (login_tecnicos.html)
    @GetMapping("/login_tecnicos")
    public String loginTecnicos() {
        // Muestra una vista de login específica para técnicos, si la tienes.
        return "login_tecnicos"; 
    }
}