package com.cercalia.sdk.model.snaptoroad;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a road segment from snap-to-road map matching.
 */
public final class SnapToRoadSegment {
    
    /** WKT geometry of the matched road. */
    private final String wkt;
    /** Distance in kilometers. */
    private final double distance;
    /** Grouping attribute. */
    private final String attribute;
    /** Speeding detection flag. */
    private final Boolean speeding;
    /** Speeding level severity. */
    private final Integer speedingLevel;
    
    private SnapToRoadSegment(Builder builder) {
        this.wkt = builder.wkt;
        this.distance = builder.distance;
        this.attribute = builder.attribute;
        this.speeding = builder.speeding;
        this.speedingLevel = builder.speedingLevel;
    }
    
    /**
     * Returns the WKT geometry of the matched road segment.
     *
     * @return the WKT geometry (never null)
     */
    @NotNull
    public String getWkt() {
        return wkt;
    }
    
    /**
     * Returns the distance of this segment in kilometers.
     *
     * @return the distance in km
     */
    public double getDistance() {
        return distance;
    }
    
    /**
     * Returns the grouping attribute for this segment.
     *
     * @return the attribute, or null if not set
     */
    @Nullable
    public String getAttribute() {
        return attribute;
    }
    
    /**
     * Returns whether speeding was detected in this segment.
     *
     * @return true if speeding, false if not, null if speeding detection was not enabled
     */
    @Nullable
    public Boolean getSpeeding() {
        return speeding;
    }
    
    /**
     * Returns the speeding level (severity) for this segment.
     *
     * @return the speeding level (0-3+), or null if no speeding
     */
    @Nullable
    public Integer getSpeedingLevel() {
        return speedingLevel;
    }
    
    /**
     * Creates a new builder for SnapToRoadSegment.
     *
     * @return a new Builder instance
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public String toString() {
        return "SnapToRoadSegment{" +
                "wkt='" + (wkt.length() > 50 ? wkt.substring(0, 50) + "..." : wkt) + '\'' +
                ", distance=" + distance +
                ", attribute='" + attribute + '\'' +
                ", speeding=" + speeding +
                ", speedingLevel=" + speedingLevel +
                '}';
    }
    
    /**
     * Builder for SnapToRoadSegment.
     */
    public static final class Builder {
        private String wkt;
        private double distance;
        private String attribute;
        private Boolean speeding;
        private Integer speedingLevel;
        
        private Builder() {
        }
        
        /**
         * Sets the WKT geometry.
         *
         * @param wkt the WKT geometry
         * @return this builder
         */
        @NotNull
        public Builder wkt(@NotNull String wkt) {
            this.wkt = wkt;
            return this;
        }
        
        /**
         * Sets the distance in kilometers.
         *
         * @param distance the distance
         * @return this builder
         */
        @NotNull
        public Builder distance(double distance) {
            this.distance = distance;
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
         * Sets whether speeding was detected.
         *
         * @param speeding the speeding flag
         * @return this builder
         */
        @NotNull
        public Builder speeding(@Nullable Boolean speeding) {
            this.speeding = speeding;
            return this;
        }
        
        /**
         * Sets the speeding level.
         *
         * @param speedingLevel the speeding level
         * @return this builder
         */
        @NotNull
        public Builder speedingLevel(@Nullable Integer speedingLevel) {
            this.speedingLevel = speedingLevel;
            return this;
        }
        
        /**
         * Builds the SnapToRoadSegment.
         *
         * @return a new SnapToRoadSegment
         * @throws IllegalStateException if wkt is not set
         */
        @NotNull
        public SnapToRoadSegment build() {
            if (wkt == null || wkt.isEmpty()) {
                throw new IllegalStateException("WKT is required");
            }
            return new SnapToRoadSegment(this);
        }
    }
}
