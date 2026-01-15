package com.cercalia.sdk.model.isochrone;

/**
 * Method used to calculate the isochrone polygon boundary.
 */
public enum IsochroneMethod {
    /** Faster, creates a convex polygon (no concave edges) */
    CONVEXHULL("convexhull"),
    /** More accurate, creates a polygon that follows the actual reachable area */
    CONCAVEHULL("concavehull"),
    /** Logistics network (only for truck routing) */
    NET("net");
    
    private final String value;
    
    IsochroneMethod(String value) {
        this.value = value;
    }
    
    /**
     * @return Raw value for the API.
     */
    public String getValue() {
        return value;
    }
}
