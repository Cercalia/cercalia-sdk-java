package com.cercalia.sdk.model.poi;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Options for searching POIs within a polygon defined by WKT.
 */
public final class PoiInPolygonOptions {
    
    @NotNull
    private final List<String> categories;
    
    @NotNull
    private final String wkt;
    
    private PoiInPolygonOptions(Builder builder) {
        this.categories = Objects.requireNonNull(builder.categories, "categories cannot be null");
        this.wkt = Objects.requireNonNull(builder.wkt, "wkt cannot be null");
    }
    
    /**
     * @return The list of POI category codes to search for.
     */
    @NotNull
    public List<String> getCategories() {
        return categories;
    }
    
    /**
     * @return The polygon geometry in Well-Known Text (WKT) format.
     */
    @NotNull
    public String getWkt() {
        return wkt;
    }
    
    /**
     * @return A new builder instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for {@link PoiInPolygonOptions}.
     */
    public static final class Builder {
        private List<String> categories;
        private String wkt;
        
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
         * @param wkt The polygon geometry in WKT format.
         * @return The builder.
         */
        public Builder wkt(@NotNull String wkt) {
            this.wkt = wkt;
            return this;
        }
        
        /**
         * @return A new {@link PoiInPolygonOptions} instance.
         */
        @NotNull
        public PoiInPolygonOptions build() {
            return new PoiInPolygonOptions(this);
        }
    }
}
