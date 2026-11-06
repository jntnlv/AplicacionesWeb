package com.aulalink.repository;

import com.aulalink.domain.Rol;
import com.aulalink.domain.TutorMateria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TutorMateriaRepository extends JpaRepository<TutorMateria, Integer> {

    List<TutorMateria> findByTutor_IdUsuario(Integer idUsuario);

    boolean existsByTutor_IdUsuarioAndMateria_IdMateria(Integer idTutor, Integer idMateria);

    @Query("""
            select relacion from TutorMateria relacion
            join fetch relacion.materia
            join fetch relacion.tutor tutor
            where tutor.rol = :rol
            order by tutor.nombreCompleto asc, relacion.materia.nombre asc
            """)
    List<TutorMateria> findConMateriaPorRol(@Param("rol") Rol rol);

    @Query("""
            select relacion from TutorMateria relacion
            join fetch relacion.tutor tutor
            join fetch relacion.materia materia
            where materia.idMateria = :idMateria
            and tutor.rol = :rol
            and tutor.activo = true
            order by tutor.nombreCompleto asc
            """)
    List<TutorMateria> findTutoresActivosPorMateria(@Param("idMateria") Integer idMateria, @Param("rol") Rol rol);
}
