package com.cercalia.sdk.model.poi;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a Point of Interest (POI) result from the Cercalia API.
 * 
 * <p>A POI contains information about a specific location, including its name,
 * category, coordinates, and optionally administrative data or route-related metrics
 * if requested in the search.</p>
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * Poi poi = result.getPois().get(0);
 * System.out.println("POI: " + poi.getName() + " (" + poi.getCategoryCode() + ")");
 * if (poi.getDistance() != null) {
 *     System.out.println("Distance: " + poi.getDistance() + " meters");
 * }
 * }</pre>
 */
public final class Poi {
    
    /**
     * Unique identifier for the POI.
     */
    @NotNull
    private final String id;
    
    /**
     * Name of the POI.
     */
    @NotNull
    private final String name;
    
    /**
     * Additional information or description of the POI.
     */
    @Nullable
    private final String info;
    
    /**
     * Primary category code for the POI.
     */
    @NotNull
    private final String categoryCode;
    
    /**
     * Subcategory code for the POI.
     */
    @Nullable
    private final String subcategoryCode;
    
    /**
     * Geometry associated with the POI (e.g., in WKT format).
     */
    @Nullable
    private final String geometry;
    
    /**
     * Euclidean distance from the search center in meters.
     */
    @Nullable
    private final Integer distance;
    
    /**
     * Position of the POI in the list of results.
     */
    @Nullable
    private final Integer position;
    
    /**
     * Distance along the route in meters (only for POI along route searches).
     */
    @Nullable
    private final Integer routeDistance;
    
    /**
     * Travel time along the route in seconds (only for POI along route searches).
     */
    @Nullable
    private final Integer routeTime;
    
    /**
     * Real-time travel time along the route in seconds (if available).
     */
    @Nullable
    private final Integer routeRealtime;
    
    /**
     * Weight value for the route calculation (e.g., based on distance or time).
     */
    @Nullable
    private final Integer routeWeight;
    
    /**
     * Geographic coordinates of the POI.
     */
    @NotNull
    private final Coordinate coord;
    
    /**
     * Geographic/administrative details for the POI.
     */
    @Nullable
    private final PoiGeographicElement ge;
    
    /**
     * Pixel coordinates for the POI in a static map context.
     */
    @Nullable
    private final PixelCoordinate pixels;
    
    private Poi(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id cannot be null");
        this.name = Objects.requireNonNull(builder.name, "name cannot be null");
        this.info = builder.info;
        this.categoryCode = Objects.requireNonNull(builder.categoryCode, "categoryCode cannot be null");
        this.subcategoryCode = builder.subcategoryCode;
        this.geometry = builder.geometry;
        this.distance = builder.distance;
        this.position = builder.position;
        this.routeDistance = builder.routeDistance;
        this.routeTime = builder.routeTime;
        this.routeRealtime = builder.routeRealtime;
        this.routeWeight = builder.routeWeight;
        this.coord = Objects.requireNonNull(builder.coord, "coord cannot be null");
        this.ge = builder.ge;
        this.pixels = builder.pixels;
    }
    
    /**
     * @return Unique identifier for the POI.
     */
    @NotNull
    public String getId() {
        return id;
    }
    
    /**
     * @return Name of the POI.
     */
    @NotNull
    public String getName() {
        return name;
    }
    
    /**
     * @return Additional information or description of the POI.
     */
    @Nullable
    public String getInfo() {
        return info;
    }
    
    /**
     * @return Primary category code for the POI.
     */
    @NotNull
    public String getCategoryCode() {
        return categoryCode;
    }
    
    /**
     * @return Subcategory code for the POI.
     */
    @Nullable
    public String getSubcategoryCode() {
        return subcategoryCode;
    }
    
    /**
     * @return Geometry associated with the POI in WKT format.
     */
    @Nullable
    public String getGeometry() {
        return geometry;
    }
    
    /**
     * @return Euclidean distance from the search center in meters.
     */
    @Nullable
    public Integer getDistance() {
        return distance;
    }
    
    /**
     * @return Position of the POI in the list of results.
     */
    @Nullable
    public Integer getPosition() {
        return position;
    }
    
    /**
     * @return Distance along the route in meters.
     */
    @Nullable
    public Integer getRouteDistance() {
        return routeDistance;
    }
    
    /**
     * @return Travel time along the route in seconds.
     */
    @Nullable
    public Integer getRouteTime() {
        return routeTime;
    }
    
    /**
     * @return Real-time travel time along the route in seconds.
     */
    @Nullable
    public Integer getRouteRealtime() {
        return routeRealtime;
    }
    
    /**
     * @return Weight value for the route calculation.
     */
    @Nullable
    public Integer getRouteWeight() {
        return routeWeight;
    }
    
