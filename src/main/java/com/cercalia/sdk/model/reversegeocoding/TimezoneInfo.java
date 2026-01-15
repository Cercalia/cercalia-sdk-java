package com.cercalia.sdk.model.reversegeocoding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Timezone information returned when level='timezone'.
 */
public final class TimezoneInfo {
    
    @NotNull
    private final String id;
    
    @NotNull
    private final String name;
    
    @NotNull
    private final String localDateTime;
    
    @NotNull
    private final String utcDateTime;
    
    private final int utcOffset;
    
    private final int daylightSavingTime;
    
    private TimezoneInfo(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id cannot be null");
        this.name = Objects.requireNonNull(builder.name, "name cannot be null");
        this.localDateTime = Objects.requireNonNull(builder.localDateTime, "localDateTime cannot be null");
        this.utcDateTime = Objects.requireNonNull(builder.utcDateTime, "utcDateTime cannot be null");
        this.utcOffset = builder.utcOffset;
        this.daylightSavingTime = builder.daylightSavingTime;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Returns the timezone ID (e.g., 'Europe/Madrid').
     *
     * @return the timezone ID
     */
    @NotNull
    public String getId() {
        return id;
    }
    
    /**
     * Returns the human-readable timezone name.
     *
     * @return the timezone name
     */
    @NotNull
    public String getName() {
        return name;
    }
    
    /**
     * Returns the local date/time at the coordinate.
     *
     * @return the local datetime
     */
    @NotNull
    public String getLocalDateTime() {
        return localDateTime;
    }
    
    /**
     * Returns the UTC date/time.
     *
     * @return the UTC datetime
     */
    @NotNull
    public String getUtcDateTime() {
        return utcDateTime;
    }
    
    /**
     * Returns the UTC offset in seconds.
     *
     * @return the UTC offset
     */
    public int getUtcOffset() {
        return utcOffset;
    }
    
    /**
     * Returns the daylight saving time offset in seconds.
     *
     * @return the DST offset
     */
    public int getDaylightSavingTime() {
        return daylightSavingTime;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimezoneInfo that = (TimezoneInfo) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(utcDateTime, that.utcDateTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, utcDateTime);
    }
    
    @Override
    public String toString() {
        return "TimezoneInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", utcOffset=" + utcOffset +
                '}';
    }
    
    public static final class Builder {
        private String id = "";
        private String name = "";
        private String localDateTime = "";
        private String utcDateTime = "";
        private int utcOffset;
        private int daylightSavingTime;
        
        private Builder() {}
        
        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder localDateTime(String localDateTime) { this.localDateTime = localDateTime; return this; }
        public Builder utcDateTime(String utcDateTime) { this.utcDateTime = utcDateTime; return this; }
        public Builder utcOffset(int utcOffset) { this.utcOffset = utcOffset; return this; }
        public Builder daylightSavingTime(int daylightSavingTime) { this.daylightSavingTime = daylightSavingTime; return this; }
        
        public TimezoneInfo build() {
            return new TimezoneInfo(this);
        }
    }
}
