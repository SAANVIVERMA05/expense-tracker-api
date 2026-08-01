# AI Notes - Smart Expense Tracker API Review & Improvements

This file documents the AI-assisted generation details, manual code reviews, refinement changes, and rejected alternatives for the Smart Expense Tracker API.

---

## What AI Generated
1. **Maven Project Scaffold**: Configured the initial `pom.xml` with dependencies for Spring Boot 3, Jakarta Bean Validation, Springdoc OpenAPI UI, and Spring Boot Starter Test, pointing test sources to the root `tests/` folder.
2. **Layered Core Architecture**:
   - **Model**: `Expense.java` with validation constraints.
   - **Service**: `ExpenseService.java` providing in-memory operations and calculations.
   - **Controller**: `ExpenseController.java` implementing endpoints and mapping outputs.
3. **Robust Error Handling**:
   - `ExpenseNotFoundException` representing retrieval missing-checks.
   - `ErrorResponse` mapping standardized error structure containing timestamps, HTTP statuses, category info, and detailed messages.
   - `GlobalExceptionHandler` intercepting conversion errors and validation constraints.
4. **Interactive OpenAPI Metadata**: Added annotations on `ExpenseController.java` to format Swagger UI descriptors.
5. **JUnit 5 Test Coverage**: Formulated tests verifying calculations, deletions, correct categories, negative/zero/null amounts, and future date constraints.

---

## What Was Manually Reviewed or Modified
During the review and improvement cycle, the following manual investigations and adjustments were performed:
1. **Local environment checks**: Verified Java 20 environment variables. Configured and tested Maven scripts via the bundled IntelliJ executable:
   `C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.3.2.2\plugins\maven\lib\maven3\bin\mvn.cmd`
2. **Added Missing Validation**: Included the `@PastOrPresent` validation rule to the `date` field in `Expense.java` to satisfy future-date constraints check.
3. **Refined Error Mapping**: Cleaned up log representations and comments inside `GlobalExceptionHandler.java`, ensuring consistency with clean code guidelines.
4. **Enhanced Test Assertions**: Expanded `ExpenseControllerTest.java` with detailed assertions verifying validation failure categories (such as future date, null dates, null amounts, zero amounts, and empty categories).

---

## What Suggestions Were Rejected and Why
1. **Placing Unit Tests under `src/test/java`**:
   - **Reason for Rejection**: The assignment requirement explicitly specified unit tests must reside in the root `tests/` folder:
     ```
     expense-tracker/
     ├── README.md
     ├── AI_NOTES.md
     ├── src/
     └── tests/
     ```
   - **Action Taken**: Configured `<testSourceDirectory>tests</testSourceDirectory>` in Maven to bind this root directory for compile and execution phases.
2. **Utilizing an In-Memory Database (e.g. H2)**:
   - **Reason for Rejection**: The requirement states: "Do NOT use any database. Store data in memory using a List."
   - **Action Taken**: Implemented `CopyOnWriteArrayList` to ensure thread-safe operations in multi-threaded runtime environments without any DB dependencies.
3. **Adding Complex DTO Translation**:
   - **Reason for Rejection**: Creating DTO layers is optional and adds duplicate code mappings for a simple in-memory list application.
   - **Action Taken**: Used the core `Expense` class directly with validation annotations at the controller entrance, preserving simplicity and preventing boilerplates.
