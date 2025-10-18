package com.hogarfix.model;

import com.hogarfix.dto.ERole;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data // Genera getters, setters, toString, equals y hashCode (si usas Lombok)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Define el nombre del rol (Ej: ROLE_ADMIN, ROLE_TECNICO, ROLE_CLIENTE)
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;
}