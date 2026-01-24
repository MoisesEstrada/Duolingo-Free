package com.duolingo.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    private Long id;
    private String enunciado;
    private String respuestaCorrecta;
    private String tipo; // Se queda como String para comunicación con Angular

    private String opciones;
    private String contenido;
    private List<String> opcionesIncorrectas;
    private String imagenUrl;
    private String audioUrl;
    private Long rondaId;
}