package com.aulalink.controller;

import com.aulalink.domain.Rol;
import com.aulalink.domain.UsuarioSesion;
import com.aulalink.service.HorarioService;
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
@RequestMapping("/horario")
public class HorarioController {

    private final HorarioService horarioService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(HttpSession session, Model model) {
        UsuarioSesion tutor = tutor(session);
        if (tutor == null) {
            return sesionInvalida(session);
        }
        var horarios = horarioService.listar(tutor.getIdUsuario());
        model.addAttribute("usuario", tutor);
        model.addAttribute("horarios", horarios);
        model.addAttribute("totalHorarios", horarios.size());
        return "horario/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam String fecha,
            @RequestParam String hora,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        UsuarioSesion tutor = tutor(session);
        if (tutor == null) {
            return sesionInvalida(session);
        }
        String titulo = "todoOk";
        String clave = "horario.ok";
        try {
            horarioService.guardar(tutor.getIdUsuario(), fecha, hora);
        } catch (IllegalArgumentException ex) {
            titulo = "error";
            clave = ex.getMessage();
        } catch (DataIntegrityViolationException ex) {
            titulo = "error";
            clave = "horario.error.duplicado";
        }
        redirectAttributes.addFlashAttribute(titulo, mensaje(clave));
        return "redirect:/horario/listado";
    }

    private UsuarioSesion tutor(HttpSession session) {
        Object valor = session.getAttribute(AccesoController.SESION);
        if (valor instanceof UsuarioSesion usuario && usuario.getRol() == Rol.TUTOR) {
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
