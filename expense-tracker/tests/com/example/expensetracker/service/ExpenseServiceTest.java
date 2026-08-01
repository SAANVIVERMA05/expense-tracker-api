package com.example.expensetracker.service;

import com.example.expensetracker.exception.ExpenseNotFoundException;
import com.example.expensetracker.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseServiceTest {

    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        // Since we are using an in-memory list inside a new instance of ExpenseService,
        // each test will run with a clean, empty state.
        expenseService = new ExpenseService();
    }

    @Test
    void testAddExpense_shouldGenerateIdAndStore() {
        Expense expense = new Expense(null, "Dinner", new BigDecimal("50.00"), "Food", LocalDate.now());
        Expense saved = expenseService.addExpense(expense);

        assertNotNull(saved.getId());
        assertEquals(1L, saved.getId());
        assertEquals("Dinner", saved.getTitle());
        assertEquals(new BigDecimal("50.00"), saved.getAmount());
        assertEquals("Food", saved.getCategory());
        
        List<Expense> all = expenseService.getAllExpenses();
        assertEquals(1, all.size());
        assertEquals(saved.getId(), all.get(0).getId());
    }

    @Test
    void testGetAllExpenses_shouldReturnCopies() {
        Expense exp1 = new Expense(null, "Pizza", new BigDecimal("30.00"), "Food", LocalDate.now());
        Expense exp2 = new Expense(null, "Rent", new BigDecimal("800.00"), "Housing", LocalDate.now());
        expenseService.addExpense(exp1);
        expenseService.addExpense(exp2);

        List<Expense> all = expenseService.getAllExpenses();
        assertEquals(2, all.size());
        
        // Mutating the returned list should not affect internal list
        all.clear();
        assertEquals(2, expenseService.getAllExpenses().size());
    }

    @Test
    void testGetExpensesByCategory_shouldBeCaseInsensitive() {
        Expense exp1 = new Expense(null, "Pizza", new BigDecimal("30.00"), "Food", LocalDate.now());
        Expense exp2 = new Expense(null, "Uber", new BigDecimal("15.00"), "Travel", LocalDate.now());
        Expense exp3 = new Expense(null, "Burger", new BigDecimal("12.50"), "food", LocalDate.now()); // lowercase

        expenseService.addExpense(exp1);
        expenseService.addExpense(exp2);
        expenseService.addExpense(exp3);

        List<Expense> foodExpenses = expenseService.getExpensesByCategory("Food");
        assertEquals(2, foodExpenses.size());
        
        List<Expense> travelExpenses = expenseService.getExpensesByCategory("travel");
        assertEquals(1, travelExpenses.size());
        assertEquals("Uber", travelExpenses.get(0).getTitle());
    }

    @Test
    void testGetTotalExpense_shouldSumCorrectly() {
        expenseService.addExpense(new Expense(null, "Pizza", new BigDecimal("300.00"), "Food", LocalDate.now()));
        expenseService.addExpense(new Expense(null, "Rent", new BigDecimal("900.00"), "Housing", LocalDate.now()));

        BigDecimal total = expenseService.getTotalExpense();
        assertEquals(0, new BigDecimal("1200.00").compareTo(total));
    }

    @Test
    void testGetTotalExpenseByCategory_shouldSumCorrectlyAndBeCaseInsensitive() {
        expenseService.addExpense(new Expense(null, "Pizza", new BigDecimal("300.00"), "Food", LocalDate.now()));
        expenseService.addExpense(new Expense(null, "Rent", new BigDecimal("900.00"), "Housing", LocalDate.now()));
        expenseService.addExpense(new Expense(null, "Salad", new BigDecimal("400.00"), "food", LocalDate.now()));

        BigDecimal foodTotal = expenseService.getTotalExpenseByCategory("Food");
        assertEquals(0, new BigDecimal("700.00").compareTo(foodTotal));

        BigDecimal housingTotal = expenseService.getTotalExpenseByCategory("HOUSING");
        assertEquals(0, new BigDecimal("900.00").compareTo(housingTotal));

        BigDecimal nonExistentTotal = expenseService.getTotalExpenseByCategory("Entertainment");
        assertEquals(BigDecimal.ZERO, nonExistentTotal);
    }

    @Test
    void testDeleteExpense_success() {
        Expense exp1 = expenseService.addExpense(new Expense(null, "Pizza", new BigDecimal("30.00"), "Food", LocalDate.now()));
        Expense exp2 = expenseService.addExpense(new Expense(null, "Rent", new BigDecimal("900.00"), "Housing", LocalDate.now()));

        assertEquals(2, expenseService.getAllExpenses().size());

        expenseService.deleteExpense(exp1.getId());

        List<Expense> remaining = expenseService.getAllExpenses();
        assertEquals(1, remaining.size());
        assertEquals(exp2.getId(), remaining.get(0).getId());
    }

    @Test
    void testDeleteExpense_notFound_throwsException() {
        assertThrows(ExpenseNotFoundException.class, () -> expenseService.deleteExpense(99L));
    }
}
