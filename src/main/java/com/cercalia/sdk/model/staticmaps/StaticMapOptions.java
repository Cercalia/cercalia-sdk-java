package com.cercalia.sdk.model.staticmaps;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Options for generating a static map.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * StaticMapOptions options = StaticMapOptions.builder()
 *     .dimensions(800, 600)
 *     .center(new Coordinate(2.1734, 41.3851))
 *     .addMarker(StaticMapMarker.builder().coordinate(new Coordinate(2.1734, 41.3851)).build())
 *     .returnImage(true)
 *     .build();
 * }</pre>
 */
public final class StaticMapOptions {
    
    @Nullable
    private final Integer width;
    
    @Nullable
    private final Integer height;
    
    @Nullable
    private final String cityName;
    
    @Nullable
    private final String countryCode;
    
    @Nullable
    private final String coordinateSystem;
    
    @Nullable
    private final StaticMapExtent extent;
    
    @Nullable
    private final Coordinate center;
    
    @Nullable
    private final Integer labelOp;
    
    @NotNull
    private final List<StaticMapMarker> markers;
    
    @NotNull
    private final List<StaticMapShape> shapes;
    
    private final boolean returnImage;
    
    private final int mode;
    
    private final boolean priorityfilter;
    
    private StaticMapOptions(Builder builder) {
        this.width = builder.width;
        this.height = builder.height;
        this.cityName = builder.cityName;
        this.countryCode = builder.countryCode;
        this.coordinateSystem = builder.coordinateSystem;
        this.extent = builder.extent;
        this.center = builder.center;
        this.labelOp = builder.labelOp;
        this.markers = new ArrayList<>(builder.markers);
        this.shapes = new ArrayList<>(builder.shapes);
        this.returnImage = builder.returnImage;
        this.mode = builder.mode;
        this.priorityfilter = builder.priorityfilter;
    }
    
    /**
     * @return Width of the image.
     */
    @Nullable
    public Integer getWidth() {
        return width;
    }
    
    /**
     * @return Height of the image.
     */
    @Nullable
    public Integer getHeight() {
        return height;
    }
    
    /**
     * @return Locality (city) name to center the map on.
     */
    @Nullable
    public String getCityName() {
        return cityName;
    }
    
    /**
     * @return Country code.
     */
    @Nullable
    public String getCountryCode() {
        return countryCode;
    }
    
    /**
     * @return Coordinate system (SRS).
     */
    @Nullable
    public String getCoordinateSystem() {
        return coordinateSystem;
    }
    
    /**
     * @return Map extent.
     */
    @Nullable
    public StaticMapExtent getExtent() {
        return extent;
    }
    
    /**
     * @return Center coordinate.
     */
    @Nullable
    public Coordinate getCenter() {
        return center;
    }
    
    /**
     * @return Label option.
     */
    @Nullable
    public Integer getLabelOp() {
        return labelOp;
    }
    
    /**
     * @return List of markers to show on the map.
     */
    @NotNull
    public List<StaticMapMarker> getMarkers() {
        return new ArrayList<>(markers);
    }
    
    /**
     * @return List of shapes to show on the map.
     */
    @NotNull
    public List<StaticMapShape> getShapes() {
        return new ArrayList<>(shapes);
    }
    
    /**
     * @return Whether to return the raw image data.
     */
    public boolean isReturnImage() {
        return returnImage;
    }
    
    /**
     * @return Map mode (default is 1).
     */
    public int getMode() {
        return mode;
    }
    
    /**
     * @return Whether priority filter is enabled (default is true).
     */
    public boolean isPriorityfilter() {
        return priorityfilter;
    }
    
    /**
     * @return a new builder for {@link StaticMapOptions}.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for {@link StaticMapOptions}.
     */
    public static final class Builder {
        private Integer width;
        private Integer height;
        private String cityName;
        private String countryCode;
        private String coordinateSystem;
        private StaticMapExtent extent;
        private Coordinate center;
        private Integer labelOp;
        private List<StaticMapMarker> markers = new ArrayList<>();
        private List<StaticMapShape> shapes = new ArrayList<>();
        private boolean returnImage = true;
        private int mode = 1;
        private boolean priorityfilter = true;
        
