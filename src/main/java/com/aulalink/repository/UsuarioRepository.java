package com.aulalink.repository;

import com.aulalink.domain.Rol;
import com.aulalink.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCaseAndIdUsuarioNot(String correo, Integer idUsuario);

    List<Usuario> findByRolOrderByNombreCompletoAsc(Rol rol);
}
