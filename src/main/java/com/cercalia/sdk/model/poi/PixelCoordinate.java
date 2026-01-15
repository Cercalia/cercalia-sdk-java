package com.cercalia.sdk.model.poi;

import java.util.Objects;

/**
 * Represents pixel coordinates for map rendering.
 */
public final class PixelCoordinate {
    
    private final int x;
    private final int y;
    
    public PixelCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * @return The horizontal pixel position.
     */
    public int getX() {
        return x;
    }
    
    /**
     * @return The vertical pixel position.
     */
    public int getY() {
        return y;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PixelCoordinate that = (PixelCoordinate) o;
        return x == that.x && y == that.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public String toString() {
        return "PixelCoordinate{x=" + x + ", y=" + y + '}';
    }
}
