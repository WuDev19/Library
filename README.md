# 📚 Hệ Thống Quản Lý Thư Viện (Library Microservices)

Hệ thống quản lý thư viện hiện đại được xây dựng theo kiến trúc **Microservices** với **Spring Boot 3**, **Spring Cloud**, **Apache Kafka**, **Redis**, **PostgreSQL** và **React (Vite/TypeScript)**. 

Hệ thống hỗ trợ phân quyền người dùng (Thủ thư & Độc giả), quản lý đầu sách/bản sao, quy trình mượn - trả sách, theo dõi quá hạn và tự động gửi thông báo qua Email & In-App.

---

## 🚀 1. Kiến Trúc Hệ Thống (Architecture)

```
                                    +-----------------------+
                                    |     React Frontend    |
                                    |    (Port 3000 / Nginx)|
                                    +-----------+-----------+
                                                |
                                                v
                                    +-----------------------+
                                    |      API Gateway      |
                                    |      (Port 8080)      |
                                    +-----------+-----------+
                                                |
            +-----------------------------------+-----------------------------------+
            |                                   |                                   |
            v                                   v                                   v
  +-------------------+               +-------------------+               +-------------------+
  |   Auth Service    |               |   User Service    |               |Book Borrow Service|
  |    (Port 8081)    |               |    (Port 8082)    |               |    (Port 8083)    |
  +---------+---------+               +---------+---------+               +---------+---------+
            |                                   |                                   |
            v                                   v                                   v
  +-------------------+               +-------------------+               +-------------------+
  |  PostgreSQL / DB  |               |  PostgreSQL / DB  |               |  PostgreSQL / DB  |
  |     (auth_db)     |               |     (user_db)     |               |     (book_db)     |
  +-------------------+               +-------------------+               +---------+---------+
                                                                                    | (Events)
                                                                                    v
                                                                          +-------------------+
                                                                          |   Kafka Cluster   |
                                                                          | (Broker 1, 2, 3)  |
                                                                          +---------+---------+
                                                                                    |
                                                                                    v
                                                                          +-------------------+
                                                                          |NotificationService|
                                                                          |    (Port 8084)    |
                                                                          +-------------------+
```

### Các dịch vụ thành phần:
- **Discovery Server (Eureka - Port 8761)**: Quản lý đăng ký và phát hiện dịch vụ (Service Registry & Discovery).
- **API Gateway (Port 8080)**: Cổng giao tiếp trung tâm, định tuyến động, Rate Limiting (Redis), Circuit Breaker (Resilience4j) và xác thực JWT.
- **Auth Service (Port 8081)**: Quản lý tài khoản, mã hóa mật khẩu (BCrypt), cấp phát JWT & Refresh Token, blacklist token trên Redis.
- **User Service (Port 8082)**: Quản lý thông tin hồ sơ độc giả và thủ thư.
- **Book Borrow Service (Port 8083)**: Quản lý danh mục sách, số lượng bản sao (copies), quy trình mượn - trả sách, tính toán nợ/quá hạn và phát sự kiện qua Kafka (Outbox Pattern).
- **Notification Service (Port 8084)**: Lắng nghe sự kiện từ Kafka để tự động gửi Email thông báo và lưu trữ hộp thư thông báo in-app.
- **Lib Frontend (Port 3000)**: Giao diện người dùng React (Vite/TypeScript) với trải nghiệm phản hồi nhanh, hỗ trợ đầy đủ tính năng cho Thủ thư & Độc giả.

---

## 🛠️ 2. Công Nghệ Sử Dụng (Tech Stack)

### Backend
- **Framework**: Java 17, Spring Boot 4.1 / Spring Cloud 2025
- **Service Discovery & Gateway**: Eureka Discovery Server, Spring Cloud Gateway
- **Resilience & Rate Limit**: Resilience4j (CircuitBreaker & TimeLimiter), Redis RateLimiter
- **Database & Migration**: PostgreSQL 16, Flyway Migration, Spring Data JPA
- **Caching & In-Memory Storage**: Redis 7
- **Message Broker & Event-Driven**: Apache Kafka & Zookeeper (Cluster 3 Brokers)
- **Security**: Spring Security, JWT (JSON Web Token), OAuth2 Resource Server

