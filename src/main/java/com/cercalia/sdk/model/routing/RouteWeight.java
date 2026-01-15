package com.cercalia.sdk.model.routing;

/**
 * Weight/optimization criteria for route calculation.
 */
public enum RouteWeight {
    /**
     * Optimize for shortest time.
     */
    TIME("time"),

    /**
     * Optimize for shortest distance.
     */
    DISTANCE("distance"),

    /**
     * Optimize for lowest toll cost.
     */
    MONEY("money"),

    /**
     * Use real-time traffic data.
     */
    REALTIME("realtime"),

    /**
     * Fast route (may be longer in distance).
     */
    FAST("fast"),

    /**
     * Short route (may be longer in time).
     */
    SHORT("short"),

    /**
     * Scheduled/predictive time (requires {@code departureTime}).
     */
    SPTIME("sptime"),

    /**
     * Scheduled/predictive toll cost (requires {@code departureTime}).
     */
    SPMONEY("spmoney"),

    /**
     * Time with traffic impedance.
     */
    TIMERIMP("timerimp");

    private final String value;

    RouteWeight(String value) {
        this.value = value;
    }

    /**
     * @return The API value for the weight.
     */
    public String getValue() {
        return value;
    }
}
