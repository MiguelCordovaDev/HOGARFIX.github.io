package com.hogarfix.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tbl_categoria")
@Data
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Relación One-to-Many con Servicio
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Service> servicios;

    // Relación One-to-Many con Técnico (Especialidad)
    @OneToMany(mappedBy = "especialidad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Technician> tecnicosEspecializados;
}
