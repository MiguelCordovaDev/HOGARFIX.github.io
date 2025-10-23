package com.hogarfix.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.hogarfix.model.Calificacion;
import com.hogarfix.repository.CalificacionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;

    public Calificacion registrarCalificacion(Calificacion calificacion) {
        return calificacionRepository.save(calificacion);
    }

    public List<Calificacion> listarCalificaciones() {
        return calificacionRepository.findAll();
    }
}
