package com.cercalia.sdk.model.geocoding;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a geocoding result candidate from the Cercalia API.
 * <p>
 * Contains comprehensive location information including:
 * <ul>
 *   <li>Geographic coordinates (latitude/longitude)</li>
 *   <li>Full address components (street, locality, municipality, etc.)</li>
 *   <li>Administrative hierarchy (district, subregion, region, country)</li>
 *   <li>Administrative IDs for each level (maintaining data integrity)</li>
 *   <li>Result type and precision level</li>
 * </ul>
 * <p>
 * All administrative fields are mapped directly from the API response without fallbacks,
 * ensuring data transparency and allowing consumers to handle missing information appropriately.
 *
 * <pre>{@code
 * GeocodingService service = new GeocodingService(config);
 * List<GeocodingCandidate> results = service.geocode(GeocodingOptions.builder()
 *     .street("Carrer de la Provença")
 *     .locality("Barcelona")
 *     .countryCode("ESP")
 *     .build());
 *
 * for (GeocodingCandidate candidate : results) {
 *     System.out.println(candidate.getName() + ": " + candidate.getCoord());
 * }
 * }</pre>
 */
public final class GeocodingCandidate {
    
    @NotNull
    private final String id;
    
    @NotNull
    private final String name;
    
    @Nullable
    private final String label;
    
    @Nullable
    private final String street;
    
    @Nullable
    private final String streetCode;
    
    @Nullable
    private final String locality;
    
    @Nullable
    private final String localityCode;
    
    @Nullable
    private final String municipality;
    
    @Nullable
    private final String municipalityCode; // Note: keeping typo for 1:1 compatibility with TS SDK
    
    @Nullable
    private final String district;
    
    @Nullable
    private final String districtCode;
    
    @Nullable
    private final String subregion;
    
    @Nullable
    private final String subregionCode;
    
    @Nullable
    private final String region;
    
    @Nullable
    private final String regionCode;
    
    @Nullable
    private final String country;
    
    @Nullable
    private final String countryCode;
    
    @Nullable
    private final String postalCode;
    
    @Nullable
    private final String houseNumber;
    
    @NotNull
    private final Coordinate coord;
    
    @NotNull
    private final GeocodingCandidateType type;
    
    @Nullable
    private final GeocodingLevel level;
    
    private GeocodingCandidate(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id cannot be null");
        this.name = Objects.requireNonNull(builder.name, "name cannot be null");
        this.label = builder.label;
        this.street = builder.street;
        this.streetCode = builder.streetCode;
        this.locality = builder.locality;
        this.localityCode = builder.localityCode;
        this.municipality = builder.municipality;
        this.municipalityCode = builder.municipalityCode;
        this.district = builder.district;
        this.districtCode = builder.districtCode;
        this.subregion = builder.subregion;
        this.subregionCode = builder.subregionCode;
        this.region = builder.region;
        this.regionCode = builder.regionCode;
        this.country = builder.country;
        this.countryCode = builder.countryCode;
        this.postalCode = builder.postalCode;
        this.houseNumber = builder.houseNumber;
        this.coord = Objects.requireNonNull(builder.coord, "coord cannot be null");
        this.type = Objects.requireNonNull(builder.type, "type cannot be null");
        this.level = builder.level;
    }
    
    /**
     * Creates a new builder for constructing {@link GeocodingCandidate} instances.
     * <p>
     * Provides a fluent API for setting all optional fields before building the final object.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the unique identifier for this location.
     * <p>
     * The ID format varies depending on the location type and data source.
     * Common formats include numeric IDs (e.g., "28"), country codes (e.g., "ESP"),
     * or composite identifiers (e.g., "ESPMAD").
     *
     * @return the location identifier, never null
     */
    @NotNull
    public String getId() { return id; }

    /**
     * Returns the display name of this location.
     * <p>
     * This is typically the most recognizable name for the location,
     * which may be a street name, city name, POI name, or country name
     * depending on the result type.
     *
     * @return the location name, never null
     */
    @NotNull
    public String getName() { return name; }

