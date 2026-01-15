package com.cercalia.sdk.model.staticmaps;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a text label to be rendered on a static map.
 */
public final class StaticMapLabel implements StaticMapShape {
    
    /** Text color of the label. */
    @NotNull
    private final RGBAColor outlineColor;
    
    /** Font size of the label text. */
    private final int outlineSize;
    
    /** Background color of the label. */
    @NotNull
    private final RGBAColor fillColor;
    
    /** Coordinate where the label will be placed. */
    @NotNull
    private final Coordinate center;
    
    /** Text content of the label. */
    @NotNull
    private final String text;
    
    private StaticMapLabel(Builder builder) {
        this.outlineColor = builder.outlineColor;
        this.outlineSize = builder.outlineSize;
        this.fillColor = builder.fillColor;
        this.center = builder.center;
        this.text = builder.text;
    }
    
    @Override
    @NotNull
    public StaticMapShapeType getType() {
        return StaticMapShapeType.LABEL;
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
    
    /** @return Placement coordinate. */
    @NotNull
    public Coordinate getCenter() {
        return center;
    }
    
    /** @return Label text. */
    @NotNull
    public String getText() {
        return text;
    }
    
    @Override
    @NotNull
    public String format() {
        // Format: [outlineColor|outlineSize|fillColor|LABEL|Y,X|text]
        return "[" + outlineColor.format() + "|" + outlineSize + "|" + 
               fillColor.format() + "|LABEL|" + 
               center.getLat() + "," + center.getLng() + "|" + text + "]";
    }
    
    /**
     * Creates a builder for a label at a coordinate with text.
     * @param center Placement coordinate.
     * @param text Content of the label.
     * @return A new builder.
     */
    @NotNull
    public static Builder builder(@NotNull Coordinate center, @NotNull String text) {
        return new Builder(center, text);
    }
    
    /**
     * Builder for {@link StaticMapLabel}.
     */
    public static final class Builder {
        private final Coordinate center;
        private final String text;
        private RGBAColor outlineColor = RGBAColor.rgb(0, 0, 0);
        private int outlineSize = 1;
        private RGBAColor fillColor = RGBAColor.rgb(255, 255, 255);
        
        private Builder(@NotNull Coordinate center, @NotNull String text) {
            this.center = center;
            this.text = text;
        }
        
        /** @param outlineColor Color of the text. @return This builder. */
        public Builder outlineColor(@NotNull RGBAColor outlineColor) {
            this.outlineColor = outlineColor;
            return this;
        }
        
        /** @param outlineSize Font size of the text. @return This builder. */
        public Builder outlineSize(int outlineSize) {
            this.outlineSize = outlineSize;
            return this;
        }
        
        /** @param fillColor Background color of the label. @return This builder. */
        public Builder fillColor(@NotNull RGBAColor fillColor) {
            this.fillColor = fillColor;
            return this;
        }
        
        /** @return A new {@link StaticMapLabel} instance. */
        @NotNull
        public StaticMapLabel build() {
            return new StaticMapLabel(this);
        }
    }
}
