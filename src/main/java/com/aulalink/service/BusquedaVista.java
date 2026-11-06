package com.aulalink.service;

import com.aulalink.domain.TutorMateria;

public record BusquedaVista(Integer idTutor, String nombre, String correo) {

    public BusquedaVista(TutorMateria relacion) {
        this(relacion.getTutor().getIdUsuario(), relacion.getTutor().getNombreCompleto(), relacion.getTutor().getCorreo());
    }
}
