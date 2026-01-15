package com.cercalia.sdk.model.geofencing;

import org.jetbrains.annotations.NotNull;

/**
 * Geofence shape definition (polygon or circle in WKT format).
 * <p>
 * Supported WKT formats:
 * <ul>
 *   <li>CIRCLE(lng lat, radiusMeters) - e.g., "CIRCLE(2.17 41.38, 1000)"</li>
 *   <li>POLYGON((lng lat, lng lat, ...)) - e.g., "POLYGON((2.15 41.37, 2.19 41.37, 2.19 41.40, 2.15 41.40, 2.15 41.37))"</li>
 * </ul>
 */
public final class GeofenceShape {
    
    private final String id;
    private final String wkt;
    
    /**
     * Creates a new {@link GeofenceShape}.
     *
     * @param id  unique identifier for the shape.
     * @param wkt shape geometry in WKT format.
     */
    public GeofenceShape(@NotNull String id, @NotNull String wkt) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Shape ID is required");
        }
        if (wkt == null || wkt.isEmpty()) {
            throw new IllegalArgumentException("Shape WKT is required");
        }
        this.id = id;
        this.wkt = wkt;
    }
    
    /**
     * Returns the unique identifier for the shape.
     *
     * @return the shape ID.
     */
    @NotNull
    public String getId() {
        return id;
    }
    
    /**
     * Returns the shape geometry in WKT format.
     *
     * @return the WKT geometry.
     */
    @NotNull
    public String getWkt() {
        return wkt;
    }
    
    /**
     * Creates a circular geofence shape.
     *
     * @param id           unique identifier.
     * @param centerLng    center longitude.
     * @param centerLat    center latitude.
     * @param radiusMeters radius in meters.
     * @return a new {@link GeofenceShape} with circle WKT.
     */
    @NotNull
    public static GeofenceShape circle(@NotNull String id, double centerLng, double centerLat, double radiusMeters) {
        String wkt = String.format("CIRCLE(%s %s, %s)", centerLng, centerLat, radiusMeters);
        return new GeofenceShape(id, wkt);
    }
    
    /**
     * Creates a rectangular (polygon) geofence shape.
     *
     * @param id     unique identifier.
     * @param swLng  southwest corner longitude.
     * @param swLat  southwest corner latitude.
     * @param neLng  northeast corner longitude.
     * @param neLat  northeast corner latitude.
     * @return a new {@link GeofenceShape} with polygon WKT.
     */
    @NotNull
    public static GeofenceShape rectangle(@NotNull String id, double swLng, double swLat, double neLng, double neLat) {
        // Corners: SW -> SE -> NE -> NW -> SW (closed polygon)
        String wkt = String.format("POLYGON((%s %s, %s %s, %s %s, %s %s, %s %s))",
                swLng, swLat,  // SW
                neLng, swLat,  // SE
                neLng, neLat,  // NE
                swLng, neLat,  // NW
                swLng, swLat   // Close polygon (back to SW)
        );
        return new GeofenceShape(id, wkt);
    }
    
    @Override
    public String toString() {
        return "GeofenceShape{" +
                "id='" + id + '\'' +
                ", wkt='" + (wkt.length() > 50 ? wkt.substring(0, 50) + "..." : wkt) + '\'' +
                '}';
    }
}
