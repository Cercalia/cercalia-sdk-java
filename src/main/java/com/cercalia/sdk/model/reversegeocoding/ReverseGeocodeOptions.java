package com.cercalia.sdk.model.reversegeocoding;

import org.jetbrains.annotations.Nullable;

/**
 * Options for reverse geocoding requests.
 * <p>
 * Allows specifying the level of detail (address, locality, subregion, etc.),
 * as well as requesting special data like timezones or agricultural parcels (Spain only).
 * </p>
 */
public final class ReverseGeocodeOptions {
    
    @Nullable
    private final ReverseGeocodeLevel level;
    
    @Nullable
    private final String dateTime;
    
    @Nullable
    private final String category;
    
    private ReverseGeocodeOptions(Builder builder) {
        this.level = builder.level;
        this.dateTime = builder.dateTime;
        this.category = builder.category;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Returns the level of detail for the result.
     *
     * @return the level
     */
    @Nullable
    public ReverseGeocodeLevel getLevel() {
        return level;
    }
    
    /**
     * Returns the datetime for timezone lookup (ISO 8601 format).
     *
     * @return the datetime
     */
    @Nullable
    public String getDateTime() {
        return dateTime;
    }
    
    /**
     * Returns the special category (censal section, sigpac - Spain only).
     * Valid values: "d00seccen", "d00sigpac"
     *
     * @return the category
     */
    @Nullable
    public String getCategory() {
        return category;
    }
    
    public static final class Builder {
        private ReverseGeocodeLevel level;
        private String dateTime;
        private String category;
        
        private Builder() {}
        
        /**
         * Sets the level of detail for the result.
         *
         * @param level the level
         * @return this builder
         */
        public Builder level(ReverseGeocodeLevel level) {
            this.level = level;
            return this;
        }
        
        /**
         * Sets the datetime for timezone lookup (ISO 8601 format).
         *
         * @param dateTime the datetime
         * @return this builder
         */
        public Builder dateTime(String dateTime) {
            this.dateTime = dateTime;
            return this;
        }
        
        /**
         * Sets the special category (Spain only).
         *
         * @param category "d00seccen" for census section, "d00sigpac" for SIGPAC parcel
         * @return this builder
         */
        public Builder category(String category) {
            this.category = category;
            return this;
        }
        
        public ReverseGeocodeOptions build() {
            return new ReverseGeocodeOptions(this);
        }
    }
}
