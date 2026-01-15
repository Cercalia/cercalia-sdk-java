package com.cercalia.sdk.model.suggest;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Options for suggest searches.
 */
public final class SuggestOptions {
    
    private final String text;
    
    @Nullable
    private final SuggestGeoType geoType;
    
    @Nullable
    private final String countryCode;
    
    @Nullable
    private final String regionCode;
    
    @Nullable
    private final String subregionCode;
    
    @Nullable
    private final String municipalityCode;
    
    @Nullable
    private final String streetCode;
    
    @Nullable
    private final String postalCodePrefix;
    
    @Nullable
    private final String language;
    
    @Nullable
    private final Coordinate center;
    
    @Nullable
    private final Integer radius;
    
    @Nullable
    private final List<String> poiCategories;
    
    private SuggestOptions(Builder builder) {
        this.text = builder.text;
        this.geoType = builder.geoType;
        this.countryCode = builder.countryCode;
        this.regionCode = builder.regionCode;
        this.subregionCode = builder.subregionCode;
        this.municipalityCode = builder.municipalityCode;
        this.streetCode = builder.streetCode;
        this.postalCodePrefix = builder.postalCodePrefix;
        this.language = builder.language;
        this.center = builder.center;
        this.radius = builder.radius;
        this.poiCategories = builder.poiCategories;
    }
    
    /**
     * @return A new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * @return The text to search for.
     */
    public String getText() { return text; }

    /**
     * @return The geographic type to search for.
     */
    @Nullable public SuggestGeoType getGeoType() { return geoType; }

    /**
     * @return The country code to filter results.
     */
    @Nullable public String getCountryCode() { return countryCode; }

    /**
     * @return The region code to filter results.
     */
    @Nullable public String getRegionCode() { return regionCode; }

    /**
     * @return The subregion code to filter results.
     */
    @Nullable public String getSubregionCode() { return subregionCode; }

    /**
     * @return The municipality code to filter results.
     */
    @Nullable public String getMunicipalityCode() { return municipalityCode; }

    /**
     * @return The street code to filter results.
     */
    @Nullable public String getStreetCode() { return streetCode; }

    /**
     * @return The postal code prefix to filter results.
     */
    @Nullable public String getPostalCodePrefix() { return postalCodePrefix; }

    /**
     * @return The language for the results.
     */
    @Nullable public String getLanguage() { return language; }

    /**
     * @return The center coordinate for biased results.
     */
    @Nullable public Coordinate getCenter() { return center; }

    /**
     * @return The search radius in meters around the center.
     */
    @Nullable public Integer getRadius() { return radius; }

    /**
     * @return The list of POI category codes to filter results.
     */
    @Nullable public List<String> getPoiCategories() { return poiCategories; }
    
    /**
     * Builder for {@link SuggestOptions}.
     */
    public static final class Builder {
        private String text;
        private SuggestGeoType geoType;
        private String countryCode;
        private String regionCode;
        private String subregionCode;
        private String municipalityCode;
        private String streetCode;
        private String postalCodePrefix;
        private String language;
        private Coordinate center;
        private Integer radius;
        private List<String> poiCategories;
        
        /**
         * @param text The text to search for.
         * @return The builder.
         */
        public Builder text(String text) { this.text = text; return this; }

        /**
         * @param geoType The geographic type filter.
         * @return The builder.
         */
        public Builder geoType(SuggestGeoType geoType) { this.geoType = geoType; return this; }

        /**
         * @param countryCode The country code filter.
         * @return The builder.
         */
        public Builder countryCode(String countryCode) { this.countryCode = countryCode; return this; }

        /**
         * @param regionCode The region code filter.
         * @return The builder.
         */
        public Builder regionCode(String regionCode) { this.regionCode = regionCode; return this; }

        /**
         * @param subregionCode The subregion code filter.
         * @return The builder.
         */
        public Builder subregionCode(String subregionCode) { this.subregionCode = subregionCode; return this; }

        /**
         * @param municipalityCode The municipality code filter.
         * @return The builder.
         */
        public Builder municipalityCode(String municipalityCode) { this.municipalityCode = municipalityCode; return this; }

        /**
         * @param streetCode The street code filter.
         * @return The builder.
         */
        public Builder streetCode(String streetCode) { this.streetCode = streetCode; return this; }

        /**
         * @param postalCodePrefix The postal code prefix filter.
         * @return The builder.
         */
        public Builder postalCodePrefix(String postalCodePrefix) { this.postalCodePrefix = postalCodePrefix; return this; }

        /**
         * @param language The language for results.
         * @return The builder.
         */
        public Builder language(String language) { this.language = language; return this; }

        /**
         * @param center The center coordinate.
         * @return The builder.
         */
        public Builder center(Coordinate center) { this.center = center; return this; }

        /**
         * @param radius The search radius in meters.
         * @return The builder.
         */
        public Builder radius(Integer radius) { this.radius = radius; return this; }

        /**
         * @param poiCategories The list of POI category codes.
         * @return The builder.
         */
        public Builder poiCategories(List<String> poiCategories) { this.poiCategories = poiCategories; return this; }
        
        /**
         * @return A new {@link SuggestOptions} instance.
         */
        public SuggestOptions build() {
            return new SuggestOptions(this);
        }
    }
}
