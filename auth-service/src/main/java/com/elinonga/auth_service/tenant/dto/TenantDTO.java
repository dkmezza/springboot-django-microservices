package com.elinonga.auth_service.tenant.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TenantDTO {
    private UUID id;
    private String name;
    private String status;
    private UUID createdBy;
}
