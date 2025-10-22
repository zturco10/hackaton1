package org.example.hackaton1.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api")
public class UsuarioController {


    @Autowired
    private UsuarioRepository usuarioRepository;

    @PreAuthorize("hasAnyRole('CENTRAL,BRANCH')")
    @GetMapping("/user/me")
    public UsuarioResponse getCurrentUser(@AuthenticationPrincipal Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getUsername(), usuario.getEmail());
    }
}
