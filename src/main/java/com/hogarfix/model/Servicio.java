package com.hogarfix.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;
import java.math.BigDecimal;


@Entity
@Table(name = "tb_servicio")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idCliente", nullable = false)
    private Long idCliente; 

    @Column(name = "idTecnico")
    private Long idTecnico; 

    @Column(name = "idCategoria", nullable = false)
    private Long idCategoria; 

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fechaSolicitud", nullable = false)
    private Date fechaSolicitud = new Date();

    @Column(name = "fechaAsignacion")
    private Date fechaAsignacion;

    @Column(name = "fechaCierre")
    private Date fechaCierre;

    @Column(name = "estado", length = 50, nullable = false)
    private String estado = "PENDIENTE"; 

    @Column(name = "costoEstimado")
    private BigDecimal costoEstimado;

    @Column(name = "calificacionCliente")
    private Integer calificacionCliente;

    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Long getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(Long idTecnico) {
        this.idTecnico = idTecnico;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public Date getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Date fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getCostoEstimado() {
        return costoEstimado;
    }

    public void setCostoEstimado(BigDecimal costoEstimado) {
        this.costoEstimado = costoEstimado;
    }

    public Integer getCalificacionCliente() {
        return calificacionCliente;
    }

    public void setCalificacionCliente(Integer calificacionCliente) {
        this.calificacionCliente = calificacionCliente;
    }
}
