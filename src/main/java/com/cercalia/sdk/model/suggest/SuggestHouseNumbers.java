package com.cercalia.sdk.model.suggest;

import org.jetbrains.annotations.Nullable;

/**
 * House number availability information.
 */
public final class SuggestHouseNumbers {
    
    private final boolean available;
    
    @Nullable
    private final Integer min;
    
    @Nullable
    private final Integer max;
    
    @Nullable
    private final Integer current;
    
    @Nullable
    private final Integer adjusted;
    
    @Nullable
    private final Boolean isEnglishFormat;
    
    @Nullable
    private final String hint;
    
    private SuggestHouseNumbers(Builder builder) {
        this.available = builder.available;
        this.min = builder.min;
        this.max = builder.max;
        this.current = builder.current;
        this.adjusted = builder.adjusted;
        this.isEnglishFormat = builder.isEnglishFormat;
        this.hint = builder.hint;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public boolean isAvailable() { return available; }
    @Nullable public Integer getMin() { return min; }
    @Nullable public Integer getMax() { return max; }
    @Nullable public Integer getCurrent() { return current; }
    @Nullable public Integer getAdjusted() { return adjusted; }
    @Nullable public Boolean getIsEnglishFormat() { return isEnglishFormat; }
    @Nullable public String getHint() { return hint; }
    
    public static final class Builder {
        private boolean available;
        private Integer min;
        private Integer max;
        private Integer current;
        private Integer adjusted;
        private Boolean isEnglishFormat;
        private String hint;
        
        public Builder available(boolean available) { this.available = available; return this; }
        public Builder min(Integer min) { this.min = min; return this; }
        public Builder max(Integer max) { this.max = max; return this; }
        public Builder current(Integer current) { this.current = current; return this; }
        public Builder adjusted(Integer adjusted) { this.adjusted = adjusted; return this; }
        public Builder isEnglishFormat(Boolean isEnglishFormat) { this.isEnglishFormat = isEnglishFormat; return this; }
        public Builder hint(String hint) { this.hint = hint; return this; }
        
        public SuggestHouseNumbers build() {
            return new SuggestHouseNumbers(this);
        }
    }
}
