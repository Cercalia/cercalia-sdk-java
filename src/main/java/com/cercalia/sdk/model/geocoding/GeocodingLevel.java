package com.cercalia.sdk.model.geocoding;

/**
 * Geocoding level indicating precision and administrative hierarchy of result.
 * <p>
 * Maps directly to Cercalia API values and indicates the granularity of
 * the geocoding result. Higher precision levels (ADR, ST) have more
 * specific location information than lower levels (CTRY, REG).
 * <p>
 * Precision levels from highest to lowest:
 * <ol>
 *   <li>{@link #ADR} - Full address with house number</li>
 *   <li>{@link #ST} - Street level</li>
 *   <li>{@link #CT} - City/locality level</li>
 *   <li>{@link #PCODE} - Postal code area</li>
 *   <li>{@link #MUN} - Municipality level</li>
 *   <li>{@link #SUBREG} - Subregion/province level</li>
 *   <li>{@link #REG} - Region/state level</li>
 *   <li>{@link #CTRY} - Country level</li>
 * </ol>
 *
 * @see GeocodingCandidate
 */
public enum GeocodingLevel {
    /** Highest precision: full address with house number */
    ADR("adr"),

    /** Street level: road without specific address */
    ST("st"),

    /** City (locality) level */
    CT("ct"),

    /** Postal code level */
    PCODE("pcode"),

    /** Municipality level */
    MUN("mun"),

    /** Subregion (subregion) level */
    SUBREG("subreg"),

    /** Region (state/autonomous community) level */
    REG("reg"),

    /** Lowest precision: country level */
    CTRY("ctry"),

    /** Road level */
    RD("rd"),

    /** Milestone (kilometer mark) level */
    PK("pk"),

    /** POI (Point of Interest) level */
    POI("poi");

    private final String value;

    GeocodingLevel(String value) {
        this.value = value;
    }

    /**
     * Returns the Cercalia API value for this level.
     *
     * @return API value
     */
    public String getValue() {
        return value;
    }

    /**
     * Parses a Cercalia level string to {@link GeocodingLevel}.
     * <p>
     * Case-insensitive comparison with Cercalia API values.
     *
     * @param value Cercalia level string
     * @return corresponding GeocodingLevel, or null if not recognized
     */
    public static GeocodingLevel fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (GeocodingLevel level : values()) {
            if (level.value.equalsIgnoreCase(value)) {
                return level;
            }
        }
        return null;
    }
}
