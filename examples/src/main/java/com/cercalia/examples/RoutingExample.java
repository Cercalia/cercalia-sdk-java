package com.cercalia.examples;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.routing.*;
import com.cercalia.sdk.services.RoutingService;
import com.cercalia.sdk.util.Logger;

import java.util.Arrays;

/**
 * Routing examples demonstrating the Cercalia SDK.
 * 
 * Includes:
 * - Simple car route
 * - Route with waypoints
 * - Route avoiding tolls
 * - Truck routing with restrictions
 * - Distance and time only (no geometry)
 */
public class RoutingExample {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY") != null ? System.getenv("CERCALIA_API_KEY") : "YOUR_API_KEY";
    
    public static void main(String[] args) {
        // Enable debug logging
        Logger.setDebugEnabled(true);
        
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        RoutingService routing = new RoutingService(config);
        
        System.out.println("=== Cercalia SDK - Routing Examples ===\n");
        
        // Define coordinates
        Coordinate barcelona = new Coordinate(41.3851, 2.1734);
        Coordinate madrid = new Coordinate(40.4168, -3.7038);
        Coordinate zaragoza = new Coordinate(41.6488, -0.8891);
        Coordinate valencia = new Coordinate(39.4699, -0.3763);
        
        try {
            // Example 1: Simple car route
            simpleCarRoute(routing, barcelona, madrid);
            
            // Example 2: Route with waypoints
            routeWithWaypoints(routing, barcelona, madrid, zaragoza, valencia);
            
            // Example 3: Route avoiding tolls
            routeAvoidingTolls(routing, barcelona, madrid);
            
            // Example 4: Truck routing with restrictions
            truckRouting(routing, barcelona, madrid);
            
            // Example 5: Distance and time only
            distanceTimeOnly(routing, barcelona, madrid);
            
            // Example 6: Truck with avoid restrictions
            truckWithAvoidRestrictions(routing, barcelona, madrid);
            
            System.out.println("=== All routing examples completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Example failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void simpleCarRoute(RoutingService routing, Coordinate origin, Coordinate destination) {
        System.out.println("1. SIMPLE CAR ROUTE (Barcelona → Madrid)");
        System.out.println("   Example from docs: Basic route with time optimization\n");
        
        RouteResult result = routing.calculateRoute(origin, destination, null);
        
        System.out.println("   Distance: " + String.format("%.2f", result.getDistance() / 1000) + " km");
        System.out.println("   Duration: " + Math.round(result.getDuration() / 60) + " minutes");
        if (result.getWkt() != null && !result.getWkt().isEmpty()) {
            System.out.println("   WKT preview: " + result.getWkt().substring(0, Math.min(100, result.getWkt().length())) + "...\n");
        }
    }
    
    private static void routeWithWaypoints(RoutingService routing, Coordinate origin, Coordinate destination,
                                           Coordinate zaragoza, Coordinate valencia) {
        System.out.println("2. ROUTE WITH WAYPOINTS (Barcelona → Zaragoza → Valencia → Madrid)");
        System.out.println("   Example from docs: Using mo_1, mo_2 parameters for intermediate stops\n");
        
        RoutingOptions options = RoutingOptions.builder()
                .waypoints(Arrays.asList(zaragoza, valencia))
                .build();
        
        RouteResult result = routing.calculateRoute(origin, destination, options);
        
        System.out.println("   Route: Barcelona → Zaragoza → Valencia → Madrid");
        System.out.println("   Waypoints: " + (result.getWaypoints() != null ? result.getWaypoints().size() : 0));
        System.out.println("   Total distance: " + String.format("%.2f", result.getDistance() / 1000) + " km");
        System.out.println("   Total duration: " + Math.round(result.getDuration() / 60) + " minutes\n");
    }
    
    private static void routeAvoidingTolls(RoutingService routing, Coordinate origin, Coordinate destination) {
        System.out.println("3. ROUTE AVOIDING TOLLS (Barcelona → Madrid)");
        System.out.println("   Example from docs: Comparing weight=time vs weight=money\n");
        
        // With tolls
        RoutingOptions withTollsOptions = RoutingOptions.builder()
                .avoidTolls(false)
                .build();
        RouteResult withTolls = routing.calculateRoute(origin, destination, withTollsOptions);
        
        // Without tolls
        RoutingOptions withoutTollsOptions = RoutingOptions.builder()
                .avoidTolls(true)
                .build();
        RouteResult withoutTolls = routing.calculateRoute(origin, destination, withoutTollsOptions);
        
        double diffKm = (withoutTolls.getDistance() - withTolls.getDistance()) / 1000;
        double diffMin = (withoutTolls.getDuration() - withTolls.getDuration()) / 60;
        
        System.out.println("   With tolls (weight=time):");
        System.out.println("     Distance: " + String.format("%.2f", withTolls.getDistance() / 1000) + " km");
        System.out.println("     Duration: " + Math.round(withTolls.getDuration() / 60) + " minutes");
        System.out.println("   Without tolls (weight=money):");
        System.out.println("     Distance: " + String.format("%.2f", withoutTolls.getDistance() / 1000) + " km");
        System.out.println("     Duration: " + Math.round(withoutTolls.getDuration() / 60) + " minutes");
        System.out.println("   Difference: " + String.format("%+.2f", diffKm) + " km, " + 
                String.format("%+d", Math.round(diffMin)) + " minutes\n");
    }
    
    private static void truckRouting(RoutingService routing, Coordinate origin, Coordinate destination) {
        System.out.println("4. TRUCK ROUTING WITH PHYSICAL RESTRICTIONS");
        System.out.println("   Example from docs: Logistics network with truck parameters\n");
        
        RoutingOptions options = RoutingOptions.builder()
                .vehicleType(VehicleType.TRUCK)
                .truckWeight(40000)       // 40 tons (in kg)
                .truckHeight(450)         // 4.5 meters (in cm)
                .truckWidth(255)          // 2.55 meters (in cm)
                .truckLength(1800)        // 18 meters (in cm)
                .build();
        
        RouteResult result = routing.calculateRoute(origin, destination, options);
        
        System.out.println("   Vehicle: 40t truck (H:4.5m, W:2.55m, L:18m)");
        System.out.println("   Network: logistics (net=logistics)");
        System.out.println("   Distance: " + String.format("%.2f", result.getDistance() / 1000) + " km");
        System.out.println("   Duration: " + Math.round(result.getDuration() / 60) + " minutes");
        System.out.println("   Note: Route considers truck restrictions (bridges, tunnels, weight limits)\n");
    }
    
    private static void distanceTimeOnly(RoutingService routing, Coordinate origin, Coordinate destination) {
        System.out.println("5. GET DISTANCE AND TIME ONLY (No Geometry)");
        System.out.println("   Example from docs: Fast query with stagegeometry=0\n");
        
        RoutingService.DistanceTime result = routing.getDistanceTime(origin, destination, null);
        
        System.out.println("   Distance: " + String.format("%.2f", result.getDistance() / 1000) + " km");
        System.out.println("   Duration: " + Math.round(result.getDuration() / 60) + " minutes");
        System.out.println("   Note: No WKT geometry returned (faster response)\n");
    }
    
    private static void truckWithAvoidRestrictions(RoutingService routing, Coordinate origin, Coordinate destination) {
        System.out.println("6. TRUCK WITH AVOID RESTRICTIONS (not block)");
        System.out.println("   Example from docs: Avoid height/weight restrictions if alternatives exist\n");
        
        RoutingOptions options = RoutingOptions.builder()
                .vehicleType(VehicleType.TRUCK)
                .truckWeight(38000)       // 38 tons
                .truckHeight(400)         // 4 meters
                .avoidTruckWeight(true)   // Try to avoid, but allow if no alternatives
                .avoidTruckHeight(true)
                .blockTruckWeight(false)  // Don't force block
                .build();
        
        RouteResult result = routing.calculateRoute(origin, destination, options);
        
        System.out.println("   Vehicle: 38t truck (H:4m)");
        System.out.println("   Restriction handling: AVOID (not BLOCK)");
        System.out.println("   Distance: " + String.format("%.2f", result.getDistance() / 1000) + " km");
        System.out.println("   Duration: " + Math.round(result.getDuration() / 60) + " minutes");
        System.out.println("   Note: Prefers routes without restrictions, but accepts them if necessary\n");
    }
}
