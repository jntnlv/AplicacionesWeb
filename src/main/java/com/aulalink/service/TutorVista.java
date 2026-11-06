package com.aulalink.service;

import com.aulalink.domain.Materia;
import com.aulalink.domain.Usuario;
import java.util.List;
import lombok.Getter;

@Getter
public class TutorVista {

    private final Usuario tutor;
    private final List<Materia> materias;

    public TutorVista(Usuario tutor, List<Materia> materias) {
        this.tutor = tutor;
        this.materias = materias;
    }

    public String getTextoMaterias() {
        if (materias.isEmpty()) {
            return "";
        }
        return materias.stream().map(Materia::getNombre).reduce((a, b) -> a + ", " + b).orElse("");
    }

    public String getIdsMaterias() {
        return materias.stream().map(materia -> String.valueOf(materia.getIdMateria())).reduce((a, b) -> a + "," + b).orElse("");
    }
}
