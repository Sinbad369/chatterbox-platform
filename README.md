# 💬 Chatterbox Platform

**Chatterbox** is a full-stack social networking platform built with **Java Spring Boot**. It allows users to connect, share updates ("gossips"), and interact with a live feed in real-time.

The project demonstrates a production-grade implementation of a **RESTful API** architecture, integrating a secure backend with a dynamic frontend via AJAX.

## 🚀 Key Features

### 🔐 Security & Auth
* **Secure Authentication:** Full Registration and Login flows utilizing **Spring Security**.
* **Account Management:** Password Reset functionality via Email verification.
* **Role-Based Access Control:** Secure endpoints for users and admins.

### 📡 Core Functionality
* **CRUD Operations:** Users can Create, Read, Update, and Delete posts.
* **Live Feed:** Optimized data fetching to display user posts.
* **Notifications:** Integrated **Email** and **SMS** sending services for user alerts.
* **Internationalization (i18n):** Support for multiple languages.

### ⚙️ Engineering & DevOps
* **Database Migration:** Managed schema changes using **Flyway** for version control of the database.
* **API Documentation:** Fully documented endpoints using **Swagger/OpenAPI**.
* **Logging:** Comprehensive system logging for debugging and monitoring.
* **Configuration:** utilized `property files` for environment-specific settings.

---

## 🛠️ Tech Stack

**Backend**
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat&logo=spring-security&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat&logo=postgresql&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=flat&logo=hibernate&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=flat&logo=flyway&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apache-maven&logoColor=white)

**Frontend Integration**
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat&logo=javascript&logoColor=black)
![AJAX](https://img.shields.io/badge/AJAX-API-blue?style=flat)
![Bootstrap](https://img.shields.io/badge/Bootstrap-563D7C?style=flat&logo=bootstrap&logoColor=white)

---

## 🏗️ Architecture

The application follows a **Layered Architecture** (Controller-Service-Repository pattern) to ensure separation of concerns.

1.  **Controller Layer:** Handles HTTP requests and exposes REST APIs.
2.  **Service Layer:** Contains business logic (Email sending, validation, calculations).
3.  **Repository Layer:** Manages data persistence with **PostgreSQL**.
4.  **Client:** The frontend communicates purely via **JSON/AJAX**, making the backend completely decoupled and scalable.

---

## 📸 Screenshots
*I will Add screenshots later*

---

## 🏃‍♂️ How to Run

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/Sinbad369/chatterbox-platform.git](https://github.com/Sinbad369/chatterbox-platform.git)
    ```
2.  **Configure Database**
    * Create a PostgreSQL database named `chatterbox_db`.
    * Update `application.properties` with your credentials.
3.  **Run with Maven**
    ```bash
    mvn spring-boot:run
    ```
4.  **Access Swagger UI**
    * Go to: `http://localhost:8080/swagger-ui/index.html`
