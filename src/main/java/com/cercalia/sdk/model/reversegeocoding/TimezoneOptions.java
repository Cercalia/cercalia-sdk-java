package com.cercalia.sdk.model.reversegeocoding;

import org.jetbrains.annotations.Nullable;

/**
 * Options for timezone requests.
 */
public final class TimezoneOptions {
    
    @Nullable
    private final String dateTime;
    
    private TimezoneOptions(Builder builder) {
        this.dateTime = builder.dateTime;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Returns the datetime to check (ISO 8601 format, optional).
     * If provided, returns local time at that moment.
     *
     * @return the datetime
     */
    @Nullable
    public String getDateTime() {
        return dateTime;
    }
    
    public static final class Builder {
        private String dateTime;
        
        private Builder() {}
        
        /**
         * Sets the datetime to check (ISO 8601 format).
         *
         * @param dateTime the datetime
         * @return this builder
         */
        public Builder dateTime(String dateTime) {
            this.dateTime = dateTime;
            return this;
        }
        
        public TimezoneOptions build() {
            return new TimezoneOptions(this);
        }
    }
}
