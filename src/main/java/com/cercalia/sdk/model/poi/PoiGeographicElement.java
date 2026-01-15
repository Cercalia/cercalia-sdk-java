package com.cercalia.sdk.model.poi;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents geographic/administrative element data for a POI.
 * 
 * <p>Follows the Golden Rules: direct mapping from the API, providing both names
 * and IDs for all administrative levels. No administrative fallbacks are performed;
 * if the API returns {@code null}, this model returns {@code null}.</p>
 */
public final class PoiGeographicElement {
    
    /**
     * House number or address range.
     */
    @Nullable
    private final String houseNumber;
    
    /**
     * Name of the street.
     */
    @Nullable
    private final String street;
    
    /**
     * Unique identifier for the street.
     */
    @Nullable
    private final String streetCode;
    
    /**
     * Name of the locality (city/town).
     */
    @Nullable
    private final String locality;
    
    /**
     * Unique identifier for the locality.
     */
    @Nullable
    private final String localityCode;
    
    /**
     * Name of the municipality.
     */
    @Nullable
    private final String municipality;
    
    /**
     * Unique identifier for the municipality.
     * Note: Field name has intentional typo 'municipalityCode' for 1:1 compatibility with TypeScript SDK.
     */
    @Nullable
    private final String municipalityCode;
    
    /**
     * Name of the subregion (e.g., county/district).
     */
    @Nullable
    private final String subregion;
    
    /**
     * Unique identifier for the subregion.
     */
    @Nullable
    private final String subregionCode;
    
    /**
     * Name of the region (e.g., state/province).
     */
    @Nullable
    private final String region;
    
    /**
     * Unique identifier for the region.
     */
    @Nullable
    private final String regionCode;
    
    /**
     * Name of the country.
     */
    @Nullable
    private final String country;
    
    /**
     * ISO code or unique identifier for the country.
     */
    @Nullable
    private final String countryCode;
    
    private PoiGeographicElement(Builder builder) {
        this.houseNumber = builder.houseNumber;
        this.street = builder.street;
        this.streetCode = builder.streetCode;
        this.locality = builder.locality;
        this.localityCode = builder.localityCode;
        this.municipality = builder.municipality;
        this.municipalityCode = builder.municipalityCode;
        this.subregion = builder.subregion;
        this.subregionCode = builder.subregionCode;
        this.region = builder.region;
        this.regionCode = builder.regionCode;
        this.country = builder.country;
        this.countryCode = builder.countryCode;
    }
    
    /**
     * @return House number or address range.
     */
    @Nullable
    public String getHouseNumber() {
        return houseNumber;
    }
    
    /**
     * @return Name of the street.
     */
    @Nullable
    public String getStreet() {
        return street;
    }
    
    /**
     * @return Unique identifier for the street.
     */
    @Nullable
    public String getStreetCode() {
        return streetCode;
    }
    
    /**
     * @return Name of the locality.
     */
    @Nullable
    public String getLocality() {
        return locality;
    }
    
    /**
     * @return Unique identifier for the locality.
     */
    @Nullable
    public String getLocalityCode() {
        return localityCode;
    }
    
    /**
     * @return Name of the municipality.
     */
    @Nullable
    public String getMunicipality() {
        return municipality;
    }
    
    /**
     * Returns the municipality code.
     * <p>Note: Method name preserves the intentional typo {@code municipalityCode} for compatibility.</p>
     *
     * @return The municipality code.
     */
    @Nullable
    public String getMunicipalityCode() {
        return municipalityCode;
    }
    
    /**
     * @return Name of the subregion.
     */
    @Nullable
    public String getSubregion() {
        return subregion;
    }
    
    /**
     * @return Unique identifier for the subregion.
     */
    @Nullable
    public String getSubregionCode() {
        return subregionCode;
    }
    
    /**
     * @return Name of the region.
     */
    @Nullable
    public String getRegion() {
        return region;
    }
    
    /**
     * @return Unique identifier for the region.
     */
    @Nullable
    public String getRegionCode() {
        return regionCode;
    }
    
