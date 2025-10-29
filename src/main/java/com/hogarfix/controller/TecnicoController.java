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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.hogarfix.model.Servicio;
import com.hogarfix.model.Tecnico;
import com.hogarfix.model.TecnicoCategoria;
import com.hogarfix.model.Usuario;
import com.hogarfix.model.Direccion;
import com.hogarfix.model.Ciudad;
import com.hogarfix.service.CiudadService;
import com.hogarfix.service.CategoriaService;
import java.util.Optional;
import com.hogarfix.service.ServicioService;
import com.hogarfix.service.TecnicoService;
import com.hogarfix.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/tecnicos")
public class TecnicoController {

    private final TecnicoService tecnicoService;
    private final ServicioService servicioService; // ✅ Se inyecta correctamente
    private final CiudadService ciudadService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;
    private final com.hogarfix.service.EmailService emailService;

    @GetMapping
    public String listarTecnicos(Model model) {
        List<Tecnico> tecnicos = tecnicoService.listarTecnicos();
        model.addAttribute("tecnicos", tecnicos);
        return "tecnico/lista";
    }

    @GetMapping("/login")
    public String mostrarLoginTecnico() {
        return "tecnico/logintecnico";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        Tecnico tecnico = new Tecnico();
        tecnico.setUsuario(new Usuario());
        tecnico.setDireccion(new Direccion());
        model.addAttribute("tecnico", tecnico);
        model.addAttribute("ciudades", ciudadService.listarCiudades());
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "tecnico/registrotec";
    }

    @PostMapping("/registro")
    public String registrarTecnico(@ModelAttribute("tecnico") Tecnico tecnico,
        @RequestParam("certificadoPdfFile") MultipartFile archivo,
        @RequestParam(value = "fotoPerfilFile", required = false) MultipartFile fotoFile,
        @RequestParam("categoriaId") Long categoriaId,
        Model model) throws IOException {
        log.info("Iniciando registro de técnico: email={}", tecnico != null && tecnico.getUsuario() != null ? tecnico.getUsuario().getEmail() : "(sin usuario)");

        // procesar archivo (certificado)
        if (archivo != null && !archivo.isEmpty()) {
            String rutaRel = saveUploadedFile(archivo, "certificados");
            tecnico.setCertificadoPdf(rutaRel);
            log.info("Certificado guardado: {}", rutaRel);
        }

        // procesar foto de perfil (opcional)
        if (fotoFile != null && !fotoFile.isEmpty()) {
            String contentType = fotoFile.getContentType();
            if (contentType == null || !(contentType.equalsIgnoreCase("image/png") || contentType.equalsIgnoreCase("image/jpeg") || contentType.equalsIgnoreCase("image/jpg"))) {
                model.addAttribute("error", "La foto de perfil debe ser PNG o JPG/JPEG");
                model.addAttribute("ciudades", ciudadService.listarCiudades());
                model.addAttribute("categorias", categoriaService.listarCategorias());
                return "tecnico/registrotec";
            }

            String rutaRel = saveUploadedFile(fotoFile, "tecnicos");
            tecnico.setFotoPerfil(rutaRel);
            log.info("Foto de perfil guardada: {}", rutaRel);
        }

            // asegurar username desde email si no viene
            if (tecnico.getUsuario() != null && (tecnico.getUsuario().getUsername() == null || tecnico.getUsuario().getUsername().isEmpty())) {
                tecnico.getUsuario().setUsername(tecnico.getUsuario().getEmail());
            }

            // Resolver ciudad seleccionada
            if (tecnico.getDireccion() != null && tecnico.getDireccion().getCiudad() != null) {
                Long idCiudad = tecnico.getDireccion().getCiudad().getIdCiudad();
                if (idCiudad != null) {
                    Optional<Ciudad> ciudadOpt = ciudadService.buscarPorId(idCiudad);
                    if (ciudadOpt.isPresent()) {
                        tecnico.getDireccion().setCiudad(ciudadOpt.get());
                    } else {
                        throw new RuntimeException("Ciudad seleccionada no válida");
                    }
                } else {
                    throw new RuntimeException("Seleccione una ciudad");
                }
            } else {
                throw new RuntimeException("Dirección incompleta");
            }

            // validar que se haya subido el certificado (campo obligatorio en la entidad)
            if (tecnico.getCertificadoPdf() == null || tecnico.getCertificadoPdf().isEmpty()) {
                model.addAttribute("error", "Debe subir un certificado PDF");
                model.addAttribute("ciudades", ciudadService.listarCiudades());
                model.addAttribute("categorias", categoriaService.listarCategorias());
                return "tecnico/registrotec";
            }

            // Validar y asignar categoría
            var categoriaOpt = categoriaService.buscarPorId(categoriaId);
            if (categoriaOpt.isEmpty()) {
                model.addAttribute("error", "Debe seleccionar una categoría válida");
                model.addAttribute("ciudades", ciudadService.listarCiudades());
                model.addAttribute("categorias", categoriaService.listarCategorias());
                return "tecnico/registrotec";
            }

            // Crear la relación TecnicoCategoria
            TecnicoCategoria tecnicoCategoria = TecnicoCategoria.builder()
                .categoria(categoriaOpt.get())
                .tecnico(tecnico)
                .build();
            tecnico.setCategorias(List.of(tecnicoCategoria));

        Tecnico saved = tecnicoService.registrarTecnico(tecnico);
        log.info("Técnico registrado correctamente: email={}", tecnico.getUsuario().getEmail());

        // EmailService maneja internamente excepciones, así que no es necesario try/catch aquí
        emailService.sendWelcomeEmail(saved.getUsuario().getEmail(), saved.getNombres());
        return "redirect:/tecnicos/login?registro=exitoso";
    }

