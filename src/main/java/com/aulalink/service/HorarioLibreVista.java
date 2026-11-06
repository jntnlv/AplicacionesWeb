package com.aulalink.service;

import com.aulalink.domain.Disponibilidad;
import java.time.format.DateTimeFormatter;

public record HorarioLibreVista(Integer idDisponibilidad, String fecha, String hora, boolean solicitada) {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    public HorarioLibreVista(Disponibilidad horario, boolean solicitada) {
        this(horario.getIdDisponibilidad(), horario.getFecha().format(FECHA), horario.getHora().format(HORA), solicitada);
    }
}
