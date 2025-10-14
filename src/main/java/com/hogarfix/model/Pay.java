package com.hogarfix.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_pago")
@Data
@NoArgsConstructor
public class Pay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private String metodoPago; // Ej: 'Tarjeta', 'Efectivo', 'Transferencia'

    @Column(nullable = false)
    private LocalDateTime fechaPago;

    private String estado; // Ej: 'Completado', 'Pendiente', 'Fallido'

    // Relación One-to-One con Servicio (Un servicio tiene un pago)
    // El servicio es el dueño de la relación con Pago según la convención de Spring Data
    // Por simplicidad en este modelo, hacemos que Pago apunte a Servicio.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio", referencedColumnName = "idServicio", unique = true)
    private Service servicio;
}
