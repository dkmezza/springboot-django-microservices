"""
Serializers for Company model
"""

from rest_framework import serializers
from .models import Company


class CompanySerializer(serializers.ModelSerializer):
    """
    Company serializer for CRUD operations
    """

    class Meta:
        model = Company
        fields = [
            "id",
            "name",
            "description",
            "tenant_id",
            "created_by",
            "created_at",
            "updated_at",
        ]
        read_only_fields = ["id", "tenant_id", "created_by", "created_at", "updated_at"]

    def validate_name(self, value):
        """
        Validate company name
        """
        if not value or not value.strip():
            raise serializers.ValidationError("Company name cannot be empty")

        if len(value.strip()) < 2:
            raise serializers.ValidationError(
                "Company name must be at least 2 characters long"
            )

        return value.strip()


class CompanyCreateSerializer(serializers.ModelSerializer):
    """
    Serializer for creating companies (excludes read-only fields from input)
    """

    class Meta:
        model = Company
        fields = ["name", "description"]

    def validate_name(self, value):
        """
        Validate company name
        """
        if not value or not value.strip():
            raise serializers.ValidationError("Company name cannot be empty")

        if len(value.strip()) < 2:
            raise serializers.ValidationError(
                "Company name must be at least 2 characters long"
            )

        return value.strip()


class CompanyUpdateSerializer(serializers.ModelSerializer):
    """
    Serializer for updating companies (name and description only)
    """

    class Meta:
        model = Company
        fields = ["name", "description"]

    def validate_name(self, value):
        """
        Validate company name
        """
        if value is not None:
            if not value.strip():
                raise serializers.ValidationError("Company name cannot be empty")

            if len(value.strip()) < 2:
                raise serializers.ValidationError(
                    "Company name must be at least 2 characters long"
                )

            return value.strip()

        return value
