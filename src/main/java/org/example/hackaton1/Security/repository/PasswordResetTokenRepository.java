package org.example.hackaton1.Security.repository;



import org.example.hackaton1.Security.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio para el manejo de tokens de restablecimiento de contraseña
 * Extiende JpaRepository para operaciones CRUD automáticas con la base de datos
 * Proporciona métodos personalizados para consultas específicas de tokens
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    /**
     * Busca un token de restablecimiento que coincida con el token dado y que no haya sido usado
     * Este método es crucial para validar tokens de restablecimiento de contraseña
     * 
     * @param token El string del token a buscar
     * @return Optional<PasswordResetToken> Token encontrado o vacío si no existe o ya fue usado
     */
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);
}