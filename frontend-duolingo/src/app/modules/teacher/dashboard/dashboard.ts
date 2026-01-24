import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TeacherService, Ronda, ReporteProgreso } from '../../../services/teacher';
import { AuthService } from '../../../services/auth';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-teacher-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit {

  rondas: Ronda[] = [];
  reportes: ReporteProgreso[] = [];
  vistaActual: 'dashboard' | 'reportes' = 'dashboard';
  filtroBusqueda: string = '';
  username = 'Docente';

  // --- ¡AQUÍ FALTABA ESTA VARIABLE! ---
  aulasDocente: string[] = [];

  cargando = true;
  error = false;

  constructor(
    private teacherService: TeacherService,
    private authService: AuthService,
    private router: Router,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Usamos 'any' para que no reclame si la interfaz User no tiene 'aulas' definido aún
    const user: any = this.authService.getUser();

    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    this.username = user.username;

    // --- CARGAR AULAS DESDE EL LOCALSTORAGE ---
    if (user.aulas) {
        this.aulasDocente = user.aulas.split(',');
    }

    this.cargarDatos();
  }

  get reportesFiltrados() {
    if (!this.filtroBusqueda) {
      return this.reportes;
    }
    const texto = this.filtroBusqueda.toLowerCase();
    return this.reportes.filter(rep =>
      rep.estudiante.username.toLowerCase().includes(texto)
    );
  }

  cargarDatos() {
    this.cargando = true;
    this.vistaActual = 'dashboard';

    this.teacherService.getRondas().subscribe({
      next: (data: any) => {
        this.rondas = Array.isArray(data) ? data : (data?.content || []);
        this.cargando = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        this.error = true;
        this.cargando = false;
        this.cd.detectChanges();
        Swal.fire('Error', 'No se pudo conectar con el servidor', 'error');
      }
    });
  }

  verReportes() {
    this.cargando = true;
    this.vistaActual = 'reportes';

    this.teacherService.obtenerReporteGlobal().subscribe({
      next: (data) => {
        this.reportes = data || [];
        this.cargando = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        this.cargando = false;
        this.vistaActual = 'dashboard';
        this.cd.detectChanges();
        Swal.fire('Error', 'No se pudieron cargar las notas.', 'error');
      }
    });
  }

  toggleStatus(ronda: Ronda): void {
    const estadoOriginal = ronda.activo;
    ronda.activo = !ronda.activo;

    this.teacherService.toggleRonda(ronda.id).subscribe({
      next: () => {
        this.cd.detectChanges();
        const Toast = Swal.mixin({
            toast: true, position: 'top-end', showConfirmButton: false, timer: 1500
        });
        Toast.fire({ icon: 'success', title: `Ronda ${ronda.activo ? 'Activada' : 'Desactivada'}` });
      },
      error: () => {
        ronda.activo = estadoOriginal;
        this.cd.detectChanges();
        Swal.fire('Error', 'No se pudo guardar el cambio', 'error');
      }
    });
  }

  deleteRound(id: number): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: "Esta acción no se puede deshacer",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.teacherService.eliminarRonda(id).subscribe({
          next: () => {
            this.rondas = this.rondas.filter(r => r.id !== id);
            this.cd.detectChanges();
            Swal.fire('Eliminado', 'La ronda ha sido eliminada', 'success');
          },
          error: () => Swal.fire('Error', 'No se pudo eliminar', 'error')
        });
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
