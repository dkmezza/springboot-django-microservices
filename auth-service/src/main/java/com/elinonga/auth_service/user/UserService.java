package com.elinonga.auth_service.user;

import com.elinonga.auth_service.security.JwtUtil;
import com.elinonga.auth_service.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOptional.get().getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        String token = jwtUtil.generateToken(userOptional.get());
        return new LoginResponse(token);
    }

    public User register(String email, String password, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        return userRepository.save(user);
    }

    public UserDTO getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new UserDTO(user.getId(), user.getEmail(), user.getRole()))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
