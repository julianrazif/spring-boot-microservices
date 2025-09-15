-- Database: PostgreSQL

-- Ensure extension for UUID v4 generation is available
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;

-- Create a cart table
CREATE TABLE IF NOT EXISTS carts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    product_id UUID NOT NULL,
    user_id UUID NOT NULL,
    quantity NUMERIC(10, 0) NOT NULL,
    status VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Foreign key to products mirroring onUpdate/onDelete CASCADE behavior from template
ALTER TABLE carts
    ADD CONSTRAINT fk_carts_product_id
    FOREIGN KEY (product_id) REFERENCES products(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;

-- NOTE:
-- A foreign key to users(id) from user_id was present in the template.
-- This repository does not define a user table in its migrations. To avoid
-- migration failures in environments where the user table is managed elsewhere,
-- the FK is intentionally omitted here. If you manage users in the same schema,
-- you can add the following constraint in a separate, environment-specific migration:
-- ALTER TABLE carts
--     ADD CONSTRAINT fk_carts_user_id
--     FOREIGN KEY (user_id) REFERENCES users(id)
--     ON UPDATE CASCADE
--     ON DELETE CASCADE;
