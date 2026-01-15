package com.cercalia.sdk.model.geofencing;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Result of a geofencing check operation.
 */
public final class GeofenceResult {
    
    private final List<GeofenceMatch> matches;
    private final int totalPointsChecked;
    private final int totalShapesChecked;
    
    private GeofenceResult(Builder builder) {
        this.matches = builder.matches != null 
                ? Collections.unmodifiableList(builder.matches)
                : Collections.emptyList();
        this.totalPointsChecked = builder.totalPointsChecked;
        this.totalShapesChecked = builder.totalShapesChecked;
    }
    
    /**
     * Returns the shapes that contain at least one point.
     *
     * @return unmodifiable list of matches.
     */
    @NotNull
    public List<GeofenceMatch> getMatches() {
        return matches;
    }
    
    /**
     * Returns the total number of points checked.
     *
     * @return total points count.
     */
    public int getTotalPointsChecked() {
        return totalPointsChecked;
    }
    
    /**
     * Returns the total number of shapes checked.
     *
     * @return total shapes count.
     */
    public int getTotalShapesChecked() {
        return totalShapesChecked;
    }
    
    /**
     * Returns whether any matches were found.
     *
     * @return {@code true} if at least one shape contains a point.
     */
    public boolean hasMatches() {
        return !matches.isEmpty();
    }
    
    /**
     * Returns the number of shapes that contain points.
     *
     * @return match count.
     */
    public int getMatchCount() {
        return matches.size();
    }
    
    /**
     * Creates a new builder for {@link GeofenceResult}.
     *
     * @return a new {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Creates an empty result with no matches.
     *
     * @param totalPoints total points checked.
     * @param totalShapes total shapes checked.
     * @return an empty result.
     */
    @NotNull
    public static GeofenceResult empty(int totalPoints, int totalShapes) {
        return builder()
                .totalPointsChecked(totalPoints)
                .totalShapesChecked(totalShapes)
                .build();
    }
    
    @Override
    public String toString() {
        return "GeofenceResult{" +
                "matchCount=" + matches.size() +
                ", totalPointsChecked=" + totalPointsChecked +
                ", totalShapesChecked=" + totalShapesChecked +
                '}';
    }
    
    /**
     * Builder for {@link GeofenceResult}.
     */
    public static final class Builder {
        private List<GeofenceMatch> matches;
        private int totalPointsChecked;
        private int totalShapesChecked;
        
        private Builder() {
        }
        
        /**
         * Sets the matches.
         *
         * @param matches list of geofence matches.
         * @return this builder.
         */
        @NotNull
        public Builder matches(@NotNull List<GeofenceMatch> matches) {
            this.matches = matches;
            return this;
        }
        
        /**
         * Sets the total points checked.
         *
         * @param totalPointsChecked total count.
         * @return this builder.
         */
        @NotNull
        public Builder totalPointsChecked(int totalPointsChecked) {
            this.totalPointsChecked = totalPointsChecked;
            return this;
        }
        
        /**
         * Sets the total shapes checked.
         *
         * @param totalShapesChecked total count.
         * @return this builder.
         */
        @NotNull
        public Builder totalShapesChecked(int totalShapesChecked) {
            this.totalShapesChecked = totalShapesChecked;
            return this;
        }
        
        /**
         * Builds the {@link GeofenceResult}.
         *
         * @return a new {@link GeofenceResult}.
         */
        @NotNull
        public GeofenceResult build() {
            return new GeofenceResult(this);
        }
    }
}
