
package org.example.hackaton1.Security.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.hackaton1.Security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación JWT que intercepta todas las peticiones HTTP
 * Se ejecuta una sola vez por petición para validar tokens JWT
 * Extiende OncePerRequestFilter para garantizar ejecución única por request
 */
@Component // Registra esta clase como componente de Spring para inyección de dependencias
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Servicio para manejo y validación de tokens JWT
    @Autowired
    private JwtService jwtService;

    // Servicio que carga los detalles del usuario desde la base de datos
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Método principal del filtro que se ejecuta en cada petición
     * Extrae el JWT del header, lo valida y configura el contexto de seguridad
     * 
     * @param request La petición HTTP entrante
     * @param response La respuesta HTTP saliente
     * @param filterChain Cadena de filtros de Spring Security
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        // Intenta extraer el token JWT del header Authorization
        String jwt = getJwtFromRequest(request);

        // Si se encuentra un token JWT en el header
        if (jwt != null) {
            try {
                // Valida el token y extrae el subject (número de teléfono del usuario)
                String correo = jwtService.validateAndGetSubject(jwt);

                // Carga los detalles completos del usuario usando el número de teléfono
                UserDetails userDetails = userDetailsService.loadUserByUsername(correo);
                
                // Crea el objeto de autenticación con los datos del usuario
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,    // Principal: datos del usuario autenticado
                                null,          // Credentials: no necesarias después de validar JWT
                                userDetails.getAuthorities() // Authorities: roles y permisos
                        );
                
                // Establece la autenticación en el contexto de seguridad de Spring
                // Esto hace que el usuario esté "logueado" para el resto de la petición
                SecurityContextHolder.getContext().setAuthentication(auth);
                
            } catch (Exception ex) {
                // Si el token es inválido, expirado o hay algún error en la validación
                // Limpia el contexto de seguridad para asegurar que no hay autenticación residual
                SecurityContextHolder.clearContext();
            }
        }

        // Continúa con el siguiente filtro en la cadena (muy importante)
        // Sin esto, la petición se bloquearía aquí
        filterChain.doFilter(request, response);
    }

    /**
     * Método privado que extrae el token JWT del header Authorization
     * Busca el patrón "Bearer <token>" en el header
     * 
     * @param request La petición HTTP de la cual extraer el token
     * @return El token JWT sin el prefijo "Bearer ", o null si no se encuentra
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        // Obtiene el valor del header "Authorization"
        String header = request.getHeader("Authorization");
        
        // Verifica si el header existe y comienza con "Bearer "
        if (header != null && header.startsWith("Bearer ")) {
            // Extrae solo el token, removiendo "Bearer " (7 caracteres)
            return header.substring(7);
        }
        
        // Retorna null si no hay header o no tiene el formato correcto
        return null;
    }
}
