package com.cercalia.sdk.model.isochrone;

/**
 * Weight type for isochrone calculation.
 */
public enum IsochroneWeight {
    /** Time-based isochrone (value in minutes) */
    TIME("time"),
    /** Distance-based isochrone (value in meters) */
    DISTANCE("distance");
    
    private final String value;
    
    IsochroneWeight(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
