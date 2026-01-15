package com.cercalia.sdk.model.geofencing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Options for geofencing operations.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * GeofenceOptions options = GeofenceOptions.builder()
 *     .shapeSrs("EPSG:4326")
 *     .pointSrs("EPSG:4326")
 *     .build();
 * }</pre>
 */
public final class GeofenceOptions {
    
    private final String shapeSrs;
    private final String pointSrs;
    
    private GeofenceOptions(Builder builder) {
        this.shapeSrs = builder.shapeSrs;
        this.pointSrs = builder.pointSrs;
    }
    
    /**
     * @return the coordinate system for shape geometries (e.g., "EPSG:4326"), or {@code null} for default.
     */
    @Nullable
    public String getShapeSrs() {
        return shapeSrs;
    }
    
    /**
     * @return the coordinate system for point coordinates (e.g., "EPSG:4326"), or {@code null} for default.
     */
    @Nullable
    public String getPointSrs() {
        return pointSrs;
    }
    
    /**
     * @return a new builder for {@link GeofenceOptions}.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * @return Default {@link GeofenceOptions} with default coordinate systems.
     */
    @NotNull
    public static GeofenceOptions defaults() {
        return builder().build();
    }
    
    /**
     * Builder for {@link GeofenceOptions}.
     */
    public static final class Builder {
        private String shapeSrs;
        private String pointSrs;
        
        private Builder() {
        }
        
        /**
         * @param shapeSrs the SRS for shapes (e.g., "EPSG:4326", "EPSG:3857").
         * @return this builder.
         */
        @NotNull
        public Builder shapeSrs(@Nullable String shapeSrs) {
            this.shapeSrs = shapeSrs;
            return this;
        }
        
        /**
         * @param pointSrs the SRS for points (e.g., "EPSG:4326", "EPSG:3857").
         * @return this builder.
         */
        @NotNull
        public Builder pointSrs(@Nullable String pointSrs) {
            this.pointSrs = pointSrs;
            return this;
        }
        
        /**
         * @return a new instance of {@link GeofenceOptions}.
         */
        @NotNull
        public GeofenceOptions build() {
            return new GeofenceOptions(this);
        }
    }
}