        private Builder() {}
        
        /**
         * @param width Image width.
         * @return current builder
         */
        public Builder width(@Nullable Integer width) {
            this.width = width;
            return this;
        }
        
        /**
         * @param height Image height.
         * @return current builder
         */
        public Builder height(@Nullable Integer height) {
            this.height = height;
            return this;
        }
        
        /**
         * @param width Image width.
         * @param height Image height.
         * @return current builder
         */
        public Builder dimensions(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }
        
        /**
         * @param cityName Locality (city) name.
         * @return current builder
         */
        public Builder cityName(@Nullable String cityName) {
            this.cityName = cityName;
            return this;
        }
        
        /**
         * @param countryCode Country code.
         * @return current builder
         */
        public Builder countryCode(@Nullable String countryCode) {
            this.countryCode = countryCode;
            return this;
        }
        
        /**
         * @param coordinateSystem SRS.
         * @return current builder
         */
        public Builder coordinateSystem(@Nullable String coordinateSystem) {
            this.coordinateSystem = coordinateSystem;
            return this;
        }
        
        /**
         * @param extent Map extent.
         * @return current builder
         */
        public Builder extent(@Nullable StaticMapExtent extent) {
            this.extent = extent;
            return this;
        }
        
        /**
         * @param center Center coordinate.
         * @return current builder
         */
        public Builder center(@Nullable Coordinate center) {
            this.center = center;
            return this;
        }
        
        /**
         * @param labelOp Label option.
         * @return current builder
         */
        public Builder labelOp(@Nullable Integer labelOp) {
            this.labelOp = labelOp;
            return this;
        }
        
        /**
         * @param markers List of markers.
         * @return current builder
         */
        public Builder markers(@NotNull List<StaticMapMarker> markers) {
            this.markers = new ArrayList<>(markers);
            return this;
        }
        
        /**
         * @param markers Array of markers.
         * @return current builder
         */
        public Builder markers(@NotNull StaticMapMarker... markers) {
            this.markers = Arrays.asList(markers);
            return this;
        }
        
        /**
         * @param marker Marker to add.
         * @return current builder
         */
        public Builder addMarker(@NotNull StaticMapMarker marker) {
            this.markers.add(marker);
            return this;
        }
        
        /**
         * @param shapes List of shapes.
         * @return current builder
         */
        public Builder shapes(@NotNull List<StaticMapShape> shapes) {
            this.shapes = new ArrayList<>(shapes);
            return this;
        }
        
        /**
         * @param shapes Array of shapes.
         * @return current builder
         */
        public Builder shapes(@NotNull StaticMapShape... shapes) {
            this.shapes = Arrays.asList(shapes);
            return this;
        }
        
        /**
         * @param shape Shape to add.
         * @return current builder
         */
        public Builder addShape(@NotNull StaticMapShape shape) {
            this.shapes.add(shape);
            return this;
        }
        
        /**
         * @param returnImage Return image.
         * @return current builder
         */
        public Builder returnImage(boolean returnImage) {
            this.returnImage = returnImage;
            return this;
        }
        
        /**
         * @param mode Map mode (default is 1).
         * @return current builder
         */
        public Builder mode(int mode) {
            this.mode = mode;
            return this;
        }
        
        /**
         * @param priorityfilter Enable priority filter (default is true).
         * @return current builder
         */
        public Builder priorityfilter(boolean priorityfilter) {
            this.priorityfilter = priorityfilter;
            return this;
        }
        
        /**
         * @return A new instance of {@link StaticMapOptions}.
         */
        @NotNull
        public StaticMapOptions build() {
            return new StaticMapOptions(this);
        }
    }
}
