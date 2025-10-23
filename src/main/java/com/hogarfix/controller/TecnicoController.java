package com.hogarfix.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.hogarfix.model.Servicio;
import com.hogarfix.model.Tecnico;
import com.hogarfix.service.ServicioService;
import com.hogarfix.service.TecnicoService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tecnicos")
public class TecnicoController {

    private final TecnicoService tecnicoService;
    private final ServicioService servicioService; // ✅ Se inyecta correctamente

    @GetMapping
    public String listarTecnicos(Model model) {
        List<Tecnico> tecnicos = tecnicoService.listarTecnicos();
        model.addAttribute("tecnicos", tecnicos);
        return "tecnicos/lista";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("tecnico", new Tecnico());
        return "tecnicos/registro";
    }

    @PostMapping("/registro")
    public String registrarTecnico(@ModelAttribute Tecnico tecnico,
            @RequestParam("certificadoPdf") MultipartFile archivo) throws IOException {

        if (!archivo.isEmpty()) {
            String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
            Path rutaArchivo = Paths.get("src/main/resources/static/uploads/certificados/" + nombreArchivo);
            Files.createDirectories(rutaArchivo.getParent());
            archivo.transferTo(rutaArchivo.toFile());
            tecnico.setCertificadoPdf(nombreArchivo);
        }

        tecnicoService.registrarTecnico(tecnico);
        return "redirect:/login?registro=exitoso";
    }

    @GetMapping("/certificados/{nombreArchivo}")
    public ResponseEntity<Resource> verCertificado(@PathVariable String nombreArchivo) throws IOException {
        Path archivoPath = Paths.get("uploads/certificados/").resolve(nombreArchivo).normalize();
        Resource recurso = new UrlResource(archivoPath.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreArchivo + "\"")
                .body(recurso);
    }

    @GetMapping("/panel")
    public String panelTecnico(Model model, Principal principal) {
        Tecnico tecnico = tecnicoService.obtenerPorEmail(principal.getName());
        List<Servicio> servicios = servicioService.obtenerPorTecnico(tecnico.getIdTecnico());

        model.addAttribute("tecnico", tecnico);
        model.addAttribute("servicios", servicios);
        return "tecnico/panel";
    }
}