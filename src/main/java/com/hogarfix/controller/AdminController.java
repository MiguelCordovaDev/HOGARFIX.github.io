package com.hogarfix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.hogarfix.service.PagoService;
import com.hogarfix.service.ServicioService;
import com.hogarfix.service.TecnicoService;
import com.hogarfix.service.ClienteService;
import java.security.Principal;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

        private final ServicioService servicioService;
        private final TecnicoService tecnicoService;
        private final ClienteService clienteService;
        private final PagoService pagoService;

        @GetMapping("/dashboard")
        public String dashboard(Model model, Principal principal) {
                if (principal == null) {
                        return "redirect:/auth/login";
                }

                // Datos relevantes
                var servicios = servicioService.listarServicios();
                var tecnicos = tecnicoService.listarTecnicos();
                var clientes = clienteService.listarClientes();

                // Ejemplo de cálculos relevantes
                var servicioMasSolicitado = servicios.stream()
                                .max((a, b) -> Integer.compare(
                                                a.getNumSolicitudes() == null ? 0 : a.getNumSolicitudes(),
                                                b.getNumSolicitudes() == null ? 0 : b.getNumSolicitudes()))
                                .orElse(null);

                var tecnicoMasDestacado = tecnicos.stream()
                                .max((a, b) -> Double.compare(a.getPromedioCalificacion(), b.getPromedioCalificacion()))
                                .orElse(null);

                model.addAttribute("servicioMasSolicitado", servicioMasSolicitado);
                model.addAttribute("tecnicoMasDestacado", tecnicoMasDestacado);
                model.addAttribute("totalPagos", pagoService.listarPagos().size());
                model.addAttribute("clientes", clientes);
                model.addAttribute("tecnicos", tecnicos);
                model.addAttribute("clientesCount", clientes != null ? clientes.size() : 0);
                model.addAttribute("tecnicosCount", tecnicos != null ? tecnicos.size() : 0);

                return "admin/dashboard";
        }

        @GetMapping("/clientes")
        public String verClientes(Model model, Principal principal) {
                if (principal == null) {
                        return "redirect:/auth/login";
                }
                var clientes = clienteService.listarClientes();
                model.addAttribute("clientes", clientes);
                return "admin/clientes";
        }

        @GetMapping("/tecnicos")
        public String verTecnicos(Model model, Principal principal) {
                if (principal == null) {
                        return "redirect:/auth/login";
                }
                var tecnicos = tecnicoService.listarTecnicos();
                model.addAttribute("tecnicos", tecnicos);
                return "admin/tecnicos";
        }
}