package com.duolingo.demo.repository;

import com.duolingo.demo.model.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {

    List<Progress> findByEstudianteId(Long estudianteId);
    Optional<Progress> findByEstudianteIdAndRondaId(Long estudianteId, Long rondaId);

    // --- FILTRO INTELIGENTE ---
    // Trae los progresos DONDE el grado y sección del estudiante coincidan con los parámetros
    @Query("SELECT p FROM Progress p WHERE p.estudiante.grado = :grado AND p.estudiante.seccion = :seccion")
    List<Progress> findByGradoAndSeccion(@Param("grado") String grado, @Param("seccion") String seccion);
}