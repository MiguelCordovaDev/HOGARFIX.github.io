package com.hogarfix.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendWelcomeEmail(String to, String nombre) {
        if (to == null || to.isBlank()) return;

        String subject = "Bienvenido a HogarFix";
        String text = "Hola " + (nombre != null && !nombre.isBlank() ? nombre : "") + ",\n\n"
                + "Gracias por registrarte en HogarFix. Nos alegra darte la bienvenida y esperamos ayudarte a conectar con los mejores técnicos para tu hogar.\n\n"
                + "Saludos,\nEl equipo de HogarFix";

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception ex) {
            // Log error but don't prevent flow
            System.err.println("No se pudo enviar email de bienvenida a " + to + ": " + ex.getMessage());
        }
    }
}
