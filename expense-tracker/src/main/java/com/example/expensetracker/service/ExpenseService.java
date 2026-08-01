package com.example.expensetracker.service;

import com.example.expensetracker.exception.ExpenseNotFoundException;
import com.example.expensetracker.model.Expense;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final List<Expense> expenses = new CopyOnWriteArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Adds a new expense with a generated ID.
     */
    public Expense addExpense(Expense expense) {
        expense.setId(idGenerator.getAndIncrement());
        expenses.add(expense);
        return expense;
    }

    /**
     * Retrieves all expenses.
     */
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    /**
     * Filters expenses by category (case-insensitive).
     */
    public List<Expense> getExpensesByCategory(String category) {
        if (category == null) {
            return new ArrayList<>();
        }
        return expenses.stream()
                .filter(expense -> category.equalsIgnoreCase(expense.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * Calculates the total amount of all expenses.
     */
    public BigDecimal getTotalExpense() {
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the total amount of expenses in a specific category (case-insensitive).
     */
    public BigDecimal getTotalExpenseByCategory(String category) {
        if (category == null) {
            return BigDecimal.ZERO;
        }
        return expenses.stream()
                .filter(expense -> category.equalsIgnoreCase(expense.getCategory()))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Deletes an expense by its ID. Throws ExpenseNotFoundException if not found.
     */
    public void deleteExpense(Long id) {
        boolean removed = expenses.removeIf(expense -> expense.getId().equals(id));
        if (!removed) {
            throw new ExpenseNotFoundException("Expense with ID " + id + " not found");
        }
    }
}
