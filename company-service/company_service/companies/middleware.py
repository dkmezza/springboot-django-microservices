"""
JWT Authentication Middleware
"""

import logging
from django.utils.deprecation import MiddlewareMixin
from .authentication import JWTAuthentication

logger = logging.getLogger(__name__)


class JWTAuthenticationMiddleware(MiddlewareMixin):
    """
    Middleware to handle JWT authentication for all requests
    """

    def __init__(self, get_response):
        self.get_response = get_response
        self.jwt_auth = JWTAuthentication()
        super().__init__(get_response)

    def process_request(self, request):
        """
        Process the request and add JWT user to request if token is valid
        """
        try:
            # Skip authentication for admin and static URLs
            if (
                request.path.startswith("/admin/")
                or request.path.startswith("/static/")
                or request.path.startswith("/health/")
            ):
                return None

            # Try to authenticate using JWT
            auth_result = self.jwt_auth.authenticate(request)

            if auth_result:
                user, token = auth_result
                request.user = user
                request.jwt_token = token
                logger.debug(f"Authenticated user: {user}")

        except Exception as e:
            # Don't fail the request, let DRF handle authentication
            logger.debug(f"JWT middleware authentication failed: {e}")

        return None
