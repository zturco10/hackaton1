package org.example.hackaton1.config;



import org.example.hackaton1.Security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración principal de seguridad de Spring Security
 * Define todas las reglas de autenticación, autorización y manejo de CORS
 * Esta es la clase más importante del sistema de seguridad
 */
@Configuration     // Marca como clase de configuración de Spring
@EnableWebSecurity // Habilita la seguridad web de Spring Security
@EnableMethodSecurity // Permite usar anotaciones de seguridad en métodos (@PreAuthorize, etc.)
@EnableAsync       // Habilita el procesamiento asíncrono
public class SecurityConfig {

    // Filtro personalizado para validar tokens JWT en cada petición
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Servicio que carga detalles del usuario desde la base de datos
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Constructor que inyecta las dependencias necesarias
     * 
     * @param jwtAuthenticationFilter Filtro para validación de JWT
     * @param userDetailsService Servicio para cargar usuarios
     */
    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          UserDetailsServiceImpl userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configuración principal de la cadena de filtros de seguridad
     * Define qué endpoints requieren autenticación y cuáles son públicos
     *
     * @param http Objeto HttpSecurity para configurar la seguridad
     * @return SecurityFilterChain cadena de filtros configurada
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement(manager ->
                        manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        // Endpoints públicos para registro y login
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuración de CORS (Cross-Origin Resource Sharing)
     * Permite que el frontend (React, Angular, etc.) acceda al backend desde otros puertos/dominios
     * 
     * @return CorsConfigurationSource configuración de CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*")); // Permitir todos los orígenes
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    /**
     * Bean que configura el proveedor de autenticación
     * Define cómo Spring Security debe validar las credenciales de usuario
     * 
     * @return AuthenticationProvider proveedor configurado
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // DaoAuthenticationProvider: valida contra una base de datos
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        
        // Establece el servicio que carga los detalles del usuario
        provider.setUserDetailsService(userDetailsService);
        
        // Establece el encoder para verificar contraseñas
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        
        return provider;
    }

    /**
     * Bean que proporciona el AuthenticationManager
     * Componente central que coordina la autenticación en Spring Security
     * 
     * @param config Configuración de autenticación de Spring
     * @return AuthenticationManager manager configurado
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean para executor de tareas (nombrado específicamente)
     * Evita conflictos con el asyncTaskExecutor definido en AsyncConfig
     * 
     * @return TaskExecutor executor simple para tareas
     */
    @Bean(name = "securityTaskExecutor")
    public TaskExecutor securityTaskExecutor() {
        // SimpleAsyncTaskExecutor: crea un nuevo hilo para cada tarea
        return new SimpleAsyncTaskExecutor();
    }

    /**
     * Bean que define la jerarquía de roles en el sistema
     * Los roles superiores heredan permisos de roles inferiores
     * 
     * @return RoleHierarchy jerarquía configurada
     */
    @Bean
    static RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        // Jerarquía: ADMINISTRADOR > ENCARGADO_LOCAL > CLIENTE > REPARTIDOR
        // Significa que ADMINISTRADOR puede hacer todo lo que pueden hacer los demás roles
        hierarchy.setHierarchy("ADMINISTRADOR > ENCARGADO_LOCAL > CLIENTE > REPARTIDOR");
        return hierarchy;
    }

    /**
     * Bean que configura el handler para expresiones de seguridad en métodos
     * Permite usar la jerarquía de roles en anotaciones como @PreAuthorize
     * 
     * @param roleHierarchy Jerarquía de roles definida arriba
     * @return MethodSecurityExpressionHandler handler configurado
     */
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler =
                new DefaultMethodSecurityExpressionHandler();
        
        // Establece la jerarquía de roles
        handler.setRoleHierarchy(roleHierarchy);
        
        // Remueve el prefijo "ROLE_" por defecto (usamos nombres directos como "CLIENTE")
        handler.setDefaultRolePrefix("");
        
        return handler;
    }
}