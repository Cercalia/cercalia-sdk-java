package com.cercalia.sdk.model.isochrone;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Result of an isochrone calculation.
 */
public final class IsochroneResult {
    
    /** WKT geometry of the isochrone polygon */
    @NotNull
    private final String wkt;
    
    /** Center coordinate of the isochrone */
    @NotNull
    private final Coordinate center;
    
    /** Value used for calculation (minutes for time, meters for distance) */
    private final int value;
    
    /** Weight type: 'time' (minutes) or 'distance' (meters) */
    @NotNull
    private final IsochroneWeight weight;
    
    /** Level value returned by Cercalia API (raw value in ms for time, m for distance) */
    @NotNull
    private final String level;
    
    private IsochroneResult(Builder builder) {
        this.wkt = Objects.requireNonNull(builder.wkt, "wkt cannot be null");
        this.center = Objects.requireNonNull(builder.center, "center cannot be null");
        this.value = builder.value;
        this.weight = Objects.requireNonNull(builder.weight, "weight cannot be null");
        this.level = Objects.requireNonNull(builder.level, "level cannot be null");
    }
    
    /**
     * @return WKT geometry of the isochrone polygon.
     */
    @NotNull
    public String getWkt() {
        return wkt;
    }
    
    /**
     * @return Center coordinate of the isochrone.
     */
    @NotNull
    public Coordinate getCenter() {
        return center;
    }
    
    /**
     * @return Value used for calculation (minutes for time, meters for distance).
     */
    public int getValue() {
        return value;
    }
    
    /**
     * @return Weight type used for the calculation.
     */
    @NotNull
    public IsochroneWeight getWeight() {
        return weight;
    }
    
    /**
     * @return Level value returned by Cercalia API.
     */
    @NotNull
    public String getLevel() {
        return level;
    }
    
    /**
     * @return A new builder for {@link IsochroneResult}.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for {@link IsochroneResult}.
     */
    public static final class Builder {
        private String wkt;
        private Coordinate center;
        private int value;
        private IsochroneWeight weight;
        private String level;
        
        private Builder() {}
        
        /**
         * @param wkt WKT geometry string.
         * @return The builder.
         */
        public Builder wkt(@NotNull String wkt) {
            this.wkt = wkt;
            return this;
        }
        
        /**
         * @param center Center coordinate.
         * @return The builder.
         */
        public Builder center(@NotNull Coordinate center) {
            this.center = center;
            return this;
        }
        
        /**
         * @param value Value in minutes or meters.
         * @return The builder.
         */
        public Builder value(int value) {
            this.value = value;
            return this;
        }
        
        /**
         * @param weight Weight type.
         * @return The builder.
         */
        public Builder weight(@NotNull IsochroneWeight weight) {
            this.weight = weight;
            return this;
        }
        
        /**
         * @param level Raw level from API.
         * @return The builder.
         */
        public Builder level(@NotNull String level) {
            this.level = level;
            return this;
        }
        
        /**
         * @return A new instance of {@link IsochroneResult}.
         */
        @NotNull
        public IsochroneResult build() {
            return new IsochroneResult(this);
        }
    }
}
