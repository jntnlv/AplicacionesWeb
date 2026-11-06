package com.aulalink.controller;

import com.aulalink.domain.Rol;
import com.aulalink.domain.UsuarioSesion;
import com.aulalink.service.EstudianteService;
import jakarta.servlet.http.HttpSession;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/estudiante")
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(HttpSession session, Model model) {
        UsuarioSesion administrador = administrador(session);
        if (administrador == null) {
            return sesionInvalida(session);
        }
        var estudiantes = estudianteService.listar();
        model.addAttribute("usuario", administrador);
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("totalEstudiantes", estudiantes.size());
        return "estudiante/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) Integer idUsuario,
            @RequestParam String nombreCompleto,
            @RequestParam String correo,
            @RequestParam(required = false) String contrasena,
            @RequestParam(defaultValue = "false") boolean activo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (administrador(session) == null) {
            return sesionInvalida(session);
        }
        String titulo = "todoOk";
        String clave = "estudiante.ok";
        try {
            estudianteService.guardar(idUsuario, nombreCompleto, correo, contrasena, activo);
        } catch (IllegalArgumentException ex) {
            titulo = "error";
            clave = ex.getMessage();
        } catch (DataIntegrityViolationException ex) {
            titulo = "error";
            clave = "estudiante.error.correo";
        }
        redirectAttributes.addFlashAttribute(titulo, mensaje(clave));
        return "redirect:/estudiante/listado";
    }

    private UsuarioSesion administrador(HttpSession session) {
        Object valor = session.getAttribute(AccesoController.SESION);
        if (valor instanceof UsuarioSesion usuario && usuario.getRol() == Rol.ADMIN) {
            return usuario;
        }
        return null;
    }

    private String sesionInvalida(HttpSession session) {
        Object valor = session.getAttribute(AccesoController.SESION);
        return valor == null ? "redirect:/" : "redirect:/inicio";
    }

    private String mensaje(String clave) {
        return messageSource.getMessage(clave, null, clave, Locale.forLanguageTag("es"));
    }
}
