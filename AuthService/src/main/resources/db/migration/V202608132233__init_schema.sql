CREATE TABLE roles
(
    role_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_name   VARCHAR(50) NOT NULL,
    description TEXT,
    created_at  TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_roles_role_name UNIQUE (role_name)
);

CREATE TABLE accounts
(
    user_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username   VARCHAR(255),
    password   VARCHAR(255),
    email      VARCHAR(255),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    is_active  BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE account_role
(
    account_id BIGINT NOT NULL,
    role_id    BIGINT NOT NULL,
    CONSTRAINT pk_account_role PRIMARY KEY (account_id, role_id),
    CONSTRAINT fk_account_role_account FOREIGN KEY (account_id)
        REFERENCES accounts (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_account_role_role FOREIGN KEY (role_id)
        REFERENCES roles (role_id) ON DELETE CASCADE
);

CREATE TABLE refresh_token
(
    refresh_token_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id          BIGINT       NOT NULL,
    refresh_token    VARCHAR(500) NOT NULL,
    expire_date      TIMESTAMPTZ  NOT NULL,
    is_revoked       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMPTZ  NOT NULL,
    CONSTRAINT uk_refresh_token_refresh_token UNIQUE (refresh_token),
    CONSTRAINT fk_refresh_token_account FOREIGN KEY (user_id)
        REFERENCES accounts (user_id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_token_user_id ON refresh_token (user_id);
CREATE INDEX idx_account_role_role_id ON account_role (role_id);