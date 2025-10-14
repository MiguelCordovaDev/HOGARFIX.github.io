package com.hogarfix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TechnicianController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // templates/dashboard.html
    }
}
