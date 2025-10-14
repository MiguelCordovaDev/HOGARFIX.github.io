package com.hogarfix.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_servicio")
@Data
@NoArgsConstructor
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idServicio;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fechaSolicitud;

    private LocalDateTime fechaRealizacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceState estado; // Enum para manejar los estados del flujo

    // Relación Many-to-One con Cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Customer cliente;

    // Relación Many-to-One con Técnico (Puede ser nulo al inicio)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tecnico")
    private Technician tecnico;

    // Relación Many-to-One con Categoría
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Category categoria;

    // Relación One-to-One con Pago
    @OneToOne(mappedBy = "servicio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Pay pago;

    // Relación One-to-One con Calificación
    @OneToOne(mappedBy = "servicio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Qualification calificacion;
}
