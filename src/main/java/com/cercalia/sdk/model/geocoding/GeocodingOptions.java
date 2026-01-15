package com.cercalia.sdk.model.geocoding;

import org.jetbrains.annotations.Nullable;

/**
 * Options for geocoding requests to Cercalia API.
 * <p>
 * Provides a fluent builder interface for constructing geocoding queries.
 * Supports structured search by address components or free-form text queries.
 *
 * <pre>{@code
 * GeocodingService service = new GeocodingService(config);
 *
 * // Structured search by address components
 * List<GeocodingCandidate> results = service.geocode(GeocodingOptions.builder()
 *     .street("Carrer de la Provença")
 *     .locality("Barcelona")
 *     .postalCode("08013")
 *     .countryCode("ESP")
 *     .limit(10)
 *     .build());
 *
 * // Free-form text search
 * List<GeocodingCandidate> results = service.geocode(GeocodingOptions.builder()
 *     .query("Provença 5, Barcelona")
 *     .fullSearch(true)
 *     .build());
 * }</pre>
 */
public final class GeocodingOptions {
    
    @Nullable
    private final String query;
    
    @Nullable
    private final String country;
    
    @Nullable
    private final String countryCode;
    
    @Nullable
    private final String locality;
    
    @Nullable
    private final String municipality;
    
    @Nullable
    private final String region;
    
    @Nullable
    private final String subregion;
    
    @Nullable
    private final String street;
    
    @Nullable
    private final String postalCode;
    
    @Nullable
    private final String houseNumber;
    
    @Nullable
    private final Integer limit;
    
    @Nullable
    private final Boolean fullSearch;
    
    private GeocodingOptions(Builder builder) {
        this.query = builder.query;
        this.country = builder.country;
        this.countryCode = builder.countryCode;
        this.locality = builder.locality;
        this.municipality = builder.municipality;
        this.region = builder.region;
        this.subregion = builder.subregion;
        this.street = builder.street;
        this.postalCode = builder.postalCode;
        this.houseNumber = builder.houseNumber;
        this.limit = builder.limit;
        this.fullSearch = builder.fullSearch;
    }

    /**
     * Creates a new builder for constructing {@link GeocodingOptions} instances.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the free-form text query for geocoding.
     * <p>
     * When set, this takes precedence over structured address components.
     * Use this for general text searches like "Provença 5, Barcelona".
     *
     * @return the text query, or null if using structured search
     */
    @Nullable
    public String getQuery() { return query; }

    /**
     * Returns the country name.
     * <p>
     * The full country name (e.g., "Spain", "España").
     *
     * @return the country name, or null if not specified
     */
    @Nullable
    public String getCountry() { return country; }

    /**
     * Returns the country code (ISO 3166-1).
     * <p>
     * Recommended to use 2-letter codes (e.g., "ES" for Spain) or 3-letter codes (e.g., "ESP").
     * Default is "ESP" if not specified.
     *
     * @return the country code, or null if not specified
     */
    @Nullable
    public String getCountryCode() { return countryCode; }

    /**
     * Returns the locality (locality) name.
     * <p>
     * The primary locality for the search.
     *
     * @return the locality name, or null if not specified
     */
    @Nullable
    public String getLocality() { return locality; }

    /**
     * Returns the municipality name.
     * <p>
     * The administrative municipality for the search.
     *
     * @return the municipality name, or null if not specified
     */
    @Nullable
    public String getMunicipality() { return municipality; }

    /**
     * Returns the region (state/autonomous community) name.
     * <p>
     * The administrative region for the search.
     *
     * @return the region name, or null if not specified
     */
    @Nullable
    public String getRegion() { return region; }

    /**
     * Returns the subregion (subregion) name.
     * <p>
     * The administrative subregion (typically province) for the search.
     *
     * @return the subregion name, or null if not specified
     */
    @Nullable
    public String getSubregion() { return subregion; }

    /**
     * Returns the street name.
     * <p>
     * The street name for structured address search.
     *
     * @return the street name, or null if not specified
     */
    @Nullable
    public String getStreet() { return street; }

    /**
     * Returns the postal code.
     * <p>
     * The postal code for the search area.
     *
     * @return the postal code, or null if not specified
     */
    @Nullable
    public String getPostalCode() { return postalCode; }

