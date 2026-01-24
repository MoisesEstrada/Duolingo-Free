package com.duolingo.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;

    // --- ESTOS SON LOS CAMPOS QUE TE FALTABAN ---
    // ...
    private String grado;
    private String seccion;

    // --- NUEVO ---
    private String aulas;
}