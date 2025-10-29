package com.hogarfix.controller;

import com.hogarfix.model.Ciudad;
import com.hogarfix.model.Cliente;
import com.hogarfix.model.Direccion;
import com.hogarfix.model.Usuario;
import com.hogarfix.service.CiudadService;
import com.hogarfix.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/clientes")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;
    private final CiudadService ciudadService;
    private final AuthenticationManager authenticationManager;
    private final com.hogarfix.service.ServicioService servicioService;
    private final com.hogarfix.service.PagoService pagoService;
    private final com.hogarfix.service.EmailService emailService;

    @GetMapping
    public String listarClientes(Model model) {
        List<Cliente> clientes = clienteService.listarClientes();
        model.addAttribute("clientes", clientes);
        return "cliente/lista";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model, Principal principal) {
        // Comprobar que el usuario está autenticado
        if (principal == null) {
            return "redirect:/auth/login";
        }
        try {
            // Obtener cliente a partir del principal (puede ser email o username)
            String name = principal.getName();
            Cliente cliente = null;
            var optByEmail = clienteService.buscarPorEmail(name);
            if (optByEmail.isPresent()) {
                cliente = optByEmail.get();
            } else {
                var optByUsername = clienteService.buscarPorUsername(name);
                if (optByUsername.isPresent()) cliente = optByUsername.get();
            }

            if (cliente == null) {
                // No es cliente o no lo encontramos — redirigir al inicio o mostrar mensaje
                return "redirect:/";
            }
            model.addAttribute("cliente", cliente);
            // añadir solicitudes pendientes del cliente al modelo
            try {
                var servicios = servicioService.listarPorCliente(cliente);
                var pendientes = servicios.stream()
                        .filter(s -> "PENDIENTE".equalsIgnoreCase(s.getEstado()))
                        .toList();
                model.addAttribute("serviciosPendientes", pendientes);
            } catch (Exception ex) {
                // no bloquear la vista por errores al recuperar servicios
                model.addAttribute("serviciosPendientes", java.util.List.of());
            }
            // añadir pagos pendientes del cliente al modelo (creados cuando el técnico finaliza)
            try {
                var pagos = pagoService.listarPorCliente(cliente);
                var pagosPendientes = pagos.stream()
                        .filter(p -> p.getEstado() != null && p.getEstado().equalsIgnoreCase("PENDIENTE"))
                        .toList();
                model.addAttribute("pagosPendientes", pagosPendientes);
            } catch (Exception ex) {
                model.addAttribute("pagosPendientes", java.util.List.of());
            }
            return "cliente/perfil";
        } catch (Exception ex) {
            logger.error("Error al mostrar perfil del cliente", ex);
            return "redirect:/auth/login?error=perfil_error";
        }
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        // Inicializamos objetos anidados para que Thymeleaf los pueda enlazar
        Cliente cliente = new Cliente();
        cliente.setUsuario(new Usuario());
        cliente.setDireccion(new Direccion());
        model.addAttribute("cliente", cliente);
        // Pasamos lista de ciudades para el select
        model.addAttribute("ciudades", ciudadService.listarCiudades());
        return "cliente/registro";
    }

    @PostMapping("/registro")
    public String registrarCliente(@ModelAttribute Cliente cliente, Model model, HttpSession session) {
        try {
            // Aseguramos username a partir del email si no se proporcionó
            if (cliente.getUsuario() != null && (cliente.getUsuario().getUsername() == null || cliente.getUsuario().getUsername().isEmpty())) {
                cliente.getUsuario().setUsername(cliente.getUsuario().getEmail());
            }

            // Resolver la ciudad seleccionada (el binding deja el id en direccion.ciudad.idCiudad)
            if (cliente.getDireccion() != null && cliente.getDireccion().getCiudad() != null) {
                Long idCiudad = cliente.getDireccion().getCiudad().getIdCiudad();
                if (idCiudad != null) {
                    Optional<Ciudad> ciudadOpt = ciudadService.buscarPorId(idCiudad);
                    if (ciudadOpt.isPresent()) {
                        cliente.getDireccion().setCiudad(ciudadOpt.get());
                    } else {
                        throw new RuntimeException("Ciudad seleccionada no válida");
                    }
                } else {
                    throw new RuntimeException("Seleccione una ciudad");
                }
            } else {
                throw new RuntimeException("Dirección incompleta");
            }

            // Guarda el cliente y luego autentica la sesión automáticamente
            String rawPassword = cliente.getUsuario().getPassword();
            Cliente clienteGuardado = clienteService.registrarCliente(cliente);

            // Autenticar usando AuthenticationManager para establecer SecurityContext
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(clienteGuardado.getUsuario().getEmail(), rawPassword);
            Authentication auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            // Persist SecurityContext in HTTP session so Spring Security recognizes the login across requests
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // Guardar en sesión el usuario y un nombre de visualización para la navbar
            session.setAttribute("usuarioActual", clienteGuardado.getUsuario());
            session.setAttribute("usuarioNombre", clienteGuardado.getNombres() + " " + clienteGuardado.getApellidoPaterno());

            // Enviar email de bienvenida (EmailService maneja errores internamente)
            emailService.sendWelcomeEmail(clienteGuardado.getUsuario().getEmail(), clienteGuardado.getNombres());

            return "redirect:/";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            // volver a cargar ciudades para el formulario en caso de error
            model.addAttribute("ciudades", ciudadService.listarCiudades());
            return "cliente/registro";
        }
    }
}
