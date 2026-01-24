    package com.duolingo.demo.model;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import com.fasterxml.jackson.annotation.JsonIgnore;

    import java.util.List;

    @Entity
    @Table(name = "ejercicios")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Exercise {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 500)
        private String enunciado;

        @Column(nullable = false)
        private String respuestaCorrecta;

        @Enumerated(EnumType.STRING)
        private TipoEjercicio tipo;

        private String opciones; // El String de comas que viene de Angular

        @ElementCollection
        @CollectionTable(name = "ejercicio_opciones", joinColumns = @JoinColumn(name = "ejercicio_id"))
        @Column(name = "opcion")
        private List<String> opcionesIncorrectas;

        private String imagenUrl;
        private String audioUrl;
        private String contenido; // Match con el front

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "ronda_id")
        @JsonIgnore // Rompe la recursión infinita
        private Round ronda;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEnunciado() {
            return enunciado;
        }

        public void setEnunciado(String enunciado) {
            this.enunciado = enunciado;
        }

        public String getRespuestaCorrecta() {
            return respuestaCorrecta;
        }

        public void setRespuestaCorrecta(String respuestaCorrecta) {
            this.respuestaCorrecta = respuestaCorrecta;
        }

        public TipoEjercicio getTipo() {
            return tipo;
        }

        public void setTipo(TipoEjercicio tipo) {
            this.tipo = tipo;
        }

        public String getOpciones() {
            return opciones;
        }

        public void setOpciones(String opciones) {
            this.opciones = opciones;
        }

        public List<String> getOpcionesIncorrectas() {
            return opcionesIncorrectas;
        }

        public void setOpcionesIncorrectas(List<String> opcionesIncorrectas) {
            this.opcionesIncorrectas = opcionesIncorrectas;
        }

        public String getImagenUrl() {
            return imagenUrl;
        }

        public void setImagenUrl(String imagenUrl) {
            this.imagenUrl = imagenUrl;
        }

        public String getAudioUrl() {
            return audioUrl;
        }

        public void setAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
        }

        public String getContenido() {
            return contenido;
        }

        public void setContenido(String contenido) {
            this.contenido = contenido;
        }

        public Round getRonda() {
            return ronda;
        }

        public void setRonda(Round ronda) {
            this.ronda = ronda;
        }
    }