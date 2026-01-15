package com.cercalia.sdk.model.reversegeocoding;

import org.jetbrains.annotations.Nullable;

/**
 * Level of detail for reverse geocoding requests.
 * Determines what geographic level to snap the coordinate to.
 */
public enum ReverseGeocodeLevel {
    /** Address (streets/roads with name, no road number) */
    CADR("cadr"),
    /** Address/road (including road number) and milestone */
    ADR("adr"),
    /** Street (no house number)/road (no milestone) */
    ST("st"),
    /** City (locality) */
    CT("ct"),
    /** Postal code */
    PCODE("pcode"),
    /** Municipality */
    MUN("mun"),
    /** Subregion (subregion) */
    SUBREG("subreg"),
    /** Region */
    REG("reg"),
    /** Country */
    CTRY("ctry"),
    /** Time zone info */
    TIMEZONE("timezone");

    private final String value;

    ReverseGeocodeLevel(String value) {
        this.value = value;
    }

    /**
     * Returns the API value for this level.
     *
     * @return the API value
     */
    public String getValue() {
        return value;
    }

    /**
     * Converts a string value to a ReverseGeocodeLevel.
     *
     * @param value the string value
     * @return the corresponding level, or null if not found
     */
    @Nullable
    public static ReverseGeocodeLevel fromValue(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String lowerValue = value.toLowerCase();
        for (ReverseGeocodeLevel level : values()) {
            if (level.value.equals(lowerValue)) {
                return level;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
