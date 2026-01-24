package com.duolingo.demo.service;

import com.duolingo.demo.dto.RoundDto; // <--- ASEGÚRATE DE TENER ESTE IMPORT
import com.duolingo.demo.model.Round;
import com.duolingo.demo.model.User;
import com.duolingo.demo.repository.RoundRepository;
import com.duolingo.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoundService {

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private UserRepository userRepository;

    // ============================================================
    // METODO PARA EL TEACHERCONTROLLER (El que te daba error)
    // ============================================================
    @Transactional
    public Round crearRonda(RoundDto dto) {
        User docente = userRepository.findById(dto.getCreadorId())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        Round ronda = new Round();
        ronda.setTitulo(dto.getTitulo());
        ronda.setDescripcion(dto.getDescripcion());
        ronda.setNivel(dto.getNivel());
        ronda.setCreador(docente);
        ronda.setActivo(true); // Por defecto activa

        return roundRepository.save(ronda);
    }

    // ============================================================
    // MÉTODOS PARA EL ROUNDCONTROLLER (Angular)
    // ============================================================

    public List<Round> listarTodas() {
        return roundRepository.findAll();
    }

    public List<Round> listarPorDocente(Long docenteId) {
        return roundRepository.findByCreadorId(docenteId);
    }

    public Round obtenerPorId(Long id) {
        return roundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ronda no encontrada con ID: " + id));
    }

    @Transactional
    public Round guardarRonda(Round ronda, User docente) {
        ronda.setCreador(docente);

        if (ronda.getEjercicios() != null) {
            ronda.getEjercicios().forEach(e -> e.setRonda(ronda));
        }

        return roundRepository.save(ronda);
    }

    @Transactional
    public Round actualizarRonda(Long id, Round datosNuevos) {
        Round existente = obtenerPorId(id);

        existente.setTitulo(datosNuevos.getTitulo());
        existente.setDescripcion(datosNuevos.getDescripcion());
        existente.setNivel(datosNuevos.getNivel());
        existente.setActivo(datosNuevos.isActivo());

        if (datosNuevos.getEjercicios() != null) {
            existente.getEjercicios().clear();
            datosNuevos.getEjercicios().forEach(ejercicio -> {
                ejercicio.setRonda(existente);
                existente.getEjercicios().add(ejercicio);
            });
        }

        return roundRepository.save(existente);
    }

    @Transactional
    public void toggleEstado(Long id) {
        Round ronda = obtenerPorId(id);
        ronda.setActivo(!ronda.isActivo());
        roundRepository.save(ronda);
    }

    @Transactional
    public void eliminarRonda(Long id) {
        roundRepository.deleteById(id);
    }
}