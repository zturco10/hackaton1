package org.example.hackaton1.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String numero;
}
