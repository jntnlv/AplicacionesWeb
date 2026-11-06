package com.aulalink.service;

import com.aulalink.domain.Disponibilidad;
import com.aulalink.domain.EstadoHorario;
import com.aulalink.domain.Rol;
import com.aulalink.domain.Usuario;
import com.aulalink.repository.DisponibilidadRepository;
import com.aulalink.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HorarioService {

    private final DisponibilidadRepository disponibilidadRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<HorarioVista> listar(Integer idTutor) {
        return disponibilidadRepository.findByTutor_IdUsuarioOrderByFechaAscHoraAsc(idTutor).stream()
                .map(HorarioVista::new)
                .toList();
    }

    @Transactional
    public void guardar(Integer idTutor, String fechaTexto, String horaTexto) {
        LocalDate fecha;
        LocalTime hora;
        try {
            fecha = LocalDate.parse(fechaTexto);
            hora = LocalTime.parse(horaTexto).withSecond(0).withNano(0);
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException("horario.error.datos");
        }
        if (disponibilidadRepository.existsByTutor_IdUsuarioAndFechaAndHora(idTutor, fecha, hora)) {
            throw new IllegalArgumentException("horario.error.duplicado");
        }
        Usuario tutor = usuarioRepository.findById(idTutor)
                .filter(usuario -> usuario.getRol() == Rol.TUTOR)
                .orElseThrow(() -> new IllegalArgumentException("horario.error.tutor"));
        Disponibilidad horario = new Disponibilidad();
        horario.setTutor(tutor);
        horario.setFecha(fecha);
        horario.setHora(hora);
        horario.setEstadoHorario(EstadoHorario.DISPONIBLE);
        disponibilidadRepository.save(horario);
    }
}
