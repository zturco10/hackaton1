package org.example.hackaton1.service;

import org.example.hackaton1.config.JwtUtil;
import org.example.hackaton1.dto.AuthRequest;
import org.example.hackaton1.dto.AuthResponse;
import org.example.hackaton1.dto.RegisterRequest;
import org.example.hackaton1.model.User;
import org.example.hackaton1.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public User register(RegisterRequest req) {
        if (req.getRole() == null) {
            throw new IllegalArgumentException("Role es obligatorio");
        }
        if ("BRANCH".equalsIgnoreCase(req.getRole()) && (req.getBranch() == null || req.getBranch().isBlank())) {
            throw new IllegalArgumentException("Branch es obligatorio para role BRANCH");
        }

        userRepository.findByUsername(req.getUsername()).ifPresent(u -> {
            throw new IllegalArgumentException("Username en uso");
        });
        userRepository.findByEmail(req.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email en uso");
        });

        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole(req.getRole().toUpperCase()); // CENTRAL o BRANCH
        u.setBranch("CENTRAL".equalsIgnoreCase(req.getRole()) ? null : req.getBranch());
        u.setCreatedAt(Instant.now());
        return userRepository.save(u);
    }

    public AuthResponse login(AuthRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getBranch());
        return new AuthResponse(token, jwtUtil.getExpirationSeconds(), user.getRole(), user.getBranch());
    }
}