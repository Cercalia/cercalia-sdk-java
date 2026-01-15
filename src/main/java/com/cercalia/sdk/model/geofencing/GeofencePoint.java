package com.cercalia.sdk.model.geofencing;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;

/**
 * Point to check against geofences.
 */
public final class GeofencePoint {
    
    private final String id;
    private final Coordinate coord;
    
    /**
     * Creates a new {@link GeofencePoint}.
     *
     * @param id    unique identifier for the point.
     * @param coord point coordinates.
     */
    public GeofencePoint(@NotNull String id, @NotNull Coordinate coord) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Point ID is required");
        }
        if (coord == null) {
            throw new IllegalArgumentException("Point coordinate is required");
        }
        this.id = id;
        this.coord = coord;
    }
    
    /**
     * Creates a new {@link GeofencePoint} with coordinates.
     *
     * @param id  unique identifier.
     * @param lat latitude.
     * @param lng longitude.
     * @return a new {@link GeofencePoint}.
     */
    @NotNull
    public static GeofencePoint of(@NotNull String id, double lat, double lng) {
        return new GeofencePoint(id, new Coordinate(lat, lng));
    }
    
    /**
     * Returns the unique identifier for the point.
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
        return "GeofencePoint{" +
                "id='" + id + '\'' +
                ", coord=" + coord +
                '}';
    }
}
