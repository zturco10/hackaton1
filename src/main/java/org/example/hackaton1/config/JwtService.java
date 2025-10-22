<<<<<<< HEAD:src/main/java/org/example/hackaton1/Security/config/JwtService.java
package org.example.hackaton1.Security.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.hackaton1.Usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Servicio encargado del manejo completo de tokens JWT (JSON Web Tokens)
 * Proporciona funcionalidades para generar, validar y extraer información de tokens
 */
@Service // Marca esta clase como un servicio de Spring para inyección de dependencias
public class JwtService {

    // Clave secreta para firmar los tokens JWT, obtenida del archivo application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Tiempo de expiración del token en milisegundos, obtenido del archivo application.properties
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Método privado que construye la clave de firma segura
     * Convierte la clave secreta en string a una clave HMAC-SHA adecuada para JWT
     *
     * @return Key objeto clave para firmar/verificar tokens JWT
     */
    private Key getSigningKey() {
        // Convierte la clave secreta en bytes y crea una clave HMAC-SHA
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Genera un token JWT completo basado en los datos del usuario
     * El token incluye información básica del usuario y tiempo de expiración
     *
     * @param user Usuario para el cual generar el token
     * @return String token JWT firmado y listo para usar
     * @throws IllegalArgumentException si el usuario o campos requeridos son nulos
     */
    public String generateToken(Usuario user) {
        // Validación exhaustiva para evitar errores durante la generación del token
        if (user == null || user.getEmail() == null || user.getId() == null ||
                user.getUsername() == null || user.getRol() == null) {
            throw new IllegalArgumentException("Usuario o campos requeridos nulos para generar el token");
        }

        // Construcción del token JWT usando el patrón Builder
        return Jwts.builder()
                // Subject: identificador principal del usuario (número de teléfono)
                .setSubject(user.getEmail())

                // Claims personalizados: información adicional del usuario
                .claim("userId", user.getId())           // ID único del usuario
                .claim("nombre", user.getUsername())       // Nombre del usuario
                .claim("rol", user.getRol().name())     // Rol del usuario como string

                // Timestamps: cuándo fue emitido y cuándo expira
                .setIssuedAt(new Date())                                    // Fecha/hora actual
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Fecha de expiración

                // Firma: asegura la integridad del token
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)       // Algoritmo HMAC-SHA512

                // Construye y retorna el token final como string
                .compact();
    }

    /**
     * Verifica si un token JWT sigue siendo válido (no ha expirado)
     *
     * @param token Token JWT a verificar
     * @return true si el token es válido, false si ha expirado
     */
    public boolean isTokenValid(String token) {
        // Extrae la fecha de expiración y la compara con la fecha actual
        return extractExpiration(token).after(new Date());
    }

    /**
     * Extrae el subject (número de teléfono) del token JWT
     *
     * @param token Token JWT del cual extraer el subject
     * @return String número de teléfono del usuario
     */
    public String extractSubject(String token) {
        // Utiliza el método genérico extractClaim con una función lambda
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Método principal de validación que verifica firma y extrae el subject
     * Combina validación de integridad con extracción de datos
     *
     * @param token Token JWT a validar
     * @return String subject (número de teléfono) si el token es válido
     * @throws io.jsonwebtoken.JwtException si la firma es inválida o el token está expirado
     */
    public String validateAndGetSubject(String token) {
        // Este método automáticamente valida la firma y expiración
        // Si falla alguna validación, lanza una JwtException
        return extractSubject(token);
    }

    /**
     * Método genérico privado para extraer cualquier claim del token
     * Permite reutilizar código para diferentes tipos de claims
     *
     * @param token Token JWT del cual extraer el claim
     * @param claimsResolver Función que especifica qué claim extraer
     * @return T el valor del claim solicitado
     */
    private <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        // Extrae todos los claims del token
        final Claims claims = extractAllClaims(token);
        // Aplica la función para obtener el claim específico
        return claimsResolver.apply(claims);
    }

    /**
     * Método privado que extrae todos los claims del token JWT
     * Realiza la validación de firma automáticamente
     *
     * @param token Token JWT a parsear
     * @return Claims objeto con todos los claims del token
     * @throws io.jsonwebtoken.JwtException si la firma es inválida
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                // Establece la clave para verificar la firma
                .setSigningKey(getSigningKey())
                .build()
                // Parsea y valida el token (aquí ocurre la validación de firma y expiración)
                .parseClaimsJws(token)
                // Obtiene el cuerpo del token (los claims)
                .getBody();
    }

    /**
     * Método privado que extrae específicamente la fecha de expiración del token
     *
     * @param token Token JWT del cual extraer la fecha de expiración
     * @return Date fecha de expiración del token
     */
    private Date extractExpiration(String token) {
        // Utiliza el método genérico para extraer el claim de expiración
        return extractClaim(token, Claims::getExpiration);
    }
}
=======
package org.example.hackaton1.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.hackaton1.Usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Servicio encargado del manejo completo de tokens JWT (JSON Web Tokens)
 * Proporciona funcionalidades para generar, validar y extraer información de tokens
 */
