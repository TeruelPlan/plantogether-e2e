package com.plantogether.e2e.config;

/**
 * Central configuration for E2E tests.
 * Service URLs are derived from the E2E_BASE_URL environment variable,
 * which defaults to http://localhost for local runs.
 */
public final class E2EConfig {

    public static final String BASE_URL =
            System.getenv().getOrDefault("E2E_BASE_URL", "http://localhost");

    public static final String TRIP_SERVICE_URL        = BASE_URL + ":8081";
    public static final String POLL_SERVICE_URL        = BASE_URL + ":8082";
    public static final String DESTINATION_SERVICE_URL = BASE_URL + ":8083";
    public static final String EXPENSE_SERVICE_URL     = BASE_URL + ":8084";
    public static final String TASK_SERVICE_URL        = BASE_URL + ":8085";
    public static final String CHAT_SERVICE_URL        = BASE_URL + ":8086";
    public static final String FILE_SERVICE_URL        = BASE_URL + ":8088";

    private E2EConfig() {}
}
