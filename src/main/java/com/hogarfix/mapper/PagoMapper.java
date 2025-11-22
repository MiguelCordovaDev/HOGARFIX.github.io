package com.hogarfix.mapper;

import com.hogarfix.dto.PagoDTO;
import com.hogarfix.model.Cliente;
import com.hogarfix.model.Pago;
import com.hogarfix.model.Servicio;

public class PagoMapper {

    public static PagoDTO toDTO(Pago pago) {
        if (pago == null) {
            return null;
        }

        return PagoDTO.builder()
                .idPago(pago.getIdPago())
                .idServicio(pago.getServicio() != null ? pago.getServicio().getIdServicio() : null)
                .idCliente(pago.getCliente() != null ? pago.getCliente().getIdCliente() : null)
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago())
                .fechaPago(pago.getFechaPago())
                .estado(pago.getEstado())
                .createdAt(pago.getCreatedAt())
                .build();
    }

    public static Pago toEntity(PagoDTO dto) {
        if (dto == null) {
            return null;
        }

        Pago pago = new Pago();
        pago.setIdPago(dto.getIdPago());

        if (dto.getIdServicio() != null) {
            Servicio servicio = new Servicio();
            servicio.setIdServicio(dto.getIdServicio());
            pago.setServicio(servicio);
        }

        if (dto.getIdCliente() != null) {
            Cliente cliente = new Cliente();
            cliente.setIdCliente(dto.getIdCliente());
            pago.setCliente(cliente);
        }

        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setFechaPago(dto.getFechaPago());
        pago.setEstado(dto.getEstado());
        pago.setCreatedAt(dto.getCreatedAt());


        return pago;
    }
}