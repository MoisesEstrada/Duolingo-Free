package com.duolingo.demo.controller;

import com.duolingo.demo.model.Exercise;
import com.duolingo.demo.model.Progress;
import com.duolingo.demo.model.Round;
import com.duolingo.demo.model.User; // <--- Importar
import com.duolingo.demo.repository.RoundRepository; // <--- Importar
import com.duolingo.demo.repository.UserRepository; // <--- Importar
import com.duolingo.demo.service.ExerciseService;
import com.duolingo.demo.service.ProgressService;
import com.duolingo.demo.service.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // <--- Importar
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasAuthority('ESTUDIANTE')")
public class StudentController {

    @Autowired
    private RoundService roundService;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private ProgressService progressService;

    @Autowired
    private UserRepository userRepository; // <--- NECESARIO

    @Autowired
    private RoundRepository roundRepository; // <--- NECESARIO

    // 1. Ver MIS rondas (Filtradas por mi salón)
    @GetMapping("/rondas")
    public ResponseEntity<List<Round>> getMyRounds(Authentication auth) {

        // 1. Buscamos al estudiante conectado
        User estudiante = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // 2. Pedimos al repositorio SOLO las rondas de su Grado y Sección
        List<Round> misRondas = roundRepository.findByGradoAndSeccion(
                estudiante.getGrado(),
                estudiante.getSeccion()
        );

        return ResponseEntity.ok(misRondas);
    }

    // ... (El resto de métodos getExercisesForRound, saveProgress, etc. quedan IGUAL)
    @GetMapping("/rondas/{rondaId}/ejercicios")
    public ResponseEntity<List<Exercise>> getExercisesForRound(@PathVariable Long rondaId) {
        return ResponseEntity.ok(exerciseService.obtenerEjerciciosDeRonda(rondaId));
    }

    @PostMapping("/progreso")
    public ResponseEntity<Progress> saveProgress(
            @RequestParam Long estudianteId,
            @RequestParam Long rondaId,
            @RequestParam Integer puntaje) {
        return ResponseEntity.ok(progressService.guardarProgreso(estudianteId, rondaId, puntaje));
    }

    @GetMapping("/mi-historial/{estudianteId}")
    public ResponseEntity<List<Progress>> getMyHistory(@PathVariable Long estudianteId) {
        return ResponseEntity.ok(progressService.historialEstudiante(estudianteId));
    }
}