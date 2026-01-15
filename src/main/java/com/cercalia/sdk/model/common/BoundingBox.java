package com.cercalia.sdk.model.common;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a geographic bounding box defined by minimum and maximum coordinates.
 * <p>
 * Used for defining map extents, spatial filters, and search boundaries.
 */
public final class BoundingBox {
    
    private final double minLat;
    private final double minLng;
    private final double maxLat;
    private final double maxLng;
    
    /**
     * Creates a new BoundingBox.
     *
     * @param minLat minimum latitude (south)
     * @param minLng minimum longitude (west)
     * @param maxLat maximum latitude (north)
     * @param maxLng maximum longitude (east)
     */
    public BoundingBox(double minLat, double minLng, double maxLat, double maxLng) {
        this.minLat = minLat;
        this.minLng = minLng;
        this.maxLat = maxLat;
        this.maxLng = maxLng;
    }
    
    /**
     * Creates a BoundingBox from two corner coordinates.
     *
     * @param southwest the southwest corner
     * @param northeast the northeast corner
     * @return a new BoundingBox
     */
    @NotNull
    public static BoundingBox fromCorners(@NotNull Coordinate southwest, @NotNull Coordinate northeast) {
        return new BoundingBox(
            southwest.getLat(), southwest.getLng(),
            northeast.getLat(), northeast.getLng()
        );
    }
    
    /**
     * Returns the minimum latitude (south).
     *
     * @return the minimum latitude
     */
    public double getMinLat() {
        return minLat;
    }
    
    /**
     * Returns the minimum longitude (west).
     *
     * @return the minimum longitude
     */
    public double getMinLng() {
        return minLng;
    }
    
    /**
     * Returns the maximum latitude (north).
     *
     * @return the maximum latitude
     */
    public double getMaxLat() {
        return maxLat;
    }
    
    /**
     * Returns the maximum longitude (east).
     *
     * @return the maximum longitude
     */
    public double getMaxLng() {
        return maxLng;
    }
    
    /**
     * Returns the southwest corner coordinate.
     *
     * @return the southwest corner
     */
    @NotNull
    public Coordinate getSouthwest() {
        return new Coordinate(minLat, minLng);
    }
    
    /**
     * Returns the northeast corner coordinate.
     *
     * @return the northeast corner
     */
    @NotNull
    public Coordinate getNortheast() {
        return new Coordinate(maxLat, maxLng);
    }
    
    /**
     * Returns the center coordinate of the bounding box.
     *
     * @return the center coordinate
     */
    @NotNull
    public Coordinate getCenter() {
        return new Coordinate((minLat + maxLat) / 2, (minLng + maxLng) / 2);
    }
    
    /**
     * Returns the bounding box as a Cercalia format string "minLng,minLat,maxLng,maxLat".
     *
     * @return the bounding box string
     */
    @NotNull
    public String toCercaliaString() {
        return minLng + "," + minLat + "," + maxLng + "," + maxLat;
    }
    
    /**
     * Checks if a coordinate is within this bounding box.
     *
     * @param coord the coordinate to check
     * @return true if the coordinate is within the bounding box
     */
    public boolean contains(@NotNull Coordinate coord) {
        return coord.getLat() >= minLat && coord.getLat() <= maxLat &&
               coord.getLng() >= minLng && coord.getLng() <= maxLng;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoundingBox that = (BoundingBox) o;
        return Double.compare(that.minLat, minLat) == 0 &&
               Double.compare(that.minLng, minLng) == 0 &&
               Double.compare(that.maxLat, maxLat) == 0 &&
               Double.compare(that.maxLng, maxLng) == 0;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(minLat, minLng, maxLat, maxLng);
    }
    
    @Override
    public String toString() {
        return "BoundingBox{" +
                "minLat=" + minLat +
                ", minLng=" + minLng +
                ", maxLat=" + maxLat +
                ", maxLng=" + maxLng +
                '}';
    }
}
