package com.duolingo.demo.dto;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String role;
    private String aulas; // <--- NUEVO CAMPO

    public JwtResponse(String accessToken, Long id, String username, String email, String role, String aulas) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.aulas = aulas; // <--- ASIGNAR
    }
}