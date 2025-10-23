package com.hogarfix.dto;

import java.util.List;
import lombok.Data;

@Data
public class TecnicoDTO {
    private Long idTecnico;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String dni;
    private String certificadoPdf; // Ruta del certificado
    private Long idUsuario;
    private Long idDireccion;
    private Double promedioCalificacion;
    private List<String> categorias; // Nombres de categorías del técnico
}
