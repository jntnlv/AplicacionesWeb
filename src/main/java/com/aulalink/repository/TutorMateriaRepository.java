package com.aulalink.repository;

import com.aulalink.domain.Rol;
import com.aulalink.domain.TutorMateria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TutorMateriaRepository extends JpaRepository<TutorMateria, Integer> {

    List<TutorMateria> findByTutor_IdUsuario(Integer idUsuario);

    @Query("""
            select relacion from TutorMateria relacion
            join fetch relacion.materia
            join fetch relacion.tutor tutor
            where tutor.rol = :rol
            order by tutor.nombreCompleto asc, relacion.materia.nombre asc
            """)
    List<TutorMateria> findConMateriaPorRol(@Param("rol") Rol rol);
}
