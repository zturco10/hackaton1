package org.example.hackaton1.Security.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) para las peticiones de inicio de sesión
 * Contiene las credenciales que el usuario envía para autenticarse
 * Se recibe en el endpoint POST /api/auth/login
 */
@Data // Genera getters, setters, toString, equals y hashCode automáticamente
public class SigninRequest {
    
    /**
     * Número de teléfono del usuario (usado como username/identificador)
     * Debe corresponder con el número registrado en la base de datos
     */
    private String numero;
    
    /**
     * Contraseña en texto plano enviada por el cliente
     * Será comparada con la contraseña cifrada almacenada en la base de datos
     * usando BCrypt para verificación segura
     */
    private String contrasena;
}
