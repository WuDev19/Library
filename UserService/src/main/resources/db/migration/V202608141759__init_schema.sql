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

CREATE INDEX idx_users_deleted_at ON users (deleted_at);