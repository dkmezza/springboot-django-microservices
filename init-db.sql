-- Initialize databases for microservices
-- This script runs when PostgreSQL container starts for the first time

-- Create auth_db for Spring Boot service
CREATE DATABASE auth_db;

-- Create company_db for Django service
CREATE DATABASE company_db;

-- Grant all privileges to the user
GRANT ALL PRIVILEGES ON DATABASE auth_db TO mezza;
GRANT ALL PRIVILEGES ON DATABASE company_db TO mezza;

-- Optional: Create schemas if needed
\c auth_db;
-- Any auth_db specific setup can go here

\c company_db;
-- Any company_db specific setup can go here

-- Log completion
SELECT 'Databases auth_db and company_db created successfully!' as status;