"""
URL configuration for companies app
"""

from django.urls import path
from . import views

urlpatterns = [
    path("", views.company_list, name="company-list"),
    path("<uuid:company_id>/", views.company_detail, name="company-detail"),
]
