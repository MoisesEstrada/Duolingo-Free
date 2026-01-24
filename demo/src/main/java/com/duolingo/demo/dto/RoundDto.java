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
public class RoundDto {

    private Long id;
    private String titulo;
    private String descripcion;
    private String nivel;
    private boolean activo;

    private String grado;
    private String seccion;

    private Long creadorId;
    private String creadorNombre;
    private int cantidadEjercicios;

    // 🔥 AÑADIR ESTO
    private List<ExerciseDto> ejercicios;
}
