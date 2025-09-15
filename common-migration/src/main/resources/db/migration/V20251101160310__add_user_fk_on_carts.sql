-- Database: PostgreSQL

-- Add foreign key to users for carts.user_id after users table exists
ALTER TABLE carts
    ADD CONSTRAINT fk_carts_user_id
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;
