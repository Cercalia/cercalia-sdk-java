package com.cercalia.sdk.model.staticmaps;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Result from generating a static map.
 */
public final class StaticMapResult {
    
    /**
     * URL to the generated map image.
     */
    @Nullable
    private final String imageUrl;
    
    /**
     * Local path to the generated map image if saved.
     */
    @Nullable
    private final String imagePath;
    
    /**
     * Width of the generated image.
     */
    @Nullable
    private final Integer width;
    
    /**
     * Height of the generated image.
     */
    @Nullable
    private final Integer height;
    
    /**
     * Image format (e.g., png, jpg).
     */
    @Nullable
    private final String format;
    
    /**
     * Map scale.
     */
    @Nullable
    private final Integer scale;
    
    /**
     * Center coordinate of the map.
     */
    @Nullable
    private final Coordinate center;
    
    /**
     * Map extent.
     */
    @Nullable
    private final StaticMapExtent extent;
    
    /**
     * Map label.
     */
    @Nullable
    private final String label;
    
    /**
     * Raw image bytes if requested.
     */
    @Nullable
    private final byte[] imageData;
    
    private StaticMapResult(Builder builder) {
        this.imageUrl = builder.imageUrl;
        this.imagePath = builder.imagePath;
        this.width = builder.width;
        this.height = builder.height;
        this.format = builder.format;
        this.scale = builder.scale;
        this.center = builder.center;
        this.extent = builder.extent;
        this.label = builder.label;
        this.imageData = builder.imageData;
    }
    
    /** @return URL to the image. */
    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }
    
    /** @return Path to the image file. */
    @Nullable
    public String getImagePath() {
        return imagePath;
    }
    
    /** @return Image width. */
    @Nullable
    public Integer getWidth() {
        return width;
    }
    
    /** @return Image height. */
    @Nullable
    public Integer getHeight() {
        return height;
    }
    
    /** @return Image format. */
    @Nullable
    public String getFormat() {
        return format;
    }
    
    /** @return Map scale. */
    @Nullable
    public Integer getScale() {
        return scale;
    }
    
    /** @return Map center. */
    @Nullable
    public Coordinate getCenter() {
        return center;
    }
    
    /** @return Map extent. */
    @Nullable
    public StaticMapExtent getExtent() {
        return extent;
    }
    
    /** @return Map label. */
    @Nullable
    public String getLabel() {
        return label;
    }
    
    /** @return Raw image data. */
    @Nullable
    public byte[] getImageData() {
        return imageData;
    }
    
    /** @return A new builder for {@link StaticMapResult}. */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public String toString() {
        return "StaticMapResult{" +
                "imageUrl='" + imageUrl + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", format='" + format + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
    
    /**
     * Builder for {@link StaticMapResult}.
     */
    public static final class Builder {
        private String imageUrl;
        private String imagePath;
        private Integer width;
        private Integer height;
        private String format;
        private Integer scale;
        private Coordinate center;
        private StaticMapExtent extent;
        private String label;
        private byte[] imageData;
        
        private Builder() {}
        
        /** @param imageUrl Image URL. @return This builder. */
        public Builder imageUrl(@Nullable String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }
        
        /** @param imagePath Image path. @return This builder. */
        public Builder imagePath(@Nullable String imagePath) {
            this.imagePath = imagePath;
            return this;
        }
        
        /** @param width Width. @return This builder. */
        public Builder width(@Nullable Integer width) {
            this.width = width;
            return this;
        }
        
        /** @param height Height. @return This builder. */
        public Builder height(@Nullable Integer height) {
            this.height = height;
            return this;
        }
        
        /** @param format Format. @return This builder. */
        public Builder format(@Nullable String format) {
            this.format = format;
            return this;
        }
        
        /** @param scale Scale. @return This builder. */
        public Builder scale(@Nullable Integer scale) {
            this.scale = scale;
            return this;
        }
        
        /** @param center Center. @return This builder. */
        public Builder center(@Nullable Coordinate center) {
            this.center = center;
            return this;
        }
        
        /** @param extent Extent. @return This builder. */
        public Builder extent(@Nullable StaticMapExtent extent) {
            this.extent = extent;
            return this;
        }
        
        /** @param label Label. @return This builder. */
        public Builder label(@Nullable String label) {
            this.label = label;
            return this;
        }
        
        /** @param imageData Image data. @return This builder. */
        public Builder imageData(@Nullable byte[] imageData) {
            this.imageData = imageData;
            return this;
        }
        
        /** @return A new {@link StaticMapResult} instance. */
        @NotNull
        public StaticMapResult build() {
            return new StaticMapResult(this);
        }
    }
}
