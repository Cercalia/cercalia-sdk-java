package com.cercalia.sdk.model.geoment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Options for fetching municipality or region geometry.
 */
public final class GeomentMunicipalityOptions {
    
    @Nullable
    private final String munc;
    
    @Nullable
    private final String subregc;
    
    @Nullable
    private final Integer tolerance;
    
    private GeomentMunicipalityOptions(Builder builder) {
        this.munc = builder.munc;
        this.subregc = builder.subregc;
        this.tolerance = builder.tolerance;
    }
    
    /**
     * @return municipality code.
     */
    @Nullable
    public String getMunc() {
        return munc;
    }
    
    /**
     * @return subregion (province) code.
     */
    @Nullable
    public String getSubregc() {
        return subregc;
    }
    
    /**
     * @return geometry simplification tolerance.
     */
    @Nullable
    public Integer getTolerance() {
        return tolerance;
    }
    
    /**
     * Creates options for a locality (municipality) by code.
     * @param munc municipality code.
     * @return a new {@link GeomentMunicipalityOptions} instance.
     */
    @NotNull
    public static GeomentMunicipalityOptions municipality(@NotNull String munc) {
        return new Builder().munc(munc).build();
    }
    
    /**
     * Creates options for a locality (municipality) by code with tolerance.
     * @param munc municipality code.
     * @param tolerance geometry simplification tolerance.
     * @return a new {@link GeomentMunicipalityOptions} instance.
     */
    @NotNull
    public static GeomentMunicipalityOptions municipality(@NotNull String munc, int tolerance) {
        return new Builder().munc(munc).tolerance(tolerance).build();
    }
    
    /**
     * Creates options for a subregion (province) by code.
     * @param subregc subregion code.
     * @return a new {@link GeomentMunicipalityOptions} instance.
     */
    @NotNull
    public static GeomentMunicipalityOptions region(@NotNull String subregc) {
        return new Builder().subregc(subregc).build();
    }
    
    /**
     * Creates options for a subregion (province) by code with tolerance.
     * @param subregc subregion code.
     * @param tolerance geometry simplification tolerance.
     * @return a new {@link GeomentMunicipalityOptions} instance.
     */
    @NotNull
    public static GeomentMunicipalityOptions region(@NotNull String subregc, int tolerance) {
        return new Builder().subregc(subregc).tolerance(tolerance).build();
    }
    
    /**
     * @return a new builder for {@link GeomentMunicipalityOptions}.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for {@link GeomentMunicipalityOptions}.
     */
    public static final class Builder {
        private String munc;
        private String subregc;
        private Integer tolerance;
        
        private Builder() {}
        
        /**
         * @param munc municipality code.
         * @return this builder.
         */
        public Builder munc(@Nullable String munc) {
            this.munc = munc;
            return this;
        }
        
        /**
         * @param subregc subregion (province) code.
         * @return this builder.
         */
        public Builder subregc(@Nullable String subregc) {
            this.subregc = subregc;
            return this;
        }
        
        /**
         * @param tolerance geometry simplification tolerance.
         * @return this builder.
         */
        public Builder tolerance(@Nullable Integer tolerance) {
            this.tolerance = tolerance;
            return this;
        }
        
        /**
         * @return a new instance of {@link GeomentMunicipalityOptions}.
         */
        @NotNull
        public GeomentMunicipalityOptions build() {
            if (munc == null && subregc == null) {
                throw new IllegalArgumentException("Either munc or subregc must be provided");
            }
            return new GeomentMunicipalityOptions(this);
        }
    }
}
