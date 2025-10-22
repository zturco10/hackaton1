package org.example.hackaton1.Usuario;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioDTO {
    @NotNull
    private String nombre;
    @NotNull
    private String numero;
    @NotNull
    private String contrasena;
    // Los siguientes campos se rellenan automáticamente en el backend
    // private Long id;
    // private boolean activo;
    // private String fechaRegistro;
    // private Rol rol;
}
