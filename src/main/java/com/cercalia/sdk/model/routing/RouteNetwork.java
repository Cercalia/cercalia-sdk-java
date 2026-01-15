package com.cercalia.sdk.model.routing;

/**
 * Network type for routing.
 */
public enum RouteNetwork {
    /**
     * Standard car network (default).
     */
    CAR("car"),

    /**
     * Truck/logistics network with weight/height restrictions.
     */
    LOGISTICS("logistics"),

    /**
     * Spanish walking network.
     */
    ESPW("espw"),

    /**
     * USA walking network.
     */
    USAW("usaw");

    private final String value;

    RouteNetwork(String value) {
        this.value = value;
    }

    /**
     * @return The API value for the network.
     */
    public String getValue() {
        return value;
    }
}
