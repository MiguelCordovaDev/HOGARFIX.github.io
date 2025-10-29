package com.hogarfix.controller;

import com.hogarfix.model.Cliente;
import com.hogarfix.model.Pago;
import com.hogarfix.service.ClienteService;
import com.hogarfix.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class VistaPagoController {

    private final PagoService pagoService;
    private final ClienteService clienteService;

    @GetMapping("/pagos")
    public String verPagos(Principal principal, HttpSession session, Model model, @org.springframework.web.bind.annotation.RequestParam(value = "pagoId", required = false) Long pagoId, jakarta.servlet.http.HttpServletRequest request) {
        Cliente cliente = null;
        Object usr = session.getAttribute("usuarioActual");
        if (usr instanceof com.hogarfix.model.Usuario) {
            var u = (com.hogarfix.model.Usuario) usr;
            var opt = clienteService.buscarPorEmail(u.getEmail());
            if (opt.isPresent()) cliente = opt.get();
        }

        if (cliente == null && principal != null) {
            var opt2 = clienteService.buscarPorEmail(principal.getName());
            if (opt2.isPresent()) cliente = opt2.get();
        }

        if (cliente == null) {
            return "redirect:/auth/login";
        }

        List<Pago> pagos = pagoService.listarPorCliente(cliente);
        if (pagoId != null) {
            // move selected pago to the front if present
            pagos = pagos.stream().sorted((a, b) -> {
                if (a.getIdPago().equals(pagoId)) return -1;
                if (b.getIdPago().equals(pagoId)) return 1;
                return 0;
            }).toList();
        }
        model.addAttribute("pagos", pagos);
        // Exponer token CSRF para llamadas fetch desde JS
        Object csrf = request.getAttribute("_csrf");
        if (csrf != null) model.addAttribute("_csrf", csrf);
        return "pagos";
    }
}
