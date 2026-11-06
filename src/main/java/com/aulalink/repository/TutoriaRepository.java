package com.aulalink.repository;

import com.aulalink.domain.EstadoTutoria;
import com.aulalink.domain.Tutoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TutoriaRepository extends JpaRepository<Tutoria, Integer> {

    @Query("""
            select tutoria from Tutoria tutoria
            join fetch tutoria.estudiante
            join fetch tutoria.materia
            join fetch tutoria.disponibilidad
            where tutoria.tutor.idUsuario = :idTutor
            order by tutoria.disponibilidad.fecha asc, tutoria.disponibilidad.hora asc
            """)
    List<Tutoria> findRecibidas(@Param("idTutor") Integer idTutor);

    List<Tutoria> findByDisponibilidad_IdDisponibilidadAndEstadoTutoriaAndIdTutoriaNot(
            Integer idDisponibilidad, EstadoTutoria estadoTutoria, Integer idTutoria);
}
