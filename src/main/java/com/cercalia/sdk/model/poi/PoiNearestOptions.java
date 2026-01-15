package com.cercalia.sdk.model.poi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Options for searching nearest POIs by straight-line distance.
 */
public final class PoiNearestOptions {
    
    @NotNull
    private final List<String> categories;
    
    @Nullable
    private final Integer limit;
    
    @Nullable
    private final Integer radius;
    
    private PoiNearestOptions(Builder builder) {
        this.categories = Objects.requireNonNull(builder.categories, "categories cannot be null");
        this.limit = builder.limit;
        this.radius = builder.radius;
    }
    
    /**
     * @return The list of POI category codes to search for.
     */
    @NotNull
    public List<String> getCategories() {
        return categories;
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
     * @return A new builder instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Quick constructor for simple searches.
     *
     * @param categories The POI category codes to search for.
     * @return A new {@link PoiNearestOptions} instance.
     */
    @NotNull
    public static PoiNearestOptions of(@NotNull String... categories) {
        return builder().categories(Arrays.asList(categories)).build();
    }
    
    /**
     * Builder for {@link PoiNearestOptions}.
     */
    public static final class Builder {
        private List<String> categories;
        private Integer limit;
        private Integer radius;
        
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
         * @return A new {@link PoiNearestOptions} instance.
         */
        @NotNull
        public PoiNearestOptions build() {
            return new PoiNearestOptions(this);
        }
    }
}
