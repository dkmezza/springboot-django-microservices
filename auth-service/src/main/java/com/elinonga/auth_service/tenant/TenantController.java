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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<TenantDTO>> getAll() {
        return ResponseEntity.ok(tenantService.getAll());
    }

    @PostMapping
    public ResponseEntity<TenantDTO> create(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TenantDTO dto
    ) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = UUID.fromString(jwtUtil.extractUserId(token));
        return ResponseEntity.ok(tenantService.createTenant(dto, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantDTO> get(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantService.getTenant(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantDTO> update(@PathVariable UUID id, @RequestBody TenantDTO dto) {
        return ResponseEntity.ok(tenantService.updateTenant(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }
}
