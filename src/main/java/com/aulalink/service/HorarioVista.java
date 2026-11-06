package com.aulalink.service;

import com.aulalink.domain.Disponibilidad;
import com.aulalink.domain.EstadoHorario;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

@Getter
public class HorarioVista {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final String fecha;
    private final String hora;
    private final boolean disponible;

    public HorarioVista(Disponibilidad horario) {
        this.fecha = horario.getFecha().format(FECHA);
        this.hora = horario.getHora().format(HORA);
        this.disponible = horario.getEstadoHorario() == EstadoHorario.DISPONIBLE;
    }
}