@Service // Marca esta clase como un servicio de Spring para inyección de dependencias
public class JwtService {

    // Clave secreta para firmar los tokens JWT, obtenida del archivo application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Tiempo de expiración del token en milisegundos, obtenido del archivo application.properties
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Método privado que construye la clave de firma segura
     * Convierte la clave secreta en string a una clave HMAC-SHA adecuada para JWT
     * 
     * @return Key objeto clave para firmar/verificar tokens JWT
     */
    private Key getSigningKey() {
        // Convierte la clave secreta en bytes y crea una clave HMAC-SHA
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Genera un token JWT completo basado en los datos del usuario
     * El token incluye información básica del usuario y tiempo de expiración
     * 
     * @param user Usuario para el cual generar el token
     * @return String token JWT firmado y listo para usar
     * @throws IllegalArgumentException si el usuario o campos requeridos son nulos
     */
    public String generateToken(Usuario user) {
        // Validación exhaustiva para evitar errores durante la generación del token
        if (user == null || user.getNumero() == null || user.getId() == null || 
            user.getNombre() == null || user.getRol() == null) {
            throw new IllegalArgumentException("Usuario o campos requeridos nulos para generar el token");
        }
        
        // Construcción del token JWT usando el patrón Builder
        return Jwts.builder()
                // Subject: identificador principal del usuario (número de teléfono)
                .setSubject(user.getNumero())
                
                // Claims personalizados: información adicional del usuario
                .claim("userId", user.getId())           // ID único del usuario
                .claim("nombre", user.getNombre())       // Nombre del usuario
                .claim("rol", user.getRol().name())     // Rol del usuario como string
                
                // Timestamps: cuándo fue emitido y cuándo expira
                .setIssuedAt(new Date())                                    // Fecha/hora actual
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Fecha de expiración
                
                // Firma: asegura la integridad del token
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)       // Algoritmo HMAC-SHA512
                
                // Construye y retorna el token final como string
                .compact();
    }

    /**
     * Verifica si un token JWT sigue siendo válido (no ha expirado)
     * 
     * @param token Token JWT a verificar
     * @return true si el token es válido, false si ha expirado
     */
    public boolean isTokenValid(String token) {
        // Extrae la fecha de expiración y la compara con la fecha actual
        return extractExpiration(token).after(new Date());
    }

    /**
     * Extrae el subject (número de teléfono) del token JWT
     * 
     * @param token Token JWT del cual extraer el subject
     * @return String número de teléfono del usuario
     */
    public String extractSubject(String token) {
        // Utiliza el método genérico extractClaim con una función lambda
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Método principal de validación que verifica firma y extrae el subject
     * Combina validación de integridad con extracción de datos
     * 
     * @param token Token JWT a validar
     * @return String subject (número de teléfono) si el token es válido
     * @throws io.jsonwebtoken.JwtException si la firma es inválida o el token está expirado
     */
    public String validateAndGetSubject(String token) {
        // Este método automáticamente valida la firma y expiración
        // Si falla alguna validación, lanza una JwtException
        return extractSubject(token);
    }

    /**
     * Método genérico privado para extraer cualquier claim del token
     * Permite reutilizar código para diferentes tipos de claims
     * 
     * @param token Token JWT del cual extraer el claim
     * @param claimsResolver Función que especifica qué claim extraer
     * @return T el valor del claim solicitado
     */
    private <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        // Extrae todos los claims del token
        final Claims claims = extractAllClaims(token);
        // Aplica la función para obtener el claim específico
        return claimsResolver.apply(claims);
    }

    /**
     * Método privado que extrae todos los claims del token JWT
     * Realiza la validación de firma automáticamente
     * 
     * @param token Token JWT a parsear
     * @return Claims objeto con todos los claims del token
     * @throws io.jsonwebtoken.JwtException si la firma es inválida
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                // Establece la clave para verificar la firma
                .setSigningKey(getSigningKey())
                .build()
                // Parsea y valida el token (aquí ocurre la validación de firma y expiración)
                .parseClaimsJws(token)
                // Obtiene el cuerpo del token (los claims)
                .getBody();
    }

    /**
     * Método privado que extrae específicamente la fecha de expiración del token
     * 
     * @param token Token JWT del cual extraer la fecha de expiración
     * @return Date fecha de expiración del token
     */
    private Date extractExpiration(String token) {
        // Utiliza el método genérico para extraer el claim de expiración
        return extractClaim(token, Claims::getExpiration);
    }
}
>>>>>>> 72f457db4729ec57ae474cb21926179cb939d87c:src/main/java/org/example/hackaton1/config/JwtService.java
