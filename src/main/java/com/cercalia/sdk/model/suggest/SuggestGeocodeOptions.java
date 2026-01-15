package com.cercalia.sdk.model.suggest;

import org.jetbrains.annotations.Nullable;

/**
 * Options for geocoding a suggestion.
 */
public final class SuggestGeocodeOptions {
    
    @Nullable
    private final String cityCode;
    
    @Nullable
    private final String postalCode;
    
    @Nullable
    private final String streetCode;
    
    @Nullable
    private final String streetNumber;
    
    @Nullable
    private final String countryCode;
    
    private SuggestGeocodeOptions(Builder builder) {
        this.cityCode = builder.cityCode;
        this.postalCode = builder.postalCode;
        this.streetCode = builder.streetCode;
        this.streetNumber = builder.streetNumber;
        this.countryCode = builder.countryCode;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Nullable public String getCityCode() { return cityCode; }
    @Nullable public String getPostalCode() { return postalCode; }
    @Nullable public String getStreetCode() { return streetCode; }
    @Nullable public String getStreetNumber() { return streetNumber; }
    @Nullable public String getCountryCode() { return countryCode; }
    
    public static final class Builder {
        private String cityCode;
        private String postalCode;
        private String streetCode;
        private String streetNumber;
        private String countryCode;
        
        public Builder cityCode(String cityCode) { this.cityCode = cityCode; return this; }
        public Builder postalCode(String postalCode) { this.postalCode = postalCode; return this; }
        public Builder streetCode(String streetCode) { this.streetCode = streetCode; return this; }
        public Builder streetNumber(String streetNumber) { this.streetNumber = streetNumber; return this; }
        public Builder countryCode(String countryCode) { this.countryCode = countryCode; return this; }
        
        public SuggestGeocodeOptions build() {
            return new SuggestGeocodeOptions(this);
        }
    }
}
