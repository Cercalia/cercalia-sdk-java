package com.cercalia.sdk.model.suggest;

import org.jetbrains.annotations.Nullable;

/**
 * Street information in a suggestion result.
 */
public final class SuggestStreet {
    
    /**
     * Internal Cercalia ID for the street.
     */
    @Nullable
    private final String code;
    
    /**
     * Name of the street.
     */
    @Nullable
    private final String name;
    
    /**
     * Description or additional information about the street.
     */
    @Nullable
    private final String description;
    
    /**
     * Type of the street (e.g., Street, Avenue).
     */
    @Nullable
    private final String type;
    
    /**
     * Article associated with the street name.
     */
    @Nullable
    private final String article;
    
    private SuggestStreet(Builder builder) {
        this.code = builder.code;
        this.name = builder.name;
        this.description = builder.description;
        this.type = builder.type;
        this.article = builder.article;
    }
    
    /**
     * @return A new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * @return Internal Cercalia ID for the street.
     */
    @Nullable public String getCode() { return code; }

    /**
     * @return Name of the street.
     */
    @Nullable public String getName() { return name; }

    /**
     * @return Description or additional information.
     */
    @Nullable public String getDescription() { return description; }

    /**
     * @return Type of the street.
     */
    @Nullable public String getType() { return type; }

    /**
     * @return Article associated with the street name.
     */
    @Nullable public String getArticle() { return article; }
    
    /**
     * Builder for {@link SuggestStreet}.
     */
    public static final class Builder {
        private String code;
        private String name;
        private String description;
        private String type;
        private String article;
        
        /**
         * @param code Street code.
         * @return This builder.
         */
        public Builder code(String code) { this.code = code; return this; }

        /**
         * @param name Street name.
         * @return This builder.
         */
        public Builder name(String name) { this.name = name; return this; }

        /**
         * @param description Street description.
         * @return This builder.
         */
        public Builder description(String description) { this.description = description; return this; }

        /**
         * @param type Street type.
         * @return This builder.
         */
        public Builder type(String type) { this.type = type; return this; }

        /**
         * @param article Street article.
         * @return This builder.
         */
        public Builder article(String article) { this.article = article; return this; }
        
        /**
         * @return A new {@link SuggestStreet} instance.
         */
        public SuggestStreet build() {
            return new SuggestStreet(this);
        }
    }
}
