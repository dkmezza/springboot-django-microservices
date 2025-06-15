from django.contrib import admin
from .models import Company


@admin.register(Company)
class CompanyAdmin(admin.ModelAdmin):
    list_display = ["name", "tenant_id", "created_by", "created_at"]
    list_filter = ["created_at", "tenant_id"]
    search_fields = ["name", "description"]
    readonly_fields = ["id", "created_at", "updated_at"]
