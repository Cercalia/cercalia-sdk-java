package com.cercalia.sdk.model.poi;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents weather forecast data for a single day.
 */
public final class WeatherDayForecast {
    
    private final String date;
    
    @Nullable
    private final Double precipitationChance0012;
    
    @Nullable
    private final Double precipitationChance1224;
    
    @Nullable
    private final Double snowLevel0012;
    
    @Nullable
    private final Double snowLevel1224;
    
    @Nullable
    private final Double skyConditions0012;
    
    @Nullable
    private final Double skyConditions1224;
    
    @Nullable
    private final Double windSpeed0012;
    
    @Nullable
    private final Double windSpeed1224;
    
    @Nullable
    private final Double temperatureMax;
    
    @Nullable
    private final Double temperatureMin;
    
    private WeatherDayForecast(Builder builder) {
        this.date = Objects.requireNonNull(builder.date, "date cannot be null");
        this.precipitationChance0012 = builder.precipitationChance0012;
        this.precipitationChance1224 = builder.precipitationChance1224;
        this.snowLevel0012 = builder.snowLevel0012;
        this.snowLevel1224 = builder.snowLevel1224;
        this.skyConditions0012 = builder.skyConditions0012;
        this.skyConditions1224 = builder.skyConditions1224;
        this.windSpeed0012 = builder.windSpeed0012;
        this.windSpeed1224 = builder.windSpeed1224;
        this.temperatureMax = builder.temperatureMax;
        this.temperatureMin = builder.temperatureMin;
    }
    
    /**
     * @return The date of the forecast in {@code YYYYMMDD} format.
     */
    public String getDate() {
        return date;
    }
    
    /**
     * @return The probability of precipitation (0-1) between 00:00 and 12:00.
     */
    @Nullable
    public Double getPrecipitationChance0012() {
        return precipitationChance0012;
    }
    
    /**
     * @return The probability of precipitation (0-1) between 12:00 and 24:00.
     */
    @Nullable
    public Double getPrecipitationChance1224() {
        return precipitationChance1224;
    }
    
    /**
     * @return The estimated snow level in meters between 00:00 and 12:00.
     */
    @Nullable
    public Double getSnowLevel0012() {
        return snowLevel0012;
    }
    
    /**
     * @return The estimated snow level in meters between 12:00 and 24:00.
     */
    @Nullable
    public Double getSnowLevel1224() {
        return snowLevel1224;
    }
    
    /**
     * @return Sky condition code between 00:00 and 12:00.
     */
    @Nullable
    public Double getSkyConditions0012() {
        return skyConditions0012;
    }
    
    /**
     * @return Sky condition code between 12:00 and 24:00.
     */
    @Nullable
    public Double getSkyConditions1224() {
        return skyConditions1224;
    }
    
    /**
     * @return Average wind speed in km/h between 00:00 and 12:00.
     */
    @Nullable
    public Double getWindSpeed0012() {
        return windSpeed0012;
    }
    
    /**
     * @return Average wind speed in km/h between 12:00 and 24:00.
     */
    @Nullable
    public Double getWindSpeed1224() {
        return windSpeed1224;
    }
    
    /**
     * @return Maximum temperature in Celsius.
     */
    @Nullable
    public Double getTemperatureMax() {
        return temperatureMax;
    }
    
    /**
     * @return Minimum temperature in Celsius.
     */
    @Nullable
    public Double getTemperatureMin() {
        return temperatureMin;
    }
    
    /**
     * @return A new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public String toString() {
        return "WeatherDayForecast{" +
                "date='" + date + '\'' +
                ", tempMax=" + temperatureMax +
                ", tempMin=" + temperatureMin +
                '}';
    }
    
    /**
     * Builder for {@link WeatherDayForecast}.
     */
    public static final class Builder {
        private String date;
        private Double precipitationChance0012;
        private Double precipitationChance1224;
        private Double snowLevel0012;
        private Double snowLevel1224;
        private Double skyConditions0012;
        private Double skyConditions1224;
        private Double windSpeed0012;
        private Double windSpeed1224;
        private Double temperatureMax;
        private Double temperatureMin;
        
        private Builder() {}
        
        /**
         * @param date Date in {@code YYYYMMDD} format.
         * @return The builder.
         */
        public Builder date(String date) {
            this.date = date;
            return this;
        }
        
        /**
         * @param value Precipitation probability (0-1).
         * @return The builder.
         */
        public Builder precipitationChance0012(@Nullable Double value) {
            this.precipitationChance0012 = value;
            return this;
        }
        
        /**
         * @param value Precipitation probability (0-1).
         * @return The builder.
         */
        public Builder precipitationChance1224(@Nullable Double value) {
            this.precipitationChance1224 = value;
            return this;
        }
        
        /**
         * @param value Snow level in meters.
         * @return The builder.
         */
        public Builder snowLevel0012(@Nullable Double value) {
            this.snowLevel0012 = value;
            return this;
        }
        
        /**
         * @param value Snow level in meters.
         * @return The builder.
         */
        public Builder snowLevel1224(@Nullable Double value) {
            this.snowLevel1224 = value;
            return this;
        }
        
        /**
         * @param value Sky condition code.
         * @return The builder.
         */
        public Builder skyConditions0012(@Nullable Double value) {
            this.skyConditions0012 = value;
            return this;
        }
        
        /**
         * @param value Sky condition code.
         * @return The builder.
         */
        public Builder skyConditions1224(@Nullable Double value) {
            this.skyConditions1224 = value;
            return this;
        }
        
        /**
         * @param value Wind speed in km/h.
         * @return The builder.
         */
        public Builder windSpeed0012(@Nullable Double value) {
            this.windSpeed0012 = value;
            return this;
        }
        
        /**
         * @param value Wind speed in km/h.
         * @return The builder.
         */
        public Builder windSpeed1224(@Nullable Double value) {
            this.windSpeed1224 = value;
            return this;
        }
        
        /**
         * @param value Maximum temperature in Celsius.
         * @return The builder.
         */
        public Builder temperatureMax(@Nullable Double value) {
            this.temperatureMax = value;
            return this;
        }
        
        /**
         * @param value Minimum temperature in Celsius.
         * @return The builder.
         */
        public Builder temperatureMin(@Nullable Double value) {
            this.temperatureMin = value;
            return this;
        }
        
        /**
         * @return A new {@link WeatherDayForecast} instance.
         */
        public WeatherDayForecast build() {
            return new WeatherDayForecast(this);
        }
    }
}
