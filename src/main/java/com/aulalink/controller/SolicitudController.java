package com.aulalink.controller;

import com.aulalink.domain.Rol;
import com.aulalink.domain.UsuarioSesion;
import com.aulalink.service.SolicitudService;
import jakarta.servlet.http.HttpSession;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/solicitud")
public class SolicitudController {

    private static final String VISTA = "solicitud/listado";

    private final SolicitudService solicitudService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(HttpSession session, Model model) {
        UsuarioSesion tutor = tutor(session);
        if (tutor == null) {
            return sesionInvalida(session);
        }
        var solicitudes = solicitudService.listarRecibidas(tutor.getIdUsuario());
        model.addAttribute("usuario", tutor);
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("totalSolicitudes", solicitudes.size());
        return VISTA;
    }

    @PostMapping("/decidir")
    public String decidir(@RequestParam Integer idTutoria,
            @RequestParam String decision,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        UsuarioSesion tutor = tutor(session);
        if (tutor == null) {
            return sesionInvalida(session);
        }
        String titulo = "todoOk";
        String clave = "aceptar".equals(decision) ? "solicitud.ok.aceptada" : "solicitud.ok.rechazada";
        try {
            boolean aceptada = solicitudService.decidir(tutor.getIdUsuario(), idTutoria, "aceptar".equals(decision));
            clave = aceptada ? "solicitud.ok.aceptada" : "solicitud.ok.rechazada";
        } catch (IllegalArgumentException ex) {
            titulo = "error";
            clave = ex.getMessage();
        }
        redirectAttributes.addFlashAttribute(titulo, mensaje(clave));
        return "redirect:/solicitud/listado";
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
