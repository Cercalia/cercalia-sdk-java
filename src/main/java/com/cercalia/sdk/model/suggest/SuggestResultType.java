package com.cercalia.sdk.model.suggest;

/**
 * Type of suggestion result.
 */
public enum SuggestResultType {
    STREET("street"),
    CITY("city"),
    POI("poi"),
    ADDRESS("address");

    private final String value;

    SuggestResultType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
