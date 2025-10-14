package com.hogarfix.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_calificacion")
@Data
@NoArgsConstructor
public class Qualification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCalificacion;

    @Column(nullable = false)
    private Integer puntuacion; // Valoración del 1 al 5

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fechaCalificacion;

    // Relación Many-to-One con Servicio (Una calificación pertenece a un servicio)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio", referencedColumnName = "idServicio", unique = true)
    private Service servicio;

    // Relación Many-to-One con Técnico (¿A quién se califica?)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tecnico", nullable = false)
    private Technician tecnico;
}