    @PostMapping("/login")
    public String procesarLoginTecnico(@RequestParam("username") String username,
        @RequestParam("password") String password,
        HttpSession session) {
        try {
        log.info("Intento de login técnico: username={}", username);
        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(username, password);
        Authentication auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
        // Persistir el SecurityContext en la sesión para que Spring Security
        // lo reconozca en las siguientes requests (consistente con AuthController)
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        log.info("Autenticación exitosa para usuario={}", username);

            // verificar rol TECNICO
            var usuarioOpt = usuarioService.buscarPorId(((com.hogarfix.model.Usuario) auth.getPrincipal()).getIdUsuario());
            if (usuarioOpt.isPresent()) {
                Usuario u = usuarioOpt.get();
                if (u.getRol() != null && u.getRol().getTipoRol() == com.hogarfix.model.enums.TipoRol.TECNICO) {
                    session.setAttribute("usuarioActual", u);
                    // Guardar nombre para mostrar en la navbar
                    try {
                        Tecnico t = tecnicoService.obtenerPorEmail(u.getEmail());
                        if (t != null) {
                            session.setAttribute("usuarioNombre", t.getNombres() + " " + t.getApellidoPaterno());
                        } else {
                            session.setAttribute("usuarioNombre", u.getEmail());
                        }
                    } catch (Exception ex) {
                        session.setAttribute("usuarioNombre", u.getEmail());
                    }
                    // Redirigir al panel del técnico (mostrar nombre en navbar)
                    return "redirect:/tecnicos/panel";
                } else {
                    // si no es técnico, cerrar sesión y devolver error
                    SecurityContextHolder.clearContext();
                    return "redirect:/tecnicos/login?error";
                }
            }

            return "redirect:/tecnicos/login?error";
        } catch (AuthenticationException ex) {
            log.warn("Fallo de autenticación para usuario={}: {}", username, ex.getMessage());
            return "redirect:/tecnicos/login?error";
        } catch (Exception ex) {
            log.error("Error inesperado en procesarLoginTecnico para usuario={}", username, ex);
            return "redirect:/tecnicos/login?error=server";
        }
    }

