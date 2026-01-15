package com.cercalia.sdk.model.staticmaps;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a sector (pie slice) shape to be rendered on a static map.
 */
public final class StaticMapSector implements StaticMapShape {
    
    /** Outline color of the sector. */
    @NotNull
    private final RGBAColor outlineColor;
    
    /** Size of the sector's outline in pixels. */
    private final int outlineSize;
    
    /** Fill color of the sector. */
    @NotNull
    private final RGBAColor fillColor;
    
    /** Center coordinate of the sector. */
    @NotNull
    private final Coordinate center;
    
    /** Inner radius of the sector in meters. */
    private final int innerRadius;
    
    /** Outer radius of the sector in meters. */
    private final int outerRadius;
    
    /** Start angle of the sector in degrees (0 is North, clockwise). */
    private final int startAngle;
    
    /** End angle of the sector in degrees. */
    private final int endAngle;
    
    private StaticMapSector(Builder builder) {
        this.outlineColor = builder.outlineColor;
        this.outlineSize = builder.outlineSize;
        this.fillColor = builder.fillColor;
        this.center = builder.center;
        this.innerRadius = builder.innerRadius;
        this.outerRadius = builder.outerRadius;
        this.startAngle = builder.startAngle;
        this.endAngle = builder.endAngle;
    }
    
    @Override
    @NotNull
    public StaticMapShapeType getType() {
        return StaticMapShapeType.SECTOR;
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
    
    /** @return Inner radius in meters. */
    public int getInnerRadius() {
        return innerRadius;
    }
    
    /** @return Outer radius in meters. */
    public int getOuterRadius() {
        return outerRadius;
    }
    
    /** @return Start angle in degrees. */
    public int getStartAngle() {
        return startAngle;
    }
    
    /** @return End angle in degrees. */
    public int getEndAngle() {
        return endAngle;
    }
    
    @Override
    @NotNull
    public String format() {
        // Format: [outlineColor|outlineSize|fillColor|SECTOR|center|innerRadius|outerRadius|startAngle|endAngle]
        return "[" + outlineColor.format() + "|" + outlineSize + "|" + 
               fillColor.format() + "|SECTOR|" + 
               center.getLat() + "," + center.getLng() + "|" +
               innerRadius + "|" + outerRadius + "|" + startAngle + "|" + endAngle + "]";
    }
    
    /**
     * Creates a builder for a sector centered at a coordinate.
     * @param center Center location.
     * @return A new builder.
     */
    @NotNull
    public static Builder builder(@NotNull Coordinate center) {
        return new Builder(center);
    }
    
    /**
     * Builder for {@link StaticMapSector}.
     */
    public static final class Builder {
        private final Coordinate center;
        private int innerRadius = 0;
        private int outerRadius = 1000;
        private int startAngle = 0;
        private int endAngle = 90;
        private RGBAColor outlineColor = RGBAColor.red();
        private int outlineSize = 2;
        private RGBAColor fillColor = RGBAColor.green(128);
        
        private Builder(@NotNull Coordinate center) {
            this.center = center;
        }
        
        /** @param innerRadius Inner radius in meters. @return This builder. */
        public Builder innerRadius(int innerRadius) {
            this.innerRadius = innerRadius;
            return this;
        }
        
        /** @param outerRadius Outer radius in meters. @return This builder. */
        public Builder outerRadius(int outerRadius) {
            this.outerRadius = outerRadius;
            return this;
        }
        
        /** @param startAngle Start angle in degrees. @return This builder. */
        public Builder startAngle(int startAngle) {
            this.startAngle = startAngle;
            return this;
        }
        
        /** @param endAngle End angle in degrees. @return This builder. */
        public Builder endAngle(int endAngle) {
            this.endAngle = endAngle;
            return this;
        }
        
        /** @param outlineColor Color of the outline. @return This builder. */
        public Builder outlineColor(@NotNull RGBAColor outlineColor) {
            this.outlineColor = outlineColor;
            return this;
        }
        
        /** @param outlineSize Width of the outline in pixels. @return This builder. */
        public Builder outlineSize(int outlineSize) {
            this.outlineSize = outlineSize;
            return this;
        }
        
        /** @param fillColor Fill color of the sector. @return This builder. */
        public Builder fillColor(@NotNull RGBAColor fillColor) {
            this.fillColor = fillColor;
            return this;
        }
        
        /** @return A new {@link StaticMapSector} instance. */
        @NotNull
        public StaticMapSector build() {
            return new StaticMapSector(this);
        }
    }
}
