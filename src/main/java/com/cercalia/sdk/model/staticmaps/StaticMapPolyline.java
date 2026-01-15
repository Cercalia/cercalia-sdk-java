package com.cercalia.sdk.model.staticmaps;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a polyline shape to be rendered on a static map.
 */
public final class StaticMapPolyline implements StaticMapShape {
    
    /** Outline color of the polyline. */
    @NotNull
    private final RGBAColor outlineColor;
    
    /** Size of the polyline's outline in pixels. */
    private final int outlineSize;
    
    /** Fill color of the polyline (if closed). */
    @NotNull
    private final RGBAColor fillColor;
    
    /** List of coordinates defining the polyline. */
    @NotNull
    private final List<Coordinate> coordinates;
    
    private StaticMapPolyline(Builder builder) {
        this.outlineColor = builder.outlineColor;
        this.outlineSize = builder.outlineSize;
        this.fillColor = builder.fillColor;
        this.coordinates = new ArrayList<>(builder.coordinates);
    }
    
    @Override
    @NotNull
    public StaticMapShapeType getType() {
        return StaticMapShapeType.POLYLINE;
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
    
    /** @return List of coordinates. */
    @NotNull
    public List<Coordinate> getCoordinates() {
        return new ArrayList<>(coordinates);
    }
    
    @Override
    @NotNull
    public String format() {
        // Format: [outlineColor|outlineSize|fillColor|POLYLINE|Y1,X1|Y2,X2|...|Yn,Xn]
        String coords = coordinates.stream()
                .map(c -> c.getLat() + "," + c.getLng())
                .collect(Collectors.joining("|"));
        return "[" + outlineColor.format() + "|" + outlineSize + "|" + 
               fillColor.format() + "|POLYLINE|" + coords + "]";
    }
    
    /** @return A new builder for {@link StaticMapPolyline}. */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * @param coordinates Initial list of coordinates.
     * @return A new builder.
     */
    @NotNull
    public static Builder builder(@NotNull List<Coordinate> coordinates) {
        return new Builder().coordinates(coordinates);
    }
    
    /**
     * Builder for {@link StaticMapPolyline}.
     */
    public static final class Builder {
        private List<Coordinate> coordinates = new ArrayList<>();
        private RGBAColor outlineColor = RGBAColor.red();
        private int outlineSize = 2;
        private RGBAColor fillColor = RGBAColor.red();
        
        private Builder() {}
        
        /** @param coordinates List of coordinates. @return This builder. */
        public Builder coordinates(@NotNull List<Coordinate> coordinates) {
            this.coordinates = new ArrayList<>(coordinates);
            return this;
        }
        
        /** @param coordinates Array of coordinates. @return This builder. */
        public Builder coordinates(@NotNull Coordinate... coordinates) {
            this.coordinates = Arrays.asList(coordinates);
            return this;
        }
        
        /** @param coordinate Coordinate to add. @return This builder. */
        public Builder addCoordinate(@NotNull Coordinate coordinate) {
            this.coordinates.add(coordinate);
            return this;
        }
        
        /** @param outlineColor Outline color. @return This builder. */
        public Builder outlineColor(@NotNull RGBAColor outlineColor) {
            this.outlineColor = outlineColor;
            return this;
        }
        
        /** @param outlineSize Outline size. @return This builder. */
        public Builder outlineSize(int outlineSize) {
            this.outlineSize = outlineSize;
            return this;
        }
        
        /** @param fillColor Fill color. @return This builder. */
        public Builder fillColor(@NotNull RGBAColor fillColor) {
            this.fillColor = fillColor;
            return this;
        }
        
        /**
         * Builds a {@link StaticMapPolyline}.
         * @return A new instance.
         * @throws IllegalArgumentException if no coordinates are provided.
         */
        @NotNull
        public StaticMapPolyline build() {
            if (coordinates.isEmpty()) {
                throw new IllegalArgumentException("Polyline must have at least one coordinate");
            }
            return new StaticMapPolyline(this);
        }
    }
}
