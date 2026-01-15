package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaClient;
import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.exception.CercaliaException;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.routing.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cercalia.sdk.util.CercaliaResponseParser.*;

/**
 * Service for route calculation using the Cercalia API.
 * 
 * <pre>{@code
 * RoutingService service = new RoutingService(config);
 * Coordinate origin = new Coordinate(41.3851, 2.1734);
 * Coordinate destination = new Coordinate(40.4168, -3.7038);
 * 
 * // 1. Basic route calculation
 * RouteResult result = service.calculateRoute(origin, destination, RoutingOptions.builder()
 *     .avoidTolls(true)
 *     .vehicleType(VehicleType.CAR)
 *     .build());
 *
 * // 2. Truck routing with dimensions
 * RouteResult truckRoute = service.calculateRoute(origin, destination, RoutingOptions.builder()
 *     .vehicleType(VehicleType.TRUCK)
 *     .truckWeight(18000) // 18 tons in kg
 *     .truckHeight(400)   // 4 meters in cm
 *     .build());
 *
 * // 3. Quick distance and duration estimation
 * DistanceTime dt = service.getDistanceTime(origin, destination, VehicleType.CAR);
 * double meters = dt.getDistance();
 * double seconds = dt.getDuration();
 * }</pre>
 * 
 * @see RoutingOptions
 * @see RouteResult
 */
public class RoutingService extends CercaliaClient {
    
    private static final Pattern LINESTRING_PATTERN = Pattern.compile("LINESTRING\\s*\\((.*)\\)");
    
    public RoutingService(@NotNull CercaliaConfig config) {
        super(config);
    }
    
    /**
     * Calculate a route between origin and destination.
     *
     * @param origin      starting point
     * @param destination ending point
     * @param options     routing options (optional)
     * @return route result with geometry, distance and duration
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public RouteResult calculateRoute(@NotNull Coordinate origin, 
                                       @NotNull Coordinate destination,
                                       @Nullable RoutingOptions options) {
        Map<String, String> params = newParams("route");
        params.put("v", "1");
        params.put("srs", "EPSG:4326");
        params.put("mocs", "gdd");
        params.put("mo_o", origin.getLat() + "," + origin.getLng());
        params.put("mo_d", destination.getLat() + "," + destination.getLng());
        params.put("weight", options != null && Boolean.TRUE.equals(options.getAvoidTolls()) ? "money" : "time");
        params.put("stagegeometry", "1");
        params.put("stagegeometrysrs", "EPSG:4326");
        params.put("report", "0");
        params.put("lang", "en");
        
        List<Coordinate> waypoints = null;
        if (options != null) {
            waypoints = options.getWaypoints();
            if (waypoints != null && !waypoints.isEmpty()) {
                for (int i = 0; i < waypoints.size(); i++) {
                    Coordinate wp = waypoints.get(i);
                    params.put("mo_" + (i + 1), wp.getLat() + "," + wp.getLng());
                }
            }
            
            if (options.getVehicleType() == VehicleType.TRUCK) {
                params.put("net", "logistics");
                
                // Physical dimensions (conversions: kg -> tons, cm -> meters)
                if (options.getTruckWeight() != null) {
                    params.put("vweight", String.valueOf(options.getTruckWeight() / 1000.0));
                }
                if (options.getTruckAxleWeight() != null) {
                    params.put("vaxleweight", String.valueOf(options.getTruckAxleWeight() / 1000.0));
                }
                if (options.getTruckHeight() != null) {
                    params.put("vheight", String.valueOf(options.getTruckHeight() / 100.0));
                }
                if (options.getTruckWidth() != null) {
                    params.put("vwidth", String.valueOf(options.getTruckWidth() / 100.0));
                }
                if (options.getTruckLength() != null) {
                    params.put("vlength", String.valueOf(options.getTruckLength() / 100.0));
                }
                if (options.getTruckMaxVelocity() != null) {
                    params.put("vmaxvel", String.valueOf(options.getTruckMaxVelocity()));
                }
                
                // Restriction flags
                addTruckRestriction(params, "vweight", options.getBlockTruckWeight(), options.getAvoidTruckWeight());
                addTruckRestriction(params, "vaxleweight", options.getBlockTruckAxleWeight(), options.getAvoidTruckAxleWeight());
                addTruckRestriction(params, "vheight", options.getBlockTruckHeight(), options.getAvoidTruckHeight());
                addTruckRestriction(params, "vwidth", options.getBlockTruckWidth(), options.getAvoidTruckWidth());
                addTruckRestriction(params, "vlength", options.getBlockTruckLength(), options.getAvoidTruckLength());
            }
        }
        
        JsonNode response = request(params, "Routing");
        return parseRouteResponse(response, origin, destination, waypoints);
    }
    
    /**
     * Calculate a route asynchronously.
     */
    @NotNull
    public CompletableFuture<RouteResult> calculateRouteAsync(@NotNull Coordinate origin,
                                                               @NotNull Coordinate destination,
                                                               @Nullable RoutingOptions options) {
        return CompletableFuture.supplyAsync(() -> calculateRoute(origin, destination, options));
    }
    
