import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import Swal from 'sweetalert2';
import { AdminService } from '../../../services/admin';

declare var bootstrap: any;

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './users.html',
  styleUrl: './users.css'
})
export class UsersComponent implements OnInit {

  activeTab: string = 'DOCENTE';
  userList: any[] = [];
  isEditing: boolean = false;

  // Objeto del usuario
  selectedUser: any = {
    id: null,
    username: '',
    email: '',
    password: '',
    role: 'DOCENTE',
    grado: '',
    seccion: '',
    aulas: '' // <--- El string largo del backend
  };

  // --- VARIABLES TEMPORALES PARA EL DOCENTE ---
  aulasDocente: string[] = []; // Lista visual: ["1ro-A", "2do-B"]
  tempGrado: string = '';
  tempSeccion: string = '';

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers() {
    this.adminService.getUsersByRole(this.activeTab).subscribe({
      next: (data: any) => this.userList = data,
      error: (e: any) => console.error(e)
    });
  }

  switchTab(tab: string) {
    this.activeTab = tab;
    this.loadUsers();
  }

  openModal(user?: any) {
    // Limpiamos temporales
    this.aulasDocente = [];
    this.tempGrado = '';
    this.tempSeccion = '';

    if (user) {
      this.isEditing = true;
      this.selectedUser = { ...user, password: '' };

      // --- LOGICA DE CARGA DE AULAS ---
      // Si es docente y tiene aulas guardadas (ej: "1ro-A,2do-B"), las convertimos a array
      if (this.selectedUser.role === 'DOCENTE' && this.selectedUser.aulas) {
         this.aulasDocente = this.selectedUser.aulas.split(',');
      }

    } else {
      this.isEditing = false;
      this.selectedUser = {
        id: null, username: '', email: '', password: '',
        role: this.activeTab, grado: '', seccion: '', aulas: ''
      };
    }
    const modal = new bootstrap.Modal(document.getElementById('userModal'));
    modal.show();
  }

  // --- MÉTODOS PARA AGREGAR/QUITAR AULAS (DOCENTE) ---
  agregarAula() {
    if (this.tempGrado && this.tempSeccion) {
        const nuevaAula = `${this.tempGrado}-${this.tempSeccion}`;

        // Evitar duplicados
        if (!this.aulasDocente.includes(nuevaAula)) {
            this.aulasDocente.push(nuevaAula);
        } else {
            Swal.fire('Atención', 'Esa aula ya está agregada', 'info');
        }

        // Resetear inputs pequeños
        this.tempGrado = '';
        this.tempSeccion = '';
    } else {
        Swal.fire('Falta info', 'Selecciona Grado y Sección primero', 'warning');
    }
  }

  borrarAula(aula: string) {
    this.aulasDocente = this.aulasDocente.filter(a => a !== aula);
  }

  saveUser() {
    // Validaciones básicas
    if (!this.selectedUser.username || !this.selectedUser.email) {
      Swal.fire('Error', 'Usuario y correo son obligatorios.', 'warning');
      return;
    }

    // --- VALIDACIÓN Y CONVERSIÓN SEGÚN ROL ---
    if (this.selectedUser.role === 'ESTUDIANTE') {
        if (!this.selectedUser.grado || !this.selectedUser.seccion) {
            Swal.fire('Falta información', 'El estudiante debe tener Grado y Sección', 'warning');
            return;
        }
        // Limpiamos el campo aulas por si acaso
        this.selectedUser.aulas = null;
    }
    else if (this.selectedUser.role === 'DOCENTE') {
        if (this.aulasDocente.length === 0) {
            Swal.fire('Falta información', 'Asigna al menos un aula al docente', 'warning');
            return;
        }
        // CONVERSIÓN FINAL: Array ["1ro-A", "2do-B"] -> String "1ro-A,2do-B"
        this.selectedUser.aulas = this.aulasDocente.join(',');

        // Limpiamos grado/seccion simples
        this.selectedUser.grado = null;
        this.selectedUser.seccion = null;
    }

    // Guardar o Editar
    if (this.isEditing) {
      if (!this.selectedUser.password) delete this.selectedUser.password;
      this.adminService.updateUser(this.selectedUser.id, this.selectedUser).subscribe({
        next: () => {
          Swal.fire('¡Éxito!', 'Usuario actualizado.', 'success');
          this.loadUsers();
          this.closeModal();
        },
        error: () => Swal.fire('Error', 'No se pudo actualizar.', 'error')
      });
    } else {
      if (!this.selectedUser.password) {
         Swal.fire('Password', 'La contraseña es obligatoria.', 'warning');
         return;
      }
      this.adminService.createUser(this.selectedUser).subscribe({
        next: () => {
          Swal.fire('¡Creado!', 'Usuario registrado.', 'success');
          this.loadUsers();
          this.closeModal();
        },
        error: () => Swal.fire('Error', 'No se pudo crear.', 'error')
      });
    }
  }

  deleteUser(user: any) {
     /* (Tu código de delete igual que antes) */
     this.adminService.deleteUser(user.id).subscribe(() => this.loadUsers());
  }

  closeModal() {
    const modalEl = document.getElementById('userModal');
    const modal = bootstrap.Modal.getInstance(modalEl);
    modal?.hide();
  }
}
