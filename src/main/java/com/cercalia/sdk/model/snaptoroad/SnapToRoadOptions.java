package com.cercalia.sdk.model.snaptoroad;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Options for snap-to-road map matching requests.
 */
public final class SnapToRoadOptions {
    
    /** Weight type for route calculation. */
    private final Weight weight;
    /** Country network code. */
    private final String net;
    /** Coordinate system for result geometry. */
    private final String geometrySrs;
    /** Geometry simplification tolerance in meters. */
    private final Integer geometryTolerance;
    /** Whether to include original GPS points displaced on road. */
    private final Boolean points;
    /** Whether speeding detection is enabled. */
    private final Boolean speeding;
    /** Speed tolerance in km/h for speeding detection. */
    private final Integer speedTolerance;
    /** Whether to return only track multipoint geometry. */
    private final Boolean onlyTrack;
    /** Maximum allowable deviation considering direction. */
    private final Integer maxDirectionSearchDistance;
    /** Maximum allowable deviation ignoring direction. */
    private final Integer maxSearchDistance;
    /** Factor between route distance and straight-line distance. */
    private final Double factor;
    
    private SnapToRoadOptions(Builder builder) {
        this.weight = builder.weight;
        this.net = builder.net;
        this.geometrySrs = builder.geometrySrs;
        this.geometryTolerance = builder.geometryTolerance;
        this.points = builder.points;
        this.speeding = builder.speeding;
        this.speedTolerance = builder.speedTolerance;
        this.onlyTrack = builder.onlyTrack;
        this.maxDirectionSearchDistance = builder.maxDirectionSearchDistance;
        this.maxSearchDistance = builder.maxSearchDistance;
        this.factor = builder.factor;
    }
    
    /** @return Weight type. */
    @Nullable
    public Weight getWeight() {
        return weight;
    }
    
    /** @return Network code. */
    @Nullable
    public String getNet() {
        return net;
    }
    
    /** @return Geometry SRS. */
    @Nullable
    public String getGeometrySrs() {
        return geometrySrs;
    }
    
    /** @return Geometry tolerance. */
    @Nullable
    public Integer getGeometryTolerance() {
        return geometryTolerance;
    }
    
    /** @return True to include points. */
    @Nullable
    public Boolean getPoints() {
        return points;
    }
    
    /** @return True if speeding detection enabled. */
    @Nullable
    public Boolean getSpeeding() {
        return speeding;
    }
    
    /** @return Speed tolerance. */
    @Nullable
    public Integer getSpeedTolerance() {
        return speedTolerance;
    }
    
    /** @return True for only track. */
    @Nullable
    public Boolean getOnlyTrack() {
        return onlyTrack;
    }
    
    /** @return Max direction search distance. */
    @Nullable
    public Integer getMaxDirectionSearchDistance() {
        return maxDirectionSearchDistance;
    }
    
    /** @return Max search distance. */
    @Nullable
    public Integer getMaxSearchDistance() {
        return maxSearchDistance;
    }
    
    /** @return Factor. */
    @Nullable
    public Double getFactor() {
        return factor;
    }
    
    /** @return A new builder for {@link SnapToRoadOptions}. */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    /** @return Default options. */
    @NotNull
    public static SnapToRoadOptions defaults() {
        return builder().build();
    }
    
    /**
     * Weight type for route calculation.
     */
    public enum Weight {
        /** Optimize for shortest distance. */
        DISTANCE("distance"),
        /** Optimize for shortest time. */
        TIME("time");
        
        private final String value;
        
        Weight(String value) {
            this.value = value;
        }
        
        /** @return API parameter value. */
        @NotNull
        public String getValue() {
            return value;
        }
    }
    
    /**
     * Builder for {@link SnapToRoadOptions}.
     */
    public static final class Builder {
        private Weight weight;
        private String net;
        private String geometrySrs;
        private Integer geometryTolerance;
        private Boolean points;
        private Boolean speeding;
        private Integer speedTolerance;
        private Boolean onlyTrack;
        private Integer maxDirectionSearchDistance;
        private Integer maxSearchDistance;
        private Double factor;
        
        private Builder() {
        }
        
        /** @param weight Weight type. @return This builder. */
        @NotNull
        public Builder weight(@Nullable Weight weight) {
            this.weight = weight;
            return this;
        }
        
        /** @param net Network code. @return This builder. */
        @NotNull
        public Builder net(@Nullable String net) {
            this.net = net;
            return this;
        }
        
        /** @param geometrySrs Geometry SRS. @return This builder. */
        @NotNull
        public Builder geometrySrs(@Nullable String geometrySrs) {
            this.geometrySrs = geometrySrs;
            return this;
        }
        
        /** @param geometryTolerance Geometry tolerance. @return This builder. */
        @NotNull
        public Builder geometryTolerance(@Nullable Integer geometryTolerance) {
            this.geometryTolerance = geometryTolerance;
            return this;
        }
        
        /** @param points Include points. @return This builder. */
        @NotNull
        public Builder points(@Nullable Boolean points) {
            this.points = points;
            return this;
        }
        
        /** @param speeding Enable speeding detection. @return This builder. */
        @NotNull
        public Builder speeding(@Nullable Boolean speeding) {
            this.speeding = speeding;
            return this;
        }
        
        /** @param speedTolerance Speed tolerance. @return This builder. */
        @NotNull
        public Builder speedTolerance(@Nullable Integer speedTolerance) {
            this.speedTolerance = speedTolerance;
            return this;
        }
        
        /** @param onlyTrack Only track. @return This builder. */
        @NotNull
        public Builder onlyTrack(@Nullable Boolean onlyTrack) {
            this.onlyTrack = onlyTrack;
            return this;
        }
        
        /** @param maxDirectionSearchDistance Max direction search distance. @return This builder. */
        @NotNull
        public Builder maxDirectionSearchDistance(@Nullable Integer maxDirectionSearchDistance) {
            this.maxDirectionSearchDistance = maxDirectionSearchDistance;
            return this;
        }
        
        /** @param maxSearchDistance Max search distance. @return This builder. */
        @NotNull
        public Builder maxSearchDistance(@Nullable Integer maxSearchDistance) {
            this.maxSearchDistance = maxSearchDistance;
            return this;
        }
        
        /** @param factor Factor. @return This builder. */
        @NotNull
        public Builder factor(@Nullable Double factor) {
            this.factor = factor;
            return this;
        }
        
        /** @return A new {@link SnapToRoadOptions} instance. */
        @NotNull
        public SnapToRoadOptions build() {
            return new SnapToRoadOptions(this);
        }
    }
}
