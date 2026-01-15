package com.cercalia.sdk.model.routing;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Route calculation result from Cercalia API.
 * <p>
 * Contains route geometry, statistics, and metadata for a calculated route
 * between two or more points. Includes information such as:
 * <ul>
 *   <li>Distance and duration</li>
 *   <li>Route geometry in WKT format</li>
 *   <li>Waypoints (intermediate stops)</li>
 *   <li>Toll costs (if applicable)</li>
 * </ul>
 *
 * <pre>{@code
 * RoutingService service = new RoutingService(config);
 * RouteResult route = service.route(RoutingOptions.builder()
 *     .origin(new Coordinate(41.3851, 2.1734))
 *     .destination(new Coordinate(41.3954, 2.1576))
 *     .vehicle(VehicleType.CAR)
 *     .build());
 *
 * System.out.println("Distance: " + route.getDistance() + " meters");
 * System.out.println("Duration: " + (route.getDuration() / 60) + " minutes");
 * }</pre>
 *
 * @see RoutingOptions
 * @see VehicleType
 */
public final class RouteResult {
    
    @NotNull
    private final String wkt;
    
    private final double distance;
    
    private final double duration;
    
    @NotNull
    private final Coordinate origin;
    
    @NotNull
    private final Coordinate destination;
    
    @Nullable
    private final List<Coordinate> waypoints;
    
    @Nullable
    private final Double tollCost;
    
    @Nullable
    private final String currency;
    
    private RouteResult(Builder builder) {
        this.wkt = builder.wkt;
        this.distance = builder.distance;
        this.duration = builder.duration;
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.waypoints = builder.waypoints;
        this.tollCost = builder.tollCost;
        this.currency = builder.currency;
    }

    /**
     * Creates a new builder for constructing {@link RouteResult} instances.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return The route geometry in Well-Known Text (WKT) format.
     */
    @NotNull public String getWkt() { return wkt; }

    /**
     * @return The total distance of the route in meters.
     */
    public double getDistance() { return distance; }

    /**
     * @return The estimated travel time for the route in seconds.
     */
    public double getDuration() { return duration; }

    /**
     * @return The starting point of the route.
     */
    @NotNull public Coordinate getOrigin() { return origin; }

    /**
     * @return The destination point of the route.
     */
    @NotNull public Coordinate getDestination() { return destination; }

    /**
     * @return The intermediate waypoints of the route.
     */
    @Nullable public List<Coordinate> getWaypoints() { return waypoints; }

    /**
     * @return The estimated toll cost for the route, or {@code null} if not applicable.
     */
    @Nullable public Double getTollCost() { return tollCost; }

    /**
     * @return The currency code (ISO 4217) for the toll cost, or {@code null} if not applicable.
     */
    @Nullable public String getCurrency() { return currency; }

    /**
     * Builder for constructing {@link RouteResult} instances.
     */
    public static final class Builder {
        private String wkt = "";
        private double distance;
        private double duration;
        private Coordinate origin;
        private Coordinate destination;
        private List<Coordinate> waypoints;
        private Double tollCost;
        private String currency;

        /**
         * @param wkt The route geometry in WKT format.
         * @return The builder.
         */
        public Builder wkt(String wkt) { this.wkt = wkt; return this; }

        /**
         * @param distance The total distance in meters.
         * @return The builder.
         */
        public Builder distance(double distance) { this.distance = distance; return this; }

        /**
         * @param duration The estimated duration in seconds.
         * @return The builder.
         */
        public Builder duration(double duration) { this.duration = duration; return this; }

        /**
         * @param origin The starting point coordinate.
         * @return The builder.
         */
        public Builder origin(Coordinate origin) { this.origin = origin; return this; }

        /**
         * @param destination The destination point coordinate.
         * @return The builder.
         */
        public Builder destination(Coordinate destination) { this.destination = destination; return this; }

        /**
         * @param waypoints The intermediate waypoints.
         * @return The builder.
         */
        public Builder waypoints(List<Coordinate> waypoints) { this.waypoints = waypoints; return this; }

        /**
         * @param tollCost The estimated toll cost.
         * @return The builder.
         */
        public Builder tollCost(Double tollCost) { this.tollCost = tollCost; return this; }

        /**
         * @param currency The currency code (ISO 4217).
         * @return The builder.
         */
        public Builder currency(String currency) { this.currency = currency; return this; }

        /**
         * @return A new {@link RouteResult} instance.
         */
        public RouteResult build() {
            return new RouteResult(this);
        }
    }
}
