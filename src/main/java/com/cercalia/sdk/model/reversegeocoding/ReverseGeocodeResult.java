package com.cercalia.sdk.model.reversegeocoding;

import com.cercalia.sdk.model.geocoding.GeocodingCandidate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Extended reverse geocoding result with all possible data.
 * 
 * <p>A reverse geocoding result maps a geographic coordinate to its nearest
 * administrative or geographic feature, such as an address, a road milestone,
 * or a timezone.</p>
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * ReverseGeocodeResult result = response.getResults().get(0);
 * System.out.println("Address: " + result.getGe().getLabel());
 * if (result.getDistance() != null) {
 *     System.out.println("Distance to feature: " + result.getDistance() + "m");
 * }
 * }</pre>
 */
public final class ReverseGeocodeResult {
    
    /**
     * Geographic information as a GeocodingCandidate.
     */
    @NotNull
    private final GeocodingCandidate ge;
    
    /**
     * Distance from input coordinate to the feature in meters.
     */
    @Nullable
    private final Double distance;
    
    /**
     * Milestone (KM) if available (for roads).
     */
    @Nullable
    private final String km;
    
    /**
     * Road direction (A=ascending/D=descending).
     */
    @Nullable
    private final String direction;
    
    /**
     * Maximum speed limit on the road in km/h.
     */
    @Nullable
    private final Double maxSpeed;
    
    /**
     * Timezone information (available when level is {@code ReverseGeocodeLevel.TIMEZONE}).
     */
    @Nullable
    private final TimezoneInfo timezone;
    
    /**
     * Census section ID (Spain only, when category is {@code d00seccen}).
     */
    @Nullable
    private final String censusId;
    
    /**
     * SIGPAC agricultural parcel info (Spain only, when category is {@code d00sigpac}).
     */
    @Nullable
    private final SigpacInfo sigpac;
    
    private ReverseGeocodeResult(Builder builder) {
        this.ge = Objects.requireNonNull(builder.ge, "ge cannot be null");
        this.distance = builder.distance;
        this.km = builder.km;
        this.direction = builder.direction;
        this.maxSpeed = builder.maxSpeed;
        this.timezone = builder.timezone;
        this.censusId = builder.censusId;
        this.sigpac = builder.sigpac;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * @return The geographic information.
     */
    @NotNull
    public GeocodingCandidate getGe() {
        return ge;
    }
    
    /**
     * @return The distance from input coordinate to the feature in meters.
     */
    @Nullable
    public Double getDistance() {
        return distance;
    }
    
    /**
     * @return The milestone (KM) if available (for roads).
     */
    @Nullable
    public String getKm() {
        return km;
    }
    
    /**
     * @return The road direction (A=ascending/D=descending).
     */
    @Nullable
    public String getDirection() {
        return direction;
    }
    
    /**
     * @return The maximum speed limit on the road in km/h.
     */
    @Nullable
    public Double getMaxSpeed() {
        return maxSpeed;
    }
    
    /**
     * @return The timezone information.
     */
    @Nullable
    public TimezoneInfo getTimezone() {
        return timezone;
    }
    
    /**
     * @return The census section ID (Spain only).
     */
    @Nullable
    public String getCensusId() {
        return censusId;
    }
    
    /**
     * @return The SIGPAC info (Spain only).
     */
    @Nullable
    public SigpacInfo getSigpac() {
        return sigpac;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReverseGeocodeResult that = (ReverseGeocodeResult) o;
        return Objects.equals(ge, that.ge);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(ge);
    }
    
    @Override
    public String toString() {
        return "ReverseGeocodeResult{" +
                "ge=" + ge +
                ", distance=" + distance +
                '}';
    }
    
    /**
     * Builder for {@link ReverseGeocodeResult}.
     */
    public static final class Builder {
        private GeocodingCandidate ge;
        private Double distance;
        private String km;
        private String direction;
        private Double maxSpeed;
        private TimezoneInfo timezone;
        private String censusId;
        private SigpacInfo sigpac;
        
        private Builder() {}
        
        /**
         * @param ge Geographic information.
         * @return This builder.
         */
        public Builder ge(GeocodingCandidate ge) { this.ge = ge; return this; }

        /**
         * @param distance Distance in meters.
         * @return This builder.
         */
        public Builder distance(Double distance) { this.distance = distance; return this; }

        /**
         * @param km Milestone (KM).
         * @return This builder.
         */
        public Builder km(String km) { this.km = km; return this; }

        /**
         * @param direction Road direction.
         * @return This builder.
         */
        public Builder direction(String direction) { this.direction = direction; return this; }

        /**
         * @param maxSpeed Maximum speed limit.
         * @return This builder.
         */
        public Builder maxSpeed(Double maxSpeed) { this.maxSpeed = maxSpeed; return this; }

        /**
         * @param timezone Timezone info.
         * @return This builder.
         */
        public Builder timezone(TimezoneInfo timezone) { this.timezone = timezone; return this; }

        /**
         * @param censusId Census section ID.
         * @return This builder.
         */
        public Builder censusId(String censusId) { this.censusId = censusId; return this; }

        /**
         * @param sigpac SIGPAC info.
         * @return This builder.
         */
        public Builder sigpac(SigpacInfo sigpac) { this.sigpac = sigpac; return this; }
        
        /**
         * Builds a new {@link ReverseGeocodeResult} instance.
         * @return The new instance.
         */
        public ReverseGeocodeResult build() {
            return new ReverseGeocodeResult(this);
        }
    }
}
