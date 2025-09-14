-- Database: PostgreSQL

-- Ensure extension for UUID v4 generation is available
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;

-- Create a product table
CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    name VARCHAR(50) NOT NULL,
    image_url VARCHAR(10000) NOT NULL,
    price NUMERIC(20, 0) NOT NULL,
    stock NUMERIC(10, 0) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
