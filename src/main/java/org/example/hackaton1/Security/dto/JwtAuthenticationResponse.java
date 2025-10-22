package org.example.hackaton1.Security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para la respuesta de autenticación exitosa
 * Se envía al cliente después de un login o registro exitoso
 * Contiene el token JWT que el cliente debe usar para futuras peticiones autenticadas
 */
@Data // Genera getters, setters, toString, equals y hashCode automáticamente
@AllArgsConstructor // Genera constructor que acepta todos los campos como parámetros
public class JwtAuthenticationResponse {
    
    /**
     * Token JWT que contiene la información del usuario autenticado
     * Este token debe ser enviado en el header "Authorization: Bearer <token>"
     * en todas las peticiones que requieren autenticación
     */
    private String token;
}
