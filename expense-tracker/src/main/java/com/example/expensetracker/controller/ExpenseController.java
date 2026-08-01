package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * POST /expenses
     * Adds a new expense.
     * Returns 201 Created on success.
     */
    @PostMapping
    public ResponseEntity<Expense> addExpense(@Valid @RequestBody Expense expense) {
        Expense created = expenseService.addExpense(expense);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * GET /expenses
     * Retrieves all expenses.
     */
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses() {
        List<Expense> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);
    }

    /**
     * GET /expenses/category/{category}
     * Filters expenses by category (case-insensitive).
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Expense>> getExpensesByCategory(@PathVariable String category) {
        List<Expense> expenses = expenseService.getExpensesByCategory(category);
        return ResponseEntity.ok(expenses);
    }

    /**
     * GET /expenses/total
     * Calculates the overall total expenses.
     * Returns e.g. {"total": 1200}
     */
    @GetMapping("/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotalExpense() {
        BigDecimal total = expenseService.getTotalExpense();
        return ResponseEntity.ok(Map.of("total", total));
    }

    /**
     * GET /expenses/total/{category}
     * Calculates the total expenses for a specific category.
     * Returns e.g. {"category": "Food", "total": 700}
     */
    @GetMapping("/total/{category}")
    public ResponseEntity<Map<String, Object>> getTotalExpenseByCategory(@PathVariable String category) {
        BigDecimal total = expenseService.getTotalExpenseByCategory(category);
        // Using Map.of to return a structured JSON response
        return ResponseEntity.ok(Map.of(
                "category", category,
                "total", total
        ));
    }

    /**
     * DELETE /expenses/{id}
     * Deletes an expense by its ID.
     * Returns {"message": "Expense deleted successfully"}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(Map.of("message", "Expense deleted successfully"));
    }
}
