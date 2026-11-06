package com.aulalink.service;

import com.aulalink.domain.Materia;
import com.aulalink.repository.MateriaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepository;

    @Transactional(readOnly = true)
    public List<Materia> listar() {
        return materiaRepository.findAllByOrderByNombreAsc();
    }

    @Transactional
    public void guardar(Integer idMateria, String nombre) {
        String nombreNormalizado = nombre == null ? "" : nombre.trim();
        if (nombreNormalizado.isBlank() || nombreNormalizado.length() > 80) {
            throw new IllegalArgumentException("materia.error.datos");
        }
        boolean esNueva = idMateria == null;
        if (esNueva) {
            if (materiaRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
                throw new IllegalArgumentException("materia.error.nombre");
            }
            Materia materia = new Materia();
            materia.setNombre(nombreNormalizado);
            materiaRepository.save(materia);
            return;
        }
        Materia materia = materiaRepository.findById(idMateria)
                .orElseThrow(() -> new IllegalArgumentException("materia.error.noExiste"));
        if (materiaRepository.existsByNombreIgnoreCaseAndIdMateriaNot(nombreNormalizado, idMateria)) {
            throw new IllegalArgumentException("materia.error.nombre");
        }
        materia.setNombre(nombreNormalizado);
    }
}
