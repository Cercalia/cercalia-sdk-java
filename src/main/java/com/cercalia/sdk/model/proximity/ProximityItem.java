package com.cercalia.sdk.model.proximity;

import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.poi.PoiGeographicElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a single item in proximity search results.
 */
public final class ProximityItem {
    
    @NotNull
    private final String id;
    
    @NotNull
    private final String name;
    
    @NotNull
    private final Coordinate coord;
    
    private final int distance;
    
    @Nullable
    private final Integer position;
    
    @Nullable
    private final String categoryCode;
    
    @Nullable
    private final String subcategoryCode;
    
    @Nullable
    private final String geometry;
    
    @Nullable
    private final String info;
    
    @Nullable
    private final PoiGeographicElement ge;
    
    @Nullable
    private final Integer routeDistance;
    
    @Nullable
    private final Integer routeTime;
    
    @Nullable
    private final Integer routeRealtime;
    
    @Nullable
    private final Integer routeWeight;
    
    private ProximityItem(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id cannot be null");
        this.name = Objects.requireNonNull(builder.name, "name cannot be null");
        this.coord = Objects.requireNonNull(builder.coord, "coord cannot be null");
        this.distance = builder.distance;
        this.position = builder.position;
        this.categoryCode = builder.categoryCode;
        this.subcategoryCode = builder.subcategoryCode;
        this.geometry = builder.geometry;
        this.info = builder.info;
        this.ge = builder.ge;
        this.routeDistance = builder.routeDistance;
        this.routeTime = builder.routeTime;
        this.routeRealtime = builder.routeRealtime;
        this.routeWeight = builder.routeWeight;
    }
    
    /**
     * @return Unique identifier for the item.
     */
    @NotNull
    public String getId() {
        return id;
    }
    
    /**
     * @return Name of the item.
     */
    @NotNull
    public String getName() {
        return name;
    }
    
    /**
     * @return Geographic coordinates of the item.
     */
    @NotNull
    public Coordinate getCoord() {
        return coord;
    }
    
    /**
     * @return Euclidean distance from the search center in meters.
     */
    public int getDistance() {
        return distance;
    }
    
    /**
     * @return Position of the item in the result list.
     */
    @Nullable
    public Integer getPosition() {
        return position;
    }
    
    /**
     * @return Primary category code for the item.
     */
    @Nullable
    public String getCategoryCode() {
        return categoryCode;
    }
    
    /**
     * @return Subcategory code for the item.
     */
    @Nullable
    public String getSubcategoryCode() {
        return subcategoryCode;
    }
    
    /**
     * @return Geometry associated with the item in WKT format.
     */
    @Nullable
    public String getGeometry() {
        return geometry;
    }
    
    /**
     * @return Additional information or description of the item.
     */
    @Nullable
    public String getInfo() {
        return info;
    }
    
    /**
     * @return Geographic/administrative details for the item.
     */
    @Nullable
    public PoiGeographicElement getGe() {
        return ge;
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
     * @return A new builder instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public String toString() {
        return "ProximityItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", distance=" + distance +
                ", categoryCode='" + categoryCode + '\'' +
                '}';
    }
    
    /**
     * Builder for {@link ProximityItem}.
     */
    public static final class Builder {
        private String id;
        private String name;
        private Coordinate coord;
        private int distance;
        private Integer position;
        private String categoryCode;
        private String subcategoryCode;
        private String geometry;
        private String info;
        private PoiGeographicElement ge;
        private Integer routeDistance;
        private Integer routeTime;
        private Integer routeRealtime;
        private Integer routeWeight;
        
        private Builder() {}
        
        /**
         * @param id Unique identifier.
         * @return The builder.
         */
        public Builder id(@NotNull String id) {
            this.id = id;
            return this;
        }
        
        /**
         * @param name Name of the item.
         * @return The builder.
         */
        public Builder name(@NotNull String name) {
            this.name = name;
            return this;
        }
        
        /**
         * @param coord Geographic coordinates.
         * @return The builder.
         */
        public Builder coord(@NotNull Coordinate coord) {
            this.coord = coord;
            return this;
        }
        
        /**
         * @param distance Distance in meters.
         * @return The builder.
         */
        public Builder distance(int distance) {
            this.distance = distance;
            return this;
        }
        
        /**
         * @param position Result position.
         * @return The builder.
         */
        public Builder position(@Nullable Integer position) {
            this.position = position;
            return this;
        }
        
        /**
         * @param categoryCode Primary category code.
         * @return The builder.
         */
        public Builder categoryCode(@Nullable String categoryCode) {
            this.categoryCode = categoryCode;
            return this;
        }
        
        /**
         * @param subcategoryCode Subcategory code.
         * @return The builder.
         */
        public Builder subcategoryCode(@Nullable String subcategoryCode) {
            this.subcategoryCode = subcategoryCode;
            return this;
        }
        
        /**
         * @param geometry Geometry in WKT format.
         * @return The builder.
         */
        public Builder geometry(@Nullable String geometry) {
            this.geometry = geometry;
            return this;
        }
        
        /**
         * @param info Additional information.
         * @return The builder.
         */
        public Builder info(@Nullable String info) {
            this.info = info;
            return this;
        }
        
        /**
         * @param ge Administrative details.
         * @return The builder.
         */
        public Builder ge(@Nullable PoiGeographicElement ge) {
            this.ge = ge;
            return this;
        }
        
        /**
         * @param routeDistance Distance along route in meters.
         * @return The builder.
         */
        public Builder routeDistance(@Nullable Integer routeDistance) {
            this.routeDistance = routeDistance;
            return this;
        }
        
        /**
         * @param routeTime Time along route in seconds.
         * @return The builder.
         */
        public Builder routeTime(@Nullable Integer routeTime) {
            this.routeTime = routeTime;
            return this;
        }
        
        /**
         * @param routeRealtime Real-time along route in seconds.
         * @return The builder.
         */
        public Builder routeRealtime(@Nullable Integer routeRealtime) {
            this.routeRealtime = routeRealtime;
            return this;
        }
        
        /**
         * @param routeWeight Route weight value.
         * @return The builder.
         */
        public Builder routeWeight(@Nullable Integer routeWeight) {
            this.routeWeight = routeWeight;
            return this;
        }
        
        /**
         * @return A new {@link ProximityItem} instance.
         */
        @NotNull
        public ProximityItem build() {
            return new ProximityItem(this);
        }
    }
}
