package com.cercalia.sdk.model.poi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Options for searching nearest POIs with routing distance/time calculation.
 */
public final class PoiNearestWithRoutingOptions {
    
    @NotNull
    private final List<String> categories;
    
    @NotNull
    private final PoiRouteWeight weight;
    
    @Nullable
    private final Integer limit;
    
    @Nullable
    private final Integer radius;
    
    @Nullable
    private final Integer inverse;
    
    @Nullable
    private final Boolean includeRealtime;
    
    @Nullable
    private final String departureTime;
    
    private PoiNearestWithRoutingOptions(Builder builder) {
        this.categories = Objects.requireNonNull(builder.categories, "categories cannot be null");
        this.weight = Objects.requireNonNull(builder.weight, "weight cannot be null");
        this.limit = builder.limit;
        this.radius = builder.radius;
        this.inverse = builder.inverse;
        this.includeRealtime = builder.includeRealtime;
        this.departureTime = builder.departureTime;
    }
    
    /**
     * @return The list of POI category codes to search for.
     */
    @NotNull
    public List<String> getCategories() {
        return categories;
    }
    
    /**
     * @return The optimization weight criteria for routing.
     */
    @NotNull
    public PoiRouteWeight getWeight() {
        return weight;
    }
    
    /**
     * @return The maximum number of results to return.
     */
    @Nullable
    public Integer getLimit() {
        return limit;
    }
    
    /**
     * @return The search radius in meters.
     */
    @Nullable
    public Integer getRadius() {
        return radius;
    }
    
    /**
     * @return The inverse routing flag (0: from center to POIs, 1: from POIs to center).
     */
    @Nullable
    public Integer getInverse() {
        return inverse;
    }
    
    /**
     * @return Whether to include real-time traffic data in routing calculation.
     */
    @Nullable
    public Boolean getIncludeRealtime() {
        return includeRealtime;
    }
    
    /**
     * @return The departure time in {@code YYYYMMDDHHmm} format.
     */
    @Nullable
    public String getDepartureTime() {
        return departureTime;
    }
    
    /**
     * @return A new builder instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for {@link PoiNearestWithRoutingOptions}.
     */
    public static final class Builder {
        private List<String> categories;
        private PoiRouteWeight weight;
        private Integer limit;
        private Integer radius;
        private Integer inverse;
        private Boolean includeRealtime;
        private String departureTime;
        
        private Builder() {}
        
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
         * @param weight The optimization weight criteria.
         * @return The builder.
         */
        public Builder weight(@NotNull PoiRouteWeight weight) {
            this.weight = weight;
            return this;
        }
        
        /**
         * @param limit The maximum number of results.
         * @return The builder.
         */
        public Builder limit(@Nullable Integer limit) {
            this.limit = limit;
            return this;
        }
        
        /**
         * @param radius The search radius in meters.
         * @return The builder.
         */
        public Builder radius(@Nullable Integer radius) {
            this.radius = radius;
            return this;
        }
        
        /**
         * Set inverse routing: 0 = routes from center to POIs (default), 1 = routes from POIs to center.
         *
         * @param inverse 0 or 1.
         * @return The builder.
         */
        public Builder inverse(@Nullable Integer inverse) {
            this.inverse = inverse;
            return this;
        }
        
        /**
         * @param includeRealtime Whether to include real-time traffic data.
         * @return The builder.
         */
        public Builder includeRealtime(@Nullable Boolean includeRealtime) {
            this.includeRealtime = includeRealtime;
            return this;
        }
        
        /**
         * @param departureTime Departure time in {@code YYYYMMDDHHmm} format.
         * @return The builder.
         */
        public Builder departureTime(@Nullable String departureTime) {
            this.departureTime = departureTime;
            return this;
        }
        
        /**
         * @return A new {@link PoiNearestWithRoutingOptions} instance.
         */
        @NotNull
        public PoiNearestWithRoutingOptions build() {
            return new PoiNearestWithRoutingOptions(this);
        }
    }
}
