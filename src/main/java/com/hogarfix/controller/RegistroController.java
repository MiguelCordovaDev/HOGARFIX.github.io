package com.hogarfix.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

     @GetMapping("/register")
    public String mostrarFormularioRegistro(Model model) {
        // 'cliente' es el th:object que espera la plantilla HTML
        model.addAttribute("cliente", new Cliente()); 
        return "registro";
    }

    @PostMapping("/cliente/registro")
    public String registrarCliente(
                                   // Puedes usar @Valid aquí si tienes anotaciones de validación en la entidad
                                   @ModelAttribute("cliente") Cliente cliente,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {

        
        if (result.hasErrors()) {
            // Si hay errores de validación de campos (@Valid), regresa al formulario.
            return "registro";
        }
        
        try {
            // 1. Llama al servicio para guardar el cliente (hashea y verifica email)
            clienteService.guardarCliente(cliente);
            
            // 2. Si el guardado es exitoso, redirige con un mensaje de éxito.
            redirectAttributes.addFlashAttribute("exito", "¡Registro completado! Por favor, inicia sesión.");
            return "redirect:/login"; 
            
        } catch (IllegalStateException e) {
            // 3. Manejo de error de unicidad (lanzado desde ClienteService)
            // Agrega el mensaje de error específico a los atributos flash.
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            
            // 4. Vuelve a la página de registro
            return "redirect:/register"; 
            
        } catch (Exception e) {
             // Manejo de error general (ej: error de BD no esperado)
            redirectAttributes.addFlashAttribute("error", "Error inesperado al registrar el usuario. Inténtalo de nuevo.");
            System.err.println("Error al registrar cliente: " + e.getMessage());
            return "redirect:/register";
        }
    }
}