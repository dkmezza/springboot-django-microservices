"""
Company API views
"""

import logging
from django.db import IntegrityError
from rest_framework import status
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.pagination import PageNumberPagination

from .models import Company
from .serializers import (
    CompanySerializer,
    CompanyCreateSerializer,
    CompanyUpdateSerializer,
)

logger = logging.getLogger(__name__)


class CompanyPagination(PageNumberPagination):
    page_size = 20
    page_size_query_param = "page_size"
    max_page_size = 100


@api_view(["GET", "POST"])
@permission_classes([IsAuthenticated])
def company_list(request):
    """
    List all companies for the authenticated user's tenant or create a new company
    """
    if request.method == "GET":
        try:
            # Filter companies by user's tenant
            companies = Company.objects.filter(tenant_id=request.user.tenant_id)

            # Apply pagination
            paginator = CompanyPagination()
            page = paginator.paginate_queryset(companies, request)

            if page is not None:
                serializer = CompanySerializer(page, many=True)
                return paginator.get_paginated_response(serializer.data)

            serializer = CompanySerializer(companies, many=True)
            return Response(serializer.data)

        except Exception as e:
            logger.error(f"Error fetching companies: {e}")
            return Response(
                {"error": "Failed to fetch companies"},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR,
            )

    elif request.method == "POST":
        try:
            serializer = CompanyCreateSerializer(data=request.data)

            if serializer.is_valid():
                # Create company with tenant_id and created_by from JWT
                company = serializer.save(
                    tenant_id=request.user.tenant_id, created_by=request.user.user_id
                )

                # Return full company data
                response_serializer = CompanySerializer(company)
                return Response(
                    response_serializer.data, status=status.HTTP_201_CREATED
                )

            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

        except IntegrityError:
            return Response(
                {"error": "A company with this name already exists in your tenant"},
                status=status.HTTP_409_CONFLICT,
            )
        except Exception as e:
            logger.error(f"Error creating company: {e}")
            return Response(
                {"error": "Failed to create company"},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR,
            )


@api_view(["GET", "PUT", "DELETE"])
@permission_classes([IsAuthenticated])
def company_detail(request, company_id):
    """
    Retrieve, update or delete a company
    """
    try:
        # Get company that belongs to user's tenant
        company = Company.objects.get(id=company_id, tenant_id=request.user.tenant_id)
    except Company.DoesNotExist:
        return Response(
            {"error": "Company not found"}, status=status.HTTP_404_NOT_FOUND
        )

    if request.method == "GET":
        serializer = CompanySerializer(company)
        return Response(serializer.data)

    elif request.method == "PUT":
        try:
            serializer = CompanyUpdateSerializer(
                company, data=request.data, partial=True
            )

            if serializer.is_valid():
                serializer.save()

                # Return full company data
                response_serializer = CompanySerializer(company)
                return Response(response_serializer.data)

            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

        except IntegrityError:
            return Response(
                {"error": "A company with this name already exists in your tenant"},
                status=status.HTTP_409_CONFLICT,
            )
        except Exception as e:
            logger.error(f"Error updating company: {e}")
            return Response(
                {"error": "Failed to update company"},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR,
            )

    elif request.method == "DELETE":
        try:
            company.delete()
            return Response(status=status.HTTP_204_NO_CONTENT)

        except Exception as e:
            logger.error(f"Error deleting company: {e}")
            return Response(
                {"error": "Failed to delete company"},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR,
            )


@api_view(["GET"])
def health_check(request):
    """
    Health check endpoint
    """
    return Response(
        {"status": "healthy", "service": "company-service", "version": "1.0.0"}
    )
