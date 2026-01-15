package com.cercalia.sdk.model.routing;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Options for route calculation.
 */
public final class RoutingOptions {
    
    @Nullable private final VehicleType vehicleType;
    @Nullable private final RouteWeight weight;
    @Nullable private final Boolean avoidTolls;
    @Nullable private final Boolean report;
    @Nullable private final RouteNetwork net;
    @Nullable private final String departureTime;
    @Nullable private final Integer alternatives;
    @Nullable private final String direction;
    @Nullable private final Boolean reorder;
    @Nullable private final String startWindow;
    @Nullable private final String endWindow;
    @Nullable private final Boolean blockRealtime;
    @Nullable private final Boolean avoidRealtime;
    @Nullable private final Boolean blockFerries;
    @Nullable private final Boolean avoidFerries;
    @Nullable private final List<Coordinate> waypoints;
    
    // Truck specific options
    @Nullable private final Integer truckWeight;
    @Nullable private final Integer truckAxleWeight;
    @Nullable private final Integer truckHeight;
    @Nullable private final Integer truckWidth;
    @Nullable private final Integer truckLength;
    @Nullable private final Integer truckMaxVelocity;
    
    // Truck restriction handling
    @Nullable private final Boolean blockTruckWeight;
    @Nullable private final Boolean avoidTruckWeight;
    @Nullable private final Boolean blockTruckAxleWeight;
    @Nullable private final Boolean avoidTruckAxleWeight;
    @Nullable private final Boolean blockTruckHeight;
    @Nullable private final Boolean avoidTruckHeight;
    @Nullable private final Boolean blockTruckLength;
    @Nullable private final Boolean avoidTruckLength;
    @Nullable private final Boolean blockTruckWidth;
    @Nullable private final Boolean avoidTruckWidth;
    
    private RoutingOptions(Builder builder) {
        this.vehicleType = builder.vehicleType;
        this.weight = builder.weight;
        this.avoidTolls = builder.avoidTolls;
        this.report = builder.report;
        this.net = builder.net;
        this.departureTime = builder.departureTime;
        this.alternatives = builder.alternatives;
        this.direction = builder.direction;
        this.reorder = builder.reorder;
        this.startWindow = builder.startWindow;
        this.endWindow = builder.endWindow;
        this.blockRealtime = builder.blockRealtime;
        this.avoidRealtime = builder.avoidRealtime;
        this.blockFerries = builder.blockFerries;
        this.avoidFerries = builder.avoidFerries;
        this.waypoints = builder.waypoints;
        this.truckWeight = builder.truckWeight;
        this.truckAxleWeight = builder.truckAxleWeight;
        this.truckHeight = builder.truckHeight;
        this.truckWidth = builder.truckWidth;
        this.truckLength = builder.truckLength;
        this.truckMaxVelocity = builder.truckMaxVelocity;
        this.blockTruckWeight = builder.blockTruckWeight;
        this.avoidTruckWeight = builder.avoidTruckWeight;
        this.blockTruckAxleWeight = builder.blockTruckAxleWeight;
        this.avoidTruckAxleWeight = builder.avoidTruckAxleWeight;
        this.blockTruckHeight = builder.blockTruckHeight;
        this.avoidTruckHeight = builder.avoidTruckHeight;
        this.blockTruckLength = builder.blockTruckLength;
        this.avoidTruckLength = builder.avoidTruckLength;
        this.blockTruckWidth = builder.blockTruckWidth;
        this.avoidTruckWidth = builder.avoidTruckWidth;
    }
    
    /**
     * @return A new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * @return The vehicle type for routing.
     */
    @Nullable public VehicleType getVehicleType() { return vehicleType; }

    /**
     * @return The optimization weight criteria.
     */
    @Nullable public RouteWeight getWeight() { return weight; }

    /**
     * @return Whether to avoid toll roads.
     */
    @Nullable public Boolean getAvoidTolls() { return avoidTolls; }

    /**
     * @return Whether to return a detailed report.
     */
    @Nullable public Boolean getReport() { return report; }

