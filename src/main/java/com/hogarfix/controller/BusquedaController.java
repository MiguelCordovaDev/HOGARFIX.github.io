package com.hogarfix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import com.hogarfix.model.Cliente;


@Controller
public class BusquedaController {
    
    @GetMapping("/busqueda")
    public String busquedapage(Model model) {
         model.addAttribute("cliente", new Cliente());
        return "busqueda"; 
    }

    

}
