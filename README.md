# 🌿 EcoImpact360 - Backend


## 📌 Table of Contents
1. [General Info](#-general-info)
2. [Technologies](#-technologies)
3. [Architecture](#-architecture)
4. [Functional Requirements](#-functional-requirements)
5. [Installation](#-installation)
6. [Project Structure](#-project-structure)
7. [API Endpoints](#-api-endpoints)

---

## 📖 General Info
**EcoImpact360** is a sustainability management platform built with Spring Boot. It allows educational institutions to track waste production, calculate carbon footprint (CO₂) equivalents, and generate competitive rankings between classrooms to encourage environmental responsibility.

---

## 🛠 Technologies
* **Language:** Java 21
* **Framework:** Spring Boot 3.5
* **Data Access:** Spring Data JPA
* **Database:** PostgreSQL / H2 (Configurable)
* **Build Tool:** Apache Maven
* **Core Libraries:** * [Lombok](https://projectlombok.org/) (Boilerplate reduction)
    * [Jakarta Persistence](https://jakarta.ee/specifications/persistence/) (ORM)
    * [Spring Web](https://spring.io/guides/gs/rest-service/) (RESTful APIs)

---

## 🏛 Architecture
The project follows a **Layered Architecture** (N-Tier) to ensure separation of concerns and maintainability:

* **Layer 1: Model (Entities):** JPA entities representing the database schema (School, Classroom, WasteEntry).
* **Layer 2: Repository:** Data access layer using the Repository pattern to abstract SQL operations.
* **Layer 3A: Service (Business Logic):** The "brain" of the app. Handles CO₂ calculations, ranking scores, and dashboard metrics.
* **Layer 3B: Controller (REST Endpoints):** Entry points for the frontend to interact with the system.
* **DTO (Data Transfer Objects):** Used to decouple the internal database models from the external API responses.
* **Exception Handling:** Global error management via `GlobalExceptionHandler`.

---

## ✅ Functional Requirements
* [x] **School Management:** Organization of schools and specific classrooms.
* [x] **Waste Tracking:** Logging of waste entries categorized by material type.
* [x] **Impact Calculation:** Automated conversion of waste weight into CO₂ impact.
* [x] **Gamification:** Ranking system to identify the most eco-friendly classrooms.
* [x] **Dashboard Metrics:** Real-time data visualization of environmental performance.
* [x] **Alert System:** Automated notifications for critical environmental data.

---

## ⚙️ Installation

**Clone the repository:**

```bash
git clone [https://github.com/EcoImpact-360/Backend.git]
cd ecoimpact360-backend

1. **Configure Database:**
   Edit `src/main/resources/application.properties` with your database credentials.

2. **Initial Data:**
   The system automatically populates initial waste types and conversion factors via `src/main/resources/data.sql`.

3. **Build and Run:**
```bash
mvn clean install
mvn spring-boot:run

Project structure

src/main/java/com/ecoimpact/
├── EcoImpactApplication.java  # Main Entry Point
├── model/                     # JPA Entities (Layer 1)
├── repository/                # JPA Repositories (Layer 2)
├── service/                   # Business Logic (Layer 3A)
├── controller/                # REST Controllers (Layer 3B)
├── dto/                       # Request/Response Data Objects
└── exception/                 # Custom Exception Handlers


Gemini dijo

Here is the final part of the README formatted in Markdown, ready to be copied and pasted:
Markdown

---

## ⚙️ Installation

1. **Configure Database:**
   Edit `src/main/resources/application.properties` with your database credentials.

2. **Initial Data:**
   The system automatically populates initial waste types and conversion factors via `src/main/resources/data.sql`.

3. **Build and Run:**
```bash
mvn clean install
mvn spring-boot:run

📂 Project Structure

Following the structure visible in the source code:
Plaintext

src/main/java/com/ecoimpact/
├── EcoImpactApplication.java  # Main Entry Point
├── model/                     # JPA Entities (Layer 1)
├── repository/                # JPA Repositories (Layer 2)
├── service/                   # Business Logic (Layer 3A)
├── controller/                # REST Controllers (Layer 3B)
├── dto/                       # Request/Response Data Objects
└── exception/                 # Custom Exception Handlers

🚀 API Endpoints

The backend provides a RESTful API with the following main resources:
Endpoint	Method	Description
/api/v1/waste-entries	POST/GET	Manage waste logs and entries.
/api/v1/rankings	GET	Retrieve classroom performance rankings.
/api/v1/dashboards	GET	Fetch calculated metrics and equivalencies.
/api/v1/alerts	GET	Monitor system-generated alerts.
/api/v1/schools	GET/POST	Manage institutional data.


Gemini dijo

Here is the final part of the README formatted in Markdown, ready to be copied and pasted:
Markdown

---

## ⚙️ Installation

1. **Configure Database:**
   Edit `src/main/resources/application.properties` with your database credentials.

2. **Initial Data:**
   The system automatically populates initial waste types and conversion factors via `src/main/resources/data.sql`.

3. **Build and Run:**
```bash
mvn clean install
mvn spring-boot:run

📂 Project Structure

Following the structure visible in the source code:
Plaintext

src/main/java/com/ecoimpact/
├── EcoImpactApplication.java  # Main Entry Point
├── model/                     # JPA Entities (Layer 1)
├── repository/                # JPA Repositories (Layer 2)
├── service/                   # Business Logic (Layer 3A)
├── controller/                # REST Controllers (Layer 3B)
├── dto/                       # Request/Response Data Objects
└── exception/                 # Custom Exception Handlers

🚀 API Endpoints

The backend provides a RESTful API with the following main resources:
Endpoint	Method	Description
/api/waste-entries	POST/GET	Manage waste logs and entries.
/api/rankings	GET	Retrieve classroom performance rankings.
/api/dashboards	GET	Fetch calculated metrics and equivalencies.
/api/alerts	GET	Monitor system-generated alerts.
/api/schools	GET/POST	Manage institutional data.
🧪 Testing

To run the automated test suite:
Bash

mvn test

Tests are located in src/test/java/com/ecoimpact_360/backend, covering service logic and repository persistence.
---
🤝 Collaboration

This project was developed with a focus on Clean Code and SOLID principles.

Coding Standards: Use CamelCase for all Java classes and variables.

Follow: All business logic must reside in the Service layer; Controllers should only handle requests/responses.


---
