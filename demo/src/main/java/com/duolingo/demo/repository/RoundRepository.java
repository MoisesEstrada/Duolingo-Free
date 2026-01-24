package com.duolingo.demo.repository;

import com.duolingo.demo.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {

    List<Round> findByCreadorId(Long creadorId);

    // --- NUEVO: FILTRO PARA EL ESTUDIANTE ---
    // "Dame las rondas que sean para 1ro y para la sección A"
    List<Round> findByGradoAndSeccion(String grado, String seccion);
}