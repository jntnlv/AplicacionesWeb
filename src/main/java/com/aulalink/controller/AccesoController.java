package com.aulalink.controller;

import com.aulalink.domain.Rol;
import com.aulalink.domain.Usuario;
import com.aulalink.domain.UsuarioSesion;
import com.aulalink.service.AccesoService;
import jakarta.servlet.http.HttpSession;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AccesoController {

    static final String SESION = "usuarioSesion";

    private final AccesoService accesoService;
    private final MessageSource messageSource;

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombreCompleto,
            @RequestParam String correo,
            @RequestParam String contrasena,
            RedirectAttributes redirectAttributes) {
        try {
            accesoService.registrarEstudiante(nombreCompleto, correo, contrasena);
            redirectAttributes.addFlashAttribute("registroOk", mensaje("acceso.ok.registro"));
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorRegistro", mensaje(ex.getMessage()));
            redirectAttributes.addFlashAttribute("nombreCompleto", nombreCompleto);
            redirectAttributes.addFlashAttribute("correoRegistro", correo);
        } catch (DataAccessException | TransactionException ex) {
            redirectAttributes.addFlashAttribute("errorRegistro", mensaje("acceso.error.bd"));
            redirectAttributes.addFlashAttribute("nombreCompleto", nombreCompleto);
            redirectAttributes.addFlashAttribute("correoRegistro", correo);
        }
        return "redirect:/#registro";
    }

    @PostMapping("/login")
    public String login(@RequestParam String correo,
            @RequestParam String contrasena,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = accesoService.autenticar(correo, contrasena);
            session.setAttribute(SESION, new UsuarioSesion(usuario));
            if (usuario.getRol() == Rol.ADMIN) {
                return "redirect:/estudiante/listado";
            }
            if (usuario.getRol() == Rol.TUTOR) {
                return "redirect:/horario/listado";
            }
            return "redirect:/inicio";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorLogin", mensaje(ex.getMessage()));
            redirectAttributes.addFlashAttribute("correoLogin", correo);
            return "redirect:/#iniciar-sesion";
        } catch (DataAccessException | TransactionException ex) {
            redirectAttributes.addFlashAttribute("errorLogin", mensaje("acceso.error.bd"));
            redirectAttributes.addFlashAttribute("correoLogin", correo);
            return "redirect:/#iniciar-sesion";
        }
    }

    @GetMapping("/inicio")
    public String panel(HttpSession session, Model model) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute(SESION);
        if (usuario == null) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        return "acceso/inicio";
    }

    @PostMapping("/salir")
    public String salir(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private String mensaje(String clave) {
        return messageSource.getMessage(clave, null, clave, Locale.forLanguageTag("es"));
    }
}
