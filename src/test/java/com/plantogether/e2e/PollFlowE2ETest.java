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
 * Validates the poll lifecycle: create trip → create poll → cast vote.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PollFlowE2ETest extends BaseE2ETest {

    private static String tripId;
    private static String pollId;
    private static final String deviceId = newDeviceId();

    @Test
    @Order(1)
    void setup_createTrip() {
        tripId = asDevice(deviceId)
                .baseUri(E2EConfig.TRIP_SERVICE_URL)
                .body(Map.of(
                        "name", "Poll E2E Trip",
                        "description", "Poll test setup",
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
    void createPoll_returnsCreatedPoll() {
        // adjust field names to match actual API contract
        Response response = asDevice(deviceId)
                .baseUri(E2EConfig.POLL_SERVICE_URL)
                .body(Map.of(
                        "tripId", tripId,
                        "type", "DATE",
                        "title", "When to go?",
                        "options", List.of("2025-06-01", "2025-06-15")
                ))
                .post("/api/v1/polls")
                .then()
                .statusCode(201)
                .extract()
                .response();

        pollId = response.jsonPath().getString("id");
        assertThat(pollId).isNotBlank();
    }

    @Test
    @Order(3)
    void castVote_succeeds() {
        // adjust field names to match actual API contract
        asDevice(deviceId)
                .baseUri(E2EConfig.POLL_SERVICE_URL)
                .body(Map.of("selectedOption", "2025-06-01"))
                .post("/api/v1/polls/{pollId}/votes", pollId)
                .then()
                .statusCode(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(200),
                        org.hamcrest.Matchers.is(201)
                ));
    }
}