### Frontend
- **Framework**: React 18, TypeScript, Vite
- **Web Server**: Nginx (Docker containerized)
- **UI & Icons**: Vanilla CSS (Custom Glassmorphism Design Token), FontAwesome Icons

### Infrastructure & DevOps
- **Containerization**: Docker, Docker Compose
- **Orchestration**: Healthcheck dependency chain, restart policies

---

## 📁 3. Cấu Trúc Thư Mục Project

```
Library/
├── ApiGateway/                # Spring Cloud Gateway Service
├── AuthService/               # Authentication & JWT Management Service
├── UserService/               # User Profile Service
├── BookBorrowService/         # Book & Borrow Transaction Service
├── NotificationService/       # Notification & Email Service
├── DiscoveryServer/           # Netflix Eureka Server
├── ConfigServer/              # Centralized Configuration Server (Optional)
├── lib-frontend/              # React / TypeScript Frontend Application
├── kafka/                     # Docker Compose config cho Kafka Cluster
├── postgres/                  # Init SQL scripts cho PostgreSQL databases
├── docker-compose.yml         # Container Orchestration toàn bộ hệ thống
└── README.md                  # Tài liệu hướng dẫn sử dụng
```

---

## 📋 4. Yêu Cầu Hệ Thống (Prerequisites)

Trước khi khởi chạy hệ thống, máy tính của bạn cần cài đặt:
- **Docker Desktop** (hoặc Docker Engine + Docker Compose v2)
- Khuyên dùng RAM khả dụng tối thiểu: **8 GB - 16 GB** (Do chạy nhiều microservices Java & Kafka cluster)

---

## ⚡ 5. Hướng Dẫn Cài Đặt & Khởi Chạy (Quick Start)

### Bước 1: Clone repository
```bash
git clone <repository-url>
cd Library
```

### Bước 2: Khởi tạo file cấu hình môi trường `.env`
Tạo file `.env` tại thư mục gốc `Library/` (nếu chưa có) với nội dung mẫu:

```env
DB_PASSWORD=root
AUTH_DB_PASSWORD=root
USER_DB_PASSWORD=root
BOOK_DB_PASSWORD=root
NOTIFICATION_PASSWORD=root
REDIS_PASS=redis123
JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
APP_EMAIL=your-email@gmail.com
APP_EMAIL_PASSWORD=your-app-password
```

### Bước 3: Khởi chạy toàn bộ hệ thống bằng Docker Compose

```bash
docker compose up --build -d
```

> **Lưu ý**: Lần đầu tiên chạy, Docker sẽ tải ảnh và build các file JAR. Quá trình khởi động hoàn tất khi các container đạt trạng thái `healthy` (khoảng 1 - 2 phút).

### Bước 4: Kiểm tra trạng thái các service
```bash
docker compose ps
```

---

## 🌐 6. Các Cổng Dịch Vụ & Địa Chỉ Truy Cập

