package com.cercalia.sdk.model.geoment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Options for fetching postal code geometry.
 */
public final class GeomentPostalCodeOptions {
    
    @NotNull
    private final String pcode;
    
    @Nullable
    private final String ctryc;
    
    @Nullable
    private final Integer tolerance;
    
    private GeomentPostalCodeOptions(Builder builder) {
        this.pcode = builder.pcode;
        this.ctryc = builder.ctryc;
        this.tolerance = builder.tolerance;
    }
    
    /**
     * @return postal code.
     */
    @NotNull
    public String getPcode() {
        return pcode;
    }
    
    /**
     * @return country code.
     */
    @Nullable
    public String getCtryc() {
        return ctryc;
    }
    
    /**
     * @return geometry simplification tolerance.
     */
    @Nullable
    public Integer getTolerance() {
        return tolerance;
    }
    
    /**
     * Creates options for a postal code.
     * @param pcode postal code.
     * @return a new {@link GeomentPostalCodeOptions} instance.
     */
    @NotNull
    public static GeomentPostalCodeOptions of(@NotNull String pcode) {
        return new Builder(pcode).build();
    }
    
    /**
     * Creates options for a postal code with country code.
     * @param pcode postal code.
     * @param ctryc country code.
     * @return a new {@link GeomentPostalCodeOptions} instance.
     */
    @NotNull
    public static GeomentPostalCodeOptions of(@NotNull String pcode, @NotNull String ctryc) {
        return new Builder(pcode).ctryc(ctryc).build();
    }
    
    /**
     * Creates options for a postal code with country code and tolerance.
     * @param pcode postal code.
     * @param ctryc country code.
     * @param tolerance geometry simplification tolerance.
     * @return a new {@link GeomentPostalCodeOptions} instance.
     */
    @NotNull
    public static GeomentPostalCodeOptions of(@NotNull String pcode, @NotNull String ctryc, int tolerance) {
        return new Builder(pcode).ctryc(ctryc).tolerance(tolerance).build();
    }
    
    /**
     * @param pcode postal code.
     * @return a new builder for {@link GeomentPostalCodeOptions}.
     */
    @NotNull
    public static Builder builder(@NotNull String pcode) {
        return new Builder(pcode);
    }
    
    /**
     * Builder for {@link GeomentPostalCodeOptions}.
     */
    public static final class Builder {
        private final String pcode;
        private String ctryc;
        private Integer tolerance;
        
        private Builder(@NotNull String pcode) {
            this.pcode = pcode;
        }
        
        /**
         * @param ctryc country code.
         * @return this builder.
         */
        public Builder ctryc(@Nullable String ctryc) {
            this.ctryc = ctryc;
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
         * @return a new instance of {@link GeomentPostalCodeOptions}.
         */
        @NotNull
        public GeomentPostalCodeOptions build() {
            return new GeomentPostalCodeOptions(this);
        }
    }
}
