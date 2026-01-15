package com.cercalia.sdk.model.geofencing;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Match result for a geofence shape that contains points.
 */
public final class GeofenceMatch {
    
    private final String shapeId;
    private final String shapeWkt;
    private final List<MatchedPoint> pointsInside;
    
    private GeofenceMatch(Builder builder) {
        this.shapeId = builder.shapeId;
        this.shapeWkt = builder.shapeWkt;
        this.pointsInside = builder.pointsInside != null 
                ? Collections.unmodifiableList(builder.pointsInside)
                : Collections.emptyList();
    }
    
    /**
     * Returns the unique identifier of the shape.
     *
     * @return the shape ID.
     */
    @NotNull
    public String getShapeId() {
        return shapeId;
    }
    
    /**
     * Returns the WKT representation of the shape.
     *
     * @return the shape WKT.
     */
    @NotNull
    public String getShapeWkt() {
        return shapeWkt;
    }
    
    /**
     * Returns the points that are inside this shape.
     *
     * @return unmodifiable list of matched points.
     */
    @NotNull
    public List<MatchedPoint> getPointsInside() {
        return pointsInside;
    }
    
    /**
     * Returns whether this shape contains any points.
     *
     * @return {@code true} if points are inside.
     */
    public boolean hasPointsInside() {
        return !pointsInside.isEmpty();
    }
    
    /**
     * Creates a new builder for {@link GeofenceMatch}.
     *
     * @return a new {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public String toString() {
        return "GeofenceMatch{" +
                "shapeId='" + shapeId + '\'' +
                ", pointsInsideCount=" + pointsInside.size() +
                '}';
    }
    
    /**
     * A point that was matched inside a geofence shape.
     */
    public static final class MatchedPoint {
        private final String id;
        private final Coordinate coord;
        
        /**
         * Creates a new {@link MatchedPoint}.
         *
         * @param id    point identifier.
         * @param coord point coordinates.
         */
        public MatchedPoint(@NotNull String id, @NotNull Coordinate coord) {
            this.id = id;
            this.coord = coord;
        }
        
        /**
         * Returns the point identifier.
         *
         * @return the point ID.
         */
        @NotNull
        public String getId() {
            return id;
        }
        
        /**
         * Returns the point coordinates.
         *
         * @return the coordinate.
         */
        @NotNull
        public Coordinate getCoord() {
            return coord;
        }
        
        @Override
        public String toString() {
            return "MatchedPoint{id='" + id + "', coord=" + coord + '}';
        }
    }
    
    /**
     * Builder for {@link GeofenceMatch}.
     */
    public static final class Builder {
        private String shapeId;
        private String shapeWkt;
        private List<MatchedPoint> pointsInside;
        
        private Builder() {
        }
        
        /**
         * Sets the shape ID.
         *
         * @param shapeId the shape identifier.
         * @return this builder.
         */
        @NotNull
        public Builder shapeId(@NotNull String shapeId) {
            this.shapeId = shapeId;
            return this;
        }
        
        /**
         * Sets the shape WKT.
         *
         * @param shapeWkt the WKT geometry.
         * @return this builder.
         */
        @NotNull
        public Builder shapeWkt(@NotNull String shapeWkt) {
            this.shapeWkt = shapeWkt;
            return this;
        }
        
        /**
         * Sets the matched points inside this shape.
         *
         * @param pointsInside list of matched points.
         * @return this builder.
         */
        @NotNull
        public Builder pointsInside(@NotNull List<MatchedPoint> pointsInside) {
            this.pointsInside = pointsInside;
            return this;
        }
        
        /**
         * Builds the {@link GeofenceMatch}.
         *
         * @return a new {@link GeofenceMatch}.
         */
        @NotNull
        public GeofenceMatch build() {
            if (shapeId == null) shapeId = "";
            if (shapeWkt == null) shapeWkt = "";
            return new GeofenceMatch(this);
        }
    }
}
