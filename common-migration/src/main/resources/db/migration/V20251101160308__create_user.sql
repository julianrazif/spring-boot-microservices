-- Database: PostgreSQL

-- Ensure extension for UUID v4 generation is available
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;

-- Create a user table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    display_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(10000) NOT NULL,
    role VARCHAR(25) NOT NULL DEFAULT 'customer',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
