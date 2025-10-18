package com.hogarfix.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "tb_pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idServicio", nullable = false)
    private Long idServicio; // FK: El servicio que se está pagando

    @Column(name = "monto", nullable = false)
    private BigDecimal monto; // Monto de la transacción

    @Column(name = "fechaPago", nullable = false)
    private Date fechaPago = new Date();

    @Column(name = "metodoPago", length = 50, nullable = false)
    private String metodoPago; // Ej: 'Tarjeta Crédito', 'Transferencia'

    @Column(name = "referenciaTransaccion", length = 100, unique = true)
    private String referenciaTransaccion; // Identificador único del banco/pasarela

    @Column(name = "estado", length = 50, nullable = false)
    private String estado = "COMPLETADO"; // Ej: PENDIENTE, COMPLETADO, FALLIDO

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Long idServicio) {
        this.idServicio = idServicio;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferenciaTransaccion() {
        return referenciaTransaccion;
    }

    public void setReferenciaTransaccion(String referenciaTransaccion) {
        this.referenciaTransaccion = referenciaTransaccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}