| Dịch Vụ | Cổng (Port) | URL Truy Cập / Healthcheck |
| :--- | :--- | :--- |
| **Giao diện Web (Frontend)** | `3000` | [http://localhost:3000](http://localhost:3000) |
| **API Gateway** | `8080` | [http://localhost:8080](http://localhost:8080) |
| **Eureka Discovery Server** | `8761` | [http://localhost:8761](http://localhost:8761) |
| **Auth Service** | `8081` | [http://localhost:8081](http://localhost:8081) |
| **User Service** | `8082` | [http://localhost:8082](http://localhost:8082) |
| **Book Borrow Service** | `8083` | [http://localhost:8083](http://localhost:8083) |
| **Notification Service** | `8084` | [http://localhost:8084](http://localhost:8084) |
| **PostgreSQL** | `5432` | `localhost:5432` |
| **Redis** | `6379` | `localhost:6379` |

---

## 🔑 7. Hướng Dẫn Khởi Tạo Tài Khoản (User Accounts & Roles)

Hệ thống được khởi tạo sẵn **2 Vai trò (Roles)** trong Database (`V202608142211__seed_role.sql`):
1. **`LIBRARIAN` (Thủ thư)**: Có quyền xem Dashboard hệ thống, tạo/sửa/xóa đầu sách, nhập bản sao, quản lý danh mục, lập phiếu mượn/trả sách và quản lý người dùng.
2. **`BORROWER` (Độc giả)**: Có quyền xem kho sách, thực hiện mượn sách trực tuyến, xem lịch sử mượn trả và nhận thông báo.

### 📝 Đăng ký tài khoản mới:
Do hệ thống không lưu cứng mật khẩu mẫu trong script migration, bạn có thể tạo tài khoản mới bằng 2 cách:
1. **Trên Giao diện Web (`http://localhost:3000`)**:
   - Nhấn nút **Đăng ký (Sign Up)** trên góc màn hình.
   - Điền thông tin Username, Password, Email, Họ tên và chọn Vai trò (`LIBRARIAN` hoặc `BORROWER`).
2. **Qua API (`POST /api/v1/auth/sign-up`)**:
   ```json
   {
     "username": "librarian1",
     "password": "yourpassword",
     "passwordConfirm": "yourpassword",
     "email": "librarian@example.com",
     "fullName": "Nguyễn Văn Thủ Thư",
     "phone": "0987654321",
     "role": "LIBRARIAN"
   }
   ```

---

## 📌 8. Danh Sách API Chính (Core Endpoints)

Tất cả API được gọi thông qua **API Gateway (`http://localhost:8080`)**:

### Authentication (`/api/v1/auth`)
- `POST /api/v1/auth/login`: Đăng nhập lấy JWT Token & Refresh Token.
- `POST /api/v1/auth/sign-up`: Đăng ký tài khoản độc giả mới.
- `POST /api/v1/auth/refresh`: Làm mới Access Token.
- `POST /api/v1/auth/logout`: Đăng xuất và đưa Token vào blacklist Redis.

### Users (`/api/v1/user`)
- `GET /api/v1/user/all`: Lấy danh sách người dùng (Dành cho Thủ thư).
- `GET /api/v1/user/profile`: Lấy thông tin cá nhân hiện tại.
- `GET /api/v1/user/search?keyword=...`: Tìm kiếm người dùng theo tên/email.

### Books & Categories (`/api/v1/books`, `/api/v1/categories`)
- `GET /api/v1/books`: Lấy danh sách tất cả các đầu sách và số lượng bản sao.
- `POST /api/v1/books`: Thêm đầu sách mới (Thủ thư).
- `POST /api/v1/books/{id}/import`: Nhập thêm số lượng bản sao cho đầu sách.
- `GET /api/v1/categories`: Lấy danh sách danh mục sách.

### Borrows (`/api/v1/borrows`)
- `POST /api/v1/borrows`: Tạo phiếu mượn sách mới.
- `PUT /api/v1/borrows/{borrowCode}/return`: Thực hiện thủ tục trả sách.
- `GET /api/v1/borrows/my-history`: Xem lịch sử mượn sách của độc giả hiện tại.
- `GET /api/v1/borrows/all`: Xem tất cả phiếu mượn hệ thống (Thủ thư).

---

## ❓ 9. Xử Lý Sự Cố Thường Gặp (Troubleshooting)

1. **Service hiển thị 503 "Server hiện tại không khả dụng" khi mới up xong**:
   - Các dịch vụ Spring Boot mất khoảng 30s - 45s để kết nối DB, nạp Hibernate và đăng ký lên Eureka. Vui lòng chờ đến khi lệnh `docker compose ps` báo tất cả dịch vụ ở trạng thái `healthy`.

2. **Muốn xem log của một service cụ thể**:
   ```bash
   docker compose logs -f api-gateway-service
   docker compose logs -f auth-service
   ```

3. **Khởi động lại toàn bộ từ đầu (Clean state)**:
   ```bash
   docker compose down -v   # Xóa toàn bộ container và volume dữ liệu cũ
   docker compose up --build -d
   ```

---

## 📝 10. Giấy Phép & Đóng Góp (License)
Dự án được phát triển phục vụ mục đích học tập và làm đồ án kiến trúc phần mềm Microservices.
