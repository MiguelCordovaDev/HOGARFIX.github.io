package com.hogarfix.controller;

import com.hogarfix.model.Technician;
import com.hogarfix.service.TechnicianService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final TechnicianService tecnicoService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')") // Restricción de seguridad
    public String showAdminDashboard(Model model) {

        List<Technician> tecnicos = tecnicoService.findAll();
        model.addAttribute("tecnicos", tecnicos);

        List<Technician> topTecnicos = tecnicoService.findTopTecnicosByValoracion();
        model.addAttribute("topTecnicos", topTecnicos);


        return "admin_dashboard";
    }

    @GetMapping("/tecnicos/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String showNewTecnicoForm(Model model) {
        model.addAttribute("tecnico", new Technician());
        return "admin_tecnico_form";
    }
}
