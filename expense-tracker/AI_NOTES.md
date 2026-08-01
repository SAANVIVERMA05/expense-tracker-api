# AI Notes - Smart Expense Tracker API

This file outlines the generation details, manual adjustments, and design trade-offs made during the implementation of the Smart Expense Tracker API.

---

## What AI Generated
The entire project scaffold, source code, and tests were systematically generated step-by-step:
1. **Build Configuration**: `pom.xml` with dependencies for Spring Boot 3 Web, Jakarta Validation, Springdoc OpenAPI, and Spring Boot Starter Test. Custom redirection of the test directory to the root `tests/` directory.
2. **Entity Design**: `Expense.java` with validation rules using Jakarta Constraints (`@NotBlank`, `@NotNull`, `@Positive`, `@Size`).
3. **Business Logic Layer**: `ExpenseService.java` implementing thread-safe calculations (`CopyOnWriteArrayList` and `AtomicLong` for primary key generation).
4. **API Endpoint Layer**: `ExpenseController.java` maps the specific paths and formats requested by the user, utilizing Spring Web controllers.
5. **Robust Error Handling**:
   - `ExpenseNotFoundException` for explicit element check errors.
   - `ErrorResponse` formatting standardized JSON errors with dates and field-level message details.
   - `GlobalExceptionHandler` returning proper HTTP status codes (`400 Bad Request`, `404 Not Found`, `500 Internal Server Error`).
6. **Tests**: JUnit 5 tests inside the custom `tests/` folder covering `ExpenseService` and mock-mvc controller endpoints.
7. **Documentation**: `README.md` and this `AI_NOTES.md`.

---

## What Was Manually Reviewed or Modified
During execution, the following was analyzed and verified:
1. **Java Platform and Maven**: Checked java version (`java 20`) and maven path. Since `mvn` was not in user's global PATH environment variable, we successfully located IntelliJ's bundled maven script at `C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.3.2.2\plugins\maven\lib\maven3\bin\mvn.cmd` and leveraged it to verify builds and test runs.
2. **Spring Boot Compilation Check**: Ran clean compile command after structural code generation to guarantee proper package mapping and syntax correction before writing test files.
3. **Response Wrappers**: Verified the `Map.of()` dynamic JSON models correctly serialized numeric fields (like totals) as numbers rather than strings.

---

## What Suggestions Were Rejected and Why
1. **Using Default Test Source Folder (`src/test/java`)**:
   - **Reason for Rejection**: The assignment specified the project structure must be exactly:
     ```
     expense-tracker/
     ├── README.md
     ├── AI_NOTES.md
     ├── src/
     └── tests/
     ```
   - **Alternative Implemented**: Modified the Maven build configuration in `pom.xml` by defining `<testSourceDirectory>tests</testSourceDirectory>` to correctly recognize tests in the custom directory.
2. **Using JPA or H2 Database**:
   - **Reason for Rejection**: The assignment stated: "Do NOT use any database. Store data in memory using a List."
   - **Alternative Implemented**: Used `CopyOnWriteArrayList` to ensure thread safety without database integration overhead.
3. **Returning Standard Spring Boot Error Fields**:
   - **Reason for Rejection**: Standard Spring Boot JSON error response structures include fields like `path` and `trace` that can expose backend internal details and might not match custom front-end expectations.
   - **Alternative Implemented**: Crafted a clean, bespoke `ErrorResponse` class containing only `timestamp`, `status`, `error`, `message`, and validation error details (`details`).
4. **String Amount Parsing**:
   - **Reason for Rejection**: The amount could have been kept as a simple floating-point `Double`.
   - **Alternative Implemented**: Utilized `BigDecimal` for currency representation to prevent float-point rounding errors, adhering to production-grade software standards.
