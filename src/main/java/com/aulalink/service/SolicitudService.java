package com.aulalink.service;

import com.aulalink.domain.Disponibilidad;
import com.aulalink.domain.EstadoHorario;
import com.aulalink.domain.EstadoTutoria;
import com.aulalink.domain.Materia;
import com.aulalink.domain.Rol;
import com.aulalink.domain.Tutoria;
import com.aulalink.domain.Usuario;
import com.aulalink.repository.DisponibilidadRepository;
import com.aulalink.repository.MateriaRepository;
import com.aulalink.repository.TutorMateriaRepository;
import com.aulalink.repository.TutoriaRepository;
import com.aulalink.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SolicitudService {

    private final TutoriaRepository tutoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MateriaRepository materiaRepository;
    private final TutorMateriaRepository tutorMateriaRepository;
    private final DisponibilidadRepository disponibilidadRepository;

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

    @Transactional
    public void solicitar(Integer idEstudiante, Integer idTutor, Integer idMateria, Integer idDisponibilidad) {
        Usuario estudiante = usuarioRepository.findById(idEstudiante)
                .filter(usuario -> usuario.getRol() == Rol.ESTUDIANTE && usuario.isActivo())
                .orElseThrow(() -> new IllegalArgumentException("solicitud.error.datos"));
        Usuario tutor = usuarioRepository.findById(idTutor)
                .filter(usuario -> usuario.getRol() == Rol.TUTOR && usuario.isActivo())
                .orElseThrow(() -> new IllegalArgumentException("solicitud.error.datos"));
        Materia materia = idMateria == null ? null : materiaRepository.findById(idMateria).orElse(null);
        if (materia == null || !tutorMateriaRepository.existsByTutor_IdUsuarioAndMateria_IdMateria(idTutor, idMateria)) {
            throw new IllegalArgumentException("solicitud.error.materia");
        }
        Disponibilidad horario = disponibilidadRepository.findById(idDisponibilidad)
                .orElseThrow(() -> new IllegalArgumentException("solicitud.error.horario"));
        if (!horario.getTutor().getIdUsuario().equals(tutor.getIdUsuario()) || !sigueLibre(horario)) {
            throw new IllegalArgumentException("solicitud.error.horario");
        }
        if (tutoriaRepository.existsByEstudiante_IdUsuarioAndDisponibilidad_IdDisponibilidadAndEstadoTutoriaIn(
                idEstudiante, idDisponibilidad, List.of(EstadoTutoria.SOLICITADA, EstadoTutoria.ACEPTADA))) {
            throw new IllegalArgumentException("solicitud.error.duplicada");
        }
        Tutoria solicitud = new Tutoria();
        solicitud.setEstudiante(estudiante);
        solicitud.setTutor(tutor);
        solicitud.setMateria(materia);
        solicitud.setDisponibilidad(horario);
        solicitud.setEstadoTutoria(EstadoTutoria.SOLICITADA);
        tutoriaRepository.save(solicitud);
    }

    private boolean sigueLibre(Disponibilidad horario) {
        if (horario.getEstadoHorario() != EstadoHorario.DISPONIBLE) {
            return false;
        }
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now().withSecond(0).withNano(0);
        return horario.getFecha().isAfter(hoy)
                || (horario.getFecha().isEqual(hoy) && !horario.getHora().isBefore(ahora));
    }
}
