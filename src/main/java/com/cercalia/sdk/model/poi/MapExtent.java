package com.cercalia.sdk.model.poi;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a map extent defined by upper-left and lower-right coordinates.
 */
public final class MapExtent {
    
    @NotNull
    private final Coordinate upperLeft;
    
    @NotNull
    private final Coordinate lowerRight;
    
    public MapExtent(@NotNull Coordinate upperLeft, @NotNull Coordinate lowerRight) {
        this.upperLeft = Objects.requireNonNull(upperLeft, "upperLeft cannot be null");
        this.lowerRight = Objects.requireNonNull(lowerRight, "lowerRight cannot be null");
    }
    
    /**
     * @return The upper-left coordinate of the extent.
     */
    @NotNull
    public Coordinate getUpperLeft() {
        return upperLeft;
    }
    
    /**
     * @return The lower-right coordinate of the extent.
     */
    @NotNull
    public Coordinate getLowerRight() {
        return lowerRight;
    }
    
    /**
     * Returns the extent as a Cercalia format string: {@code lat,lng|lat,lng}.
     *
     * @return The extent string.
     */
    @NotNull
    public String toCercaliaString() {
        return upperLeft.getLat() + "," + upperLeft.getLng() + "|" +
               lowerRight.getLat() + "," + lowerRight.getLng();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapExtent mapExtent = (MapExtent) o;
        return Objects.equals(upperLeft, mapExtent.upperLeft) &&
               Objects.equals(lowerRight, mapExtent.lowerRight);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(upperLeft, lowerRight);
    }
    
    @Override
    public String toString() {
        return "MapExtent{upperLeft=" + upperLeft + ", lowerRight=" + lowerRight + '}';
    }
}
