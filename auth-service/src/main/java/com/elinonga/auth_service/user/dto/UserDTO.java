package com.elinonga.auth_service.user.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User profile information")
public class UserDTO {
    
    @Schema(description = "User unique identifier", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    
    @Schema(description = "User email address", example = "user@example.com")
    private String email;
    
    @Schema(description = "User role", example = "user", allowableValues = {"user", "admin"})
    private String role;
}
