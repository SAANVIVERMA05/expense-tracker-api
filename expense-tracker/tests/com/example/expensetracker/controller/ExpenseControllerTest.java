package com.example.expensetracker.controller;

import com.example.expensetracker.exception.ExpenseNotFoundException;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService expenseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /expenses - Success")
    void testAddExpense_success() throws Exception {
        Expense input = new Expense(null, "Pizza", new BigDecimal("300.00"), "Food", LocalDate.of(2025, 8, 2));
        Expense output = new Expense(1L, "Pizza", new BigDecimal("300.00"), "Food", LocalDate.of(2025, 8, 2));

        when(expenseService.addExpense(any(Expense.class))).thenReturn(output);

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Pizza")))
                .andExpect(jsonPath("$.amount", is(300.00)))
                .andExpect(jsonPath("$.category", is("Food")))
                .andExpect(jsonPath("$.date", is("2025-08-02")));
    }

    @Test
    @DisplayName("POST /expenses - Validation Failure - Empty Title")
    void testAddExpense_validationFailure_emptyTitle() throws Exception {
        Expense invalid = new Expense(null, "", new BigDecimal("300.00"), "Food", LocalDate.of(2025, 8, 2));

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.details", hasItem(containsString("title"))));
    }

    @Test
    @DisplayName("POST /expenses - Validation Failure - Title Too Long")
    void testAddExpense_validationFailure_titleTooLong() throws Exception {
        String longTitle = "a".repeat(101);
        Expense invalid = new Expense(null, longTitle, new BigDecimal("300.00"), "Food", LocalDate.of(2025, 8, 2));

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.details", hasItem(containsString("Title must not exceed 100 characters"))));
    }

    @Test
    @DisplayName("POST /expenses - Validation Failure - Negative Amount")
    void testAddExpense_validationFailure_negativeAmount() throws Exception {
        Expense invalid = new Expense(null, "Pizza", new BigDecimal("-5.00"), "Food", LocalDate.of(2025, 8, 2));

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.details", hasItem(containsString("amount"))));
    }

    @Test
    @DisplayName("POST /expenses - Validation Failure - Zero Amount")
    void testAddExpense_validationFailure_zeroAmount() throws Exception {
        Expense invalid = new Expense(null, "Pizza", BigDecimal.ZERO, "Food", LocalDate.of(2025, 8, 2));

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.details", hasItem(containsString("Amount must be greater than zero"))));
    }

    @Test
    @DisplayName("POST /expenses - Validation Failure - Null Amount")
    void testAddExpense_validationFailure_nullAmount() throws Exception {
        Expense invalid = new Expense(null, "Pizza", null, "Food", LocalDate.of(2025, 8, 2));

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.details", hasItem(containsString("Amount is required"))));
    }

    @Test
    @DisplayName("POST /expenses - Validation Failure - Empty Category")
    void testAddExpense_validationFailure_emptyCategory() throws Exception {
        Expense invalid = new Expense(null, "Pizza", new BigDecimal("300.00"), "", LocalDate.of(2025, 8, 2));

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.details", hasItem(containsString("Category is required"))));
    }

    @Test
    @DisplayName("POST /expenses - Validation Failure - Null Date")
    void testAddExpense_validationFailure_nullDate() throws Exception {
        Expense invalid = new Expense(null, "Pizza", new BigDecimal("300.00"), "Food", null);

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.details", hasItem(containsString("Date is required"))));
    }

    @Test
    @DisplayName("POST /expenses - Validation Failure - Future Date")
    void testAddExpense_validationFailure_futureDate() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        Expense invalid = new Expense(null, "Pizza", new BigDecimal("300.00"), "Food", futureDate);

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.details", hasItem(containsString("Date cannot be in the future"))));
    }

    @Test
    @DisplayName("GET /expenses - Success")
    void testGetAllExpenses() throws Exception {
        Expense exp = new Expense(1L, "Pizza", new BigDecimal("300.00"), "Food", LocalDate.of(2025, 8, 2));
        when(expenseService.getAllExpenses()).thenReturn(List.of(exp));

        mockMvc.perform(get("/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Pizza")));
    }

    @Test
    @DisplayName("GET /expenses/category/{category} - Success")
    void testGetExpensesByCategory() throws Exception {
        Expense exp = new Expense(1L, "Pizza", new BigDecimal("300.00"), "Food", LocalDate.of(2025, 8, 2));
        when(expenseService.getExpensesByCategory("Food")).thenReturn(List.of(exp));

        mockMvc.perform(get("/expenses/category/Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category", is("Food")));
    }

    @Test
    @DisplayName("GET /expenses/total - Success")
    void testGetTotalExpense() throws Exception {
        when(expenseService.getTotalExpense()).thenReturn(new BigDecimal("1200.00"));

        mockMvc.perform(get("/expenses/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(1200.00)));
    }

    @Test
    @DisplayName("GET /expenses/total/{category} - Success")
    void testGetTotalExpenseByCategory() throws Exception {
        when(expenseService.getTotalExpenseByCategory("Food")).thenReturn(new BigDecimal("700.00"));

        mockMvc.perform(get("/expenses/total/Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category", is("Food")))
                .andExpect(jsonPath("$.total", is(700.00)));
    }

    @Test
    @DisplayName("DELETE /expenses/{id} - Success")
    void testDeleteExpense_success() throws Exception {
        Mockito.doNothing().when(expenseService).deleteExpense(1L);

        mockMvc.perform(delete("/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Expense deleted successfully")));
    }

    @Test
    @DisplayName("DELETE /expenses/{id} - Not Found")
    void testDeleteExpense_notFound() throws Exception {
        doThrow(new ExpenseNotFoundException("Expense with ID 99 not found"))
                .when(expenseService).deleteExpense(99L);

        mockMvc.perform(delete("/expenses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Expense with ID 99 not found")))
                .andExpect(jsonPath("$.details", hasItem("Expense with ID 99 not found")));
    }
}
