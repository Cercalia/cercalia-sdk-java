package com.cercalia.sdk.model.snaptoroad;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a GPS track point for snap-to-road map matching.
 */
public final class SnapToRoadPoint {
    
    /** Geographic coordinate of the point. */
    private final Coordinate coord;
    /** Compass direction (0-360 degrees). */
    private final Integer compass;
    /** Angle/heading. */
    private final Integer angle;
    /** Speed in km/h. */
    private final Integer speed;
    /** Grouping attribute for segment identification. */
    private final String attribute;
    
    private SnapToRoadPoint(Builder builder) {
        this.coord = builder.coord;
        this.compass = builder.compass;
        this.angle = builder.angle;
        this.speed = builder.speed;
        this.attribute = builder.attribute;
    }
    
    /**
     * Returns the geographic coordinate.
     *
     * @return the coordinate (never null)
     */
    @NotNull
    public Coordinate getCoord() {
        return coord;
    }
    
    /**
     * Returns the compass direction (0-360 degrees).
     *
     * @return the compass direction, or null if not set
     */
    @Nullable
    public Integer getCompass() {
        return compass;
    }
    
    /**
     * Returns the angle/heading.
     *
     * @return the angle, or null if not set
     */
    @Nullable
    public Integer getAngle() {
        return angle;
    }
    
    /**
     * Returns the speed in km/h.
     *
     * @return the speed, or null if not set
     */
    @Nullable
    public Integer getSpeed() {
        return speed;
    }
    
    /**
     * Returns the grouping attribute for segment identification.
     *
     * @return the attribute, or null if not set
     */
    @Nullable
    public String getAttribute() {
        return attribute;
    }
    
    /**
     * Creates a new builder for SnapToRoadPoint.
     *
     * @return a new Builder instance
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Creates a simple point with only coordinates.
     *
     * @param lat the latitude
     * @param lng the longitude
     * @return a new SnapToRoadPoint
     */
    @NotNull
    public static SnapToRoadPoint of(double lat, double lng) {
        return builder().coord(new Coordinate(lat, lng)).build();
    }
    
    /**
     * Creates a point with coordinates and speed.
     *
     * @param lat   the latitude
     * @param lng   the longitude
     * @param speed the speed in km/h
     * @return a new SnapToRoadPoint
     */
    @NotNull
    public static SnapToRoadPoint of(double lat, double lng, int speed) {
        return builder()
                .coord(new Coordinate(lat, lng))
                .speed(speed)
                .build();
    }
    
    @Override
    public String toString() {
        return "SnapToRoadPoint{" +
                "coord=" + coord +
                ", compass=" + compass +
                ", angle=" + angle +
                ", speed=" + speed +
                ", attribute='" + attribute + '\'' +
                '}';
    }
    
    /**
     * Builder for SnapToRoadPoint.
     */
    public static final class Builder {
        private Coordinate coord;
        private Integer compass;
        private Integer angle;
        private Integer speed;
        private String attribute;
        
        private Builder() {
        }
        
        /**
         * Sets the geographic coordinate.
         *
         * @param coord the coordinate
         * @return this builder
         */
        @NotNull
        public Builder coord(@NotNull Coordinate coord) {
            this.coord = coord;
            return this;
        }
        
        /**
         * Sets the compass direction.
         *
         * @param compass the compass direction (0-360)
         * @return this builder
         */
        @NotNull
        public Builder compass(@Nullable Integer compass) {
            this.compass = compass;
            return this;
        }
        
        /**
         * Sets the angle/heading.
         *
         * @param angle the angle
         * @return this builder
         */
        @NotNull
        public Builder angle(@Nullable Integer angle) {
            this.angle = angle;
            return this;
        }
        
        /**
         * Sets the speed in km/h.
         *
         * @param speed the speed
         * @return this builder
         */
        @NotNull
        public Builder speed(@Nullable Integer speed) {
            this.speed = speed;
            return this;
        }
        
        /**
         * Sets the grouping attribute.
         *
         * @param attribute the attribute
         * @return this builder
         */
        @NotNull
        public Builder attribute(@Nullable String attribute) {
            this.attribute = attribute;
            return this;
        }
        
        /**
         * Builds the SnapToRoadPoint.
         *
         * @return a new SnapToRoadPoint
         * @throws IllegalStateException if coord is not set
         */
        @NotNull
        public SnapToRoadPoint build() {
            if (coord == null) {
                throw new IllegalStateException("Coordinate is required");
            }
            return new SnapToRoadPoint(this);
        }
    }
}
