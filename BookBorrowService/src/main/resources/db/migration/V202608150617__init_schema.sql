CREATE EXTENSION IF NOT EXISTS pg_trgm;

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
    code               VARCHAR(30)  NOT NULL UNIQUE,
    title              VARCHAR(255) NOT NULL,
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
    status       VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
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
    imported_by    BIGINT      NOT NULL, -- user_id trong UserService
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
    borrow_code      VARCHAR(30) NOT NULL UNIQUE,
    book_copy_id     BIGINT      NOT NULL REFERENCES book_copies (book_copy_id),
    borrower_id      BIGINT      NOT NULL, -- user_id người mượn
    librarian_id     BIGINT,               -- user_id quản lý xử lý
    borrow_date      DATE        NOT NULL DEFAULT CURRENT_DATE,
    due_date         DATE        NOT NULL,
    return_date      DATE,                 -- NULL = chưa trả
    status           VARCHAR(30) NOT NULL DEFAULT 'BORROWING',
    note             TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_borrow_due_after_borrow CHECK (due_date >= borrow_date)
);

CREATE INDEX idx_borrow_records_borrower_id ON borrow_records (borrower_id);
CREATE INDEX idx_borrow_records_librarian_id ON borrow_records (librarian_id);
CREATE INDEX idx_borrow_records_book_copy_id ON borrow_records (book_copy_id);
CREATE INDEX idx_borrow_records_status ON borrow_records (status);
CREATE INDEX idx_borrow_records_due_date ON borrow_records (due_date);