    /**
     * @return Name of the country.
     */
    @Nullable
    public String getCountry() {
        return country;
    }
    
    /**
     * @return ISO code or unique identifier for the country.
     */
    @Nullable
    public String getCountryCode() {
        return countryCode;
    }
    
    /**
     * Creates a new builder for {@link PoiGeographicElement}.
     * @return A new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoiGeographicElement that = (PoiGeographicElement) o;
        return Objects.equals(locality, that.locality) &&
               Objects.equals(municipality, that.municipality) &&
               Objects.equals(region, that.region) &&
               Objects.equals(country, that.country);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(locality, municipality, region, country);
    }
    
    @Override
    public String toString() {
        return "PoiGeographicElement{" +
                "locality='" + locality + '\'' +
                ", municipality='" + municipality + '\'' +
                ", region='" + region + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
    
    /**
     * Builder for {@link PoiGeographicElement}.
     */
    public static final class Builder {
        private String houseNumber;
        private String street;
        private String streetCode;
        private String locality;
        private String localityCode;
        private String municipality;
        private String municipalityCode;
        private String subregion;
        private String subregionCode;
        private String region;
        private String regionCode;
        private String country;
        private String countryCode;
        
        private Builder() {}
        
        /**
         * @param houseNumber House number or address range.
         * @return This builder.
         */
        public Builder houseNumber(@Nullable String houseNumber) {
            this.houseNumber = houseNumber;
            return this;
        }
        
        /**
         * @param street Name of the street.
         * @return This builder.
         */
        public Builder street(@Nullable String street) {
            this.street = street;
            return this;
        }
        
        /**
         * @param streetCode Unique identifier for the street.
         * @return This builder.
         */
        public Builder streetCode(@Nullable String streetCode) {
            this.streetCode = streetCode;
            return this;
        }
        
        /**
         * @param locality Name of the locality.
         * @return This builder.
         */
        public Builder locality(@Nullable String locality) {
            this.locality = locality;
            return this;
        }
        
        /**
         * @param localityCode Unique identifier for the locality.
         * @return This builder.
         */
        public Builder localityCode(@Nullable String localityCode) {
            this.localityCode = localityCode;
            return this;
        }
        
        /**
         * @param municipality Name of the municipality.
         * @return This builder.
         */
        public Builder municipality(@Nullable String municipality) {
            this.municipality = municipality;
            return this;
        }
        
        /**
         * @param municipalityCode Unique identifier for the municipality.
         * @return This builder.
         */
        public Builder municipalityCode(@Nullable String municipalityCode) {
            this.municipalityCode = municipalityCode;
            return this;
        }
        
        /**
         * @param subregion Name of the subregion.
         * @return This builder.
         */
        public Builder subregion(@Nullable String subregion) {
            this.subregion = subregion;
            return this;
        }
        
        /**
         * @param subregionCode Unique identifier for the subregion.
         * @return This builder.
         */
        public Builder subregionCode(@Nullable String subregionCode) {
            this.subregionCode = subregionCode;
            return this;
        }
        
        /**
         * @param region Name of the region.
         * @return This builder.
         */
        public Builder region(@Nullable String region) {
            this.region = region;
            return this;
        }
        
        /**
         * @param regionCode Unique identifier for the region.
         * @return This builder.
         */
        public Builder regionCode(@Nullable String regionCode) {
            this.regionCode = regionCode;
            return this;
        }
        
        /**
         * @param country Name of the country.
         * @return This builder.
         */
        public Builder country(@Nullable String country) {
            this.country = country;
            return this;
        }
        
        /**
         * @param countryCode ISO code or unique identifier for the country.
         * @return This builder.
         */
        public Builder countryCode(@Nullable String countryCode) {
            this.countryCode = countryCode;
            return this;
        }
        
        /**
         * Builds a new {@link PoiGeographicElement} instance.
         * @return The new instance.
         */
        public PoiGeographicElement build() {
            return new PoiGeographicElement(this);
        }
    }
}
