# URL Shortener API

A simple REST-based URL Shortener built with Spring Boot. This API allows you to generate short URLs for longer web links and resolve them back to their original destinations.

---

## 🔧 Features

- Shorten any valid HTTP/HTTPS URL into compact, shareable links.
- Validates URLs before shortening
- Resolve short URLs back to their original form.
- Prevent duplicate short URLs for the same original URL.
- Configurable hash algorithm, slug length, and retries
- Dockerized with PostgreSQL
- RESTful API design

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+

## ⚙️ Technologies

- Kotlin + Spring Boot
- PostgreSQL
- Docker + Docker Compose
- Hibernate / Spring Data JPA
- SLF4J Logging

---

## ⚙️ Configuration

You can configure the shortener via `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://db:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=mypass
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

url.shortener.hashAlgorithm=SHA-256
url.shortener.slugLength=8
url.shortener.maxRetries=3
```

---

## 🐳 Run with Docker Compose
1. Make sure Docker is installed
2. Clone the project
```bash
        git clone https://github.com/Tara-y/url-shortener.git
        cd url-shortener
```
3. Build and start services:
```bash
        docker compose up --build
```
4. Visit your app at:
📍 http://localhost:8080/api

## 🔁 Database Persistence
    PostgreSQL data is persisted using Docker volumes.
    You can restart containers without losing your URLs.

    To reset everything (⚠️ including data):
        docker compose down -v

## 📌 API Endpoints

See [API Documentation](API_DOCUMENTATION.md).

---

## 🧪 Sample Usage

### Shorten a URL

```bash
curl -X POST http://localhost:8080/api/shorten      -H "Content-Type: application/json"      -d '{"originalUrl": "https://example.com"}'
```

### Resolve a Short URL

```bash
curl -v http://localhost:8080/api/resolve/{shortUrl}

```

---

## 📂 Project Structure

```
src/
 └── main/
      ├── kotlin/com/samples/urlshortener/
      │    ├── config/
      │    ├── controller/
      │    ├── exception/
      │    ├── model/
      │    ├── repository/
      │    └── seervice/
      └── resources/
           └── application.properties
           └── application-dev.properties
           └── application-prod.properties
```
---
## 👤 Author
    Tara Yaghoubian
  📍 https://www.linkedin.com/in/tara-yaghoubian/
  
  📍 https://github.com/Tara-y/ 

---
