package com.plantogether.e2e;

import com.plantogether.e2e.config.E2EConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Validates the expense lifecycle: create trip → record expense → list expenses.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExpenseFlowE2ETest extends BaseE2ETest {

    private static String tripId;
    private static final String deviceId = newDeviceId();

    @Test
    @Order(1)
    void setup_createTrip() {
        tripId = asDevice(deviceId)
                .baseUri(E2EConfig.TRIP_SERVICE_URL)
                .body(Map.of(
                        "name", "Expense E2E Trip",
                        "description", "Expense test setup",
                        "referenceCurrency", "EUR"
                ))
                .post("/api/v1/trips")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getString("id");

        assertThat(tripId).isNotBlank();
    }

    @Test
    @Order(2)
    void createExpense_returnsCreatedExpense() {
        // adjust field names to match actual API contract
        Response response = asDevice(deviceId)
                .baseUri(E2EConfig.EXPENSE_SERVICE_URL)
                .body(Map.of(
                        "tripId", tripId,
                        "description", "Hotel",
                        "amount", 150.0,
                        "currency", "EUR",
                        "paidBy", deviceId,
                        "splitAmong", List.of(deviceId)
                ))
                .post("/api/v1/expenses")
                .then()
                .statusCode(201)
                .extract()
                .response();

        String expenseId = response.jsonPath().getString("id");
        assertThat(expenseId).isNotBlank();
    }

    @Test
    @Order(3)
    void getExpenses_forTrip_returnsNonEmptyList() {
        List<?> expenses = asDevice(deviceId)
                .baseUri(E2EConfig.EXPENSE_SERVICE_URL)
                .queryParam("tripId", tripId)
                .get("/api/v1/expenses")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".");

        assertThat(expenses).isNotEmpty();
    }
}
