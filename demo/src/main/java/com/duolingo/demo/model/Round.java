package com.duolingo.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "rondas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private String descripcion;
    private String nivel; // A1, A2...
    private boolean activo = true;

    // --- NUEVO: ¿PARA QUIÉN ES ESTA RONDA? ---
    private String grado;   // Ej: "1ro"
    private String seccion; // Ej: "A"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creador_id")
    @JsonIgnore
    private User creador;

    @OneToMany(mappedBy = "ronda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exercise> ejercicios;

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Exercise> getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(List<Exercise> ejercicios) {
        this.ejercicios = ejercicios;
    }

    public User getCreador() {
        return creador;
    }

    public void setCreador(User creador) {
        this.creador = creador;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }
}