    /**
     * @return The road network to use.
     */
    @Nullable public RouteNetwork getNet() { return net; }

    /**
     * @return The departure time in {@code YYYYMMDDHHmm} format.
     */
    @Nullable public String getDepartureTime() { return departureTime; }

    /**
     * @return Number of alternative routes requested.
     */
    @Nullable public Integer getAlternatives() { return alternatives; }

    /**
     * @return The direction of the route ({@code forward} or {@code backward}).
     */
    @Nullable public String getDirection() { return direction; }

    /**
     * @return Whether to reorder intermediate waypoints for optimization.
     */
    @Nullable public Boolean getReorder() { return reorder; }

    /**
     * @return The start of the time window for arrival/departure.
     */
    @Nullable public String getStartWindow() { return startWindow; }

    /**
     * @return The end of the time window for arrival/departure.
     */
    @Nullable public String getEndWindow() { return endWindow; }

    /**
     * @return Whether to block routes with real-time traffic incidents.
     */
    @Nullable public Boolean getBlockRealtime() { return blockRealtime; }

    /**
     * @return Whether to avoid routes with real-time traffic incidents.
     */
    @Nullable public Boolean getAvoidRealtime() { return avoidRealtime; }

    /**
     * @return Whether to block routes that include ferries.
     */
    @Nullable public Boolean getBlockFerries() { return blockFerries; }

    /**
     * @return Whether to avoid routes that include ferries.
     */
    @Nullable public Boolean getAvoidFerries() { return avoidFerries; }

    /**
     * @return The list of intermediate waypoints.
     */
    @Nullable public List<Coordinate> getWaypoints() { return waypoints; }

    /**
     * @return The truck weight in kilograms.
     */
    @Nullable public Integer getTruckWeight() { return truckWeight; }

    /**
     * @return The truck axle weight in kilograms.
     */
    @Nullable public Integer getTruckAxleWeight() { return truckAxleWeight; }

    /**
     * @return The truck height in centimeters.
     */
    @Nullable public Integer getTruckHeight() { return truckHeight; }

    /**
     * @return The truck width in centimeters.
     */
    @Nullable public Integer getTruckWidth() { return truckWidth; }

    /**
     * @return The truck length in centimeters.
     */
    @Nullable public Integer getTruckLength() { return truckLength; }

    /**
     * @return The maximum velocity for the truck in km/h.
     */
    @Nullable public Integer getTruckMaxVelocity() { return truckMaxVelocity; }

    /**
     * @return Whether to block roads exceeding truck weight.
     */
    @Nullable public Boolean getBlockTruckWeight() { return blockTruckWeight; }

    /**
     * @return Whether to avoid roads exceeding truck weight.
     */
    @Nullable public Boolean getAvoidTruckWeight() { return avoidTruckWeight; }

    /**
     * @return Whether to block roads exceeding truck axle weight.
     */
    @Nullable public Boolean getBlockTruckAxleWeight() { return blockTruckAxleWeight; }

    /**
     * @return Whether to avoid roads exceeding truck axle weight.
     */
    @Nullable public Boolean getAvoidTruckAxleWeight() { return avoidTruckAxleWeight; }

    /**
     * @return Whether to block roads exceeding truck height.
     */
    @Nullable public Boolean getBlockTruckHeight() { return blockTruckHeight; }

    /**
     * @return Whether to avoid roads exceeding truck height.
     */
    @Nullable public Boolean getAvoidTruckHeight() { return avoidTruckHeight; }

    /**
     * @return Whether to block roads exceeding truck length.
     */
    @Nullable public Boolean getBlockTruckLength() { return blockTruckLength; }

    /**
     * @return Whether to avoid roads exceeding truck length.
     */
    @Nullable public Boolean getAvoidTruckLength() { return avoidTruckLength; }

    /**
     * @return Whether to block roads exceeding truck width.
     */
    @Nullable public Boolean getBlockTruckWidth() { return blockTruckWidth; }

