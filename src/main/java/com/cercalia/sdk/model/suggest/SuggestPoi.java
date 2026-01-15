package com.cercalia.sdk.model.suggest;

import org.jetbrains.annotations.Nullable;

/**
 * POI-specific data in a suggestion.
 */
public final class SuggestPoi {
    
    /**
     * Internal Cercalia ID for the POI.
     */
    @Nullable
    private final String code;
    
    /**
     * Name of the POI.
     */
    @Nullable
    private final String name;
    
    /**
     * Internal Cercalia ID for the POI category.
     */
    @Nullable
    private final String categoryCode;
    
    private SuggestPoi(Builder builder) {
        this.code = builder.code;
        this.name = builder.name;
        this.categoryCode = builder.categoryCode;
    }
    
    /**
     * @return A new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * @return Internal Cercalia ID for the POI.
     */
    @Nullable public String getCode() { return code; }

    /**
     * @return Name of the POI.
     */
    @Nullable public String getName() { return name; }

    /**
     * @return Internal Cercalia ID for the POI category.
     */
    @Nullable public String getCategoryCode() { return categoryCode; }
    
    /**
     * Builder for {@link SuggestPoi}.
     */
    public static final class Builder {
        private String code;
        private String name;
        private String categoryCode;
        
        /**
         * @param code POI code.
         * @return This builder.
         */
        public Builder code(String code) { this.code = code; return this; }

        /**
         * @param name POI name.
         * @return This builder.
         */
        public Builder name(String name) { this.name = name; return this; }

        /**
         * @param categoryCode POI category code.
         * @return This builder.
         */
        public Builder categoryCode(String categoryCode) { this.categoryCode = categoryCode; return this; }
        
        /**
         * @return A new {@link SuggestPoi} instance.
         */
        public SuggestPoi build() {
            return new SuggestPoi(this);
        }
    }
}
