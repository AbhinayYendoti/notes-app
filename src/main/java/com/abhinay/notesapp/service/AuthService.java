package com.abhinay.notesapp.service;

import com.abhinay.notesapp.dto.request.LoginRequest;
import com.abhinay.notesapp.dto.request.RegisterRequest;
import com.abhinay.notesapp.dto.response.AuthResponse;
import com.abhinay.notesapp.dto.response.MessageResponse;
import com.abhinay.notesapp.entity.User;
import com.abhinay.notesapp.exception.EmailAlreadyExistsException;
import com.abhinay.notesapp.repository.UserRepository;
import com.abhinay.notesapp.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public MessageResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return new MessageResponse("User registered successfully");
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        // Generic message prevents email enumeration
        final String INVALID_CREDS = "Invalid email or password";

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_CREDS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_CREDS);
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
}
