package com.duolingo.demo.controller;

import com.duolingo.demo.dto.UserDto;
import com.duolingo.demo.model.Progress;
import com.duolingo.demo.model.Role;
import com.duolingo.demo.model.User;
import com.duolingo.demo.repository.ProgressRepository;
import com.duolingo.demo.repository.UserRepository; // <--- AGREGADO
import com.duolingo.demo.service.AdminService;
import com.duolingo.demo.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // <--- AGREGADO
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // <--- AGREGADO

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private UserRepository userRepository; // <--- NECESARIO PARA EL FILTRO

    // --- REPORTES INTELIGENTES (FILTRADO POR AULAS) ---
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCENTE')")
    @GetMapping("/progreso-global")
    public ResponseEntity<List<Progress>> obtenerTodoElProgreso(Authentication auth) {

        // 1. Obtenemos al usuario que hace la petición
        String username = auth.getName();
        User usuarioActual = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Traemos TODO el historial (Lazy approach, filtrado en memoria)
        List<Progress> todos = progressRepository.findAll();

        // 3. Aplicamos lógica según Rol
        if (usuarioActual.getRole() == Role.ADMIN) {
            // ADMIN VE TODO
            return ResponseEntity.ok(todos);
        } else {
            // DOCENTE VE SOLO SUS AULAS
            // El campo aulas es un string tipo: "1ro-A,2do-B,5to-C"
            String aulasAsignadas = usuarioActual.getAulas();

            if (aulasAsignadas == null || aulasAsignadas.isEmpty()) {
                return ResponseEntity.ok(List.of()); // No tiene aulas asignadas
            }

            // Filtramos la lista
            List<Progress> filtrados = todos.stream()
                    .filter(p -> {
                        User estudiante = p.getEstudiante();
                        // Creamos la clave del estudiante: "1ro-A"
                        String claveEstudiante = estudiante.getGrado() + "-" + estudiante.getSeccion();

                        // Verificamos si esa clave está dentro de las aulas del profe
                        return aulasAsignadas.contains(claveEstudiante);
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(filtrados);
        }
    }

    // --- GESTIÓN DE USUARIOS (SOLO ADMIN) ---

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsersByRole(@RequestParam String role) {
        try {
            Role rolEnum = Role.valueOf(role.toUpperCase());
            return ResponseEntity.ok(adminService.obtenerUsuariosPorRol(rolEnum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
        try {
            User nuevo = adminService.crearUsuarioManual(userDto);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        try {
            User actualizado = adminService.actualizarUsuario(id, userDto);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            adminService.eliminarUsuario(id);
            return ResponseEntity.ok("Usuario eliminado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/upload-users")
    public ResponseEntity<String> uploadUsersFromPdf(@RequestParam("file") MultipartFile file) {
        try {
            adminService.registrarAlumnosDesdePdf(file);
            return ResponseEntity.ok("Carga masiva completada.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // --- GESTIÓN MULTIMEDIA ---
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCENTE')")
    @PostMapping("/upload-multimedia")
    public ResponseEntity<?> uploadMultimedia(@RequestParam("file") MultipartFile file) {
        try {
            String filePath = fileStorageService.store(file);
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            return ResponseEntity.ok(Map.of("fileName", fileName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al subir archivo: " + e.getMessage());
        }
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> getFile(@PathVariable String filename) {
        try {
            org.springframework.core.io.Resource file = fileStorageService.load(filename);
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}