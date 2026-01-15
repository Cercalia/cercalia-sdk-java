package com.cercalia.sdk.model.staticmaps;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a rectangle shape to be rendered on a static map.
 */
public final class StaticMapRectangle implements StaticMapShape {
    
    /** Outline color of the rectangle. */
    @NotNull
    private final RGBAColor outlineColor;
    
    /** Size of the rectangle's outline in pixels. */
    private final int outlineSize;
    
    /** Fill color of the rectangle. */
    @NotNull
    private final RGBAColor fillColor;
    
    /** Upper-left corner of the rectangle. */
    @NotNull
    private final Coordinate upperLeft;
    
    /** Lower-right corner of the rectangle. */
    @NotNull
    private final Coordinate lowerRight;
    
    private StaticMapRectangle(Builder builder) {
        this.outlineColor = builder.outlineColor;
        this.outlineSize = builder.outlineSize;
        this.fillColor = builder.fillColor;
        this.upperLeft = builder.upperLeft;
        this.lowerRight = builder.lowerRight;
    }
    
    @Override
    @NotNull
    public StaticMapShapeType getType() {
        return StaticMapShapeType.RECTANGLE;
    }
    
    @Override
    @NotNull
    public RGBAColor getOutlineColor() {
        return outlineColor;
    }
    
    @Override
    public int getOutlineSize() {
        return outlineSize;
    }
    
    @Override
    @NotNull
    public RGBAColor getFillColor() {
        return fillColor;
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
    
    @Override
    @NotNull
    public String format() {
        // Format: [outlineColor|outlineSize|fillColor|RECTANGLE|Y1,X1|Y2,X2]
        return "[" + outlineColor.format() + "|" + outlineSize + "|" + 
               fillColor.format() + "|RECTANGLE|" + 
               upperLeft.getLat() + "," + upperLeft.getLng() + "|" +
               lowerRight.getLat() + "," + lowerRight.getLng() + "]";
    }
    
    /**
     * Creates a builder for a rectangle defined by its corners.
     * @param upperLeft Upper-left corner.
     * @param lowerRight Lower-right corner.
     * @return A new builder.
     */
    @NotNull
    public static Builder builder(@NotNull Coordinate upperLeft, @NotNull Coordinate lowerRight) {
        return new Builder(upperLeft, lowerRight);
    }
    
    /**
     * Builder for {@link StaticMapRectangle}.
     */
    public static final class Builder {
        private final Coordinate upperLeft;
        private final Coordinate lowerRight;
        private RGBAColor outlineColor = RGBAColor.red();
        private int outlineSize = 3;
        private RGBAColor fillColor = RGBAColor.green(128);
        
        private Builder(@NotNull Coordinate upperLeft, @NotNull Coordinate lowerRight) {
            this.upperLeft = upperLeft;
            this.lowerRight = lowerRight;
        }
        
        /** @param outlineColor Color of the outline. @return This builder. */
        public Builder outlineColor(@NotNull RGBAColor outlineColor) {
            this.outlineColor = outlineColor;
            return this;
        }
        
        /** @param outlineSize Size of the outline in pixels. @return This builder. */
        public Builder outlineSize(int outlineSize) {
            this.outlineSize = outlineSize;
            return this;
        }
        
        /** @param fillColor Fill color of the rectangle. @return This builder. */
        public Builder fillColor(@NotNull RGBAColor fillColor) {
            this.fillColor = fillColor;
            return this;
        }
        
        /** @return A new {@link StaticMapRectangle} instance. */
        @NotNull
        public StaticMapRectangle build() {
            return new StaticMapRectangle(this);
        }
    }
}