    /**
     * Returns the full address label or description.
     * <p>
     * This is the formatted address string as returned by the Cercalia API.
     * May include the full street address, postal code, and locality.
     *
     * @return the address label, or null if not available
     */
    @Nullable
    public String getLabel() { return label; }

    /**
     * Returns the street name.
     * <p>
     * Available for street-level and address-level results.
     *
     * @return the street name, or null if not applicable
     */
    @Nullable
    public String getStreet() { return street; }

    /**
     * Returns the street code/identifier.
     * <p>
     * The internal identifier for the street in the Cercalia database.
     *
     * @return the street code, or null if not available
     */
    @Nullable
    public String getStreetCode() { return streetCode; }

    /**
     * Returns the locality (locality) name.
     * <p>
     * This is the primary city or locality for the location.
     * Mapped directly from the API without administrative fallbacks.
     *
     * @return the locality name, or null if not available
     */
    @Nullable
    public String getLocality() { return locality; }

    /**
     * Returns the locality (locality) code/identifier.
     * <p>
     * The internal identifier for the locality in the Cercalia database.
     * Format varies but may include numeric or alphanumeric codes.
     *
     * @return the locality code, or null if not available
     */
    @Nullable
    public String getLocalityCode() { return localityCode; }

    /**
     * Returns the municipality name.
     * <p>
     * This is the administrative municipality for the location.
     * Mapped directly from the API without administrative fallbacks.
     *
     * @return the municipality name, or null if not available
     */
    @Nullable
    public String getMunicipality() { return municipality; }

    /**
     * Returns the municipality code/identifier.
     * <p>
     * The internal identifier for the municipality in the Cercalia database.
     * <b>Note:</b> This field name maintains a deliberate typo ("municipalityCode")
     * for 1:1 compatibility with the TypeScript SDK.
     *
     * @return the municipality code, or null if not available
     */
    @Nullable
    public String getMunicipalityCode() { return municipalityCode; }

    /**
     * Returns the district name.
     * <p>
     * This is an administrative subdivision within the municipality.
     * Not all locations have district information.
     *
     * @return the district name, or null if not available
     */
    @Nullable
    public String getDistrict() { return district; }

    /**
     * Returns the district code/identifier.
     * <p>
     * The internal identifier for the district in the Cercalia database.
     *
     * @return the district code, or null if not available
     */
    @Nullable
    public String getDistrictCode() { return districtCode; }

    /**
     * Returns the subregion (subregion) name.
     * <p>
     * This is the administrative subregion (typically province) for the location.
     * Mapped directly from the API without administrative fallbacks.
     *
     * @return the subregion name, or null if not available
     */
    @Nullable
    public String getSubregion() { return subregion; }

    /**
     * Returns the subregion (subregion) code/identifier.
     * <p>
     * The internal identifier for the subregion in the Cercalia database.
     *
     * @return the subregion code, or null if not available
     */
    @Nullable
    public String getSubregionCode() { return subregionCode; }

    /**
     * Returns the region (state/autonomous community) name.
     * <p>
     * This is the administrative region for the location.
     * Mapped directly from the API without administrative fallbacks.
     *
     * @return the region name, or null if not available
     */
    @Nullable
    public String getRegion() { return region; }

    /**
     * Returns the region (state/autonomous community) code/identifier.
     * <p>
     * The internal identifier for the region in the Cercalia database.
     *
     * @return the region code, or null if not available
     */
    @Nullable
    public String getRegionCode() { return regionCode; }

    /**
     * Returns the country name.
     * <p>
     * This is the country name in the language specified in the API request.
     *
     * @return the country name, or null if not available
     */
    @Nullable
    public String getCountry() { return country; }

    /**
     * Returns the country code (ISO 3166-1 alpha-2 or alpha-3).
     * <p>
     * Common formats include "ESP" (3-letter) or "ES" (2-letter).
     * The exact format depends on the Cercalia API response.
     *
     * @return the country code, or null if not available
     */
    @Nullable
    public String getCountryCode() { return countryCode; }

