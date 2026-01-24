package com.duolingo.demo.service;

import com.duolingo.demo.dto.ExerciseDto;
import com.duolingo.demo.model.Exercise;
import com.duolingo.demo.model.Round;
import com.duolingo.demo.model.TipoEjercicio;
import com.duolingo.demo.repository.ExerciseRepository;
import com.duolingo.demo.repository.RoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private FileStorageService fileStorageService;

    // =====================================================
    // CREAR EJERCICIO (CON MULTIMEDIA)
    // =====================================================
    @Transactional
    public Exercise crearEjercicio(ExerciseDto dto, MultipartFile imagen, MultipartFile audio) {

        Round ronda = roundRepository.findById(dto.getRondaId())
                .orElseThrow(() -> new RuntimeException("Ronda no encontrada"));

        Exercise ex = new Exercise();
        ex.setEnunciado(dto.getEnunciado());
        ex.setRespuestaCorrecta(dto.getRespuestaCorrecta());

        // 🔥 ENUM SEGURO (evita errores desde Angular)
        ex.setTipo(TipoEjercicio.valueOf(dto.getTipo().toUpperCase()));

        // 🔥 CAMPOS QUE ANTES NO SE GUARDABAN
        ex.setContenido(dto.getContenido());
        ex.setOpciones(dto.getOpciones());
        ex.setOpcionesIncorrectas(dto.getOpcionesIncorrectas());

        // 🔥 RELACIÓN BIDIRECCIONAL CORRECTA
        ex.setRonda(ronda);
        if (ronda.getEjercicios() == null) {
            ronda.setEjercicios(new ArrayList<>());
        }
        ronda.getEjercicios().add(ex);

        // 🔊 Archivos multimedia
        if (imagen != null && !imagen.isEmpty()) {
            ex.setImagenUrl(fileStorageService.store(imagen));
        }

        if (audio != null && !audio.isEmpty()) {
            ex.setAudioUrl(fileStorageService.store(audio));
        }

        return exerciseRepository.save(ex);
    }

    // =====================================================
    // OBTENER EJERCICIOS DE UNA RONDA
    // =====================================================
    public List<Exercise> obtenerEjerciciosDeRonda(Long rondaId) {
        return exerciseRepository.findByRondaId(rondaId);
    }
}
