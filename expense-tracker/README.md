# Smart Expense Tracker API

A production-quality REST API for tracking personal expenses, built using **Java 17** and **Spring Boot 3** with **Maven**. 

Data is stored entirely in-memory using thread-safe collections (`CopyOnWriteArrayList`), meaning no database installation is required. Input validations are verified at the REST layer using Jakarta Bean Validation, and a global controller advice is set up to capture and return standardized JSON error responses.

---

## Features
- **Add Expense**: Validates inputs (non-blank titles, positive amounts, valid categories, and past/present dates) and auto-generates IDs.
- **View All Expenses**: Lists all recorded expenses.
- **Filter by Category**: Filters expenses by category name (case-insensitive).
- **Calculate Totals**: Get the sum of all expenses or retrieve totals filtered by category.
- **Delete Expense**: Safely deletes an expense by ID, returning a custom error payload if it is missing.
- **Global Error Handling**: Translates system/validation errors into structured JSON responses.
- **Swagger/OpenAPI Documentation**: Direct, interactive UI playground for developers to explore endpoints.

---

## Project Structure
The repository is organized exactly as follows:
```text
expense-tracker/
├── pom.xml
├── README.md
├── AI_NOTES.md
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── example/
│                   └── expensetracker/
│                       ├── ExpenseTrackerApplication.java
│                       ├── config/
│                       │   └── OpenAPIConfig.java
│                       ├── controller/
│                       │   └── ExpenseController.java
│                       ├── exception/
│                       │   ├── ErrorResponse.java
│                       │   ├── ExpenseNotFoundException.java
│                       │   └── GlobalExceptionHandler.java
│                       ├── model/
│                       │   └── Expense.java
│                       └── service/
│                           └── ExpenseService.java
└── tests/
    └── com/
        └── example/
            └── expensetracker/
                ├── controller/
                │   └── ExpenseControllerTest.java
                └── service/
                    └── ExpenseServiceTest.java
```

---

## Installation & Prerequisites
- **JDK 17** or higher installed. Verify using:
  ```bash
  java -version
  ```
- **Apache Maven** installed. Alternatively, you can use the bundled Maven runner inside modern IDEs (like IntelliJ).

---

## Maven & Running Commands

### 1. Build and Package
To clean build resources and package the application into a JAR:
```bash
mvn clean install
```

### 2. Run All Unit & Integration Tests
Runs the 22 JUnit 5 tests situated in the root `tests/` folder:
```bash
mvn test
```

### 3. Run the Server
Launches the Spring Boot embedded application:
```bash
mvn spring-boot:run
```

Once the application is running, the server is available at: [http://localhost:8080](http://localhost:8080)

---

## Swagger / OpenAPI Documentation
When the server is running, the interactive Swagger UI playground can be visited at:
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## API Endpoint Details & Examples

### 1. Add an Expense
- **Method & Path**: `POST /expenses`
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
  ```json
  {
    "id": 1,
    "title": "Pizza",
    "amount": 300.0,
    "category": "Food",
    "date": "2026-08-02"
  }
  ```
- **Validation Error Response**: `400 Bad Request`
  ```json
  {
    "timestamp": "2026-08-02 00:52:30",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "details": [
      "date: Date cannot be in the future",
      "amount: Amount must be greater than zero"
    ]
  }
  ```

### 2. View All Expenses
- **Method & Path**: `GET /expenses`
- **Success Response**: `200 OK`
  ```json
  [
    {
      "id": 1,
      "title": "Pizza",
      "amount": 300.0,
      "category": "Food",
      "date": "2026-08-02"
    }
  ]
  ```

### 3. Filter Expenses by Category
- **Method & Path**: `GET /expenses/category/{category}`
- **Example**: `GET /expenses/category/Food`
- **Success Response**: `200 OK`
  ```json
  [
    {
      "id": 1,
      "title": "Pizza",
      "amount": 300.0,
      "category": "Food",
      "date": "2026-08-02"
    }
  ]
  ```

### 4. Calculate Overall Total
- **Method & Path**: `GET /expenses/total`
- **Success Response**: `200 OK`
  ```json
  {
    "total": 300.00
  }
  ```

### 5. Calculate Total by Category
- **Method & Path**: `GET /expenses/total/{category}`
- **Example**: `GET /expenses/total/Food`
- **Success Response**: `200 OK`
  ```json
  {
    "category": "Food",
    "total": 300.00
  }
  ```

### 6. Delete an Expense
- **Method & Path**: `DELETE /expenses/{id}`
- **Example**: `DELETE /expenses/1`
- **Success Response**: `200 OK`
  ```json
  {
    "message": "Expense deleted successfully"
  }
  ```
- **Error Response (Expense Not Found)**: `404 Not Found`
  ```json
  {
    "timestamp": "2026-08-02 00:53:10",
    "status": 404,
    "error": "Not Found",
    "message": "Expense with ID 99 not found",
    "details": [
      "Expense with ID 99 not found"
    ]
  }
  ```
