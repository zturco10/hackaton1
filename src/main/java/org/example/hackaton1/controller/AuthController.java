package org.example.hackaton1.controller;


import jakarta.validation.Valid;
import org.example.hackaton1.Security.dto.JwtAuthenticationResponse;
import org.example.hackaton1.Security.dto.SigninRequest;
// importar el DTO que espera el servicio
import org.example.hackaton1.Usuario.UsuarioDTO;
import org.example.hackaton1.Security.service.AuthenticationService;
import org.example.hackaton1.Usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//

/**
 * Controlador REST que maneja todas las operaciones de autenticación
 * Proporciona endpoints para registro, login y gestión de usuarios
 * Todos los endpoints están bajo la ruta base "/api/auth"
 */
@RestController // Indica que es un controlador REST que retorna JSON automáticamente
@RequestMapping("/api/auth") // Ruta base para todos los endpoints de este controlador
public class AuthController {

    // Servicio que contiene la lógica de negocio para autenticación
    @Autowired
    private AuthenticationService authenticationService;

    // Repositorio para acceso directo a datos de usuarios (para casos específicos)
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Endpoint para registrar un nuevo usuario en el sistema
     * Acepta datos del usuario en formato UsuarioDTO y retorna un token JWT
     *
     * @param usuarioDTO Datos del usuario a registrar (número, contraseña, nombre, etc.)
     * @return ResponseEntity con token JWT si el registro es exitoso
     */
    @PostMapping("/register") // Maneja peticiones POST a /api/auth/register
    public ResponseEntity<?> register(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        // Delega la lógica de registro al servicio de autenticación
        // El servicio se encarga de validar datos, cifrar contraseña y crear usuario
        JwtAuthenticationResponse tokenResponse = authenticationService.register(usuarioDTO);

        // Retorna respuesta HTTP 200 OK con el token JWT
        return ResponseEntity.ok(tokenResponse);
    }

    /**
     * Endpoint para iniciar sesión con credenciales de usuario
     * Valida número de teléfono y contraseña, retorna token JWT si son válidas
     *
     * @param signinRequest Objeto con número y contraseña del usuario
     * @return ResponseEntity con token JWT si las credenciales son válidas, 401 si no
     */
    @PostMapping("/login") // Maneja peticiones POST a /api/auth/login
    public ResponseEntity<?> login(@RequestBody SigninRequest signinRequest) {
        // Delega la autenticación al servicio
        // El servicio verifica credenciales y genera token si son correctas
        JwtAuthenticationResponse response = authenticationService.signin(signinRequest);

        // Retorna respuesta HTTP 200 OK con el token JWT
        return ResponseEntity.ok(response);
    }



}
