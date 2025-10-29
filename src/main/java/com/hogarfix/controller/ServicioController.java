package com.hogarfix.controller;

import com.hogarfix.model.Servicio;
import com.hogarfix.model.Categoria;
import com.hogarfix.model.Cliente;
import com.hogarfix.model.Usuario;
import com.hogarfix.service.ServicioService;
import com.hogarfix.service.CategoriaService;
import com.hogarfix.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/servicios")
public class ServicioController {

    private final ServicioService servicioService;
    private final CategoriaService categoriaService;
    private final com.hogarfix.service.TecnicoService tecnicoService;
    private final ClienteService clienteService;

    private final com.hogarfix.service.ServicioService servicioServiceInternal;
    private final com.hogarfix.service.PagoService pagoService;

    @GetMapping
    public String listarServicios(Model model) {
        model.addAttribute("servicios", servicioService.listarServicios());
        return "servicios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoServicio(Model model) {
        model.addAttribute("servicio", new Servicio());
        return "servicios/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Servicio servicio) {
        servicioService.registrarServicio(servicio);
        return "redirect:/servicios";
    }

    /**
     * Endpoint para solicitar un servicio desde la página pública (index).
     * Solo clientes autenticados pueden crear una solicitud.
     */
    @PostMapping("/solicitar")
    public String solicitarServicio(@RequestParam("idCategoria") Long idCategoria,
                                    @RequestParam("urgency") String urgency,
                                    @RequestParam(value = "name", required = false) String name,
                                    @RequestParam("phone") String phone,
                                    @RequestParam("address") String address,
                                    @RequestParam(value = "date", required = false) String date,
                                    @RequestParam("description") String description,
                                    @RequestParam(value = "idTecnico", required = false) Long idTecnico,
                                    Principal principal,
                                    HttpSession session,
                                    Model model) {
        // comprobar autenticación
        if (principal == null) {
            return "redirect:/auth/login";
        }

        // resolver cliente a partir del principal o de la sesión
        Cliente cliente = null;
        Object usr = session.getAttribute("usuarioActual");
        if (usr instanceof Usuario) {
            Usuario u = (Usuario) usr;
            var opt = clienteService.buscarPorEmail(u.getEmail());
            if (opt.isPresent()) cliente = opt.get();
        }

        if (cliente == null) {
            // intentar buscar por principal name
            var opt2 = clienteService.buscarPorEmail(principal.getName());
            if (opt2.isPresent()) cliente = opt2.get();
        }

        if (cliente == null) {
            // no es cliente -> forzar login
            return "redirect:/auth/login";
        }

        Categoria categoria = categoriaService.buscarPorId(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

    Servicio s = new Servicio();
    s.setCliente(cliente);
        // si el cliente seleccionó un técnico desde la lista de técnicos, asignarlo
        if (idTecnico != null) {
            tecnicoService.buscarPorId(idTecnico).ifPresent(s::setTecnico);
        } else {
            s.setTecnico(null); // asignación posterior por sistema
        }
    s.setCategoria(categoria);
    // Si no se envía nombre en el form, usar datos del cliente autenticado
    String contactoNombre = (name != null && !name.isBlank()) ? name : (cliente != null ? (cliente.getNombres() + " " + cliente.getApellidoPaterno()) : "");
    s.setDescripcion(description + "\nContacto: " + contactoNombre + " - " + phone + " - Dirección: " + address);
        s.setMonto(BigDecimal.ZERO);
        s.setEstado("PENDIENTE");
        s.setFechaSolicitud(LocalDateTime.now());

        servicioService.registrarServicio(s);

        // Añadir datos necesarios al modelo para renderizar index con mensaje de éxito
        model.addAttribute("successMessage", "Solicitud enviada correctamente. Revisar en Mi Perfil > Solicitudes.");
        model.addAttribute("categorias", categoriaService.listarCategorias());

        return "index";
    }

    // --- Acciones del técnico sobre una solicitud ---
    @PostMapping("/{id}/aceptar")
    public String aceptarSolicitud(@PathVariable("id") Long id, Principal principal) {
        if (principal == null) return "redirect:/auth/login";
        // verificar que el técnico autenticado es el asignado
        var servOpt = servicioService.buscarPorId(id);
        if (servOpt.isEmpty()) return "redirect:/tecnicos/panel?error=notfound";
        var s = servOpt.get();
        if (s.getTecnico() == null) return "redirect:/tecnicos/panel?error=noasignado";

        try {
            var tecnico = tecnicoService.obtenerPorEmail(principal.getName());
            if (tecnico == null || !tecnico.getIdTecnico().equals(s.getTecnico().getIdTecnico())) {
                return "redirect:/tecnicos/panel?error=forbidden";
            }
        } catch (Exception ex) {
            return "redirect:/tecnicos/panel?error=forbidden";
        }

        servicioServiceInternal.marcarEnProgreso(id);
        return "redirect:/tecnicos/panel";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelarSolicitud(@PathVariable("id") Long id, Principal principal) {
        if (principal == null) return "redirect:/auth/login";
        var servOpt = servicioService.buscarPorId(id);
        if (servOpt.isEmpty()) return "redirect:/tecnicos/panel?error=notfound";
        var s = servOpt.get();
        // permitir cancelar solo si el técnico coincide
        if (s.getTecnico() == null) return "redirect:/tecnicos/panel?error=noasignado";
        try {
            var tecnico = tecnicoService.obtenerPorEmail(principal.getName());
            if (tecnico == null || !tecnico.getIdTecnico().equals(s.getTecnico().getIdTecnico())) {
                return "redirect:/tecnicos/panel?error=forbidden";
            }
        } catch (Exception ex) {
            return "redirect:/tecnicos/panel?error=forbidden";
        }

        servicioServiceInternal.marcarCancelado(id);
        return "redirect:/tecnicos/panel";
    }

    @PostMapping("/{id}/finalizar")
    public String finalizarSolicitud(@PathVariable("id") Long id, Principal principal) {
        if (principal == null) return "redirect:/auth/login";
        var servOpt = servicioService.buscarPorId(id);
        if (servOpt.isEmpty()) return "redirect:/tecnicos/panel?error=notfound";
        var s = servOpt.get();
        if (s.getTecnico() == null) return "redirect:/tecnicos/panel?error=noasignado";
        try {
            var tecnico = tecnicoService.obtenerPorEmail(principal.getName());
            if (tecnico == null || !tecnico.getIdTecnico().equals(s.getTecnico().getIdTecnico())) {
                return "redirect:/tecnicos/panel?error=forbidden";
            }
        } catch (Exception ex) {
            return "redirect:/tecnicos/panel?error=forbidden";
        }

        var updatedOpt = servicioServiceInternal.marcarFinalizado(id);
        if (updatedOpt.isPresent()) {
            var updated = updatedOpt.get();
            try {
                // crear pago pendiente para el cliente con monto estático S/120
                java.math.BigDecimal monto = java.math.BigDecimal.valueOf(120L);
                com.hogarfix.model.Pago pago = com.hogarfix.model.Pago.builder()
                        .servicio(updated)
                        .cliente(updated.getCliente())
                        .monto(monto)
                        .metodoPago("POR_PAGAR")
                        .fechaPago(java.time.LocalDateTime.now())
                        .estado("PENDIENTE")
                        .build();
                pagoService.registrarPago(pago);
            } catch (Exception ex) {
                // no queremos impedir la finalización si falla la creación del pago, solo loguear
                org.slf4j.LoggerFactory.getLogger(ServicioController.class).warn("No se pudo crear pago tras finalizar servicio {}: {}", id, ex.getMessage());
            }
        }

        return "redirect:/tecnicos/panel";
    }
}