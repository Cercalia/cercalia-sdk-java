package com.cercalia.sdk.model.suggest;

import org.jetbrains.annotations.Nullable;

/**
 * Locality information in a suggestion result.
 * <p>Following SDK conventions, this corresponds to the administrative level of a city/locality.</p>
 */
public final class SuggestCity {
    
    /**
     * Internal Cercalia ID for the locality.
     */
    @Nullable
    private final String code;
    
    /**
     * Name of the locality.
     */
    @Nullable
    private final String name;
    
    /**
     * Additional locality information often displayed in brackets.
     */
    @Nullable
    private final String bracketLocality;
    
    private SuggestCity(Builder builder) {
        this.code = builder.code;
        this.name = builder.name;
        this.bracketLocality = builder.bracketLocality;
    }
    
    /**
     * @return A new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * @return Internal Cercalia ID for the locality.
     */
    @Nullable public String getCode() { return code; }

    /**
     * @return Name of the locality.
     */
    @Nullable public String getName() { return name; }

    /**
     * @return Additional locality information.
     */
    @Nullable public String getBracketLocality() { return bracketLocality; }
    
    /**
     * Builder for {@link SuggestCity}.
     */
    public static final class Builder {
        private String code;
        private String name;
        private String bracketLocality;
        
        /**
         * @param code Locality code.
         * @return This builder.
         */
        public Builder code(String code) { this.code = code; return this; }

        /**
         * @param name Locality name.
         * @return This builder.
         */
        public Builder name(String name) { this.name = name; return this; }

        /**
         * @param bracketLocality Bracketed locality info.
         * @return This builder.
         */
        public Builder bracketLocality(String bracketLocality) { this.bracketLocality = bracketLocality; return this; }
        
        /**
         * @return A new {@link SuggestCity} instance.
         */
        public SuggestCity build() {
            return new SuggestCity(this);
        }
    }
}
