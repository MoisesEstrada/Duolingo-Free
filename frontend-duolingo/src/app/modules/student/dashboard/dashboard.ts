import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { StudentService, Ronda, Progress } from '../../../services/student';
import { AuthService } from '../../../services/auth';

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit {

  // 🔥 NUEVO: rondas separadas por nivel
  rondasBasico: Ronda[] = [];
  rondasIntermedio: Ronda[] = [];

  historial: Progress[] = [];
  username = 'Estudiante';
  userId: number = 0;
  cargando = true;

  constructor(
    private studentService: StudentService,
    private authService: AuthService,
    private router: Router,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUser();
    if (user) {
      this.username = user.username;
      this.userId = user.id;
    }
    this.cargarDatos();
  }

  cargarDatos() {
    this.cargando = true;

    this.studentService.getRondas().subscribe({
      next: (data: any) => {
        const todas = Array.isArray(data) ? data : [];

        const activas = todas.filter((r: Ronda) => r.activo == true);

        // 🧠 ORDEN PERSONALIZADO
        const ordenNivel: any = { A1: 1, A2: 2, B1: 3, B2: 4 };
        activas.sort((a, b) => ordenNivel[a.nivel] - ordenNivel[b.nivel]);

        // 🔥 CLASIFICACIÓN
        this.rondasBasico = activas.filter(r => r.nivel === 'A1' || r.nivel === 'A2');
        this.rondasIntermedio = activas.filter(r => r.nivel === 'B1' || r.nivel === 'B2');

        this.cargarHistorial();
      },
      error: () => {
        this.cargando = false;
        this.cd.detectChanges();
      }
    });
  }

  cargarHistorial() {
    this.studentService.getHistorial(this.userId).subscribe({
      next: (hist: any) => {
        this.historial = Array.isArray(hist) ? hist : [];
        this.cargando = false;
        this.cd.detectChanges();
      },
      error: () => {
        this.cargando = false;
        this.cd.detectChanges();
      }
    });
  }

  getPuntajeRonda(rondaId: number): number | null {
    const progreso = this.historial.find(h => h.ronda?.id === rondaId);
    return progreso ? progreso.puntaje : null;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
