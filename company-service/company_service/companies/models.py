"""
Company models for the company service
"""

import uuid
from django.db import models
from django.utils import timezone


class Company(models.Model):
    """
    Company model that references tenant and user from auth service
    """

    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)

    name = models.CharField(max_length=255, help_text="Company name")

    description = models.TextField(
        blank=True, null=True, help_text="Company description"
    )

    # References to auth service entities (stored as UUIDs)
    tenant_id = models.UUIDField(
        help_text="ID of the tenant this company belongs to (from auth service)"
    )

    created_by = models.UUIDField(
        help_text="ID of the user who created this company (from auth service)"
    )

    # Timestamps
    created_at = models.DateTimeField(
        default=timezone.now, help_text="When the company was created"
    )

    updated_at = models.DateTimeField(
        auto_now=True, help_text="When the company was last updated"
    )

    class Meta:
        verbose_name = "Company"
        verbose_name_plural = "Companies"
        ordering = ["-created_at"]

        # Ensure company names are unique within a tenant
        constraints = [
            models.UniqueConstraint(
                fields=["name", "tenant_id"], name="unique_company_name_per_tenant"
            )
        ]

    def __str__(self):
        return f"{self.name} (Tenant: {self.tenant_id})"

    def to_dict(self):
        """
        Convert model instance to dictionary for JSON serialization
        """
        return {
            "id": str(self.id),
            "name": self.name,
            "description": self.description,
            "tenant_id": str(self.tenant_id),
            "created_by": str(self.created_by),
            "created_at": self.created_at.isoformat(),
            "updated_at": self.updated_at.isoformat(),
        }
