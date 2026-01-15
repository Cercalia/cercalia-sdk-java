package com.cercalia.sdk.model.staticmaps;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a circle shape to be rendered on a static map.
 */
public final class StaticMapCircle implements StaticMapShape {
    
    /** Outline color of the circle. */
    @NotNull
    private final RGBAColor outlineColor;
    
    /** Size of the circle's outline in pixels. */
    private final int outlineSize;
    
    /** Fill color of the circle. */
    @NotNull
    private final RGBAColor fillColor;
    
    /** Center coordinate of the circle. */
    @NotNull
    private final Coordinate center;
    
    /** Radius of the circle in meters. */
    private final int radius;
    
    private StaticMapCircle(Builder builder) {
        this.outlineColor = builder.outlineColor;
        this.outlineSize = builder.outlineSize;
        this.fillColor = builder.fillColor;
        this.center = builder.center;
        this.radius = builder.radius;
    }
    
    @Override
    @NotNull
    public StaticMapShapeType getType() {
        return StaticMapShapeType.CIRCLE;
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
    
    /** @return Center coordinate. */
    @NotNull
    public Coordinate getCenter() {
        return center;
    }
    
    /** @return Radius in meters. */
    public int getRadius() {
        return radius;
    }
    
    @Override
    @NotNull
    public String format() {
        // Format: [outlineColor|outlineSize|fillColor|CIRCLE|Y,X|radius]
        return "[" + outlineColor.format() + "|" + outlineSize + "|" + 
               fillColor.format() + "|CIRCLE|" + 
               center.getLat() + "," + center.getLng() + "|" + radius + "]";
    }
    
    /**
     * Creates a builder for a circle with specified center and radius.
     * @param center Center location.
     * @param radius Radius in meters.
     * @return A new builder.
     */
    @NotNull
    public static Builder builder(@NotNull Coordinate center, int radius) {
        return new Builder(center, radius);
    }
    
    /**
     * Builder for {@link StaticMapCircle}.
     */
    public static final class Builder {
        private final Coordinate center;
        private final int radius;
        private RGBAColor outlineColor = RGBAColor.red();
        private int outlineSize = 2;
        private RGBAColor fillColor = RGBAColor.green(128);
        
        private Builder(@NotNull Coordinate center, int radius) {
            this.center = center;
            this.radius = radius;
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
        
        /** @param fillColor Fill color of the circle. @return This builder. */
        public Builder fillColor(@NotNull RGBAColor fillColor) {
            this.fillColor = fillColor;
            return this;
        }
        
        /** @return A new {@link StaticMapCircle} instance. */
        @NotNull
        public StaticMapCircle build() {
            return new StaticMapCircle(this);
        }
    }
}
