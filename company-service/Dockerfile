# Use Python 3.11 slim image
FROM python:3.11-slim

# Set environment variables
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# Install system dependencies
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
    postgresql-client \
    curl \
    netcat-traditional \
    && rm -rf /var/lib/apt/lists/*

# Set work directory
WORKDIR /app

# Install Python dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy project
COPY . .

# Create entrypoint script
RUN echo '#!/bin/bash\n\
    set -e\n\
    \n\
    # Wait for PostgreSQL\n\
    echo "Waiting for PostgreSQL..."\n\
    while ! nc -z $DB_HOST $DB_PORT; do\n\
    sleep 1\n\
    done\n\
    echo "PostgreSQL is ready!"\n\
    \n\
    # Run migrations\n\
    echo "Running migrations..."\n\
    python manage.py makemigrations companies\n\
    python manage.py migrate\n\
    \n\
    # Create superuser if it does not exist\n\
    echo "Creating superuser if needed..."\n\
    python manage.py shell -c "from django.contrib.auth import get_user_model; User = get_user_model(); User.objects.filter(username=\"admin\").exists() or User.objects.create_superuser(\"admin\", \"admin@example.com\", \"admin123\")" || true\n\
    \n\
    # Start the server\n\
    echo "Starting Django server..."\n\
    python manage.py runserver 0.0.0.0:8000' > /app/entrypoint.sh

# Make entrypoint executable
RUN chmod +x /app/entrypoint.sh

# Create non-root user
RUN adduser --disabled-password --gecos '' appuser && \
    chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8000/health/ || exit 1

# Run the application using entrypoint
CMD ["/app/entrypoint.sh"]