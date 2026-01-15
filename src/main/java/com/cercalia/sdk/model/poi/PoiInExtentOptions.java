package com.cercalia.sdk.model.poi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Options for searching POIs within a map extent.
 */
public final class PoiInExtentOptions {
    
    @NotNull
    private final List<String> categories;
    
    @Nullable
    private final Boolean includeMap;
    
    @Nullable
    private final Integer gridSize;
    
    private PoiInExtentOptions(Builder builder) {
        this.categories = Objects.requireNonNull(builder.categories, "categories cannot be null");
        this.includeMap = builder.includeMap;
        this.gridSize = builder.gridSize;
    }
    
    /**
     * @return The list of POI category codes to search for.
     */
    @NotNull
    public List<String> getCategories() {
        return categories;
    }
    
    /**
     * @return Whether to include a static map in the response.
     */
    @Nullable
    public Boolean getIncludeMap() {
        return includeMap;
    }
    
    /**
     * @return The grid size for POI clustering, in pixels.
     */
    @Nullable
    public Integer getGridSize() {
        return gridSize;
    }
    
    /**
     * @return A new builder instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for {@link PoiInExtentOptions}.
     */
    public static final class Builder {
        private List<String> categories;
        private Boolean includeMap;
        private Integer gridSize;
        
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
         * @param includeMap Whether to include a map.
         * @return The builder.
         */
        public Builder includeMap(@Nullable Boolean includeMap) {
            this.includeMap = includeMap;
            return this;
        }
        
        /**
         * @param gridSize The clustering grid size.
         * @return The builder.
         */
        public Builder gridSize(@Nullable Integer gridSize) {
            this.gridSize = gridSize;
            return this;
        }
        
        /**
         * @return A new {@link PoiInExtentOptions} instance.
         */
        @NotNull
        public PoiInExtentOptions build() {
            return new PoiInExtentOptions(this);
        }
    }
}