    /**
     * Get distance and duration between two points (without full geometry).
     *
     * @param origin      starting point
     * @param destination ending point
     * @param vehicleType vehicle type (optional)
     * @return distance in meters and duration in seconds
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public DistanceTime getDistanceTime(@NotNull Coordinate origin,
                                         @NotNull Coordinate destination,
                                         @Nullable VehicleType vehicleType) {
        Map<String, String> params = newParams("route");
        params.put("v", "1");
        params.put("srs", "EPSG:4326");
        params.put("mocs", "gdd");
        params.put("mo_o", origin.getLat() + "," + origin.getLng());
        params.put("mo_d", destination.getLat() + "," + destination.getLng());
        params.put("weight", "time");
        params.put("stagegeometry", "0");
        params.put("report", "0");
        
        JsonNode response = request(params, "Routing DistanceTime");
        
        JsonNode routeNode = response.get("route");
        if (routeNode == null || routeNode.isNull()) {
            throw new CercaliaException("No route found");
        }
        
        double distance = parseDouble(getCercaliaAttr(routeNode, "dist"), 0) * 1000;
        double duration = parseTime(getCercaliaAttr(routeNode, "time"));
        
        return new DistanceTime(distance, duration);
    }
    
    /**
     * Get distance and duration asynchronously.
     */
    @NotNull
    public CompletableFuture<DistanceTime> getDistanceTimeAsync(@NotNull Coordinate origin,
                                                                 @NotNull Coordinate destination,
                                                                 @Nullable VehicleType vehicleType) {
        return CompletableFuture.supplyAsync(() -> getDistanceTime(origin, destination, vehicleType));
    }
    
    // ========== Private Methods ==========
    
    private void addTruckRestriction(Map<String, String> params, String key, Boolean block, Boolean avoid) {
        if (Boolean.TRUE.equals(block)) {
            params.put("block" + key, "true");
        }
        if (Boolean.TRUE.equals(avoid) || (avoid == null && !Boolean.TRUE.equals(block))) {
            params.put("avoid" + key, "true");
        }
    }
    
    private RouteResult parseRouteResponse(JsonNode response, Coordinate origin, 
                                            Coordinate destination, List<Coordinate> waypoints) {
        JsonNode routeNode = response.get("route");
        if (routeNode == null || routeNode.isNull()) {
            throw new CercaliaException("No route found");
        }
        
        // Get stages
        JsonNode stagesNode = routeNode.get("stages");
        List<String> lineStrings = new ArrayList<>();
        
        if (stagesNode != null && !stagesNode.isNull()) {
            JsonNode stageArray = stagesNode.get("stage");
            if (stageArray != null) {
                int size = getArraySize(stageArray);
                for (int i = 0; i < size; i++) {
                    JsonNode stage = getArrayElement(stageArray, i);
                    if (stage != null) {
                        String wkt = getCercaliaValue(stage.get("wkt"));
                        if (wkt != null && !wkt.isEmpty()) {
                            Matcher matcher = LINESTRING_PATTERN.matcher(wkt);
                            if (matcher.find()) {
                                lineStrings.add("(" + matcher.group(1) + ")");
                            }
                        }
                    }
                }
            }
        }
        
        String wkt = lineStrings.isEmpty() ? "" : "MULTILINESTRING(" + String.join(", ", lineStrings) + ")";
        double distance = parseDouble(getCercaliaAttr(routeNode, "dist"), 0) * 1000;
        double duration = parseTime(getCercaliaAttr(routeNode, "time"));
        
        return RouteResult.builder()
                .wkt(wkt)
                .distance(distance)
                .duration(duration)
                .origin(origin)
                .destination(destination)
                .waypoints(waypoints)
                .build();
    }
    
    private double parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return 0;
        }
        if (timeStr.contains(":")) {
            String[] parts = timeStr.split(":");
            if (parts.length == 3) {
                return Double.parseDouble(parts[0]) * 3600 + 
                       Double.parseDouble(parts[1]) * 60 + 
                       Double.parseDouble(parts[2]);
            }
            if (parts.length == 2) {
                return Double.parseDouble(parts[0]) * 60 + Double.parseDouble(parts[1]);
            }
        }
        try {
            return Double.parseDouble(timeStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private double parseDouble(String str, double defaultValue) {
        if (str == null || str.isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Simple distance/time result.
     */
    public static final class DistanceTime {
        private final double distance;
        private final double duration;
        
        public DistanceTime(double distance, double duration) {
            this.distance = distance;
            this.duration = duration;
        }
        
        /** Distance in meters */
        public double getDistance() { return distance; }
        /** Duration in seconds */
        public double getDuration() { return duration; }
    }
}
