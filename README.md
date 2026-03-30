# Vehicle Rental System

Full-stack vehicle rental application using Spring Boot, MySQL, and a static HTML/CSS/JS frontend.

## Features

- JWT-based signup and login
- Role-aware flows for Admin and Borrower
- Vehicle CRUD, service marking, and image upload
- Booking lifecycle: rent, extend, exchange, return, lost report
- Wallet top-up and security deposit handling
- Ratings and average rating display
- PDF invoice generation
- Email notifications for booking events

## Tech Stack

- Java 21
- Spring Boot 3.5.13
- Spring Web, Spring Data JPA, Spring Security
- MySQL 8
- JWT (jjwt)
- OpenPDF
- Spring Mail
- Static frontend from `src/main/resources/static`

## Project Layout

```text
src/main/java/com/rental/vehicle_rental/
  controller/      REST endpoints
  service/         business logic
  repository/      JPA repositories
  model/           entities and enums
  security/        JWT filter and security config
  dto/             request/response DTOs
  DataSeeder.java  default users and sample vehicles

src/main/resources/
  application.properties
  static/
    *.html
    css/
    js/
    uploads/
```

## Seed Data

On startup, `DataSeeder` inserts the following if they do not already exist:

- Admin: `admin@rental.com` / `admin123`
- Borrower: `user@rental.com` / `user123`
- Sample vehicles:
  - Honda City
  - Maruti Swift
  - Royal Enfield
  - Honda Activa

## Prerequisites

- Java 21
- MySQL running locally
- Maven or Maven Wrapper (`./mvnw`)

## Database Setup

Create the database before first run:

```sql
CREATE DATABASE vehicle_rental_db;
```

## Configuration

Main config file: `src/main/resources/application.properties`

Important behavior in the current code:

- `.env` loading is enabled via `spring.config.import=optional:file:.env[.properties]`
- Database and mail credentials are now read from environment placeholders (`DB_*`, `MAIL_*`)
- JWT and app URL are read from `JWT_*` and `APP_BASE_URL`

This means you can configure all sensitive values using `.env` (or environment variables) without editing tracked source files.

### Local Environment File

An example env file exists:

```bash
cp .env.example .env
```

`.env` is ignored by git.

## Run Locally

```bash
./mvnw clean spring-boot:run
```

Open:

```text
http://localhost:8080
```

## Build and Test

Compile without tests:

```bash
./mvnw -DskipTests compile
```

Run tests:

```bash
./mvnw test
```

## Main Pages

- `/index.html`
- `/admin-dashboard.html`
- `/manage-vehicles.html`
- `/admin-reports.html`
- `/borrower-management.html`
- `/borrower-dashboard.html`
- `/my-bookings.html`
- `/return-vehicle.html`
- `/profile.html`
- `/wallet.html`

## API Routes

Base path: `/api`

- Auth
  - `POST /api/auth/signup`
  - `POST /api/auth/login`
- Vehicles
  - `POST /api/vehicles`
  - `GET /api/vehicles`
  - `GET /api/vehicles/available`
  - `GET /api/vehicles/{id}`
  - `GET /api/vehicles/search`
  - `GET /api/vehicles/service-due`
  - `PUT /api/vehicles/{id}`
  - `PUT /api/vehicles/{id}/deposit`
  - `DELETE /api/vehicles/{id}`
- Bookings
  - `POST /api/bookings/rent`
  - `POST /api/bookings/return`
  - `POST /api/bookings/extend`
  - `POST /api/bookings/exchange`
  - `POST /api/bookings/lost/{bookingId}`
  - `GET /api/bookings/my`
  - `GET /api/bookings/my/active`
  - `GET /api/bookings/all`
- User
  - `GET /api/user/profile`
  - `PUT /api/user/profile`
  - `POST /api/user/wallet/topup`
  - `POST /api/user/ratings`
  - `GET /api/user/ratings/{vehicleId}`
  - `GET /api/user/ratings/{vehicleId}/average`
- Admin
  - `GET /api/admin/borrowers`
  - `PUT /api/admin/borrowers/{id}/block`
  - `PUT /api/admin/borrowers/{id}/unblock`
  - `GET /api/admin/revenue`
  - `PUT /api/admin/vehicles/{id}/service`
- Invoice and uploads
  - `GET /api/invoice/{bookingId}`
  - `POST /api/upload/vehicle/{vehicleId}/image`

Protected routes expect:

```text
Authorization: Bearer <jwt-token>
```

## Security Note

The repository currently contains sample credentials in config files intended for local development. Rotate credentials and move all secrets to environment variables or a secure secret manager before production use.

