package com.duolingo.demo.controller;

import com.duolingo.demo.dto.ExerciseDto;
import com.duolingo.demo.model.Exercise;
import com.duolingo.demo.service.ExerciseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasAuthority('DOCENTE') or hasAuthority('ADMIN')")
public class TeacherController {

    @Autowired
    private ExerciseService exerciseService;

    // CREAR EJERCICIO CON MULTIMEDIA
    @PostMapping(value = "/ejercicios", consumes = {"multipart/form-data"})
    public ResponseEntity<Exercise> createExercise(
            @RequestPart("exercise") String exerciseJson,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen,
            @RequestPart(value = "audio", required = false) MultipartFile audio
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ExerciseDto exerciseDto = mapper.readValue(exerciseJson, ExerciseDto.class);

            return ResponseEntity.ok(
                    exerciseService.crearEjercicio(exerciseDto, imagen, audio)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
