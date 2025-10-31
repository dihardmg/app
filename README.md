#  Service API

**Service API** adalah RESTful API service berbasis Spring Boot untuk development yang menyediakan layanan transaksi digital termasuk manajemen membership, layanan informasi, dan pemrosesan transaksi. Sistem ini mengimplementasikan fungsionalitas dengan JWT authentication dan database PostgreSQL.

## 🌟 Features

- **🔐 User Management**: Registrasi, login, dan manajemen profil pengguna
- **💰 Digital Wallet**: Sistem saldo dengan top-up dan transaksi
- **📱 Services**: Berbagai layanan pembayaran (PLN, PDAM, Pulsa, dll)
- **🛡️ JWT Authentication**: Autentikasi token-based yang aman
- **📁 File Upload**: Upload gambar profil dengan validasi
- **⚡ Performance**: Raw query dengan prepared statement untuk optimasi
- **📚 API Documentation**: Swagger UI interaktif

## 🛠️ Technologies

### Core Technologies
- **Java 25** - Bahasa pemrograman utama
- **Spring Boot 3.5.7** - Application framework
- **Spring Security** - Autentikasi dan otorisasi

### Database & Migration
- **PostgreSQL** - Database utama
- **Flyway** - Manajemen migrasi database

### Security & Authentication
- **JWT (JJWT 0.11.5)** - JSON Web Token implementation
- **BCrypt** - Enkripsi password
- **Spring Security** - Security framework

### API Documentation
- **SpringDoc OpenAPI 2.8.0** - Dokumentasi Swagger/OpenAPI

### Development Tools
- **Lombok 1.18.42** - Code generation
- **Maven** - Build management

## ✅ Prerequisites

- **Java 25** atau lebih tinggi
- **Maven 3.8+**
- **PostgreSQL** (installed locally)

## 🚀 Quick Start

### 1. Clone Repository
```bash
git clone https://github.com/dihardmg/app.git
cd app
```

### 2. Database Setup
Buat database dan user di PostgreSQL lokal Anda:
```sql
CREATE DATABASE app_db;
CREATE USER user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE app_db TO user;
```

### 3. Build dan Run Aplikasi
```bash
# Build project
mvn clean package

# Run dengan Maven
mvn clean spring-boot:run

# Atau run JAR yang sudah dibuild
java -jar target/digital-service-1.0.0.jar
```

### 4. Aplikasi akan tersedia di:
- **API**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **API Docs**: http://localhost:8081/api-docs

## 🚂 Railway Deployment

Cara mudah untuk deploy ke production menggunakan Railway. Lihat [RAILWAY_DEPLOYMENT.md](./RAILWAY_DEPLOYMENT.md) untuk panduan lengkap.

### Quick Deploy ke Railway
1. Push code ke GitHub repository
2. Connect repository ke Railway
3. Add PostgreSQL database di Railway
4. Set environment variable: `SPRING_PROFILES_ACTIVE=production`
5. Deploy! 🎉

**Setelah deploy ke Railway, aplikasi akan otomatis:**
- Menggunakan database PostgreSQL dari Railway
- Menggunakan domain Railway untuk API dan Swagger UI
- Konfigurasi otomatis untuk production environment

## 🌐 API Endpoints

### 🔐 Module Membership (Authentication & Profile)
**Base Path:** `/api/v1`

| Method | Endpoint | Description | Authentication | Request | Response |
|--------|----------|-------------|----------------|---------|----------|
| `POST` | `/registration` | User registration | Public | `{email, password, firstName, lastName}` | `{status, message, data}` |
| `POST` | `/login` | User login | Public | `{email, password}` | `{status, message, data: {token}}` |
| `GET` | `/profile` | Get user profile | Bearer Token | - | `{status, message, data: {email, firstName, lastName, profileImage}}` |
| `PUT` | `/profile/update` | Update user profile | Bearer Token | `{firstName, lastName}` | `{status, message, data}` |
| `PUT` | `/profile/image` | Update profile image | Bearer Token | `multipart/form-data` | `{status, message, data}` |

### 📱 Module Information (Services & Banners)
**Base Path:** `/api/v1`

| Method | Endpoint | Description | Authentication | Response |
|--------|----------|-------------|----------------|----------|
| `GET` | `/banner` | Get available banners | Public | `{status, message, data: [{banner_name, banner_image, description}]}` |
| `GET` | `/services` | Get available services | Bearer Token | `{status, message, data: [{service_code, service_name, service_icon, service_tariff}]}` |

### 💰 Module Transaction (Balance & Payments)
**Base Path:** `/api/v1`

