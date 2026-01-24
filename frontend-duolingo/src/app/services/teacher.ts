import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/* ========= DTOs ========= */

export interface EjercicioDTO {
  id?: number;
  tipo: string;
  enunciado: string;
  contenido?: string;
  respuestaCorrecta: string;
  opciones?: string;
}

export interface CrearRondaDTO {
  titulo: string;
  descripcion: string;
  nivel: string;
  grado: string;
  seccion: string;
  ejercicios: EjercicioDTO[];
}

export interface Ronda {
  id: number;
  titulo: string;
  descripcion: string;
  nivel: string;
  grado: string;
  seccion: string;
  creadorId: number;
  creadorNombre: string;
  cantidadEjercicios: number;
  activo?: boolean;
  ejercicios: EjercicioDTO[];
}

export interface ReporteProgreso {
  id: number;
  puntaje: number;
  fechaRealizacion: string;
  estudiante: {
    id: number;
    username: string;
    grado?: string;
    seccion?: string;
  };
  ronda: {
    id: number;
    titulo: string;
    nivel: string;
  };
}

@Injectable({ providedIn: 'root' })
export class TeacherService {

  private apiRounds = 'http://localhost:8080/api/rounds';
  private apiAdmin  = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  /* ========= RONDAS ========= */

  getRondas(): Observable<Ronda[]> {
    return this.http.get<Ronda[]>(this.apiRounds);
  }

  getRondaById(id: number): Observable<Ronda> {
    return this.http.get<Ronda>(`${this.apiRounds}/${id}`);
  }

  crearRonda(ronda: CrearRondaDTO): Observable<Ronda> {
    return this.http.post<Ronda>(this.apiRounds, ronda);
  }

  actualizarRonda(id: number, ronda: CrearRondaDTO): Observable<Ronda> {
    return this.http.put<Ronda>(`${this.apiRounds}/${id}`, ronda);
  }

  toggleRonda(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiRounds}/${id}/toggle`, {});
  }

  eliminarRonda(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiRounds}/${id}`);
  }

  /* ========= MULTIMEDIA ========= */

  subirArchivo(file: File): Observable<{ fileName: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ fileName: string }>(
      `${this.apiAdmin}/upload-multimedia`,
      formData
    );
  }

  /* ========= REPORTES ========= */

  obtenerReporteGlobal(): Observable<ReporteProgreso[]> {
    return this.http.get<ReporteProgreso[]>(
      `${this.apiAdmin}/progreso-global`
    );
  }
}
