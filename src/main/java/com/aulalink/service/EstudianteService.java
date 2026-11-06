package com.aulalink.service;

import com.aulalink.domain.Rol;
import com.aulalink.domain.Usuario;
import com.aulalink.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstudianteService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findByRolOrderByNombreCompletoAsc(Rol.ESTUDIANTE);
    }

    @Transactional
    public void guardar(Integer idUsuario, String nombreCompleto, String correo, String contrasena, boolean activo) {
        String nombre = nombreCompleto == null ? "" : nombreCompleto.trim();
        String correoNormalizado = correo == null ? "" : correo.trim().toLowerCase();
        boolean esNuevo = idUsuario == null;
        if (nombre.isBlank() || nombre.length() > 120
                || correoNormalizado.isBlank() || !correoNormalizado.contains("@")
                || (esNuevo && (contrasena == null || contrasena.length() < 8))
                || (!esNuevo && contrasena != null && !contrasena.isBlank() && contrasena.length() < 8)) {
            throw new IllegalArgumentException("estudiante.error.datos");
        }

        if (esNuevo) {
            if (usuarioRepository.existsByCorreoIgnoreCase(correoNormalizado)) {
                throw new IllegalArgumentException("estudiante.error.correo");
            }
            Usuario estudiante = new Usuario();
            estudiante.setNombreCompleto(nombre);
            estudiante.setCorreo(correoNormalizado);
            estudiante.setContrasena(passwordEncoder.encode(contrasena));
            estudiante.setRol(Rol.ESTUDIANTE);
            estudiante.setActivo(activo);
            usuarioRepository.save(estudiante);
            return;
        }

        Usuario estudiante = usuarioRepository.findById(idUsuario)
                .filter(usuario -> usuario.getRol() == Rol.ESTUDIANTE)
                .orElseThrow(() -> new IllegalArgumentException("estudiante.error.noExiste"));
        if (usuarioRepository.existsByCorreoIgnoreCaseAndIdUsuarioNot(correoNormalizado, idUsuario)) {
            throw new IllegalArgumentException("estudiante.error.correo");
        }
        estudiante.setNombreCompleto(nombre);
        estudiante.setCorreo(correoNormalizado);
        estudiante.setActivo(activo);
        if (contrasena != null && !contrasena.isBlank()) {
            estudiante.setContrasena(passwordEncoder.encode(contrasena));
        }
    }
}
