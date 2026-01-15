package com.cercalia.sdk.model.poi;

/**
 * Weight criteria for POI routing calculations.
 */
public enum PoiRouteWeight {
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
    SHORT("short");
    
    private final String value;
    
    PoiRouteWeight(String value) {
        this.value = value;
    }
    
    /**
     * @return The API value for the weight.
     */
    public String getValue() {
        return value;
    }
}
