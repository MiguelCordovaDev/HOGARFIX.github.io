package com.hogarfix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.ui.Model;
import com.hogarfix.model.Usuario;
import com.hogarfix.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
            usuarioService.registrarUsuario(usuario);
            return "redirect:/auth/login?success";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    /**
     * Este método puede usarse después del login para redirigir al dashboard
     * correcto.
     */
    @GetMapping("/redirect")
    public String redirigirSegunRol(@SessionAttribute("usuarioActual") Usuario usuario) {

        if (usuario.getRol() == null || usuario.getRol().getTipoRol() == null) {
            return "redirect:/auth/login?error=rol_invalido";
        }

        switch (usuario.getRol().getTipoRol()) {
            case ADMIN:
                return "redirect:/admin/dashboard";
            case TECNICO:
                return "redirect:/tecnicos/panel";
            case CLIENTE:
                return "redirect:/clientes/inicio";
            default:
                return "redirect:/auth/login?error=sin_rol";
        }
    }
}