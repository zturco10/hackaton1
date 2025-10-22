package org.example.hackaton1.Security.service;



import org.example.hackaton1.Usuario.Usuario;
import org.example.hackaton1.Usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio UserDetailsService de Spring Security
 * Se encarga de cargar los detalles del usuario desde la base de datos durante la autenticación
 * Es el puente entre Spring Security y nuestro modelo de Usuario personalizado
 */
@Service // Marca esta clase como un servicio de Spring
public class UserDetailsServiceImpl implements UserDetailsService {

    // Repositorio para acceder a los datos de usuarios en la base de datos
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Método requerido por UserDetailsService que carga un usuario por su username
     * En nuestro caso, el "username" es el número de teléfono del usuario
     * Este método es llamado automáticamente por Spring Security durante la autenticación
     *
     * @param numero Número de teléfono del usuario (usado como username)
     * @return UserDetails Objeto que contiene los detalles del usuario para Spring Security
     * @throws UsernameNotFoundException Si el usuario no existe o está inactivo
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca el usuario por número de teléfono y que esté activo
        Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        // Retorna el usuario directamente ya que nuestra clase Usuario implementa UserDetails
        // Esto significa que Usuario tiene todos los métodos requeridos por Spring Security
        return usuario;
    }
}