    /**
     * @return Whether to avoid roads exceeding truck width.
     */
    @Nullable public Boolean getAvoidTruckWidth() { return avoidTruckWidth; }
    
    /**
     * Builder for {@link RoutingOptions}.
     */
    public static final class Builder {
        private VehicleType vehicleType;
        private RouteWeight weight;
        private Boolean avoidTolls;
        private Boolean report;
        private RouteNetwork net;
        private String departureTime;
        private Integer alternatives;
        private String direction;
        private Boolean reorder;
        private String startWindow;
        private String endWindow;
        private Boolean blockRealtime;
        private Boolean avoidRealtime;
        private Boolean blockFerries;
        private Boolean avoidFerries;
        private List<Coordinate> waypoints;
        private Integer truckWeight;
        private Integer truckAxleWeight;
        private Integer truckHeight;
        private Integer truckWidth;
        private Integer truckLength;
        private Integer truckMaxVelocity;
        private Boolean blockTruckWeight;
        private Boolean avoidTruckWeight;
        private Boolean blockTruckAxleWeight;
        private Boolean avoidTruckAxleWeight;
        private Boolean blockTruckHeight;
        private Boolean avoidTruckHeight;
        private Boolean blockTruckLength;
        private Boolean avoidTruckLength;
        private Boolean blockTruckWidth;
        private Boolean avoidTruckWidth;
        
        /**
         * @param vehicleType The vehicle type for routing.
         * @return The builder.
         */
        public Builder vehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; return this; }

        /**
         * @param weight The optimization weight criteria.
         * @return The builder.
         */
        public Builder weight(RouteWeight weight) { this.weight = weight; return this; }

        /**
         * @param avoidTolls Whether to avoid toll roads.
         * @return The builder.
         */
        public Builder avoidTolls(Boolean avoidTolls) { this.avoidTolls = avoidTolls; return this; }

        /**
         * @param report Whether to return a detailed report.
         * @return The builder.
         */
        public Builder report(Boolean report) { this.report = report; return this; }

        /**
         * @param net The road network to use.
         * @return The builder.
         */
        public Builder net(RouteNetwork net) { this.net = net; return this; }

        /**
         * @param departureTime Departure time in {@code YYYYMMDDHHmm} format.
         * @return The builder.
         */
        public Builder departureTime(String departureTime) { this.departureTime = departureTime; return this; }

        /**
         * @param alternatives Number of alternative routes.
         * @return The builder.
         */
        public Builder alternatives(Integer alternatives) { this.alternatives = alternatives; return this; }

        /**
         * @param direction Route direction ({@code forward} or {@code backward}).
         * @return The builder.
         */
        public Builder direction(String direction) { this.direction = direction; return this; }

        /**
         * @param reorder Whether to reorder intermediate waypoints.
         * @return The builder.
         */
        public Builder reorder(Boolean reorder) { this.reorder = reorder; return this; }

        /**
         * @param startWindow Start of the time window.
         * @return The builder.
         */
        public Builder startWindow(String startWindow) { this.startWindow = startWindow; return this; }

        /**
         * @param endWindow End of the time window.
         * @return The builder.
         */
        public Builder endWindow(String endWindow) { this.endWindow = endWindow; return this; }

        /**
         * @param blockRealtime Block roads with traffic incidents.
         * @return The builder.
         */
        public Builder blockRealtime(Boolean blockRealtime) { this.blockRealtime = blockRealtime; return this; }

        /**
         * @param avoidRealtime Avoid roads with traffic incidents.
         * @return The builder.
         */
        public Builder avoidRealtime(Boolean avoidRealtime) { this.avoidRealtime = avoidRealtime; return this; }

        /**
         * @param blockFerries Block routes with ferries.
         * @return The builder.
         */
        public Builder blockFerries(Boolean blockFerries) { this.blockFerries = blockFerries; return this; }

        /**
         * @param avoidFerries Avoid routes with ferries.
         * @return The builder.
         */
        public Builder avoidFerries(Boolean avoidFerries) { this.avoidFerries = avoidFerries; return this; }

