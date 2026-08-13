CREATE
EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE users
(
    user_id       BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    phone         VARCHAR(20),
    is_active     BOOLEAN      NOT NULL DEFAULT FALSE,
    avatar_url    VARCHAR(500),
    public_url_id VARCHAR(50),
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL,
    deleted_at    TIMESTAMPTZ -- soft delete: NULL = chưa xoá
);

CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_full_name_trgm ON users USING gin (full_name gin_trgm_ops);
CREATE INDEX idx_users_deleted_at ON users (deleted_at);

CREATE TABLE roles
(
    role_id     BIGSERIAL PRIMARY KEY,
    role_name   VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMPTZ NOT NULL
);

CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL REFERENCES users (user_id),
    role_id BIGINT NOT NULL REFERENCES roles (role_id),
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);

CREATE TABLE refresh_token
(
    refresh_token_id BIGSERIAL PRIMARY KEY,
    user_id          BIGINT       NOT NULL REFERENCES users (user_id),
    refresh_token    VARCHAR(500) NOT NULL UNIQUE,
    expire_date      TIMESTAMPTZ  NOT NULL,
    is_revoked       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_refresh_token_user_id ON refresh_token (user_id);
CREATE INDEX idx_refresh_token_expire_date ON refresh_token (expire_date);
CREATE TABLE black_list_access_token
(
    blacklist_token_id BIGSERIAL PRIMARY KEY,
    token_id           VARCHAR(36) NOT NULL UNIQUE, -- JWT jti (UUID)
    expire_date        TIMESTAMPTZ NOT NULL,
    created_at         TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_blacklist_token_expire_date ON black_list_access_token (expire_date);

CREATE TYPE copy_status AS ENUM ('AVAILABLE', 'BORROWED', 'LOST', 'DAMAGED', 'RETIRED');
CREATE TYPE borrow_status AS ENUM ('BORROWING', 'RETURNED', 'OVERDUE', 'LOST');
CREATE TYPE notification_type AS ENUM ('DUE_SOON', 'OVERDUE', 'STILL_BORROWING');
CREATE TABLE categories
(
    category_id BIGSERIAL PRIMARY KEY,
    code        VARCHAR(30)  NOT NULL UNIQUE,
    name        VARCHAR(150) NOT NULL,
    description TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE books
(
    book_id            BIGSERIAL PRIMARY KEY,
    code               VARCHAR(30)  NOT NULL UNIQUE, -- mã sách
    title              VARCHAR(255) NOT NULL,        -- tên sách
    category_id        BIGINT REFERENCES categories (category_id),
    author             VARCHAR(255),
    publisher          VARCHAR(255),
    published_year     SMALLINT,
    isbn               VARCHAR(20),
    description        TEXT,
    total_quantity     INT          NOT NULL DEFAULT 0,
    available_quantity INT          NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_books_quantity CHECK (
        available_quantity >= 0 AND available_quantity <= total_quantity
        )
);

CREATE INDEX idx_books_code ON books (code);
CREATE INDEX idx_books_title_trgm ON books USING gin (title gin_trgm_ops);
CREATE INDEX idx_books_category_id ON books (category_id);

CREATE TABLE book_copies
(
    book_copy_id BIGSERIAL PRIMARY KEY,
    book_id      BIGINT      NOT NULL REFERENCES books (book_id),
    asset_code   VARCHAR(30) NOT NULL UNIQUE,
    status       copy_status NOT NULL DEFAULT 'AVAILABLE',
    note         TEXT,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_book_copies_asset_code ON book_copies (asset_code);
CREATE INDEX idx_book_copies_book_id ON book_copies (book_id);
CREATE INDEX idx_book_copies_status ON book_copies (status);

CREATE TABLE book_imports
(
    book_import_id BIGSERIAL PRIMARY KEY,
    import_code    VARCHAR(30) NOT NULL UNIQUE,
    book_id        BIGINT      NOT NULL REFERENCES books (book_id),
    quantity       INT         NOT NULL CHECK (quantity > 0),
    imported_by    BIGINT      NOT NULL REFERENCES users (user_id), -- quản lý thực hiện nhập
    import_date    DATE        NOT NULL DEFAULT CURRENT_DATE,
    note           TEXT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_book_imports_book_id ON book_imports (book_id);
CREATE INDEX idx_book_imports_imported_by ON book_imports (imported_by);

CREATE TABLE book_import_items
(
    book_import_item_id BIGSERIAL PRIMARY KEY,
    book_import_id      BIGINT NOT NULL REFERENCES book_imports (book_import_id),
    book_copy_id        BIGINT NOT NULL REFERENCES book_copies (book_copy_id),
    UNIQUE (book_import_id, book_copy_id)
);


CREATE TABLE borrow_records
(
    borrow_record_id BIGSERIAL PRIMARY KEY,
    borrow_code      VARCHAR(30)   NOT NULL UNIQUE,
    book_copy_id     BIGINT        NOT NULL REFERENCES book_copies (book_copy_id),
    borrower_id      BIGINT        NOT NULL REFERENCES users (user_id), -- người mượn
    librarian_id     BIGINT REFERENCES users (user_id),                 -- quản lý xử lý phiếu
    borrow_date      DATE          NOT NULL DEFAULT CURRENT_DATE,
    due_date         DATE          NOT NULL,                            -- hạn trả
    return_date      DATE,                                              -- NULL = chưa trả
    status           borrow_status NOT NULL DEFAULT 'BORROWING',
    note             TEXT,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT chk_borrow_due_after_borrow CHECK (due_date >= borrow_date)
);

CREATE INDEX idx_borrow_records_borrower_id ON borrow_records (borrower_id);
CREATE INDEX idx_borrow_records_librarian_id ON borrow_records (librarian_id);
CREATE INDEX idx_borrow_records_book_copy_id ON borrow_records (book_copy_id);
CREATE INDEX idx_borrow_records_status ON borrow_records (status);
CREATE INDEX idx_borrow_records_due_date ON borrow_records (due_date);

CREATE TABLE notifications
(
    notification_id  BIGSERIAL PRIMARY KEY,
    borrow_record_id BIGINT            NOT NULL REFERENCES borrow_records (borrow_record_id),
    user_id          BIGINT            NOT NULL REFERENCES users (user_id), -- người nhận
    type             notification_type NOT NULL,
    message          TEXT              NOT NULL,
    is_read          BOOLEAN           NOT NULL DEFAULT FALSE,
    sent_at          TIMESTAMPTZ       NOT NULL DEFAULT now(),
    created_at       TIMESTAMPTZ       NOT NULL DEFAULT now()
);

CREATE INDEX idx_notifications_user_id ON notifications (user_id);
CREATE INDEX idx_notifications_borrow_record_id ON notifications (borrow_record_id);
CREATE INDEX idx_notifications_is_read ON notifications (is_read);

CREATE
OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at
= now();
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_categories_updated_at
    BEFORE UPDATE
    ON categories
    FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_books_updated_at
    BEFORE UPDATE
    ON books
    FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_book_copies_updated_at
    BEFORE UPDATE
    ON book_copies
    FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_borrow_records_updated_at
    BEFORE UPDATE
    ON borrow_records
    FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();


CREATE
OR REPLACE FUNCTION fn_sync_book_available_quantity()
RETURNS TRIGGER AS $$
DECLARE
v_book_id BIGINT;
BEGIN
    v_book_id
:= COALESCE(NEW.book_id, OLD.book_id);

UPDATE books
SET available_quantity = (SELECT COUNT(*)
                          FROM book_copies
                          WHERE book_id = v_book_id
                            AND status = 'AVAILABLE'),
    total_quantity     = (SELECT COUNT(*)
                          FROM book_copies
                          WHERE book_id = v_book_id
                            AND status <> 'RETIRED')
WHERE book_id = v_book_id;

RETURN NULL;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_book_copies_sync_qty
    AFTER INSERT OR
UPDATE OF status OR
DELETE
ON book_copies
    FOR EACH ROW EXECUTE FUNCTION fn_sync_book_available_quantity();