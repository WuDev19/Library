CREATE DATABASE auth_db;
CREATE DATABASE user_db;
CREATE DATABASE book_db;
CREATE DATABASE notification_db;

CREATE USER auth_user WITH PASSWORD 'auth123';
CREATE USER user_user WITH PASSWORD 'user123';
CREATE USER book_user WITH PASSWORD 'book123';
CREATE USER notification_user WITH PASSWORD 'notification123';

GRANT ALL PRIVILEGES ON DATABASE auth_db TO auth_user;
GRANT ALL PRIVILEGES ON DATABASE user_db TO user_user;
GRANT ALL PRIVILEGES ON DATABASE book_db TO book_user;
GRANT ALL PRIVILEGES ON DATABASE notification_db TO notification_user;

\connect auth_db
GRANT ALL ON SCHEMA public TO auth_user;

\connect user_db
GRANT ALL ON SCHEMA public TO user_user;

\connect book_db
GRANT ALL ON SCHEMA public TO book_user;

\connect notification_db
GRANT ALL ON SCHEMA public TO notification_user;