INSERT INTO roles (role_name, description, created_at)
VALUES ('LIBRARIAN', 'Library Manager / Librarian', now()),
       ('BORROWER', 'Book Borrower', now())
ON CONFLICT (role_name) DO NOTHING;
