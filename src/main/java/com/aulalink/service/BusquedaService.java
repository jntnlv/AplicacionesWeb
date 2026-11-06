package com.aulalink.service;

import com.aulalink.domain.Materia;
import com.aulalink.domain.Rol;
import com.aulalink.domain.Usuario;
import com.aulalink.repository.DisponibilidadRepository;
import com.aulalink.repository.MateriaRepository;
import com.aulalink.domain.EstadoTutoria;
import com.aulalink.repository.TutorMateriaRepository;
import com.aulalink.repository.TutoriaRepository;
import com.aulalink.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusquedaService {

    private final MateriaRepository materiaRepository;
    private final TutorMateriaRepository tutorMateriaRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final TutoriaRepository tutoriaRepository;

    @Transactional(readOnly = true)
    public List<Materia> listarMaterias() {
        return materiaRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<BusquedaVista> buscarTutores(Integer idMateria) {
        if (idMateria == null || !materiaRepository.existsById(idMateria)) {
            return List.of();
        }
        return tutorMateriaRepository.findTutoresActivosPorMateria(idMateria, Rol.TUTOR).stream()
                .map(BusquedaVista::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> tutorActivo(Integer idTutor) {
        if (idTutor == null) {
            return Optional.empty();
        }
        return usuarioRepository.findById(idTutor)
                .filter(tutor -> tutor.getRol() == Rol.TUTOR && tutor.isActivo());
    }

    @Transactional(readOnly = true)
    public Optional<String> materiaDelTutor(Integer idTutor, Integer idMateria) {
        if (idTutor == null || idMateria == null
                || !tutorMateriaRepository.existsByTutor_IdUsuarioAndMateria_IdMateria(idTutor, idMateria)) {
            return Optional.empty();
        }
        return materiaRepository.findById(idMateria).map(Materia::getNombre);
    }

    @Transactional(readOnly = true)
    public List<HorarioLibreVista> horariosLibres(Integer idTutor, Integer idEstudiante) {
        LocalTime ahora = LocalTime.now().withSecond(0).withNano(0);
        Set<Integer> solicitadas = new HashSet<>(tutoriaRepository.findDisponibilidadesPorEstado(
                idEstudiante, idTutor, EstadoTutoria.SOLICITADA));
        return disponibilidadRepository.findLibresDesde(idTutor, LocalDate.now(), ahora).stream()
                .map(horario -> new HorarioLibreVista(horario, solicitadas.contains(horario.getIdDisponibilidad())))
                .toList();
    }
}
