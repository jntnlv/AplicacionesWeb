package com.aulalink.repository;

import com.aulalink.domain.Disponibilidad;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Integer> {

    List<Disponibilidad> findByTutor_IdUsuarioOrderByFechaAscHoraAsc(Integer idUsuario);

    boolean existsByTutor_IdUsuarioAndFechaAndHora(Integer idUsuario, LocalDate fecha, LocalTime hora);
}
