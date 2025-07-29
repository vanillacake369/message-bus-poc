-- Create databases for each service
CREATE DATABASE IF NOT EXISTS order_db;
CREATE DATABASE IF NOT EXISTS inventory_db;
CREATE DATABASE IF NOT EXISTS analytics_db;

-- Create user with permissions for all databases
CREATE USER IF NOT EXISTS 'app_user'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON order_db.* TO 'app_user'@'%';
GRANT ALL PRIVILEGES ON inventory_db.* TO 'app_user'@'%';
GRANT ALL PRIVILEGES ON analytics_db.* TO 'app_user'@'%';
FLUSH PRIVILEGES;

-- Initialize some sample data for inventory
USE inventory_db;

CREATE TABLE IF NOT EXISTS inventory (
    product_id VARCHAR(255) PRIMARY KEY,
    available_quantity INT NOT NULL,
    reserved_quantity INT NOT NULL DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Insert sample products
INSERT IGNORE INTO inventory (product_id, available_quantity, reserved_quantity) VALUES
('LAPTOP001', 100, 0),
('PHONE001', 200, 0),
('TABLET001', 150, 0),
('HEADPHONES001', 300, 0),
('MOUSE001', 500, 0);