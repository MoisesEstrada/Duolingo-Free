package com.duolingo.demo.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String role; // "DOCENTE" o "ESTUDIANTE"

    // --- NUEVOS CAMPOS ---
    private String grado;
    private String seccion;
}