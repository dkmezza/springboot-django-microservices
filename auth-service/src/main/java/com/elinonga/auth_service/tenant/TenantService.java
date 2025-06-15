package com.elinonga.auth_service.tenant;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.elinonga.auth_service.tenant.dto.TenantDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    public List<TenantDTO> getAll() {
        return tenantRepository.findAll()
                .stream()
                .map(t -> new TenantDTO(t.getId(), t.getName(), t.getStatus(), t.getCreatedBy()))
                .collect(Collectors.toList());
    }

    public TenantDTO createTenant(TenantDTO dto, UUID createdBy) {
        Tenant tenant = Tenant.builder()
                .name(dto.getName())
                .status(dto.getStatus())
                .createdBy(createdBy)
                .build();
        tenant = tenantRepository.save(tenant);
        return new TenantDTO(tenant.getId(), tenant.getName(), tenant.getStatus(), tenant.getCreatedBy());
    }

    public TenantDTO getTenant(UUID id) {
        Tenant t = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        return new TenantDTO(t.getId(), t.getName(), t.getStatus(), t.getCreatedBy());
    }

    public TenantDTO updateTenant(UUID id, TenantDTO dto) {
        Tenant existing = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        existing.setName(dto.getName());
        existing.setStatus(dto.getStatus());

        Tenant updated = tenantRepository.save(existing);

        return new TenantDTO(updated.getId(), updated.getName(), updated.getStatus(), updated.getCreatedBy());
    }

    public void deleteTenant(UUID id) {
        tenantRepository.deleteById(id);
    }
}
