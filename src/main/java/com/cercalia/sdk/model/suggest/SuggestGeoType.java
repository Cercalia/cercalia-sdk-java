package com.cercalia.sdk.model.suggest;

/**
 * Geographic type filter for suggest searches.
 */
public enum SuggestGeoType {
    /** Street suggestions */
    ST("st"),
    /** City/locality suggestions */
    CT("ct"),
    /** POI suggestions */
    POI("poi"),
    /** All types */
    ALL("all");

    private final String value;

    SuggestGeoType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
