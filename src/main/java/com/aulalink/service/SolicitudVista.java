package com.aulalink.service;

import com.aulalink.domain.EstadoTutoria;
import com.aulalink.domain.Tutoria;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

@Getter
public class SolicitudVista {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final Integer idTutoria;
    private final String estudiante;
    private final String materia;
    private final String fecha;
    private final String hora;
    private final String estado;
    private final boolean pendiente;

    public SolicitudVista(Tutoria tutoria) {
        this.idTutoria = tutoria.getIdTutoria();
        this.estudiante = tutoria.getEstudiante().getNombreCompleto();
        this.materia = tutoria.getMateria().getNombre();
        this.fecha = tutoria.getDisponibilidad().getFecha().format(FECHA);
        this.hora = tutoria.getDisponibilidad().getHora().format(HORA);
        this.pendiente = tutoria.getEstadoTutoria() == EstadoTutoria.SOLICITADA;
        this.estado = switch (tutoria.getEstadoTutoria()) {
            case SOLICITADA -> "Solicitada";
            case ACEPTADA -> "Aceptada";
            case RECHAZADA -> "Rechazada";
            case REALIZADA -> "Realizada";
            case CANCELADA -> "Cancelada";
        };
    }
}