    /**
     * Returns the house number.
     * <p>
     * The specific house or building number for the address.
     *
     * @return the house number, or null if not specified
     */
    @Nullable
    public String getHouseNumber() { return houseNumber; }

    /**
     * Returns the maximum number of results to return.
     * <p>
     * Limits the number of candidates returned by the API.
     * If not specified, the API's default limit is used.
     *
     * @return the result limit, or null for API default
     */
    @Nullable
    public Integer getLimit() { return limit; }

    /**
     * Returns whether to enable full search mode.
     * <p>
     * When enabled, performs a more exhaustive search that may include
     * partial matches and broader results. Useful when the initial search
     * returns few or no results.
     *
     * @return true if full search is enabled, false or null otherwise
     */
    @Nullable
    public Boolean getFullSearch() { return fullSearch; }

    /**
     * Builder for constructing {@link GeocodingOptions} instances.
     * <p>
     * Supports both structured search (by address components) and free-form text search.
     * All setter methods return {@code this} for method chaining.
     *
     * <pre>{@code
     * // Structured search
     * GeocodingOptions options = GeocodingOptions.builder()
     *     .street("Carrer de la Provença")
     *     .locality("Barcelona")
     *     .countryCode("ESP")
     *     .build();
     *
     * // Free-form search
     * GeocodingOptions options = GeocodingOptions.builder()
     *     .query("Provença 5, Barcelona")
     *     .fullSearch(true)
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        private String query;
        private String country;
        private String countryCode;
        private String locality;
        private String municipality;
        private String region;
        private String subregion;
        private String street;
        private String postalCode;
        private String houseNumber;
        private Integer limit;
        private Boolean fullSearch;

        private Builder() {}

        /**
         * Sets the free-form text query.
         * <p>
         * When set, this takes precedence over structured address components.
         *
         * @param query the text to search for
         * @return this Builder instance for method chaining
         */
        public Builder query(String query) { this.query = query; return this; }

        /**
         * Sets the country name.
         *
         * @param country the full country name
         * @return this Builder instance for method chaining
         */
        public Builder country(String country) { this.country = country; return this; }

        /**
         * Sets the country code (ISO 3166-1).
         * <p>
         * Recommended to use 2-letter codes (e.g., "ES") or 3-letter codes (e.g., "ESP").
         * Default is "ESP" if not specified.
         *
         * @param countryCode the country code
         * @return this Builder instance for method chaining
         */
        public Builder countryCode(String countryCode) { this.countryCode = countryCode; return this; }

        /**
         * Sets the locality (locality) name.
         *
         * @param locality the locality name
         * @return this Builder instance for method chaining
         */
        public Builder locality(String locality) { this.locality = locality; return this; }

        /**
         * Sets the municipality name.
         *
         * @param municipality the municipality name
         * @return this Builder instance for method chaining
         */
        public Builder municipality(String municipality) { this.municipality = municipality; return this; }

        /**
         * Sets the region (state/autonomous community) name.
         *
         * @param region the region name
         * @return this Builder instance for method chaining
         */
        public Builder region(String region) { this.region = region; return this; }

        /**
         * Sets the subregion (subregion) name.
         *
         * @param subregion the subregion name
         * @return this Builder instance for method chaining
         */
        public Builder subregion(String subregion) { this.subregion = subregion; return this; }

        /**
         * Sets the street name.
         *
         * @param street the street name
         * @return this Builder instance for method chaining
         */
        public Builder street(String street) { this.street = street; return this; }

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
         * Sets the maximum number of results to return.
         * <p>
         * If not specified, API's default limit is used.
         *
         * @param limit the maximum number of results (positive integer)
         * @return this Builder instance for method chaining
         */
        public Builder limit(Integer limit) { this.limit = limit; return this; }

        /**
         * Enables or disables full search mode.
         * <p>
         * When enabled, performs a more exhaustive search that may include
         * partial matches and broader results.
         *
         * @param fullSearch true to enable full search, false otherwise
         * @return this Builder instance for method chaining
         */
        public Builder fullSearch(Boolean fullSearch) { this.fullSearch = fullSearch; return this; }

        /**
         * Builds and returns a new {@link GeocodingOptions} instance.
         *
         * @return a new GeocodingOptions instance
         */
        public GeocodingOptions build() {
            return new GeocodingOptions(this);
        }
    }
}
