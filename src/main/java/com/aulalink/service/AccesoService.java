package com.aulalink.service;

import com.aulalink.domain.Rol;
import com.aulalink.domain.Usuario;
import com.aulalink.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccesoService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registrarEstudiante(String nombreCompleto, String correo, String contrasena) {
        String nombre = nombreCompleto == null ? "" : nombreCompleto.trim();
        String correoNormalizado = normalizarCorreo(correo);
        if (nombre.isBlank() || nombre.length() > 120
                || correoNormalizado.isBlank() || !correoNormalizado.contains("@")
                || contrasena == null || contrasena.length() < 8) {
            throw new IllegalArgumentException("acceso.error.datos");
        }
        if (usuarioRepository.existsByCorreoIgnoreCase(correoNormalizado)) {
            throw new IllegalArgumentException("acceso.error.correo");
        }
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(nombre);
        usuario.setCorreo(correoNormalizado);
        usuario.setContrasena(passwordEncoder.encode(contrasena));
        usuario.setRol(Rol.ESTUDIANTE);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario autenticar(String correo, String contrasena) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(normalizarCorreo(correo))
                .orElseThrow(() -> new IllegalArgumentException("acceso.error.credenciales"));
        if (contrasena == null || !passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            throw new IllegalArgumentException("acceso.error.credenciales");
        }
        if (!usuario.isActivo()) {
            throw new IllegalArgumentException("acceso.error.inactivo");
        }
        return usuario;
    }

    private String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase();
    }
}
