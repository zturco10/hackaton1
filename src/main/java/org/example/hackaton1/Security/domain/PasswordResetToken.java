package org.example.hackaton1.Security.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un token para restablecer contraseñas olvidadas
 * Se almacena en la base de datos y tiene un tiempo de vida limitado por seguridad
 */
@Entity // Marca esta clase como una entidad JPA que se mapea a una tabla
@Table(name = "reset_tokens") // Especifica el nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode automáticamente
@Builder // Permite usar el patrón Builder para crear instancias
@NoArgsConstructor // Genera constructor sin argumentos (requerido por JPA)
@AllArgsConstructor // Genera constructor con todos los argumentos
public class PasswordResetToken {
    
    // Identificador único del token en la base de datos
    @Id // Marca este campo como clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento en la BD
    private Long id;

    // El token único que se envía al usuario (generalmente un UUID o string aleatorio)
    private String token;
    
    // Fecha y hora cuando el token expira (después de esta fecha es inválido)
    private LocalDateTime expiryDate;
    
    // Bandera que indica si el token ya fue utilizado (evita reutilización)
    private boolean used;

    /**
     * Método que se ejecuta automáticamente antes de guardar en la base de datos
     * Establece la fecha de expiración del token a 15 minutos desde su creación
     */
    @PrePersist // Anotación JPA que ejecuta este método antes de persistir
    protected void onCreate() {
        // Establece la expiración a 15 minutos desde ahora por razones de seguridad
        expiryDate = LocalDateTime.now().plusMinutes(15);
    }
}