| Method | Endpoint | Description | Authentication | Request | Response |
|--------|----------|-------------|----------------|---------|----------|
| `GET` | `/balance` | Get user balance | Bearer Token | - | `{status, message, data: {balance}}` |
| `POST` | `/topup` | Top up balance | Bearer Token | `{top_up_amount}` | `{status, message, data: {balance}}` |
| `POST` | `/transaction` | Make transaction | Bearer Token | `{serviceCode}` | `{status, message, data: {invoiceNumber, description, transactionType, totalAmount, createdOn}}` |
| `GET` | `/transaction/history` | Get transaction history | Bearer Token | `{limit?, offset?}` | `{status, message, data: {offset, limit, records}}` |

## 📋 API Usage Examples

### 1. Registration
```bash
curl -X POST http://localhost:8081/api/v1/registration \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8081/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### 3. Get Balance
```bash
curl -X GET http://localhost:8081/api/v1/balance \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Top Up Balance
```bash
curl -X POST http://localhost:8081/api/v1/topup \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "top_up_amount": 100000
  }'
```

### 5. Make Transaction
```bash
curl -X POST http://localhost:8081/api/v1/transaction \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "serviceCode": "PLN"
  }'
```

## 🗄️ Database Schema

### Core Tables

#### `users` table
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    profile_image VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

#### `balances` table
```sql
CREATE TABLE balances (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE NOT NULL,
    balance BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### `transactions` table
```sql
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    service_code VARCHAR(50),
    description VARCHAR(200),
    total_amount BIGINT NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### `services` table
```sql
CREATE TABLE services (
    id BIGSERIAL PRIMARY KEY,
    service_code VARCHAR(50) UNIQUE NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    service_icon VARCHAR(500),
    service_tariff BIGINT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Available Services
| Service Code | Service Name | Tariff |
|---------------|--------------|---------|
| PAJAK | Pajak PBB | 40,000 |
| PLN | Listrik Prabayar | 10,000 |
| PDAM | PDAM Berlangganan | 40,000 |
| PULSA | Pulsa Indosat | 40,000 |
| PGN | PGN Berlangganan | 50,000 |
| MUSIK | Musik Berlangganan | 50,000 |
| TV | TV Berlangganan | 50,000 |
| PAKET_DATA | Paket data | 50,000 |
| VOUCHER_GAME | Voucher Game | 100,000 |
| VOUCHER_MAKANAN | Voucher Makanan | 100,000 |
| QURBAN | Qurban | 200,000 |
| ZAKAT | Zakat | 300,000 |

## 📊 API Response Format

Semua API mengikuti format response standar:
```json
{
  "status": 0,        // 0 = success, 102 = bad request, 103 = unauthorized, 108 = invalid token
  "message": "Response message",
  "data": null       // Response data atau null jika tidak applicable
}
```

## 🔐 Security Implementation

### JWT Authentication Flow
1. **Login**: User menyediakan email/password, menerima JWT token
2. **Token Usage**: Sertakan Bearer token di header Authorization untuk endpoint yang dilindungi
3. **Token Validation**: JWT filter memvalidasi token di setiap request
4. **User Context**: Informasi user diekstrak dari JWT token

### Security Features
- Enkripsi password menggunakan BCrypt
- Autentikasi token-based JWT
- Stateless session management
- CORS configuration
- File upload validation
- Input validation dengan Bean Validation
- **Raw Query dengan Prepared Statement** untuk mencegah SQL injection

## 🔧 Configuration

### Environment Variables
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/app_db
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=password

# JWT Configuration
JWT_SECRET=mySecretKey123456789012345678901234567890123456789012345678901234567890
JWT_EXPIRATION=43200000

# Server Configuration
SERVER_PORT=8081

# File Upload
APP_UPLOAD_DIR=uploads
APP_BASE_URL=http://localhost:8081
```

## 🧪 Testing
### Manual Testing
Gunakan file `api_test.http` di root project untuk manual testing dengan IntelliJ IDEA atau VS Code REST Client.

## 🚀 Local Development

### Prerequisites
- Java 25+
- Maven 3.8+
- PostgreSQL 16+

### Setup Database
```sql
CREATE DATABASE app_db;
CREATE USER user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE app_db TO user;
```

### Run Application
```bash
# Build project
mvn clean package

# Run dengan Maven
mvn clean spring-boot:run

# Atau run JAR yang sudah dibuild
java -jar target/digital-service-1.0.0.jar
```

### Akses Aplikasi
- **API**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html


## 📝 Logging

### Application Logs
Aplikasi menggunakan logging standar Spring Boot dan akan menampilkan log di console saat dijalankan.

#### 3. JWT Token Issues
- Pastikan token tidak expired (default 12 jam)
- Periksa format token: `Authorization: Bearer <token>`
- Validasi JWT secret key configuration

#### 4. File Upload Issues
- Pastikan directory `uploads/` ada dan writable
- Periksa file size limit (max 10MB)
- Validasi file format (JPEG/PNG only)



### Rest Client
Import collection dari file `api_test.http` untuk rest client testing.




**Built with ❤️ by dihardmg@gmail.com**