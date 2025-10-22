package org.example.hackaton1.service;



import lombok.RequiredArgsConstructor;
import org.example.hackaton1.Security.dto.JwtAuthenticationResponse;
import org.example.hackaton1.Security.dto.SigninRequest;
import org.example.hackaton1.Usuario.Rol;
import org.example.hackaton1.Usuario.Usuario;
import org.example.hackaton1.Usuario.UsuarioDTO;
import org.example.hackaton1.Usuario.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio principal que maneja toda la lógica de negocio relacionada con autenticación
 * Contiene los métodos para registro de usuarios y inicio de sesión
 * Actúa como capa intermedia entre los controladores y los repositorios
 */
@Service // Marca esta clase como un servicio de Spring para inyección de dependencias
@RequiredArgsConstructor // Genera constructor con todos los campos final (inyección por constructor)
public class AuthenticationService {

    // Encoder para cifrar y verificar contraseñas usando BCrypt
    private final PasswordEncoder passwordEncoder;
    
    // Repositorio para operaciones CRUD con usuarios
    private final UsuarioRepository usuarioRepository;
    
    // Servicio para generar y validar tokens JWT
    private final JwtService jwtService;
    
    // Manager central de autenticación de Spring Security
    private final AuthenticationManager authenticationManager;

    /**
     * Método para registrar un nuevo usuario en el sistema
     * Valida que el número no esté registrado, crea el usuario y genera un token JWT
     * 
     * @param usuarioDTO Datos del usuario a registrar (número, nombre, contraseña)
     * @return JwtAuthenticationResponse Token JWT para el usuario recién registrado
     * @throws RuntimeException Si el número ya está registrado
     */
    public JwtAuthenticationResponse register(UsuarioDTO usuarioDTO) {
        // Verifica si el número de teléfono ya está registrado y activo
        if (usuarioRepository.findByNumeroAndActivoTrue(usuarioDTO.getNumero()).isPresent()) {
            throw new RuntimeException("El número ya está registrado");
        }
        
        // Crea una nueva instancia de Usuario
        Usuario usuario = new Usuario();
        
        // Establece el número de teléfono (actúa como username)
        usuario.setNumero(usuarioDTO.getNumero());
        
        // Establece el nombre del usuario
        usuario.setNombre(usuarioDTO.getNombre());
        
        // Marca el usuario como activo por defecto
        usuario.setActivo(true);
        
        // Establece la fecha y hora actual de registro
        usuario.setFechaRegistro(java.time.LocalDateTime.now());
        
        // Asigna rol CLIENTE por defecto a todos los usuarios nuevos
        usuario.setRol(Rol.CLIENTE);
        
        // Cifra la contraseña usando BCrypt antes de guardarla
        usuario.setContrasena(passwordEncoder.encode(usuarioDTO.getContrasena()));
        
        // Guarda el usuario en la base de datos
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        // Genera un token JWT para el usuario recién registrado
        String jwt = jwtService.generateToken(usuarioGuardado);
        
        // Retorna la respuesta con el token
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Método para autenticar un usuario existente
     * Valida credenciales y genera un token JWT si son correctas
     * 
     * @param signinRequest Credenciales del usuario (número y contraseña)
     * @return JwtAuthenticationResponse Token JWT si las credenciales son válidas
     * @throws IllegalArgumentException Si faltan datos requeridos
     * @throws RuntimeException Si el número no existe o las credenciales son incorrectas
     */
    public JwtAuthenticationResponse signin(SigninRequest signinRequest) {
        // Validación de entrada: verifica que se envíen número y contraseña
        if (signinRequest.getNumero() == null || signinRequest.getNumero().isBlank() || 
            signinRequest.getContrasena() == null || signinRequest.getContrasena().isBlank()) {
            throw new IllegalArgumentException("Debes enviar número y contraseña");
        }
        
        // Busca el usuario por número de teléfono y que esté activo
        var usuarioOpt = usuarioRepository.findByNumeroAndActivoTrue(signinRequest.getNumero());
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Número no registrado");
        }
        
        // Obtiene el usuario encontrado
        Usuario usuario = usuarioOpt.get();
        
        // Verifica que la contraseña proporcionada coincida con la cifrada en la BD
        if (!passwordEncoder.matches(signinRequest.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        
        // Si las credenciales son correctas, genera un nuevo token JWT
        String jwt = jwtService.generateToken(usuario);
        
        // Retorna la respuesta con el token
        return new JwtAuthenticationResponse(jwt);
    }
}

