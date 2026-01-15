package com.cercalia.sdk.model.staticmaps;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a line shape to be rendered on a static map.
 */
public final class StaticMapLine implements StaticMapShape {
    
    /** Color of the line. */
    @NotNull
    private final RGBAColor outlineColor;
    
    /** Width of the line in pixels. */
    private final int outlineSize;
    
    /** Not used for lines, but required by {@link StaticMapShape}. */
    @NotNull
    private final RGBAColor fillColor;
    
    /** Start coordinate of the line. */
    @NotNull
    private final Coordinate start;
    
    /** End coordinate of the line. */
    @NotNull
    private final Coordinate end;
    
    private StaticMapLine(Builder builder) {
        this.outlineColor = builder.outlineColor;
        this.outlineSize = builder.outlineSize;
        this.fillColor = builder.fillColor;
        this.start = builder.start;
        this.end = builder.end;
    }
    
    @Override
    @NotNull
    public StaticMapShapeType getType() {
        return StaticMapShapeType.LINE;
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
    
    /** @return Start coordinate. */
    @NotNull
    public Coordinate getStart() {
        return start;
    }
    
    /** @return End coordinate. */
    @NotNull
    public Coordinate getEnd() {
        return end;
    }
    
    @Override
    @NotNull
    public String format() {
        // Format: [outlineColor|outlineSize|fillColor|LINE|startY,startX|endY,endX]
        return "[" + outlineColor.format() + "|" + outlineSize + "|" + 
               fillColor.format() + "|LINE|" + 
               start.getLat() + "," + start.getLng() + "|" +
               end.getLat() + "," + end.getLng() + "]";
    }
    
    /**
     * Creates a builder for a line between two points.
     * @param start Start point.
     * @param end End point.
     * @return A new builder.
     */
    @NotNull
    public static Builder builder(@NotNull Coordinate start, @NotNull Coordinate end) {
        return new Builder(start, end);
    }
    
    /**
     * Builder for {@link StaticMapLine}.
     */
    public static final class Builder {
        private final Coordinate start;
        private final Coordinate end;
        private RGBAColor outlineColor = RGBAColor.red();
        private int outlineSize = 2;
        private RGBAColor fillColor = RGBAColor.rgb(0, 0, 0); // Not used for lines
        
        private Builder(@NotNull Coordinate start, @NotNull Coordinate end) {
            this.start = start;
            this.end = end;
        }
        
        /** @param outlineColor Color of the line. @return This builder. */
        public Builder outlineColor(@NotNull RGBAColor outlineColor) {
            this.outlineColor = outlineColor;
            return this;
        }
        
        /** @param outlineSize Width of the line in pixels. @return This builder. */
        public Builder outlineSize(int outlineSize) {
            this.outlineSize = outlineSize;
            return this;
        }
        
        /** @return A new {@link StaticMapLine} instance. */
        @NotNull
        public StaticMapLine build() {
            return new StaticMapLine(this);
        }
    }
}
