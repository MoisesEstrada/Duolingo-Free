import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { TeacherService, Ronda } from '../../../services/teacher';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-create-round',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './create-round.html',
  styleUrls: ['./create-round.css']
})
export class CreateRoundComponent implements OnInit {

  ronda: any = {
    titulo: '',
    descripcion: '',
    nivel: 'A1',
    grado: '',
    seccion: '',
    ejercicios: []
  };

  tipos = [
    'TRADUCCION',
    'LISTENING',
    'IMAGEN',
    'SELECCION_MULTIPLE',
    'PRONUNCIACION',
    'VERDADERO_FALSO'
  ];

  enviando = false;
  esEdicion = false;
  idRonda: number | null = null;

  constructor(
    private teacherService: TeacherService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.esEdicion = true;
        this.idRonda = +id;
        this.cargarDatosRonda(this.idRonda);
      } else {
        this.agregarEjercicio();
      }
    });
  }

  cargarDatosRonda(id: number) {
    Swal.fire({ title: 'Cargando...', didOpen: () => Swal.showLoading() });

    this.teacherService.getRondaById(id).subscribe({
      next: (data: Ronda) => {
        this.ronda = data;

        this.ronda.grado ??= '';
        this.ronda.seccion ??= '';

        this.ronda.ejercicios.forEach((ex: any) => {
          ex.opcionesArray = ex.opciones
            ? ex.opciones.split(',')
            : [''];
        });

        Swal.close();
      },
      error: () => {
        Swal.fire('Error', 'No se pudo cargar la ronda', 'error');
        this.router.navigate(['/teacher/dashboard']);
      }
    });
  }

  agregarEjercicio() {
    this.ronda.ejercicios.push({
      tipo: 'TRADUCCION',
      enunciado: '',
      contenido: '',
      respuestaCorrecta: '',
      opciones: '',
      opcionesArray: ['']
    });
  }

  eliminarEjercicio(index: number) {
    this.ronda.ejercicios.splice(index, 1);
  }

  agregarOpcion(exIndex: number) {
    this.ronda.ejercicios[exIndex].opcionesArray.push('');
  }

  quitarOpcion(exIndex: number, optIndex: number) {
    const opciones = this.ronda.ejercicios[exIndex].opcionesArray;
    if (opciones.length > 1) {
      opciones.splice(optIndex, 1);
    }
  }

  onFileSelected(event: any, index: number) {
    const file = event.target.files[0];
    if (!file) return;

    this.teacherService.subirArchivo(file).subscribe({
      next: res => {
        this.ronda.ejercicios[index].contenido = res.fileName;
        Swal.fire({
          toast: true,
          position: 'top-end',
          icon: 'success',
          title: 'Archivo subido',
          timer: 2000,
          showConfirmButton: false
        });
      },
      error: () =>
        Swal.fire('Error', 'No se pudo subir el archivo', 'error')
    });
  }

  guardarRonda() {
    if (!this.ronda.titulo.trim()) {
      Swal.fire('Atención', 'Ponle un título a la ronda', 'warning');
      return;
    }

    if (!this.ronda.grado || !this.ronda.seccion) {
      Swal.fire('Falta información', 'Selecciona grado y sección', 'warning');
      return;
    }

    if (!this.ronda.ejercicios.length) {
      Swal.fire('Atención', 'Agrega al menos un ejercicio', 'warning');
      return;
    }

    this.enviando = true;

    this.ronda.ejercicios.forEach((ex: any) => {
      ex.opciones = Array.isArray(ex.opcionesArray)
        ? ex.opcionesArray.filter((o: string) => o.trim()).join(',')
        : '';
    });

    const request$ = this.esEdicion && this.idRonda
      ? this.teacherService.actualizarRonda(this.idRonda, this.ronda)
      : this.teacherService.crearRonda(this.ronda);

    request$.subscribe({
      next: () => {
        Swal.fire('Éxito', 'Ronda guardada correctamente', 'success');
        this.router.navigate(['/teacher/dashboard']);
      },
      error: () => {
        this.enviando = false;
        Swal.fire('Error', 'No se pudo guardar', 'error');
      }
    });
  }
}
