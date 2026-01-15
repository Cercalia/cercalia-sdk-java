package com.cercalia.sdk.model.proximity;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Options for proximity search.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ProximityOptions options = ProximityOptions.builder(new Coordinate(2.1734, 41.3851))
 *     .count(10)
 *     .categories("gas_station", "parking")
 *     .maxRadius(5000)
 *     .includeRouting(true)
 *     .build();
 * }</pre>
 */
public final class ProximityOptions {
    
    @NotNull
    private final Coordinate center;
    
    @Nullable
    private final Integer count;
    
    @Nullable
    private final List<String> categories;
    
    @Nullable
    private final Integer maxRadius;
    
    @Nullable
    private final Boolean includeRouting;
    
    @Nullable
    private final ProximityRouteWeight routeWeight;
    
    private ProximityOptions(Builder builder) {
        this.center = builder.center;
        this.count = builder.count;
        this.categories = builder.categories;
        this.maxRadius = builder.maxRadius;
        this.includeRouting = builder.includeRouting;
        this.routeWeight = builder.routeWeight;
    }
    
    /**
     * @return Center coordinate for the search.
     */
    @NotNull
    public Coordinate getCenter() {
        return center;
    }
    
    /**
     * @return Maximum number of results to return.
     */
    @Nullable
    public Integer getCount() {
        return count;
    }
    
    /**
     * @return Categories to filter by.
     */
    @Nullable
    public List<String> getCategories() {
        return categories;
    }
    
    /**
     * @return Maximum search radius in meters.
     */
    @Nullable
    public Integer getMaxRadius() {
        return maxRadius;
    }
    
    /**
     * @return Whether to include routing information in the results.
     */
    @Nullable
    public Boolean getIncludeRouting() {
        return includeRouting;
    }
    
    /**
     * @return Route weight type for routing calculation.
     */
    @Nullable
    public ProximityRouteWeight getRouteWeight() {
        return routeWeight;
    }
    
    /**
     * @param center The search center coordinate.
     * @return A new builder for {@link ProximityOptions}.
     */
    @NotNull
    public static Builder builder(@NotNull Coordinate center) {
        return new Builder(center);
    }
    
    /**
     * Builder for {@link ProximityOptions}.
     */
    public static final class Builder {
        private final Coordinate center;
        private Integer count;
        private List<String> categories;
        private Integer maxRadius;
        private Boolean includeRouting;
        private ProximityRouteWeight routeWeight;
        
        private Builder(@NotNull Coordinate center) {
            this.center = center;
        }
        
        /**
         * @param count Maximum number of results.
         * @return The builder.
         */
        public Builder count(@Nullable Integer count) {
            this.count = count;
            return this;
        }
        
        /**
         * @param categories List of category codes.
         * @return The builder.
         */
        public Builder categories(@Nullable List<String> categories) {
            this.categories = categories;
            return this;
        }
        
        /**
         * @param categories Array of category codes.
         * @return The builder.
         */
        public Builder categories(@NotNull String... categories) {
            this.categories = Arrays.asList(categories);
            return this;
        }
        
        /**
         * @param maxRadius Maximum radius in meters.
         * @return The builder.
         */
        public Builder maxRadius(@Nullable Integer maxRadius) {
            this.maxRadius = maxRadius;
            return this;
        }
        
        /**
         * @param includeRouting Whether to include routing info.
         * @return The builder.
         */
        public Builder includeRouting(@Nullable Boolean includeRouting) {
            this.includeRouting = includeRouting;
            return this;
        }
        
        /**
         * @param routeWeight Optimization criteria for routing.
         * @return The builder.
         */
        public Builder routeWeight(@Nullable ProximityRouteWeight routeWeight) {
            this.routeWeight = routeWeight;
            return this;
        }
        
        /**
         * @return A new instance of {@link ProximityOptions}.
         */
        @NotNull
        public ProximityOptions build() {
            return new ProximityOptions(this);
        }
    }
}
