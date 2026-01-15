package com.cercalia.sdk.model.poi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Options for searching POIs along a route.
 */
public final class PoiAlongRouteOptions {
    
    @NotNull
    private final String routeId;
    
    @NotNull
    private final PoiRouteWeight routeWeight;
    
    @NotNull
    private final List<String> categories;
    
    @Nullable
    private final Integer buffer;
    
    @Nullable
    private final Integer tolerance;
    
    private PoiAlongRouteOptions(Builder builder) {
        this.routeId = Objects.requireNonNull(builder.routeId, "routeId cannot be null");
        this.routeWeight = Objects.requireNonNull(builder.routeWeight, "routeWeight cannot be null");
        this.categories = Objects.requireNonNull(builder.categories, "categories cannot be null");
        this.buffer = builder.buffer;
        this.tolerance = builder.tolerance;
    }
    
    /**
     * @return The unique identifier of the route.
     */
    @NotNull
    public String getRouteId() {
        return routeId;
    }
    
    /**
     * @return The optimization weight used for the route calculation.
     */
    @NotNull
    public PoiRouteWeight getRouteWeight() {
        return routeWeight;
    }
    
    /**
     * @return The list of POI category codes to search for.
     */
    @NotNull
    public List<String> getCategories() {
        return categories;
    }
    
    /**
     * @return The search buffer distance in meters.
     */
    @Nullable
    public Integer getBuffer() {
        return buffer;
    }
    
    /**
     * @return The maximum deviation tolerance in meters.
     */
    @Nullable
    public Integer getTolerance() {
        return tolerance;
    }
    
    /**
     * @return A new builder instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for {@link PoiAlongRouteOptions}.
     */
    public static final class Builder {
        private String routeId;
        private PoiRouteWeight routeWeight;
        private List<String> categories;
        private Integer buffer;
        private Integer tolerance;
        
        private Builder() {}
        
        /**
         * @param routeId The route identifier.
         * @return The builder.
         */
        public Builder routeId(@NotNull String routeId) {
            this.routeId = routeId;
            return this;
        }
        
        /**
         * @param routeWeight The optimization weight criteria.
         * @return The builder.
         */
        public Builder routeWeight(@NotNull PoiRouteWeight routeWeight) {
            this.routeWeight = routeWeight;
            return this;
        }
        
        /**
         * @param categories The list of category codes.
         * @return The builder.
         */
        public Builder categories(@NotNull List<String> categories) {
            this.categories = categories;
            return this;
        }
        
        /**
         * @param categories The category codes.
         * @return The builder.
         */
        public Builder categories(@NotNull String... categories) {
            this.categories = Arrays.asList(categories);
            return this;
        }
        
        /**
         * @param buffer Search buffer distance in meters.
         * @return The builder.
         */
        public Builder buffer(@Nullable Integer buffer) {
            this.buffer = buffer;
            return this;
        }
        
        /**
         * @param tolerance Deviation tolerance in meters.
         * @return The builder.
         */
        public Builder tolerance(@Nullable Integer tolerance) {
            this.tolerance = tolerance;
            return this;
        }
        
        /**
         * @return A new {@link PoiAlongRouteOptions} instance.
         */
        @NotNull
        public PoiAlongRouteOptions build() {
            return new PoiAlongRouteOptions(this);
        }
    }
}
