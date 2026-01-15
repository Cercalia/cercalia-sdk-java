package com.cercalia.sdk.model.proximity;

/**
 * Route weight for proximity search with routing.
 */
public enum ProximityRouteWeight {
    /**
     * Optimize for shortest time.
     */
    TIME("time"),

    /**
     * Optimize for shortest distance.
     */
    DISTANCE("distance");
    
    private final String value;
    
    ProximityRouteWeight(String value) {
        this.value = value;
    }
    
    /**
     * @return The API value for the weight.
     */
    public String getValue() {
        return value;
    }
}
