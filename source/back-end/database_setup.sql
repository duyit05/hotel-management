-- Script để tạo lại database với schema đúng
-- Chạy script này trong PostgreSQL trước khi start ứng dụng

-- Xóa các bảng cũ nếu có
DROP TABLE IF EXISTS tbl_user_has_role CASCADE;
DROP TABLE IF EXISTS tbl_user CASCADE;
DROP TABLE IF EXISTS tbl_role CASCADE;

-- Xóa các enum types cũ nếu có
DROP TYPE IF EXISTS user_status CASCADE;
DROP TYPE IF EXISTS gender_type CASCADE;

-- Tạo enum types
CREATE TYPE gender_type AS ENUM ('male', 'female', 'other');
CREATE TYPE user_status AS ENUM ('active', 'inactive', 'block');

-- Tạo bảng roles
CREATE TABLE tbl_role (
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng users
CREATE TABLE tbl_user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(255),
    phone_number VARCHAR(20),
    gender gender_type,
    status user_status,
    date_or_birth DATE,
    active_code VARCHAR(255),
    avatar TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng user_has_role
CREATE TABLE tbl_user_has_role (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES tbl_user(id) ON DELETE CASCADE,
    role_id INTEGER REFERENCES tbl_role(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role_id)
);

-- Insert dữ liệu mặc định
INSERT INTO tbl_role (role_name) VALUES ('ADMIN'), ('USER');

-- Insert user admin mặc định (password đã được encode)
INSERT INTO tbl_user (username, password, first_name, last_name, email, gender, status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Admin', 'User', 'admin@example.com', 'male', 'active');

-- Gán role admin cho user admin
INSERT INTO tbl_user_has_role (user_id, role_id) 
VALUES (1, 1);

COMMIT;

