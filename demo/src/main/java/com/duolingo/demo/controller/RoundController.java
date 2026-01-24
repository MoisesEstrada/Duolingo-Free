package com.duolingo.demo.controller;

import com.duolingo.demo.dto.ExerciseDto;
import com.duolingo.demo.dto.RoundDto;
import com.duolingo.demo.model.Exercise;
import com.duolingo.demo.model.Round;
import com.duolingo.demo.model.TipoEjercicio;
import com.duolingo.demo.model.User;
import com.duolingo.demo.repository.UserRepository;
import com.duolingo.demo.service.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rounds")
public class RoundController {

    @Autowired
    private RoundService roundService;

    @Autowired
    private UserRepository userRepository;

    // =====================================================
    // OBTENER RONDAS (LISTA DEL DOCENTE)
    // =====================================================
    @GetMapping
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ADMIN')")
    public ResponseEntity<List<RoundDto>> getMyRounds(Authentication authentication) {

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Round> rondas = user.getRole().name().equals("ADMIN")
                ? roundService.listarTodas()
                : roundService.listarPorDocente(user.getId());

        List<RoundDto> response = rondas.stream()
                .map(r -> RoundDto.builder()
                        .id(r.getId())
                        .titulo(r.getTitulo())
                        .descripcion(r.getDescripcion())
                        .nivel(r.getNivel())
                        .activo(r.isActivo())
                        // --- ENVIAMOS GRADO Y SECCIÓN AL FRONT ---
                        .grado(r.getGrado())
                        .seccion(r.getSeccion())
                        .creadorId(r.getCreador() != null ? r.getCreador().getId() : null)
                        .creadorNombre(r.getCreador() != null ? r.getCreador().getUsername() : "—")
                        .cantidadEjercicios(r.getEjercicios() != null ? r.getEjercicios().size() : 0)
                        .build()
                )
                .toList();

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // OBTENER UNA (PARA EDITAR)
    // =====================================================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ADMIN')")
    public ResponseEntity<RoundDto> getOne(@PathVariable Long id) {

        Round r = roundService.obtenerPorId(id);

        RoundDto dto = RoundDto.builder()
                .id(r.getId())
                .titulo(r.getTitulo())
                .descripcion(r.getDescripcion())
                .nivel(r.getNivel())
                .activo(r.isActivo())
                .grado(r.getGrado())
                .seccion(r.getSeccion())
                .creadorId(r.getCreador() != null ? r.getCreador().getId() : null)
                .creadorNombre(r.getCreador() != null ? r.getCreador().getUsername() : "—")
                .cantidadEjercicios(r.getEjercicios() != null ? r.getEjercicios().size() : 0)
                // 🔥 ESTA ES LA CLAVE
                .ejercicios(
                        r.getEjercicios() == null ? List.of() :
                                r.getEjercicios().stream().map(e -> ExerciseDto.builder()
                                        .id(e.getId())
                                        .tipo(e.getTipo().name())
                                        .enunciado(e.getEnunciado())
                                        .contenido(e.getContenido())
                                        .respuestaCorrecta(e.getRespuestaCorrecta())
                                        .opciones(e.getOpciones())
                                        .build()
                                ).toList()
                )
                .build();

        return ResponseEntity.ok(dto);
    }

    // =====================================================
    // CREAR NUEVA RONDA (¡AQUÍ GUARDAMOS EL AULA!)
    // =====================================================
    @PostMapping
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<Round> create(
            @RequestBody RoundDto roundDto,
            Authentication authentication
    ) {

        User docente = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        Round ronda = new Round();
        ronda.setTitulo(roundDto.getTitulo());
        ronda.setDescripcion(roundDto.getDescripcion());
        ronda.setNivel(roundDto.getNivel());
        ronda.setGrado(roundDto.getGrado());
        ronda.setSeccion(roundDto.getSeccion());
        ronda.setActivo(true);
        ronda.setCreador(docente);

        // 🔥 AQUÍ ESTABA EL ERROR
        if (roundDto.getEjercicios() != null) {
            List<Exercise> ejercicios = roundDto.getEjercicios().stream().map(dto -> {
                Exercise e = new Exercise();
                e.setTipo(TipoEjercicio.valueOf(dto.getTipo()));
                e.setEnunciado(dto.getEnunciado());
                e.setContenido(dto.getContenido());
                e.setRespuestaCorrecta(dto.getRespuestaCorrecta());
                e.setOpciones(dto.getOpciones());
                e.setRonda(ronda); // MUY IMPORTANTE
                return e;
            }).toList();

            ronda.setEjercicios(ejercicios);
        }

        return ResponseEntity.ok(roundService.guardarRonda(ronda, docente));
    }

    // =====================================================
    // EDITAR RONDA
    // =====================================================
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<Round> update(
            @PathVariable Long id,
            @RequestBody RoundDto roundDto
    ) {

        Round existente = roundService.obtenerPorId(id);

        existente.setTitulo(roundDto.getTitulo());
        existente.setDescripcion(roundDto.getDescripcion());
        existente.setNivel(roundDto.getNivel());
        existente.setActivo(roundDto.isActivo());
        existente.setGrado(roundDto.getGrado());
        existente.setSeccion(roundDto.getSeccion());

        if (roundDto.getEjercicios() != null) {
            existente.getEjercicios().clear();

            roundDto.getEjercicios().forEach(dto -> {
                Exercise e = new Exercise();
                e.setTipo(TipoEjercicio.valueOf(dto.getTipo()));
                e.setEnunciado(dto.getEnunciado());
                e.setContenido(dto.getContenido());
                e.setRespuestaCorrecta(dto.getRespuestaCorrecta());
                e.setOpciones(dto.getOpciones());
                e.setRonda(existente);
                existente.getEjercicios().add(e);
            });
        }

        return ResponseEntity.ok(roundService.guardarRonda(existente, existente.getCreador()));
    }


    // =====================================================
    // ACTIVAR / DESACTIVAR
    // =====================================================
    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<Void> toggle(@PathVariable Long id) {
        roundService.toggleEstado(id);
        return ResponseEntity.ok().build();
    }

    // =====================================================
    // ELIMINAR
    // =====================================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roundService.eliminarRonda(id);
        return ResponseEntity.ok().build();
    }
}