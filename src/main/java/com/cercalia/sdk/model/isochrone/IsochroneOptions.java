package com.cercalia.sdk.model.isochrone;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Options for isochrone calculation.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * IsochroneOptions options = IsochroneOptions.builder()
 *     .value(15)
 *     .weight(IsochroneWeight.TIME)
 *     .method(IsochroneMethod.CONCAVEHULL)
 *     .build();
 * }</pre>
 */
public final class IsochroneOptions {
    
    /** Value in minutes (for time) or meters (for distance) */
    private final int value;
    
    /** Weight type: 'time' or 'distance'. Default: 'time' */
    @Nullable
    private final IsochroneWeight weight;
    
    /** Method for polygon calculation */
    @Nullable
    private final IsochroneMethod method;
    
    private IsochroneOptions(int value, @Nullable IsochroneWeight weight, @Nullable IsochroneMethod method) {
        this.value = value;
        this.weight = weight;
        this.method = method;
    }
    
    /**
     * @return Value in minutes or meters.
     */
    public int getValue() {
        return value;
    }
    
    /**
     * @return Weight type. Defaults to {@link IsochroneWeight#TIME}.
     */
    @NotNull
    public IsochroneWeight getWeight() {
        return weight != null ? weight : IsochroneWeight.TIME;
    }
    
    /**
     * @return Polygon calculation method. Defaults to {@link IsochroneMethod#CONCAVEHULL}.
     */
    @NotNull
    public IsochroneMethod getMethod() {
        return method != null ? method : IsochroneMethod.CONCAVEHULL;
    }
    
    /**
     * Creates options for time-based isochrone.
     *
     * @param minutes The time in minutes.
     * @return A new {@link IsochroneOptions} instance.
     */
    @NotNull
    public static IsochroneOptions time(int minutes) {
        return new IsochroneOptions(minutes, IsochroneWeight.TIME, null);
    }
    
    /**
     * Creates options for distance-based isochrone.
     *
     * @param meters The distance in meters.
     * @return A new {@link IsochroneOptions} instance.
     */
    @NotNull
    public static IsochroneOptions distance(int meters) {
        return new IsochroneOptions(meters, IsochroneWeight.DISTANCE, null);
    }
    
    /**
     * Creates new IsochroneOptions builder.
     *
     * @return A new builder instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for {@link IsochroneOptions}.
     */
    public static final class Builder {
        private int value;
        private IsochroneWeight weight;
        private IsochroneMethod method;
        
        private Builder() {}
        
        /**
         * @param value Value in minutes or meters.
         * @return The builder.
         */
        public Builder value(int value) {
            this.value = value;
            return this;
        }
        
        /**
         * @param weight Weight type.
         * @return The builder.
         */
        public Builder weight(@Nullable IsochroneWeight weight) {
            this.weight = weight;
            return this;
        }
        
        /**
         * @param method Polygon calculation method.
         * @return The builder.
         */
        public Builder method(@Nullable IsochroneMethod method) {
            this.method = method;
            return this;
        }
        
        /**
         * @return A new instance of {@link IsochroneOptions}.
         */
        @NotNull
        public IsochroneOptions build() {
            return new IsochroneOptions(value, weight, method);
        }
    }
}
