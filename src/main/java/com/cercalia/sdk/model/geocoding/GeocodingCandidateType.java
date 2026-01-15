package com.cercalia.sdk.model.geocoding;

/**
 * Type of geocoding candidate result from Cercalia API.
 * <p>
 * Indicates what kind of location the result represents, which is useful for
 * filtering results, displaying appropriate icons, or choosing the correct interaction pattern.
 * Common types include addresses, streets, POIs, and administrative regions.
 *
 * @see GeocodingCandidate
 */
public enum GeocodingCandidateType {
    /** Full address with house number */
    ADDRESS("address"),

    /** Street without specific address */
    STREET("street"),

    /** Point of Interest (POI) such as restaurant, hotel, gas station, etc. */
    POI("poi"),

    /** City (locality) */
    LOCALITY("locality"),

    /** Municipality */
    MUNICIPALITY("municipality"),

    /** Road or highway */
    ROAD("road"),

    /** Road milestone (kilometer mark) */
    MILESTONE("milestone"),

    /** Postal code area */
    POSTAL_CODE("postal_code");

    private final String value;

    GeocodingCandidateType(String value) {
        this.value = value;
    }

    /**
     * Returns the string value used in JSON serialization.
     *
     * @return string value
     */
    public String getValue() {
        return value;
    }

    /**
     * Maps a Cercalia API type string to {@link GeocodingCandidateType}.
     * <p>
     * Handles various formats returned by Cercalia API including:
     * <ul>
     *   <li>Standard format: "poi", "road", "milestone"</li>
     *   <li>Short codes: "ct" (locality), "st" (street), "pk" (milestone)</li>
     *   <li>Cercalia-specific: "pcode" (postal code)</li>
     * </ul>
     *
     * @param type Cercalia type string (case-insensitive)
     * @return corresponding GeocodingCandidateType, or ADDRESS if not recognized
     */
    public static GeocodingCandidateType fromCercaliaType(String type) {
        if (type == null) {
            return ADDRESS;
        }
        switch (type.toLowerCase()) {
            case "poi":
                return POI;
            case "ct":
                return LOCALITY;
            case "municipality":
                return MUNICIPALITY;
            case "pcode":
            case "postal_code":
                return POSTAL_CODE;
            case "rd":
            case "road":
                return ROAD;
            case "st":
                return STREET;
            case "pk":
            case "milestone":
                return MILESTONE;
            case "adr":
            default:
                return ADDRESS;
        }
    }
}