    @GetMapping("/certificados/{nombreArchivo}")
    public ResponseEntity<Resource> verCertificado(@PathVariable String nombreArchivo) throws IOException {
        Path archivoPath = Paths.get(System.getProperty("user.dir"), "uploads", "certificados").resolve(nombreArchivo).normalize();
        
        // Verificar que el archivo existe
        if (!Files.exists(archivoPath) || !Files.isReadable(archivoPath)) {
            log.warn("Certificado no encontrado o no legible: {}", archivoPath);
            return ResponseEntity.notFound().build();
        }
        
        Resource recurso = new UrlResource(archivoPath.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreArchivo + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(recurso);
    }

    @GetMapping("/fotos/{nombreArchivo}")
    public ResponseEntity<Resource> verFoto(@PathVariable String nombreArchivo) throws IOException {
        Path archivoPath = Paths.get(System.getProperty("user.dir"), "uploads", "tecnicos").resolve(nombreArchivo).normalize();
        log.info("Solicitando foto: {} -> path={}", nombreArchivo, archivoPath.toAbsolutePath());
        if (!Files.exists(archivoPath) || !Files.isReadable(archivoPath)) {
            log.warn("Foto no encontrada o no legible: {}", archivoPath);
            return ResponseEntity.notFound().build();
        }

        Resource recurso = new UrlResource(archivoPath.toUri());
        String contentType = Files.probeContentType(archivoPath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreArchivo + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : "application/octet-stream")
                .body(recurso);
    }

    @GetMapping("/panel")
    public String panelTecnico(Model model, Principal principal) {
        Tecnico tecnico = resolveTecnicoFromPrincipal(principal);

        if (tecnico == null) {
            // no autenticado o no se encontró el tecnico -> forzar login
            return "redirect:/auth/login";
        }

        List<Servicio> servicios = servicioService.obtenerPorTecnico(tecnico.getIdTecnico());

        model.addAttribute("tecnico", tecnico);
        model.addAttribute("servicios", servicios);
        
        // Obtener la categoría del técnico (asumiendo que solo tiene una)
        if (tecnico.getCategorias() != null && !tecnico.getCategorias().isEmpty()) {
            model.addAttribute("categoria", tecnico.getCategorias().get(0).getCategoria());
        }

        // calcular nombre de archivo de la foto y pasarlo al modelo para evitar usar funciones de Thymeleaf
        String fotoNombre = null;
        try {
            if (tecnico.getFotoPerfil() != null && !tecnico.getFotoPerfil().isBlank()) {
                String ruta = tecnico.getFotoPerfil();
                int idx1 = ruta.lastIndexOf('/');
                int idx2 = ruta.lastIndexOf('\\');
                int idx = Math.max(idx1, idx2);
                fotoNombre = idx >= 0 ? ruta.substring(idx + 1) : ruta;
            }
        } catch (Exception e) {
            log.warn("No se pudo extraer nombre de foto de perfil: {}", e.getMessage());
        }
        model.addAttribute("tecnicoFotoNombre", fotoNombre);
        return "tecnico/panel";
    }

    // Helper: guarda un MultipartFile en uploads/<subdir> y devuelve la ruta relativa usada en la entidad
    private String saveUploadedFile(MultipartFile file, String subdir) throws IOException {
        String nombre = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path dir = Paths.get(System.getProperty("user.dir"), "uploads", subdir);
        Files.createDirectories(dir);
        Path destino = dir.resolve(nombre);
        file.transferTo(destino.toFile());
        return "uploads/" + subdir + "/" + nombre;
    }

    // Helper: intenta resolver el Tecnico asociado al principal de forma ordenada
    private Tecnico resolveTecnicoFromPrincipal(Principal principal) {
        if (principal == null) return null;
        String name = principal.getName();
        try {
            Tecnico t = tecnicoService.obtenerPorEmail(name);
            if (t != null) return t;
        } catch (Exception ignored) {}

        try {
            var usuarioOpt = usuarioService.buscarPorUsername(name);
            if (usuarioOpt.isPresent()) {
                return tecnicoService.obtenerPorEmail(usuarioOpt.get().getEmail());
            }
        } catch (Exception ignored) {}

        try {
            var usuarioPorEmail = usuarioService.buscarPorEmail(name);
            if (usuarioPorEmail.isPresent()) {
                return tecnicoService.obtenerPorEmail(usuarioPorEmail.get().getEmail());
            }
        } catch (Exception ignored) {}

        return null;
    }
}