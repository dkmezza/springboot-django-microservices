package com.elinonga.auth_service.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login response with JWT token")
public class LoginResponse {
    
    @Schema(
        description = "JWT access token", 
        example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjMwNTAwMDAwLCJleHAiOjE2MzA1ODY0MDB9.signature",
        required = true
    )
    private String token;
}
