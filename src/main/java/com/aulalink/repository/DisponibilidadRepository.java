package com.aulalink.repository;

import com.aulalink.domain.Disponibilidad;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Integer> {

    List<Disponibilidad> findByTutor_IdUsuarioOrderByFechaAscHoraAsc(Integer idUsuario);

    boolean existsByTutor_IdUsuarioAndFechaAndHora(Integer idUsuario, LocalDate fecha, LocalTime hora);

    @Query("""
            select horario from Disponibilidad horario
            where horario.tutor.idUsuario = :idTutor
            and horario.estadoHorario = com.aulalink.domain.EstadoHorario.DISPONIBLE
            and (horario.fecha > :hoy or (horario.fecha = :hoy and horario.hora >= :ahora))
            order by horario.fecha asc, horario.hora asc
            """)
    List<Disponibilidad> findLibresDesde(@Param("idTutor") Integer idTutor,
            @Param("hoy") LocalDate hoy,
            @Param("ahora") LocalTime ahora);
}