    /**
     * @return Geographic coordinates of the POI.
     */
    @NotNull
    public Coordinate getCoord() {
        return coord;
    }
    
    /**
     * @return Geographic/administrative details for the POI.
     */
    @Nullable
    public PoiGeographicElement getGe() {
        return ge;
    }
    
    /**
     * @return Pixel coordinates for the POI in a static map context.
     */
    @Nullable
    public PixelCoordinate getPixels() {
        return pixels;
    }
    
    /**
     * @return A new builder instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poi poi = (Poi) o;
        return Objects.equals(id, poi.id) &&
               Objects.equals(name, poi.name) &&
               Objects.equals(categoryCode, poi.categoryCode) &&
               Objects.equals(coord, poi.coord);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, categoryCode, coord);
    }
    
    @Override
    public String toString() {
        return "Poi{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", categoryCode='" + categoryCode + '\'' +
                ", coord=" + coord +
                '}';
    }
    
    /**
     * Builder for {@link Poi}.
     */
    public static final class Builder {
        private String id;
        private String name;
        private String info;
        private String categoryCode;
        private String subcategoryCode;
        private String geometry;
        private Integer distance;
        private Integer position;
        private Integer routeDistance;
        private Integer routeTime;
        private Integer routeRealtime;
        private Integer routeWeight;
        private Coordinate coord;
        private PoiGeographicElement ge;
        private PixelCoordinate pixels;
        
        private Builder() {}
        
        /**
         * @param id Unique identifier for the POI.
         * @return The builder.
         */
        public Builder id(@NotNull String id) {
            this.id = id;
            return this;
        }
        
        /**
         * @param name Name of the POI.
         * @return The builder.
         */
        public Builder name(@NotNull String name) {
            this.name = name;
            return this;
        }
        
        /**
         * @param info Additional information or description of the POI.
         * @return The builder.
         */
        public Builder info(@Nullable String info) {
            this.info = info;
            return this;
        }
        
        /**
         * @param categoryCode Primary category code for the POI.
         * @return The builder.
         */
        public Builder categoryCode(@NotNull String categoryCode) {
            this.categoryCode = categoryCode;
            return this;
        }
        
        /**
         * @param subcategoryCode Subcategory code for the POI.
         * @return The builder.
         */
        public Builder subcategoryCode(@Nullable String subcategoryCode) {
            this.subcategoryCode = subcategoryCode;
            return this;
        }
        
        /**
         * @param geometry Geometry associated with the POI in WKT format.
         * @return The builder.
         */
        public Builder geometry(@Nullable String geometry) {
            this.geometry = geometry;
            return this;
        }
        
        /**
         * @param distance Euclidean distance from the search center in meters.
         * @return The builder.
         */
        public Builder distance(@Nullable Integer distance) {
            this.distance = distance;
            return this;
        }
        
        /**
         * @param position Position of the POI in the list of results.
         * @return The builder.
         */
        public Builder position(@Nullable Integer position) {
            this.position = position;
            return this;
        }
        
        /**
         * @param routeDistance Distance along the route in meters.
         * @return The builder.
         */
        public Builder routeDistance(@Nullable Integer routeDistance) {
            this.routeDistance = routeDistance;
            return this;
        }
        
        /**
         * @param routeTime Travel time along the route in seconds.
         * @return The builder.
         */
        public Builder routeTime(@Nullable Integer routeTime) {
            this.routeTime = routeTime;
            return this;
        }
        
        /**
         * @param routeRealtime Real-time travel time along the route in seconds.
         * @return The builder.
         */
        public Builder routeRealtime(@Nullable Integer routeRealtime) {
            this.routeRealtime = routeRealtime;
            return this;
        }
        
        /**
         * @param routeWeight Weight value for the route calculation.
         * @return The builder.
         */
        public Builder routeWeight(@Nullable Integer routeWeight) {
            this.routeWeight = routeWeight;
            return this;
        }
        
        /**
         * @param coord Geographic coordinates of the POI.
         * @return The builder.
         */
        public Builder coord(@NotNull Coordinate coord) {
            this.coord = coord;
            return this;
        }
        
        /**
         * @param ge Geographic/administrative details for the POI.
         * @return The builder.
         */
        public Builder ge(@Nullable PoiGeographicElement ge) {
            this.ge = ge;
            return this;
        }
        
        /**
         * @param pixels Pixel coordinates for the POI.
         * @return The builder.
         */
        public Builder pixels(@Nullable PixelCoordinate pixels) {
            this.pixels = pixels;
            return this;
        }
        
        /**
         * @return A new {@link Poi} instance.
         */
        @NotNull
        public Poi build() {
            return new Poi(this);
        }
    }
}
