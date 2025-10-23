package com.hogarfix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.hogarfix.service.PagoService;
import com.hogarfix.service.ServicioService;
import com.hogarfix.service.TecnicoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

        private final ServicioService servicioService;
        private final TecnicoService tecnicoService;
        private final PagoService pagoService;

        @GetMapping("/dashboard")
        public String dashboard(Model model) {
                // Datos relevantes
                var servicios = servicioService.listarServicios();
                var tecnicos = tecnicoService.listarTecnicos();

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

                return "admin/dashboard";
        }
}