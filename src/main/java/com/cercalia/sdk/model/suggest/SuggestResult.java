package com.cercalia.sdk.model.suggest;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Normalized suggestion result from Cercalia Suggest API.
 * 
 * <p>A suggestion represents a potential match for a partial search string,
 * which can be an address, a city, a POI, etc. Use {@link #getDisplayText()}
 * for the user-facing string and {@link #getId()} for further detailed queries.</p>
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * SuggestResult suggestion = results.get(0);
 * System.out.println("Suggestion: " + suggestion.getDisplayText());
 * if (suggestion.getCoord() != null) {
 *     System.out.println("Location: " + suggestion.getCoord());
 * }
 * }</pre>
 */
public final class SuggestResult {
    
    /**
     * Unique identifier for the suggestion.
     */
    @NotNull
    private final String id;
    
    /**
     * Formatted text intended to be displayed to the user.
     */
    @NotNull
    private final String displayText;
    
    /**
     * The type of the suggestion (e.g., ADDRESS, POI, CITY).
     */
    @NotNull
    private final SuggestResultType type;
    
    /**
     * Street information if the suggestion is an address or street.
     */
    @Nullable
    private final SuggestStreet street;
    
    /**
     * Locality information for the suggestion.
     */
    @Nullable
    private final SuggestCity city;
    
    /**
     * Postal code associated with the suggestion.
     */
    @Nullable
    private final String postalCode;
    
    /**
     * Municipality details.
     */
    @Nullable
    private final SuggestAdminEntity municipality;
    
    /**
     * Subregion (county/district) details.
     */
    @Nullable
    private final SuggestAdminEntity subregion;
    
    /**
     * Region (state/province) details.
     */
    @Nullable
    private final SuggestAdminEntity region;
    
    /**
     * Country details.
     */
    @Nullable
    private final SuggestAdminEntity country;
    
    /**
     * Geographic coordinates of the suggestion.
     */
    @Nullable
    private final Coordinate coord;
    
    /**
     * House number ranges or specific numbers if available.
     */
    @Nullable
    private final SuggestHouseNumbers houseNumbers;
    
    /**
     * POI details if the suggestion type is POI.
     */
    @Nullable
    private final SuggestPoi poi;
    
    /**
     * Whether the suggestion comes from an official data source.
     */
    @Nullable
    private final Boolean isOfficial;
    
    /**
     * Match score (0.0 to 100) indicating relevance.
     */
    @Nullable
    private final Double score;
    
    private SuggestResult(Builder builder) {
        this.id = builder.id;
        this.displayText = builder.displayText;
        this.type = builder.type;
        this.street = builder.street;
        this.city = builder.city;
        this.postalCode = builder.postalCode;
        this.municipality = builder.municipality;
        this.subregion = builder.subregion;
        this.region = builder.region;
        this.country = builder.country;
        this.coord = builder.coord;
        this.houseNumbers = builder.houseNumbers;
        this.poi = builder.poi;
        this.isOfficial = builder.isOfficial;
        this.score = builder.score;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * @return Unique identifier for the suggestion.
     */
    @NotNull public String getId() { return id; }

    /**
     * @return Formatted text intended to be displayed to the user.
     */
    @NotNull public String getDisplayText() { return displayText; }

    /**
     * @return The type of the suggestion.
     */
    @NotNull public SuggestResultType getType() { return type; }

    /**
     * @return Street information.
     */
    @Nullable public SuggestStreet getStreet() { return street; }

    /**
     * @return Locality information.
     */
    @Nullable public SuggestCity getCity() { return city; }

    /**
     * @return Postal code.
     */
    @Nullable public String getPostalCode() { return postalCode; }

    /**
     * @return Municipality details.
     */
    @Nullable public SuggestAdminEntity getMunicipality() { return municipality; }

    /**
     * @return Subregion details.
     */
    @Nullable public SuggestAdminEntity getSubregion() { return subregion; }

    /**
     * @return Region details.
     */
    @Nullable public SuggestAdminEntity getRegion() { return region; }

    /**
     * @return Country details.
     */
    @Nullable public SuggestAdminEntity getCountry() { return country; }

    /**
     * @return Geographic coordinates.
     */
    @Nullable public Coordinate getCoord() { return coord; }

    /**
     * @return House number details.
     */
    @Nullable public SuggestHouseNumbers getHouseNumbers() { return houseNumbers; }

    /**
     * @return POI details.
     */
    @Nullable public SuggestPoi getPoi() { return poi; }

    /**
     * @return Whether the suggestion is official.
     */
    @Nullable public Boolean getIsOfficial() { return isOfficial; }

    /**
     * @return Match score (0.0 to 100).
     */
    @Nullable public Double getScore() { return score; }
    
    /**
     * Builder for {@link SuggestResult}.
     */
    public static final class Builder {
        private String id = "";
        private String displayText = "";
        private SuggestResultType type = SuggestResultType.ADDRESS;
        private SuggestStreet street;
        private SuggestCity city;
        private String postalCode;
        private SuggestAdminEntity municipality;
        private SuggestAdminEntity subregion;
        private SuggestAdminEntity region;
        private SuggestAdminEntity country;
        private Coordinate coord;
        private SuggestHouseNumbers houseNumbers;
        private SuggestPoi poi;
        private Boolean isOfficial;
        private Double score;
        
        private Builder() {}

        /**
         * @param id Unique identifier.
         * @return This builder.
         */
        public Builder id(String id) { this.id = id; return this; }

        /**
         * @param displayText Display text.
         * @return This builder.
         */
        public Builder displayText(String displayText) { this.displayText = displayText; return this; }

        /**
         * @param type Suggestion type.
         * @return This builder.
         */
        public Builder type(SuggestResultType type) { this.type = type; return this; }

        /**
         * @param street Street details.
         * @return This builder.
         */
        public Builder street(SuggestStreet street) { this.street = street; return this; }

        /**
         * @param city Locality details.
         * @return This builder.
         */
        public Builder city(SuggestCity city) { this.city = city; return this; }

        /**
         * @param postalCode Postal code.
         * @return This builder.
         */
        public Builder postalCode(String postalCode) { this.postalCode = postalCode; return this; }

        /**
         * @param municipality Municipality details.
         * @return This builder.
         */
        public Builder municipality(SuggestAdminEntity municipality) { this.municipality = municipality; return this; }

        /**
         * @param subregion Subregion details.
         * @return This builder.
         */
        public Builder subregion(SuggestAdminEntity subregion) { this.subregion = subregion; return this; }

        /**
         * @param region Region details.
         * @return This builder.
         */
        public Builder region(SuggestAdminEntity region) { this.region = region; return this; }

        /**
         * @param country Country details.
         * @return This builder.
         */
        public Builder country(SuggestAdminEntity country) { this.country = country; return this; }

        /**
         * @param coord Geographic coordinates.
         * @return This builder.
         */
        public Builder coord(Coordinate coord) { this.coord = coord; return this; }

        /**
         * @param houseNumbers House number details.
         * @return This builder.
         */
        public Builder houseNumbers(SuggestHouseNumbers houseNumbers) { this.houseNumbers = houseNumbers; return this; }

        /**
         * @param poi POI details.
         * @return This builder.
         */
        public Builder poi(SuggestPoi poi) { this.poi = poi; return this; }

        /**
         * @param isOfficial Whether it's official.
         * @return This builder.
         */
        public Builder isOfficial(Boolean isOfficial) { this.isOfficial = isOfficial; return this; }

        /**
         * @param score Match score.
         * @return This builder.
         */
        public Builder score(Double score) { this.score = score; return this; }
        
        /**
         * Builds a new {@link SuggestResult} instance.
         * @return The new instance.
         */
        public SuggestResult build() {
            return new SuggestResult(this);
        }
    }
}
