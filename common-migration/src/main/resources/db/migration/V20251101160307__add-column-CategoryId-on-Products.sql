-- Database: PostgreSQL

-- Add CategoryId equivalent column to products and set up FK to categories(id)
ALTER TABLE products
    ADD COLUMN category_id UUID;

ALTER TABLE products
    ADD CONSTRAINT fk_products_category_id
    FOREIGN KEY (category_id) REFERENCES categories(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;