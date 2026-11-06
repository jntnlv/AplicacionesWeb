package com.aulalink.domain;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class UsuarioSesion implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer idUsuario;
    private final String nombreCompleto;
    private final String correo;
    private final Rol rol;

    public UsuarioSesion(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nombreCompleto = usuario.getNombreCompleto();
        this.correo = usuario.getCorreo();
        this.rol = usuario.getRol();
    }
}
