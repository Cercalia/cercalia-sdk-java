package com.cercalia.sdk.model.geoment;

/**
 * Type of geographic element returned by Geoment service.
 */
public enum GeographicElementType {
    MUNICIPALITY("municipality"),
    POSTAL_CODE("postal_code"),
    POI("poi"),
    REGION("region");
    
    private final String value;
    
    GeographicElementType(String value) {
        this.value = value;
    }
    
    /**
     * @return the string value of the geographic element type.
     */
    public String getValue() {
        return value;
    }
}
