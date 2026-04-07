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
 * Validates the full trip lifecycle:
 * create → read → invite → join → list members → reject non-member access.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TripFlowE2ETest extends BaseE2ETest {

    // Shared state across ordered tests
    private static String tripId;
    private static String inviteToken;
    private static final String organizerDeviceId = newDeviceId();
    private static final String memberDeviceId    = newDeviceId();
    private static final String outsiderDeviceId  = newDeviceId();

    @Test
    @Order(1)
    void createTrip_returnsCreatedTrip() {
        Response response = asDevice(organizerDeviceId)
                .baseUri(E2EConfig.TRIP_SERVICE_URL)
                .body(Map.of(
                        "name", "E2E Trip",
                        "description", "Test",
                        "referenceCurrency", "EUR"
                ))
                .post("/api/v1/trips")
                .then()
                .statusCode(201)
                .extract()
                .response();

        tripId = response.jsonPath().getString("id");
        assertThat(tripId).isNotBlank();
    }

    @Test
    @Order(2)
    void getTrip_returnsTripForMember() {
        String name = asDevice(organizerDeviceId)
                .baseUri(E2EConfig.TRIP_SERVICE_URL)
                .get("/api/v1/trips/{tripId}", tripId)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("name");

        assertThat(name).isEqualTo("E2E Trip");
    }

    @Test
    @Order(3)
    void createInvitation_returnsToken() {
        Response response = asDevice(organizerDeviceId)
                .baseUri(E2EConfig.TRIP_SERVICE_URL)
                .post("/api/v1/trips/{tripId}/invite", tripId)
                .then()
                .statusCode(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(200),
                        org.hamcrest.Matchers.is(201)
                ))
                .extract()
                .response();

        // adjust field name to match actual API contract
        inviteToken = response.jsonPath().getString("token");
        assertThat(inviteToken).isNotBlank();
    }

    @Test
    @Order(4)
    void joinTrip_withToken_succeeds() {
        asDevice(memberDeviceId)
                .baseUri(E2EConfig.TRIP_SERVICE_URL)
                .body(Map.of(
                        "token", inviteToken,
                        "displayName", "Member2"
                ))
                .post("/api/v1/trips/{tripId}/join", tripId)
                .then()
                .statusCode(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(200),
                        org.hamcrest.Matchers.is(201)
                ));
    }

    @Test
    @Order(5)
    void getMembers_returnsBothMembers() {
        List<?> members = asDevice(organizerDeviceId)
                .baseUri(E2EConfig.TRIP_SERVICE_URL)
                .get("/api/v1/trips/{tripId}/members", tripId)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".");

        assertThat(members).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @Order(6)
    void nonMember_cannotGetTrip_returns403or404() {
        asDevice(outsiderDeviceId)
                .baseUri(E2EConfig.TRIP_SERVICE_URL)
                .get("/api/v1/trips/{tripId}", tripId)
                .then()
                .statusCode(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(403),
                        org.hamcrest.Matchers.is(404)
                ));
    }
}
