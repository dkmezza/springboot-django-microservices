package com.elinonga.auth_service.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elinonga.auth_service.user.dto.LoginRequest;
import com.elinonga.auth_service.user.dto.LoginResponse;
import com.elinonga.auth_service.user.dto.UserDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User authentication and profile management endpoints")
public class UserController {
    
    private final UserService userService;

    @Operation(
        summary = "Register a new user",
        description = "Create a new user account with email and password. Default role is 'user'."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = UserDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data or email already exists",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping("/auth/register")
    public ResponseEntity<UserDTO> register(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User registration details",
            required = true,
            content = @Content(schema = @Schema(implementation = LoginRequest.class))
        )
        @Valid @RequestBody LoginRequest request) {
        
        var user = userService.register(request.getEmail(), request.getPassword(), "user");
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getEmail(), user.getRole()));
    }

    @Operation(
        summary = "User login",
        description = "Authenticate user with email and password to receive JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Login successful",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = LoginResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Invalid credentials",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User login credentials",
            required = true,
            content = @Content(schema = @Schema(implementation = LoginRequest.class))
        )
        @Valid @RequestBody LoginRequest request) {
        
        return ResponseEntity.ok(userService.login(request));
    }

    @Operation(
        summary = "Get current user profile",
        description = "Retrieve the profile information of the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User profile retrieved successfully",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = UserDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - Invalid or expired JWT token",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content(mediaType = "application/json")
        )
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/users/me")
    public ResponseEntity<UserDTO> getMe(
        @Parameter(
            description = "JWT token in Bearer format", 
            required = true,
            example = "Bearer eyJhbGciOiJIUzUxMiJ9..."
        )
        @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        String email = userService.extractEmailFromToken(token);
        return ResponseEntity.ok(userService.getCurrentUser(email));
    }
}
