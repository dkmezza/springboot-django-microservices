"""
JWT Authentication for Django REST Framework
Validates tokens issued by Spring Boot auth service
"""

import jwt
import logging
from django.conf import settings
from django.contrib.auth.models import AnonymousUser
from rest_framework import authentication, exceptions

logger = logging.getLogger(__name__)


class JWTUser:
    """
    Custom user class to hold JWT payload data
    """

    def __init__(self, payload):
        self.id = payload.get("sub")
        self.user_id = payload.get("sub")  # UUID from Spring Boot
        self.tenant_id = payload.get("tenant_id")
        self.email = payload.get("email")
        self.role = payload.get("role")
        self.is_authenticated = True
        self.is_anonymous = False

    def __str__(self):
        return (
            f"JWTUser(id={self.user_id}, email={self.email}, tenant={self.tenant_id})"
        )


class JWTAuthentication(authentication.BaseAuthentication):
    """
    Custom JWT authentication for Django REST Framework
    """

    def authenticate(self, request):
        """
        Authenticate the request and return a two-tuple of (user, token).
        """
        auth_header = request.META.get("HTTP_AUTHORIZATION")

        if not auth_header:
            return None

        try:
            # Parse the Authorization header
            auth_parts = auth_header.split()

            if len(auth_parts) != 2 or auth_parts[0].lower() != "bearer":
                return None

            token = auth_parts[1]

            # Decode and validate the JWT token
            payload = jwt.decode(
                token, settings.JWT_SECRET_KEY, algorithms=[settings.JWT_ALGORITHM]
            )

            logger.debug(f"JWT payload: {payload}")

            # Create JWT user from payload
            user = JWTUser(payload)

            return (user, token)

        except jwt.ExpiredSignatureError:
            logger.warning("JWT token has expired")
            raise exceptions.AuthenticationFailed("Token has expired")

        except jwt.InvalidTokenError as e:
            logger.warning(f"Invalid JWT token: {e}")
            raise exceptions.AuthenticationFailed("Invalid token")

        except Exception as e:
            logger.error(f"JWT authentication error: {e}")
            raise exceptions.AuthenticationFailed("Authentication failed")

    def authenticate_header(self, request):
        """
        Return a string to be used as the value of the `WWW-Authenticate`
        header in a `401 Unauthenticated` response.
        """
        return "Bearer"
