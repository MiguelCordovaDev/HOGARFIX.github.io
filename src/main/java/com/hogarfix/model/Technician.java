package com.hogarfix.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tbl_tecnico")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Technician {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTecnico;

    private String dni;
    private String telefono;
    private String direccion;
    private Boolean disponible;
    private Double calificacionPromedio; // Para la métrica gerencial

    // Relación Many-to-One con Usuario
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", referencedColumnName = "idUsuario")
    private User usuario;

    // Relación One-to-Many con Servicio (un técnico realiza muchos servicios)
    @OneToMany(mappedBy = "tecnico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Service> servicios;

    // Relación One-to-Many con Calificacion (un técnico recibe muchas calificaciones)
    @OneToMany(mappedBy = "tecnico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Qualification> calificaciones;

    // Asumimos que la especialidad es una relación con otra tabla
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria")
    private Category especialidad;
}
