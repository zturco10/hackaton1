<<<<<<< HEAD:src/main/java/org/example/hackaton1/Security/config/AppConfig.java
package org.example.hackaton1.Security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuración general de la aplicación donde se definen beans comunes
 * Esta clase se encarga de crear componentes que serán utilizados en toda la aplicación
 */
@Configuration // Indica que esta clase contiene definiciones de beans de Spring
public class AppConfig {

    /**
     * Bean que proporciona el encoder para cifrar contraseñas
     * BCrypt es un algoritmo de hash seguro diseñado específicamente para contraseñas
     * Incluye un "salt" aleatorio y es resistente a ataques de fuerza bruta
     *
     * @return BCryptPasswordEncoder configurado para cifrar/verificar contraseñas
     */
    @Bean // Registra este método como un bean en el contexto de Spring
    public BCryptPasswordEncoder passwordEncoder() {
        // Crea una nueva instancia del encoder con configuración por defecto (strength = 10)
        return new BCryptPasswordEncoder();
    }
}
=======
package org.example.hackaton1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuración general de la aplicación donde se definen beans comunes
 * Esta clase se encarga de crear componentes que serán utilizados en toda la aplicación
 */
@Configuration // Indica que esta clase contiene definiciones de beans de Spring
public class AppConfig {
    
    /**
     * Bean que proporciona el encoder para cifrar contraseñas
     * BCrypt es un algoritmo de hash seguro diseñado específicamente para contraseñas
     * Incluye un "salt" aleatorio y es resistente a ataques de fuerza bruta
     * 
     * @return BCryptPasswordEncoder configurado para cifrar/verificar contraseñas
     */
    @Bean // Registra este método como un bean en el contexto de Spring
    public BCryptPasswordEncoder passwordEncoder() {
        // Crea una nueva instancia del encoder con configuración por defecto (strength = 10)
        return new BCryptPasswordEncoder();
    }
}
>>>>>>> 72f457db4729ec57ae474cb21926179cb939d87c:src/main/java/org/example/hackaton1/config/AppConfig.java
