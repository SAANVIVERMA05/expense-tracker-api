package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
@Tag(name = "Expenses", description = "Operations for managing personal expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @Operation(summary = "Add a new expense", description = "Creates a new expense record with an auto-generated ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Expense created successfully",
                    content = @Content(schema = @Schema(implementation = Expense.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation errors",
                    content = @Content(schema = @Schema(example = "{\"timestamp\":\"2026-08-02 00:00:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Validation failed\",\"details\":[\"title: Title is required\"]}")))
    })
    public ResponseEntity<Expense> addExpense(@Valid @RequestBody Expense expense) {
        Expense created = expenseService.addExpense(expense);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Retrieve all expenses", description = "Returns a list of all recorded expenses.")
    @ApiResponse(responseCode = "200", description = "List of all expenses retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Expense.class))))
    public ResponseEntity<List<Expense>> getAllExpenses() {
        List<Expense> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Filter expenses by category", description = "Returns all expenses belonging to the specified category (case-insensitive).")
    @ApiResponse(responseCode = "200", description = "Filtered list of expenses retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Expense.class))))
    public ResponseEntity<List<Expense>> getExpensesByCategory(
            @Parameter(description = "Category name to filter by", example = "Food")
            @PathVariable String category) {
        List<Expense> expenses = expenseService.getExpensesByCategory(category);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/total")
    @Operation(summary = "Get overall total expenses", description = "Calculates the total monetary sum of all recorded expenses.")
    @ApiResponse(responseCode = "200", description = "Overall total calculated successfully",
            content = @Content(schema = @Schema(example = "{\"total\":1200.00}")))
    public ResponseEntity<Map<String, BigDecimal>> getTotalExpense() {
        BigDecimal total = expenseService.getTotalExpense();
        return ResponseEntity.ok(Map.of("total", total));
    }

    @GetMapping("/total/{category}")
    @Operation(summary = "Get total expenses by category", description = "Calculates the total monetary sum of expenses belonging to a specific category (case-insensitive).")
    @ApiResponse(responseCode = "200", description = "Category total calculated successfully",
            content = @Content(schema = @Schema(example = "{\"category\":\"Food\",\"total\":700.00}")))
    public ResponseEntity<Map<String, Object>> getTotalExpenseByCategory(
            @Parameter(description = "Category name to filter sum by", example = "Food")
            @PathVariable String category) {
        BigDecimal total = expenseService.getTotalExpenseByCategory(category);
        return ResponseEntity.ok(Map.of(
                "category", category,
                "total", total
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an expense", description = "Deletes an expense by its unique auto-generated ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense deleted successfully",
                    content = @Content(schema = @Schema(example = "{\"message\":\"Expense deleted successfully\"}"))),
            @ApiResponse(responseCode = "404", description = "Expense with specified ID not found",
                    content = @Content(schema = @Schema(example = "{\"timestamp\":\"2026-08-02 00:00:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Expense with ID 99 not found\",\"details\":[\"Expense with ID 99 not found\"]}")))
    })
    public ResponseEntity<Map<String, String>> deleteExpense(
            @Parameter(description = "ID of the expense to delete", example = "1")
            @PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(Map.of("message", "Expense deleted successfully"));
    }
}
