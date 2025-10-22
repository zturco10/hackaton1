package org.example.hackaton1.controller;

import jakarta.validation.Valid;
import org.example.hackaton1.dto.AuthResponse;
import org.example.hackaton1.dto.RegisterRequest;
import org.example.hackaton1.model.User;
import org.example.hackaton1.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService){this.authService=authService;}

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest r){
        User u = authService.register(r);
        // build response DTO with createdAt iso
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse(...));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req){
        AuthResponse token = authService.login(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(token);
    }
}
