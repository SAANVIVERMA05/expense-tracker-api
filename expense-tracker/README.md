# Smart Expense Tracker API

A production-quality REST API for tracking expenses, built using Java 17 and Spring Boot 3 with Maven. Data is stored in-memory using thread-safe collections. Input validation is performed using Jakarta Bean Validation, and a global exception handler is implemented to return structured JSON error responses.

## Tech Stack
- **Language**: Java 17 (or newer)
- **Framework**: Spring Boot 3.3.2
- **Build Tool**: Maven
- **Documentation**: Swagger / OpenAPI 3

---

## Installation

### Prerequisites
- Java 17 or higher installed (`java -version` should display JDK 17+)
- Maven installed (or use the provided Maven execution instructions if Maven is bundled)

### Cloning and Setup
Clone the repository and navigate into the project directory:
```bash
cd expense-tracker
```

---

## Running the Application

To start the Spring Boot application locally:

```bash
mvn spring-boot:run
```
*(If you are running the Maven command using a specific path, e.g., IntelliJ's bundled maven, use the absolute path or `mvn.cmd`)*

Once started, the application runs on port `8080` by default: `http://localhost:8080`

### Swagger / OpenAPI Documentation
You can access the API documentation and test endpoints interactively at:
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## Testing the Application

To compile the codebase and run all unit/integration tests:

```bash
mvn test
```

JUnit 5 tests are located under the custom root directory `tests/` instead of `src/test/java`, configured via the `<testSourceDirectory>` tag in `pom.xml`.

---

## API Endpoints

### 1. Add an Expense
- **Endpoint**: `POST /expenses`
- **Request Body**:
  ```json
  {
    "title": "Pizza",
    "amount": 300,
    "category": "Food",
    "date": "2026-08-02"
  }
  ```
- **Success Response**: `201 Created`
- **Error Response** (e.g. invalid request): `400 Bad Request` with structured validation errors.

### 2. View All Expenses
- **Endpoint**: `GET /expenses`
- **Success Response**: `200 OK` (JSON array of expenses)

### 3. Filter Expenses by Category
- **Endpoint**: `GET /expenses/category/{category}`
- **Success Response**: `200 OK` (JSON array of expenses matching category, case-insensitive)
- **Example**: `GET /expenses/category/Food`

### 4. Calculate Overall Total
- **Endpoint**: `GET /expenses/total`
- **Success Response**: `200 OK`
  ```json
  {
    "total": 1200
  }
  ```

### 5. Calculate Category Total
- **Endpoint**: `GET /expenses/total/{category}`
- **Success Response**: `200 OK`
  ```json
  {
    "category": "Food",
    "total": 700
  }
  ```

### 6. Delete an Expense
- **Endpoint**: `DELETE /expenses/{id}`
- **Success Response**: `200 OK`
  ```json
  {
    "message": "Expense deleted successfully"
  }
  ```
- **Error Response** (Expense not found): `404 Not Found`
  ```json
  {
    "timestamp": "2026-08-02 00:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Expense with ID 99 not found",
    "details": ["Expense with ID 99 not found"]
  }
  ```
