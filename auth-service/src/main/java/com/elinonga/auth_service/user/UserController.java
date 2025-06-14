package com.elinonga.auth_service.user;

import com.elinonga.auth_service.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/register")
    public ResponseEntity<UserDTO> register(@RequestBody LoginRequest request) {
        var user = userService.register(request.getEmail(), request.getPassword(), "user");
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getEmail(), user.getRole()));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserDTO> getMe(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = userService.extractEmailFromToken(token); // will wire up later
        return ResponseEntity.ok(userService.getCurrentUser(email));
    }
}
