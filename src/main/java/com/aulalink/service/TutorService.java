package com.aulalink.service;

import com.aulalink.domain.Materia;
import com.aulalink.domain.Rol;
import com.aulalink.domain.TutorMateria;
import com.aulalink.domain.Usuario;
import com.aulalink.repository.MateriaRepository;
import com.aulalink.repository.TutorMateriaRepository;
import com.aulalink.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TutorService {

    private final UsuarioRepository usuarioRepository;
    private final MateriaRepository materiaRepository;
    private final TutorMateriaRepository tutorMateriaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<TutorVista> listar() {
        Map<Integer, TutorVista> porTutor = new LinkedHashMap<>();
        for (Usuario tutor : usuarioRepository.findByRolOrderByNombreCompletoAsc(Rol.TUTOR)) {
            porTutor.put(tutor.getIdUsuario(), new TutorVista(tutor, new ArrayList<>()));
        }
        for (TutorMateria relacion : tutorMateriaRepository.findConMateriaPorRol(Rol.TUTOR)) {
            TutorVista vista = porTutor.get(relacion.getTutor().getIdUsuario());
            if (vista != null) {
                vista.getMaterias().add(relacion.getMateria());
            }
        }
        return List.copyOf(porTutor.values());
    }

    @Transactional(readOnly = true)
    public List<Materia> materiasDisponibles() {
        return materiaRepository.findAllByOrderByNombreAsc();
    }

    @Transactional
    public void guardar(Integer idUsuario, String nombreCompleto, String correo, String contrasena, List<Integer> idMaterias) {
        String nombre = nombreCompleto == null ? "" : nombreCompleto.trim();
        String correoNormalizado = correo == null ? "" : correo.trim().toLowerCase();
        boolean esNuevo = idUsuario == null;
        if (nombre.isBlank() || nombre.length() > 120
                || correoNormalizado.isBlank() || !correoNormalizado.contains("@")
                || (esNuevo && (contrasena == null || contrasena.length() < 8))
                || (!esNuevo && contrasena != null && !contrasena.isBlank() && contrasena.length() < 8)) {
            throw new IllegalArgumentException("tutor.error.datos");
        }

        Usuario tutor;
        if (esNuevo) {
            if (usuarioRepository.existsByCorreoIgnoreCase(correoNormalizado)) {
                throw new IllegalArgumentException("tutor.error.correo");
            }
            tutor = new Usuario();
            tutor.setNombreCompleto(nombre);
            tutor.setCorreo(correoNormalizado);
            tutor.setContrasena(passwordEncoder.encode(contrasena));
            tutor.setRol(Rol.TUTOR);
            tutor.setActivo(true);
            tutor = usuarioRepository.save(tutor);
        } else {
            tutor = usuarioRepository.findById(idUsuario)
                    .filter(usuario -> usuario.getRol() == Rol.TUTOR)
                    .orElseThrow(() -> new IllegalArgumentException("tutor.error.noExiste"));
            if (usuarioRepository.existsByCorreoIgnoreCaseAndIdUsuarioNot(correoNormalizado, idUsuario)) {
                throw new IllegalArgumentException("tutor.error.correo");
            }
            tutor.setNombreCompleto(nombre);
            tutor.setCorreo(correoNormalizado);
            if (contrasena != null && !contrasena.isBlank()) {
                tutor.setContrasena(passwordEncoder.encode(contrasena));
            }
        }
        asignarMaterias(tutor, idMaterias);
    }

    private void asignarMaterias(Usuario tutor, List<Integer> idMaterias) {
        Set<Integer> ids = new LinkedHashSet<>();
        if (idMaterias != null) {
            ids.addAll(idMaterias);
        }
        List<Materia> materias = ids.isEmpty() ? List.of() : materiaRepository.findAllById(ids);
        if (materias.size() != ids.size()) {
            throw new IllegalArgumentException("tutor.error.materia");
        }
        tutorMateriaRepository.deleteAll(tutorMateriaRepository.findByTutor_IdUsuario(tutor.getIdUsuario()));
        tutorMateriaRepository.flush();
        for (Materia materia : materias) {
            TutorMateria relacion = new TutorMateria();
            relacion.setTutor(tutor);
            relacion.setMateria(materia);
            tutorMateriaRepository.save(relacion);
        }
    }
}
