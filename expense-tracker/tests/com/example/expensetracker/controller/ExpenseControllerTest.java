package com.example.expensetracker.controller;

import com.example.expensetracker.exception.ExpenseNotFoundException;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
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
    void testAddExpense_success() throws Exception {
        Expense input = new Expense(null, "Pizza", new BigDecimal("300.00"), "Food", LocalDate.of(2026, 8, 2));
        Expense output = new Expense(1L, "Pizza", new BigDecimal("300.00"), "Food", LocalDate.of(2026, 8, 2));

        when(expenseService.addExpense(any(Expense.class))).thenReturn(output);

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Pizza")))
                .andExpect(jsonPath("$.amount", is(300.00)))
                .andExpect(jsonPath("$.category", is("Food")))
                .andExpect(jsonPath("$.date", is("2026-08-02")));
    }

    @Test
    void testAddExpense_validationFailure_emptyTitle() throws Exception {
        Expense invalid = new Expense(null, "", new BigDecimal("300.00"), "Food", LocalDate.of(2026, 8, 2));

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
    void testAddExpense_validationFailure_negativeAmount() throws Exception {
        Expense invalid = new Expense(null, "Pizza", new BigDecimal("-5.00"), "Food", LocalDate.of(2026, 8, 2));

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.details", hasItem(containsString("amount"))));
    }

    @Test
    void testGetAllExpenses() throws Exception {
        Expense exp = new Expense(1L, "Pizza", new BigDecimal("300.00"), "Food", LocalDate.of(2026, 8, 2));
        when(expenseService.getAllExpenses()).thenReturn(List.of(exp));

        mockMvc.perform(get("/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Pizza")));
    }

    @Test
    void testGetExpensesByCategory() throws Exception {
        Expense exp = new Expense(1L, "Pizza", new BigDecimal("300.00"), "Food", LocalDate.of(2026, 8, 2));
        when(expenseService.getExpensesByCategory("Food")).thenReturn(List.of(exp));

        mockMvc.perform(get("/expenses/category/Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category", is("Food")));
    }

    @Test
    void testGetTotalExpense() throws Exception {
        when(expenseService.getTotalExpense()).thenReturn(new BigDecimal("1200.00"));

        mockMvc.perform(get("/expenses/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(1200.00)));
    }

    @Test
    void testGetTotalExpenseByCategory() throws Exception {
        when(expenseService.getTotalExpenseByCategory("Food")).thenReturn(new BigDecimal("700.00"));

        mockMvc.perform(get("/expenses/total/Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category", is("Food")))
                .andExpect(jsonPath("$.total", is(700.00)));
    }

    @Test
    void testDeleteExpense_success() throws Exception {
        Mockito.doNothing().when(expenseService).deleteExpense(1L);

        mockMvc.perform(delete("/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Expense deleted successfully")));
    }

    @Test
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
