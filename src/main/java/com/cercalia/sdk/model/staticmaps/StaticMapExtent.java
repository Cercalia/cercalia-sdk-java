package com.cercalia.sdk.model.staticmaps;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a map extent (bounding box) defined by its upper-left and lower-right corners.
 */
public final class StaticMapExtent {
    
    /** Upper-left corner of the extent. */
    @NotNull
    private final Coordinate upperLeft;
    
    /** Lower-right corner of the extent. */
    @NotNull
    private final Coordinate lowerRight;
    
    private StaticMapExtent(@NotNull Coordinate upperLeft, @NotNull Coordinate lowerRight) {
        this.upperLeft = upperLeft;
        this.lowerRight = lowerRight;
    }
    
    /** @return Upper-left corner. */
    @NotNull
    public Coordinate getUpperLeft() {
        return upperLeft;
    }
    
    /** @return Lower-right corner. */
    @NotNull
    public Coordinate getLowerRight() {
        return lowerRight;
    }
    
    /**
     * Creates an extent from two corner coordinates.
     * @param upperLeft Upper-left corner.
     * @param lowerRight Lower-right corner.
     * @return A new extent.
     */
    @NotNull
    public static StaticMapExtent of(@NotNull Coordinate upperLeft, @NotNull Coordinate lowerRight) {
        return new StaticMapExtent(upperLeft, lowerRight);
    }
    
    /**
     * Formats the extent for the Cercalia API.
     * @return Formatted string: {@code "lat1,lng1|lat2,lng2"}.
     */
    @NotNull
    public String format() {
        return upperLeft.getLat() + "," + upperLeft.getLng() + "|" +
               lowerRight.getLat() + "," + lowerRight.getLng();
    }
}
