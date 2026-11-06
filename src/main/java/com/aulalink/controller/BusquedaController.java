package com.aulalink.controller;

import com.aulalink.domain.Rol;
import com.aulalink.domain.UsuarioSesion;
import com.aulalink.service.BusquedaService;
import com.aulalink.service.BusquedaVista;
import com.aulalink.service.SolicitudService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
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
@RequestMapping("/buscar")
public class BusquedaController {

    private final BusquedaService busquedaService;
    private final SolicitudService solicitudService;
    private final MessageSource messageSource;

    @GetMapping
    public String buscar(@RequestParam(required = false) Integer idMateria, HttpSession session, Model model) {
        UsuarioSesion estudiante = estudiante(session);
        if (estudiante == null) {
            return sesionInvalida(session);
        }
        var materias = busquedaService.listarMaterias();
        boolean buscado = idMateria != null && materias.stream().anyMatch(materia -> materia.getIdMateria().equals(idMateria));
        List<BusquedaVista> tutores = buscado ? busquedaService.buscarTutores(idMateria) : List.of();
        model.addAttribute("usuario", estudiante);
        model.addAttribute("materias", materias);
        model.addAttribute("idMateria", buscado ? idMateria : null);
        model.addAttribute("buscado", buscado);
        model.addAttribute("tutores", tutores);
        model.addAttribute("totalTutores", tutores.size());
        return "busqueda/listado";
    }

    @GetMapping("/horarios")
    public String horarios(@RequestParam Integer idTutor,
            @RequestParam(required = false) Integer idMateria,
            HttpSession session,
            Model model) {
        UsuarioSesion estudiante = estudiante(session);
        if (estudiante == null) {
            return sesionInvalida(session);
        }
        var tutor = busquedaService.tutorActivo(idTutor);
        var materia = busquedaService.materiaDelTutor(idTutor, idMateria);
        if (tutor.isEmpty() || materia.isEmpty()) {
            return idMateria == null ? "redirect:/buscar" : "redirect:/buscar?idMateria=" + idMateria;
        }
        var horarios = busquedaService.horariosLibres(idTutor, estudiante.getIdUsuario());
        model.addAttribute("usuario", estudiante);
        model.addAttribute("tutorNombre", tutor.get().getNombreCompleto());
        model.addAttribute("materiaNombre", materia.get());
        model.addAttribute("idTutor", idTutor);
        model.addAttribute("idMateria", idMateria);
        model.addAttribute("horarios", horarios);
        model.addAttribute("totalHorarios", horarios.size());
        return "busqueda/horarios";
    }

    @PostMapping("/solicitar")
    public String solicitar(@RequestParam Integer idTutor,
            @RequestParam Integer idMateria,
            @RequestParam Integer idDisponibilidad,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        UsuarioSesion estudiante = estudiante(session);
        if (estudiante == null) {
            return sesionInvalida(session);
        }
        String titulo = "todoOk";
        String clave = "solicitud.ok.enviada";
        try {
            solicitudService.solicitar(estudiante.getIdUsuario(), idTutor, idMateria, idDisponibilidad);
        } catch (IllegalArgumentException ex) {
            titulo = "error";
            clave = ex.getMessage();
        }
        redirectAttributes.addFlashAttribute(titulo, mensaje(clave));
        return "redirect:/buscar/horarios?idTutor=" + idTutor + "&idMateria=" + idMateria;
    }

    private String mensaje(String clave) {
        return messageSource.getMessage(clave, null, clave, Locale.forLanguageTag("es"));
    }

    private UsuarioSesion estudiante(HttpSession session) {
        Object valor = session.getAttribute(AccesoController.SESION);
        if (valor instanceof UsuarioSesion usuario && usuario.getRol() == Rol.ESTUDIANTE) {
            return usuario;
        }
        return null;
    }

    private String sesionInvalida(HttpSession session) {
        Object valor = session.getAttribute(AccesoController.SESION);
        return valor == null ? "redirect:/" : "redirect:/inicio";
    }
}
