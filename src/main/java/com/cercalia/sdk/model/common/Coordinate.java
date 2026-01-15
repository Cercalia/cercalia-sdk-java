package com.cercalia.sdk.model.common;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a geographic coordinate with latitude and longitude.
 * <p>
 * This is the fundamental spatial data type used throughout the SDK.
 * Coordinates are typically represented in WGS84 (EPSG:4326).
 */
public final class Coordinate {
    
    private final double lat;
    private final double lng;
    
    /**
     * Creates a new Coordinate.
     *
     * @param lat the latitude
     * @param lng the longitude
     */
    public Coordinate(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
    
    /**
     * Returns the latitude.
     *
     * @return the latitude
     */
    public double getLat() {
        return lat;
    }
    
    /**
     * Returns the longitude.
     *
     * @return the longitude
     */
    public double getLng() {
        return lng;
    }
    
    /**
     * Returns the coordinate as a string in "lng,lat" format (Cercalia format).
     *
     * @return the coordinate string
     */
    @NotNull
    public String toCercaliaString() {
        return lng + "," + lat;
    }
    
    /**
     * Returns the coordinate as a string in "lat,lng" format.
     *
     * @return the coordinate string
     */
    @NotNull
    public String toLatLngString() {
        return lat + "," + lng;
    }
    
    /**
     * Parses a coordinate from Cercalia format string "lng,lat".
     *
     * @param coordString the coordinate string
     * @return the parsed Coordinate
     * @throws IllegalArgumentException if the string is invalid
     */
    @NotNull
    public static Coordinate fromCercaliaString(@NotNull String coordString) {
        String[] parts = coordString.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid coordinate string: " + coordString);
        }
        double lng = Double.parseDouble(parts[0].trim());
        double lat = Double.parseDouble(parts[1].trim());
        return new Coordinate(lat, lng);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Double.compare(that.lat, lat) == 0 &&
               Double.compare(that.lng, lng) == 0;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(lat, lng);
    }
    
    @Override
    public String toString() {
        return "Coordinate{lat=" + lat + ", lng=" + lng + '}';
    }
}
