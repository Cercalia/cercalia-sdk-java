package com.cercalia.sdk.model.suggest;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Geocoded result from Cercalia Suggest Geocode API.
 */
public final class SuggestGeocodeResult {
    
    @NotNull
    private final Coordinate coord;
    
    @NotNull
    private final String formattedAddress;
    
    @Nullable
    private final String name;
    
    @Nullable
    private final String streetCode;
    
    @Nullable
    private final String streetName;
    
    @Nullable
    private final String houseNumber;
    
    @Nullable
    private final String postalCode;
    
    @Nullable
    private final String cityCode;
    
    @Nullable
    private final String cityName;
    
    @Nullable
    private final String municipalityCode;
    
    @Nullable
    private final String municipalityName;
    
    @Nullable
    private final String subregionCode;
    
    @Nullable
    private final String subregionName;
    
    @Nullable
    private final String regionCode;
    
    @Nullable
    private final String regionName;
    
    @Nullable
    private final String countryCode;
    
    @Nullable
    private final String countryName;
    
    private SuggestGeocodeResult(Builder builder) {
        this.coord = builder.coord;
        this.formattedAddress = builder.formattedAddress;
        this.name = builder.name;
        this.streetCode = builder.streetCode;
        this.streetName = builder.streetName;
        this.houseNumber = builder.houseNumber;
        this.postalCode = builder.postalCode;
        this.cityCode = builder.cityCode;
        this.cityName = builder.cityName;
        this.municipalityCode = builder.municipalityCode;
        this.municipalityName = builder.municipalityName;
        this.subregionCode = builder.subregionCode;
        this.subregionName = builder.subregionName;
        this.regionCode = builder.regionCode;
        this.regionName = builder.regionName;
        this.countryCode = builder.countryCode;
        this.countryName = builder.countryName;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @NotNull public Coordinate getCoord() { return coord; }
    @NotNull public String getFormattedAddress() { return formattedAddress; }
    @Nullable public String getName() { return name; }
    @Nullable public String getStreetCode() { return streetCode; }
    @Nullable public String getStreetName() { return streetName; }
    @Nullable public String getHouseNumber() { return houseNumber; }
    @Nullable public String getPostalCode() { return postalCode; }
    @Nullable public String getCityCode() { return cityCode; }
    @Nullable public String getCityName() { return cityName; }
    @Nullable public String getMunicipalityCode() { return municipalityCode; }
    @Nullable public String getMunicipalityName() { return municipalityName; }
    @Nullable public String getSubregionCode() { return subregionCode; }
    @Nullable public String getSubregionName() { return subregionName; }
    @Nullable public String getRegionCode() { return regionCode; }
    @Nullable public String getRegionName() { return regionName; }
    @Nullable public String getCountryCode() { return countryCode; }
    @Nullable public String getCountryName() { return countryName; }
    
    public static final class Builder {
        private Coordinate coord;
        private String formattedAddress = "";
        private String name;
        private String streetCode;
        private String streetName;
        private String houseNumber;
        private String postalCode;
        private String cityCode;
        private String cityName;
        private String municipalityCode;
        private String municipalityName;
        private String subregionCode;
        private String subregionName;
        private String regionCode;
        private String regionName;
        private String countryCode;
        private String countryName;
        
        public Builder coord(Coordinate coord) { this.coord = coord; return this; }
        public Builder formattedAddress(String formattedAddress) { this.formattedAddress = formattedAddress; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder streetCode(String streetCode) { this.streetCode = streetCode; return this; }
        public Builder streetName(String streetName) { this.streetName = streetName; return this; }
        public Builder houseNumber(String houseNumber) { this.houseNumber = houseNumber; return this; }
        public Builder postalCode(String postalCode) { this.postalCode = postalCode; return this; }
        public Builder cityCode(String cityCode) { this.cityCode = cityCode; return this; }
        public Builder cityName(String cityName) { this.cityName = cityName; return this; }
        public Builder municipalityCode(String municipalityCode) { this.municipalityCode = municipalityCode; return this; }
        public Builder municipalityName(String municipalityName) { this.municipalityName = municipalityName; return this; }
        public Builder subregionCode(String subregionCode) { this.subregionCode = subregionCode; return this; }
        public Builder subregionName(String subregionName) { this.subregionName = subregionName; return this; }
        public Builder regionCode(String regionCode) { this.regionCode = regionCode; return this; }
        public Builder regionName(String regionName) { this.regionName = regionName; return this; }
        public Builder countryCode(String countryCode) { this.countryCode = countryCode; return this; }
        public Builder countryName(String countryName) { this.countryName = countryName; return this; }
        
        public SuggestGeocodeResult build() {
            return new SuggestGeocodeResult(this);
        }
    }
}