    /**
     * Returns the postal code.
     * <p>
     * The postal code format varies by country (e.g., "08013" for Spain).
     * May be null for some location types like countries or regions.
     *
     * @return the postal code, or null if not available
     */
    @Nullable
    public String getPostalCode() { return postalCode; }

    /**
     * Returns the house number.
     * <p>
     * Available only for address-level results that include specific building numbers.
     *
     * @return the house number, or null if not available
     */
    @Nullable
    public String getHouseNumber() { return houseNumber; }

    /**
     * Returns the geographic coordinates of this location.
     * <p>
     * Coordinates are always present for valid geocoding results.
     * No default values (like 0.0, 0.0) are used to avoid incorrect data.
     *
     * @return the coordinate with latitude and longitude, never null
     */
    @NotNull
    public Coordinate getCoord() { return coord; }

    /**
     * Returns the type of this geocoding result.
     * <p>
     * Indicates whether this result is an address, street, POI, locality, etc.
     * Useful for filtering and display purposes.
     *
     * @return the result type, never null
     * @see GeocodingCandidateType
     */
    @NotNull
    public GeocodingCandidateType getType() { return type; }

    /**
     * Returns the precision level of this geocoding result.
     * <p>
     * Indicates the administrative level or precision of the result.
     * Higher precision results (e.g., ADR, ST) have more specific location information.
     *
     * @return the geocoding level, or null if not available
     * @see GeocodingLevel
     */
    @Nullable
    public GeocodingLevel getLevel() { return level; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeocodingCandidate that = (GeocodingCandidate) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(coord, that.coord);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, coord);
    }
    
    @Override
    public String toString() {
        return "GeocodingCandidate{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", locality='" + locality + '\'' +
                ", coord=" + coord +
                ", type=" + type +
                '}';
    }

    /**
     * Builder for constructing {@link GeocodingCandidate} instances.
     * <p>
     * Provides a fluent API for setting all optional fields in a chainable manner.
     * Required fields ({@code id}, {@code name}, {@code coord}, {@code type})
     * will be validated when {@link #build()} is called.
     *
     * <pre>{@code
     * GeocodingCandidate candidate = GeocodingCandidate.builder()
     *     .id("12345")
     *     .name("Carrer de la Provença")
     *     .locality("Barcelona")
     *     .countryCode("ESP")
     *     .postalCode("08013")
     *     .coord(new Coordinate(41.3851, 2.1734))
     *     .type(GeocodingCandidateType.ADDRESS)
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        private String id;
        private String name;
        private String label;
        private String street;
        private String streetCode;
        private String locality;
        private String localityCode;
        private String municipality;
        private String municipalityCode;
        private String district;
        private String districtCode;
        private String subregion;
        private String subregionCode;
        private String region;
        private String regionCode;
        private String country;
        private String countryCode;
        private String postalCode;
        private String houseNumber;
        private Coordinate coord;
        private GeocodingCandidateType type;
        private GeocodingLevel level;

        private Builder() {}

        /**
         * Sets the unique identifier for this location.
         *
         * @param id the location identifier (required)
         * @return this Builder instance for method chaining
         */
        public Builder id(String id) { this.id = id; return this; }

        /**
         * Sets the display name of this location.
         *
         * @param name the location name (required)
         * @return this Builder instance for method chaining
         */
        public Builder name(String name) { this.name = name; return this; }

        /**
         * Sets the full address label or description.
         *
         * @param label the address label
         * @return this Builder instance for method chaining
         */
        public Builder label(String label) { this.label = label; return this; }

        /**
         * Sets the street name.
         *
         * @param street the street name
         * @return this Builder instance for method chaining
         */
        public Builder street(String street) { this.street = street; return this; }

        /**
         * Sets the street code/identifier.
         *
         * @param streetCode the street code
         * @return this Builder instance for method chaining
         */
        public Builder streetCode(String streetCode) { this.streetCode = streetCode; return this; }

        /**
         * Sets the locality (city) name.
         *
         * @param locality the locality name
         * @return this Builder instance for method chaining
         */
        public Builder locality(String locality) { this.locality = locality; return this; }

        /**
         * Sets the locality (city) code/identifier.
         *
         * @param localityCode the locality code
         * @return this Builder instance for method chaining
         */
        public Builder localityCode(String localityCode) { this.localityCode = localityCode; return this; }

        /**
         * Sets the municipality name.
         *
         * @param municipality the municipality name
         * @return this Builder instance for method chaining
         */
        public Builder municipality(String municipality) { this.municipality = municipality; return this; }

        /**
         * Sets the municipality code/identifier.
         *
         * @param municipalityCode the municipality code
         * @return this Builder instance for method chaining
         */
        public Builder municipalityCode(String municipalityCode) { this.municipalityCode = municipalityCode; return this; }

        /**
         * Sets the district name.
         *
         * @param district the district name
         * @return this Builder instance for method chaining
         */
        public Builder district(String district) { this.district = district; return this; }

        /**
         * Sets the district code/identifier.
         *
         * @param districtCode the district code
         * @return this Builder instance for method chaining
         */
        public Builder districtCode(String districtCode) { this.districtCode = districtCode; return this; }

        /**
         * Sets the subregion (province) name.
         *
         * @param subregion the subregion name
         * @return this Builder instance for method chaining
         */
        public Builder subregion(String subregion) { this.subregion = subregion; return this; }

        /**
         * Sets the subregion (province) code/identifier.
         *
         * @param subregionCode the subregion code
         * @return this Builder instance for method chaining
         */
        public Builder subregionCode(String subregionCode) { this.subregionCode = subregionCode; return this; }

        /**
         * Sets the region (state/autonomous community) name.
         *
         * @param region the region name
         * @return this Builder instance for method chaining
         */
        public Builder region(String region) { this.region = region; return this; }

        /**
         * Sets the region (state/autonomous community) code/identifier.
         *
         * @param regionCode the region code
         * @return this Builder instance for method chaining
         */
        public Builder regionCode(String regionCode) { this.regionCode = regionCode; return this; }

        /**
         * Sets the country name.
         *
         * @param country the country name
         * @return this Builder instance for method chaining
         */
        public Builder country(String country) { this.country = country; return this; }

        /**
         * Sets the country code (ISO 3166-1).
         *
         * @param countryCode the country code (e.g., "ESP", "ES")
         * @return this Builder instance for method chaining
         */
        public Builder countryCode(String countryCode) { this.countryCode = countryCode; return this; }

        /**
         * Sets the postal code.
         *
         * @param postalCode the postal code
         * @return this Builder instance for method chaining
         */
        public Builder postalCode(String postalCode) { this.postalCode = postalCode; return this; }

        /**
         * Sets the house number.
         *
         * @param houseNumber the house number
         * @return this Builder instance for method chaining
         */
        public Builder houseNumber(String houseNumber) { this.houseNumber = houseNumber; return this; }

        /**
         * Sets the geographic coordinates.
         *
         * @param coord the coordinate (required)
         * @return this Builder instance for method chaining
         */
        public Builder coord(Coordinate coord) { this.coord = coord; return this; }

        /**
         * Sets the type of this geocoding result.
         *
         * @param type the result type (required)
         * @return this Builder instance for method chaining
         */
        public Builder type(GeocodingCandidateType type) { this.type = type; return this; }

        /**
         * Sets the precision level of this geocoding result.
         *
         * @param level the geocoding level
         * @return this Builder instance for method chaining
         */
        public Builder level(GeocodingLevel level) { this.level = level; return this; }

        /**
         * Builds and returns a new {@link GeocodingCandidate} instance.
         * <p>
         * Required fields ({@code id}, {@code name}, {@code coord}, {@code type})
         * must be set before calling this method.
         *
         * @return a new GeocodingCandidate instance
         * @throws NullPointerException if required fields are not set
         */
        public GeocodingCandidate build() {
            return new GeocodingCandidate(this);
        }
    }
}
