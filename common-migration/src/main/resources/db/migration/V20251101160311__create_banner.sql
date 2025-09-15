-- Database: PostgreSQL

-- Ensure extension for UUID v4 generation is available
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;

-- Create a banner table
CREATE TABLE IF NOT EXISTS banners (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    title VARCHAR(100) NOT NULL,
    status BOOLEAN NOT NULL,
    image_url VARCHAR(10000) NOT NULL,
    discovery VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
