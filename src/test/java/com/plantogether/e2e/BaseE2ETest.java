package com.plantogether.e2e;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;

import java.util.UUID;

import static io.restassured.RestAssured.given;

/**
 * Base class for all E2E tests.
 * Configures RestAssured and provides shared helpers.
 */
public abstract class BaseE2ETest {

    @BeforeAll
    static void setupRestAssured() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Register JavaTimeModule so Jackson handles java.time types (Instant, etc.)
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        RestAssured.config = RestAssuredConfig.config()
                .objectMapperConfig(
                        ObjectMapperConfig.objectMapperConfig()
                                .jackson2ObjectMapperFactory((cls, charset) -> mapper)
                );
    }

    /**
     * Generates a new random device UUID, simulating a fresh device installation.
     */
    protected static String newDeviceId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Returns a pre-configured RequestSpecification that sends JSON and
     * attaches the given device UUID as the X-Device-Id header.
     */
    protected static RequestSpecification asDevice(String deviceId) {
        return given()
                .contentType(ContentType.JSON)
                .header("X-Device-Id", deviceId);
    }
}
