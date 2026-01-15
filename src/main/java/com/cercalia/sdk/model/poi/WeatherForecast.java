package com.cercalia.sdk.model.poi;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Represents a weather forecast for a location.
 */
public final class WeatherForecast {
    
    @NotNull
    private final String locationName;
    
    @NotNull
    private final Coordinate coord;
    
    @Nullable
    private final String lastUpdate;
    
    @NotNull
    private final List<WeatherDayForecast> forecasts;
    
    private WeatherForecast(Builder builder) {
        this.locationName = Objects.requireNonNull(builder.locationName, "locationName cannot be null");
        this.coord = Objects.requireNonNull(builder.coord, "coord cannot be null");
        this.lastUpdate = builder.lastUpdate;
        this.forecasts = Objects.requireNonNull(builder.forecasts, "forecasts cannot be null");
    }
    
    /**
     * @return The location name for which the forecast is provided.
     */
    @NotNull
    public String getLocationName() {
        return locationName;
    }
    
    /**
     * @return The geographic coordinates of the location.
     */
    @NotNull
    public Coordinate getCoord() {
        return coord;
    }
    
    /**
     * @return The timestamp of the last forecast update.
     */
    @Nullable
    public String getLastUpdate() {
        return lastUpdate;
    }
    
    /**
     * @return The list of daily forecasts.
     */
    @NotNull
    public List<WeatherDayForecast> getForecasts() {
        return forecasts;
    }
    
    /**
     * @return A new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public String toString() {
        return "WeatherForecast{" +
                "locationName='" + locationName + '\'' +
                ", coord=" + coord +
                ", forecasts=" + forecasts.size() + " days" +
                '}';
    }
    
    /**
     * Builder for {@link WeatherForecast}.
     */
    public static final class Builder {
        private String locationName;
        private Coordinate coord;
        private String lastUpdate;
        private List<WeatherDayForecast> forecasts;
        
        private Builder() {}
        
        /**
         * @param locationName The location name.
         * @return The builder.
         */
        public Builder locationName(@NotNull String locationName) {
            this.locationName = locationName;
            return this;
        }
        
        /**
         * @param coord The geographic coordinates.
         * @return The builder.
         */
        public Builder coord(@NotNull Coordinate coord) {
            this.coord = coord;
            return this;
        }
        
        /**
         * @param lastUpdate The last update timestamp.
         * @return The builder.
         */
        public Builder lastUpdate(@Nullable String lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }
        
        /**
         * @param forecasts The list of daily forecasts.
         * @return The builder.
         */
        public Builder forecasts(@NotNull List<WeatherDayForecast> forecasts) {
            this.forecasts = forecasts;
            return this;
        }
        
        /**
         * @return A new {@link WeatherForecast} instance.
         */
        public WeatherForecast build() {
            return new WeatherForecast(this);
        }
    }
}
