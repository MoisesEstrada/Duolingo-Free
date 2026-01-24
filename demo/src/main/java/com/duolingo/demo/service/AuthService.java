package com.duolingo.demo.service;

import com.duolingo.demo.dto.JwtResponse;
import com.duolingo.demo.dto.LoginRequest;
import com.duolingo.demo.security.JwtUtils;
import com.duolingo.demo.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        // 1. Intentar autenticar con usuario y contraseña
        // Si falla (contraseña incorrecta), Spring lanza una excepción automáticamente
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 2. Si pasó, guardamos la sesión en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generamos el Token JWT firmado
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 4. Obtenemos los datos del usuario logueado para enviarlos al frontend
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Obtenemos el rol (ej: "ROLE_ADMIN" o "ADMIN")
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(item -> item.getAuthority())
                .orElse("ESTUDIANTE");

        // 5. Retornamos el paquete completo
        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                role,
                userDetails.getAulas());    }
}