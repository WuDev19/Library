-- =====================================================================
-- V1__init_users_schema.sql
-- Khởi tạo schema cho user-service (PostgreSQL)
-- Lưu ý: user_id KHÔNG tự sinh (không có @GeneratedValue) vì được đồng
-- bộ / dùng chung từ user_id của account bên auth-service.
-- =====================================================================

CREATE TABLE users
(
    user_id    BIGINT PRIMARY KEY,
    email      VARCHAR(150) NOT NULL,
    full_name  VARCHAR(100) NOT NULL,
    phone      VARCHAR(20),
    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ  NOT NULL,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT uk_users_email UNIQUE (email)
);

-- ---------------------------------------------------------------------
-- Index bổ sung
-- ---------------------------------------------------------------------
-- Hỗ trợ truy vấn lọc user chưa bị xoá mềm (soft delete)
CREATE INDEX idx_users_deleted_at ON users (deleted_at);