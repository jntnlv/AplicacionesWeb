package com.aulalink.repository;

import com.aulalink.domain.Materia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaRepository extends JpaRepository<Materia, Integer> {

    List<Materia> findAllByOrderByNombreAsc();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdMateriaNot(String nombre, Integer idMateria);
}
