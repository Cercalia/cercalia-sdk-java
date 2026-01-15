package com.cercalia.sdk.model.geoment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Options for fetching POI geometry.
 */
public final class GeomentPoiOptions {
    
    @NotNull
    private final String poic;
    
    @Nullable
    private final Integer tolerance;
    
    private GeomentPoiOptions(Builder builder) {
        this.poic = builder.poic;
        this.tolerance = builder.tolerance;
    }
    
    /**
     * @return Internal Cercalia ID for the POI.
     */
    @NotNull
    public String getPoic() {
        return poic;
    }
    
    /**
     * @return geometry simplification tolerance.
     */
    @Nullable
    public Integer getTolerance() {
        return tolerance;
    }
    
    /**
     * Creates options for a POI by code.
     * @param poic Internal Cercalia ID for the POI.
     * @return a new {@link GeomentPoiOptions} instance.
     */
    @NotNull
    public static GeomentPoiOptions of(@NotNull String poic) {
        return new Builder(poic).build();
    }
    
    /**
     * Creates options for a POI by code with tolerance.
     * @param poic Internal Cercalia ID for the POI.
     * @param tolerance geometry simplification tolerance.
     * @return a new {@link GeomentPoiOptions} instance.
     */
    @NotNull
    public static GeomentPoiOptions of(@NotNull String poic, int tolerance) {
        return new Builder(poic).tolerance(tolerance).build();
    }
    
    /**
     * @param poic Internal Cercalia ID for the POI.
     * @return a new builder for {@link GeomentPoiOptions}.
     */
    @NotNull
    public static Builder builder(@NotNull String poic) {
        return new Builder(poic);
    }
    
    /**
     * Builder for {@link GeomentPoiOptions}.
     */
    public static final class Builder {
        private final String poic;
        private Integer tolerance;
        
        private Builder(@NotNull String poic) {
            this.poic = poic;
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
         * @return a new instance of {@link GeomentPoiOptions}.
         */
        @NotNull
        public GeomentPoiOptions build() {
            return new GeomentPoiOptions(this);
        }
    }
}
