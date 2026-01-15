package com.cercalia.sdk.model.geoment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Result from Geoment service.
 * <p>
 * Following Golden Rules:
 * - Direct mapping from API (no fallbacks)
 * - Code suffix for identifiers
 * - level field for geometry type transparency
 */
public final class GeographicElementResult {
    
    /** WKT representation of the geometry */
    @NotNull
    private final String wkt;
    
    /** Geographic element code (from @id) */
    @NotNull
    private final String code;
    
    /** Geographic element name (from @name) */
    @Nullable
    private final String name;
    
    /** SDK type classification */
    @NotNull
    private final GeographicElementType type;
    
    /** Original geometry type from API (@type) - for transparency */
    @Nullable
    private final String level;
    
    private GeographicElementResult(Builder builder) {
        this.wkt = Objects.requireNonNull(builder.wkt, "wkt cannot be null");
        this.code = Objects.requireNonNull(builder.code, "code cannot be null");
        this.name = builder.name;
        this.type = Objects.requireNonNull(builder.type, "type cannot be null");
        this.level = builder.level;
    }
    
    /**
     * @return WKT representation of the geometry.
     */
    @NotNull
    public String getWkt() {
        return wkt;
    }
    
    /**
     * @return Internal Cercalia ID for the geographic element.
     */
    @NotNull
    public String getCode() {
        return code;
    }
    
    /**
     * @return geographic element name.
     */
    @Nullable
    public String getName() {
        return name;
    }
    
    /**
     * @return SDK type classification.
     */
    @NotNull
    public GeographicElementType getType() {
        return type;
    }
    
    /**
     * @return original geometry type from API (e.g., "municipality", "rd").
     */
    @Nullable
    public String getLevel() {
        return level;
    }
    
    /**
     * @return a new builder for {@link GeographicElementResult}.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public String toString() {
        return "GeographicElementResult{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", level='" + level + '\'' +
                ", wkt='" + (wkt.length() > 50 ? wkt.substring(0, 50) + "..." : wkt) + '\'' +
                '}';
    }
    
    /**
     * Builder for {@link GeographicElementResult}.
     */
    public static final class Builder {
        private String wkt;
        private String code;
        private String name;
        private GeographicElementType type;
        private String level;
        
        private Builder() {}
        
        /**
         * @param wkt WKT representation of the geometry.
         * @return this builder.
         */
        public Builder wkt(@NotNull String wkt) {
            this.wkt = wkt;
            return this;
        }
        
        /**
         * @param code Internal Cercalia ID.
         * @return this builder.
         */
        public Builder code(@NotNull String code) {
            this.code = code;
            return this;
        }
        
        /**
         * @param name geographic element name.
         * @return this builder.
         */
        public Builder name(@Nullable String name) {
            this.name = name;
            return this;
        }
        
        /**
         * @param type SDK type classification.
         * @return this builder.
         */
        public Builder type(@NotNull GeographicElementType type) {
            this.type = type;
            return this;
        }
        
        /**
         * @param level original geometry type from API.
         * @return this builder.
         */
        public Builder level(@Nullable String level) {
            this.level = level;
            return this;
        }
        
        /**
         * @return a new instance of {@link GeographicElementResult}.
         */
        @NotNull
        public GeographicElementResult build() {
            return new GeographicElementResult(this);
        }
    }
}
