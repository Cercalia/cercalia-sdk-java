package com.cercalia.sdk.model.geocoding;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a locality associated with a postal code.
 * <p>
 * Contains administrative information and coordinates for a locality
 * linked to a specific postal code area.
 */
public final class PostalCodeCity {
    
    @NotNull
    private final String id;
    
    @NotNull
    private final String name;
    
    @Nullable
    private final String municipality;
    
    @Nullable
    private final String municipalityCode; // Note: keeping typo for 1:1 compatibility with TS SDK
    
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
    
    @NotNull
    private final Coordinate coord;
    
    private PostalCodeCity(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id cannot be null");
        this.name = Objects.requireNonNull(builder.name, "name cannot be null");
        this.municipality = builder.municipality;
        this.municipalityCode = builder.municipalityCode;
        this.subregion = builder.subregion;
        this.subregionCode = builder.subregionCode;
        this.region = builder.region;
        this.regionCode = builder.regionCode;
        this.country = builder.country;
        this.countryCode = builder.countryCode;
        this.coord = Objects.requireNonNull(builder.coord, "coord cannot be null");
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * @return Internal Cercalia ID for the locality.
     */
    @NotNull
    public String getId() { return id; }
    
    /**
     * @return Name of the locality.
     */
    @NotNull
    public String getName() { return name; }
    
    /**
     * @return Name of the municipality.
     */
    @Nullable
    public String getMunicipality() { return municipality; }
    
    /**
     * @return Internal Cercalia ID for the municipality.
     * <p>Note: Intentional typo {@code municipalityCode} for parity with TS SDK.</p>
     */
    @Nullable
    public String getMunicipalityCode() { return municipalityCode; }
    
    /**
     * @return Name of the subregion.
     */
    @Nullable
    public String getSubregion() { return subregion; }
    
    /**
     * @return Internal Cercalia ID for the subregion.
     */
    @Nullable
    public String getSubregionCode() { return subregionCode; }
    
    /**
     * @return Name of the region.
     */
    @Nullable
    public String getRegion() { return region; }
    
    /**
     * @return Internal Cercalia ID for the region.
     */
    @Nullable
    public String getRegionCode() { return regionCode; }
    
    /**
     * @return Name of the country.
     */
    @Nullable
    public String getCountry() { return country; }
    
    /**
     * @return Internal Cercalia ID for the country.
     */
    @Nullable
    public String getCountryCode() { return countryCode; }
    
    /**
     * @return Coordinates of the locality.
     */
    @NotNull
    public Coordinate getCoord() { return coord; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostalCodeCity that = (PostalCodeCity) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "PostalCodeCity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", coord=" + coord +
                '}';
    }
    
    public static final class Builder {
        private String id;
        private String name;
        private String municipality;
        private String municipalityCode;
        private String subregion;
        private String subregionCode;
        private String region;
        private String regionCode;
        private String country;
        private String countryCode;
        private Coordinate coord;
        
        private Builder() {}
        
        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder municipality(String municipality) { this.municipality = municipality; return this; }
        public Builder municipalityCode(String municipalityCode) { this.municipalityCode = municipalityCode; return this; }
        public Builder subregion(String subregion) { this.subregion = subregion; return this; }
        public Builder subregionCode(String subregionCode) { this.subregionCode = subregionCode; return this; }
        public Builder region(String region) { this.region = region; return this; }
        public Builder regionCode(String regionCode) { this.regionCode = regionCode; return this; }
        public Builder country(String country) { this.country = country; return this; }
        public Builder countryCode(String countryCode) { this.countryCode = countryCode; return this; }
        public Builder coord(Coordinate coord) { this.coord = coord; return this; }
        
        public PostalCodeCity build() {
            return new PostalCodeCity(this);
        }
    }
}