        /**
         * @param waypoints Intermediate waypoints.
         * @return The builder.
         */
        public Builder waypoints(List<Coordinate> waypoints) { this.waypoints = waypoints; return this; }

        /**
         * @param truckWeight Truck weight in kg.
         * @return The builder.
         */
        public Builder truckWeight(Integer truckWeight) { this.truckWeight = truckWeight; return this; }

        /**
         * @param truckAxleWeight Truck axle weight in kg.
         * @return The builder.
         */
        public Builder truckAxleWeight(Integer truckAxleWeight) { this.truckAxleWeight = truckAxleWeight; return this; }

        /**
         * @param truckHeight Truck height in cm.
         * @return The builder.
         */
        public Builder truckHeight(Integer truckHeight) { this.truckHeight = truckHeight; return this; }

        /**
         * @param truckWidth Truck width in cm.
         * @return The builder.
         */
        public Builder truckWidth(Integer truckWidth) { this.truckWidth = truckWidth; return this; }

        /**
         * @param truckLength Truck length in cm.
         * @return The builder.
         */
        public Builder truckLength(Integer truckLength) { this.truckLength = truckLength; return this; }

        /**
         * @param truckMaxVelocity Maximum velocity in km/h.
         * @return The builder.
         */
        public Builder truckMaxVelocity(Integer truckMaxVelocity) { this.truckMaxVelocity = truckMaxVelocity; return this; }

        /**
         * @param blockTruckWeight Block roads exceeding truck weight.
         * @return The builder.
         */
        public Builder blockTruckWeight(Boolean blockTruckWeight) { this.blockTruckWeight = blockTruckWeight; return this; }

        /**
         * @param avoidTruckWeight Avoid roads exceeding truck weight.
         * @return The builder.
         */
        public Builder avoidTruckWeight(Boolean avoidTruckWeight) { this.avoidTruckWeight = avoidTruckWeight; return this; }

        /**
         * @param blockTruckAxleWeight Block roads exceeding axle weight.
         * @return The builder.
         */
        public Builder blockTruckAxleWeight(Boolean blockTruckAxleWeight) { this.blockTruckAxleWeight = blockTruckAxleWeight; return this; }

        /**
         * @param avoidTruckAxleWeight Avoid roads exceeding axle weight.
         * @return The builder.
         */
        public Builder avoidTruckAxleWeight(Boolean avoidTruckAxleWeight) { this.avoidTruckAxleWeight = avoidTruckAxleWeight; return this; }

        /**
         * @param blockTruckHeight Block roads exceeding height.
         * @return The builder.
         */
        public Builder blockTruckHeight(Boolean blockTruckHeight) { this.blockTruckHeight = blockTruckHeight; return this; }

        /**
         * @param avoidTruckHeight Avoid roads exceeding height.
         * @return The builder.
         */
        public Builder avoidTruckHeight(Boolean avoidTruckHeight) { this.avoidTruckHeight = avoidTruckHeight; return this; }

        /**
         * @param blockTruckLength Block roads exceeding length.
         * @return The builder.
         */
        public Builder blockTruckLength(Boolean blockTruckLength) { this.blockTruckLength = blockTruckLength; return this; }

        /**
         * @param avoidTruckLength Avoid roads exceeding length.
         * @return The builder.
         */
        public Builder avoidTruckLength(Boolean avoidTruckLength) { this.avoidTruckLength = avoidTruckLength; return this; }

        /**
         * @param blockTruckWidth Block roads exceeding width.
         * @return The builder.
         */
        public Builder blockTruckWidth(Boolean blockTruckWidth) { this.blockTruckWidth = blockTruckWidth; return this; }

        /**
         * @param avoidTruckWidth Avoid roads exceeding width.
         * @return The builder.
         */
        public Builder avoidTruckWidth(Boolean avoidTruckWidth) { this.avoidTruckWidth = avoidTruckWidth; return this; }
        
        /**
         * @return A new {@link RoutingOptions} instance.
         */
        public RoutingOptions build() {
            return new RoutingOptions(this);
        }
    }
}
