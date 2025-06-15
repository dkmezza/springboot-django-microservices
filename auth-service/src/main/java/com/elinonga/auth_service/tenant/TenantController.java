package com.elinonga.auth_service.tenant;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elinonga.auth_service.security.JwtUtil;
import com.elinonga.auth_service.tenant.dto.TenantDTO;

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
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Tenant Management", description = "Multi-tenant organization management endpoints")
public class TenantController {
    
    private final TenantService tenantService;
    private final JwtUtil jwtUtil;

    @Operation(
        summary = "Get all tenants",
        description = "Retrieve a list of all tenant organizations. Requires authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Tenants retrieved successfully",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = TenantDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - JWT token required",
            content = @Content(mediaType = "application/json")
        )
    })
    @GetMapping
    public ResponseEntity<List<TenantDTO>> getAll() {
        return ResponseEntity.ok(tenantService.getAll());
    }

    @Operation(
        summary = "Create a new tenant",
        description = "Create a new tenant organization. The authenticated user becomes the tenant creator."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Tenant created successfully",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = TenantDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data or tenant name already exists",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - JWT token required",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping
    public ResponseEntity<TenantDTO> create(
        @Parameter(
            description = "JWT token in Bearer format", 
            required = true,
            example = "Bearer eyJhbGciOiJIUzUxMiJ9..."
        )
        @RequestHeader("Authorization") String authHeader,
        
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Tenant creation details",
            required = true,
            content = @Content(schema = @Schema(implementation = TenantDTO.class))
        )
        @Valid @RequestBody TenantDTO dto) {
        
        String token = authHeader.replace("Bearer ", "");
        UUID userId = UUID.fromString(jwtUtil.extractUserId(token));
        return ResponseEntity.ok(tenantService.createTenant(dto, userId));
    }

    @Operation(
        summary = "Get tenant by ID",
        description = "Retrieve a specific tenant by its unique identifier. Requires authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Tenant found and retrieved successfully",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = TenantDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Tenant not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - JWT token required",
            content = @Content(mediaType = "application/json")
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TenantDTO> get(
        @Parameter(
            description = "Tenant unique identifier", 
            required = true,
            example = "123e4567-e89b-12d3-a456-426614174000"
        )
        @PathVariable UUID id) {
        
        return ResponseEntity.ok(tenantService.getTenant(id));
    }

    @Operation(
        summary = "Update tenant",
        description = "Update an existing tenant organization. Requires authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Tenant updated successfully",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = TenantDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Tenant not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - JWT token required",
            content = @Content(mediaType = "application/json")
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<TenantDTO> update(
        @Parameter(
            description = "Tenant unique identifier", 
            required = true,
            example = "123e4567-e89b-12d3-a456-426614174000"
        )
        @PathVariable UUID id, 
        
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated tenant details",
            required = true,
            content = @Content(schema = @Schema(implementation = TenantDTO.class))
        )
        @Valid @RequestBody TenantDTO dto) {
        
        return ResponseEntity.ok(tenantService.updateTenant(id, dto));
    }

    @Operation(
        summary = "Delete tenant",
        description = "Delete a tenant organization permanently. Requires authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Tenant deleted successfully",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Tenant not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - JWT token required",
            content = @Content(mediaType = "application/json")
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @Parameter(
            description = "Tenant unique identifier", 
            required = true,
            example = "123e4567-e89b-12d3-a456-426614174000"
        )
        @PathVariable UUID id) {
        
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }
}
