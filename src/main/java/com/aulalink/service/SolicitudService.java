package com.aulalink.service;

import com.aulalink.domain.EstadoHorario;
import com.aulalink.domain.EstadoTutoria;
import com.aulalink.domain.Tutoria;
import com.aulalink.repository.TutoriaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SolicitudService {

    private final TutoriaRepository tutoriaRepository;

    @Transactional(readOnly = true)
    public List<SolicitudVista> listarRecibidas(Integer idTutor) {
        return tutoriaRepository.findRecibidas(idTutor).stream()
                .map(SolicitudVista::new)
                .toList();
    }

    @Transactional
    public boolean decidir(Integer idTutor, Integer idTutoria, boolean aceptar) {
        Tutoria solicitud = tutoriaRepository.findById(idTutoria)
                .orElseThrow(() -> new IllegalArgumentException("solicitud.error.noExiste"));
        if (!solicitud.getTutor().getIdUsuario().equals(idTutor)) {
            throw new IllegalArgumentException("solicitud.error.ajena");
        }
        if (solicitud.getEstadoTutoria() != EstadoTutoria.SOLICITADA) {
            throw new IllegalArgumentException("solicitud.error.estado");
        }
        if (!aceptar) {
            solicitud.setEstadoTutoria(EstadoTutoria.RECHAZADA);
            return false;
        }
        if (solicitud.getDisponibilidad().getEstadoHorario() == EstadoHorario.OCUPADO) {
            throw new IllegalArgumentException("solicitud.error.ocupado");
        }
        solicitud.setEstadoTutoria(EstadoTutoria.ACEPTADA);
        solicitud.getDisponibilidad().setEstadoHorario(EstadoHorario.OCUPADO);
        for (Tutoria otra : tutoriaRepository.findByDisponibilidad_IdDisponibilidadAndEstadoTutoriaAndIdTutoriaNot(
                solicitud.getDisponibilidad().getIdDisponibilidad(), EstadoTutoria.SOLICITADA, solicitud.getIdTutoria())) {
            otra.setEstadoTutoria(EstadoTutoria.RECHAZADA);
        }
        return true;
    }
}
