package com.cercalia.examples;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.geofencing.*;
import com.cercalia.sdk.services.GeofencingService;
import com.cercalia.sdk.util.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Geofencing examples demonstrating the Cercalia SDK.
 * 
 * Point-in-Polygon geofencing using the Cercalia InsideGeoms API.
 * Use cases: delivery zone validation, fleet monitoring, service area verification.
 * 
 * Includes:
 * - Check points inside circular zone
 * - Check points inside polygon zone
 * - Multiple zones and points
 * - Check single point
 * - Is inside circle (convenience method)
 * - Filter points in shape
 * - Rectangle geofence
 */
public class GeofencingExample {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY") != null ? System.getenv("CERCALIA_API_KEY") : "YOUR_API_KEY";
    
    public static void main(String[] args) {
        // Enable debug logging
        Logger.setDebugEnabled(true);
        
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        GeofencingService geofencing = new GeofencingService(config);
        
        System.out.println("=== Cercalia SDK - Geofencing Examples ===\n");
        
        try {
            // Example 1: Check points inside a circular zone
            circularZone(geofencing);
            
            // Example 2: Check points inside a polygon zone
            polygonZone(geofencing);
            
            // Example 3: Multiple zones and points
            multipleZonesAndPoints(geofencing);
            
            // Example 4: Check single point
            checkSinglePoint(geofencing);
            
            // Example 5: Is inside circle (convenience)
            isInsideCircleConvenience(geofencing);
            
            // Example 6: Filter points in shape
            filterPointsInShape(geofencing);
            
            // Example 7: Rectangle geofence
            rectangleGeofence(geofencing);
            
            System.out.println("=== All geofencing examples completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Example failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void circularZone(GeofencingService geofencing) {
        System.out.println("--- Example 1: Check Points Inside Circular Zone ---");
        System.out.println("Use Case: Delivery zone validation\n");
        
        Coordinate warehouseLocation = new Coordinate(41.3874, 2.1686); // Plaça Catalunya
        GeofenceShape zone = geofencing.createCircle("warehouse-zone", warehouseLocation, 1000); // 1km radius
        
        List<GeofencePoint> deliveryPoints = Arrays.asList(
                new GeofencePoint("delivery-1", new Coordinate(41.3850, 2.1700)),  // Inside
                new GeofencePoint("delivery-2", new Coordinate(41.3900, 2.1650)),  // Inside
                new GeofencePoint("delivery-3", new Coordinate(41.4036, 2.1744))   // Outside (Sagrada Família)
        );
        
        GeofenceResult result = geofencing.check(
                Arrays.asList(zone), 
                deliveryPoints, 
                GeofenceOptions.defaults()
        );
        
        System.out.println("Checked " + result.getTotalPointsChecked() + " points against " + 
                result.getTotalShapesChecked() + " zones");
        System.out.println("Matches found: " + result.getMatches().size());
        for (GeofenceMatch match : result.getMatches()) {
            System.out.println("  Zone '" + match.getShapeId() + "' contains " + 
                    match.getPointsInside().size() + " points:");
            for (GeofenceMatch.MatchedPoint p : match.getPointsInside()) {
                System.out.println("    - " + p.getId() + " at (" + 
                        p.getCoord().getLat() + ", " + p.getCoord().getLng() + ")");
            }
        }
        System.out.println();
    }
    
    private static void polygonZone(GeofencingService geofencing) {
        System.out.println("--- Example 2: Check Points Inside Polygon Zone ---");
        System.out.println("Use Case: Service area verification\n");
        
        GeofenceShape serviceArea = new GeofenceShape(
                "eixample-zone",
                "POLYGON((2.15 41.38, 2.18 41.38, 2.18 41.41, 2.15 41.41, 2.15 41.38))"
        );
        
        List<GeofencePoint> customers = Arrays.asList(
                new GeofencePoint("customer-1", new Coordinate(41.4036, 2.1744)),  // Sagrada Família - Inside
                new GeofencePoint("customer-2", new Coordinate(41.3809, 2.1228)),  // Camp Nou - Outside
                new GeofencePoint("customer-3", new Coordinate(41.3900, 2.1650))   // Inside
        );
        
        GeofenceResult result = geofencing.check(
                Arrays.asList(serviceArea),
                customers,
                GeofenceOptions.defaults()
        );
        
        System.out.println("Checked " + result.getTotalPointsChecked() + " customers");
        for (GeofenceMatch match : result.getMatches()) {
            System.out.println("Zone '" + match.getShapeId() + "' contains:");
            for (GeofenceMatch.MatchedPoint p : match.getPointsInside()) {
                System.out.println("  - " + p.getId());
            }
        }
        System.out.println();
    }
    
    private static void multipleZonesAndPoints(GeofencingService geofencing) {
        System.out.println("--- Example 3: Multiple Zones and Points ---");
        System.out.println("Use Case: Fleet monitoring - which vehicles are in which zones\n");
        
        List<GeofenceShape> zones = Arrays.asList(
                geofencing.createCircle("zone-center", new Coordinate(41.3874, 2.1686), 500),   // Downtown
                geofencing.createCircle("zone-north", new Coordinate(41.4036, 2.1744), 300),    // Sagrada Família
                new GeofenceShape(
                        "zone-polygon",
                        "POLYGON((2.15 41.38, 2.18 41.38, 2.18 41.41, 2.15 41.41, 2.15 41.38))"
                )
        );
        
        List<GeofencePoint> vehicles = Arrays.asList(
                new GeofencePoint("vehicle-1", new Coordinate(41.3874, 2.1686)),  // Center
                new GeofencePoint("vehicle-2", new Coordinate(41.4036, 2.1744)),  // Sagrada Família
                new GeofencePoint("vehicle-3", new Coordinate(41.3809, 2.1228)),  // Camp Nou (outside all)
                new GeofencePoint("vehicle-4", new Coordinate(41.3900, 2.1650))   // Inside polygon
        );
        
        GeofenceResult result = geofencing.check(zones, vehicles, GeofenceOptions.defaults());
        
        System.out.println("Checked " + result.getTotalPointsChecked() + " vehicles against " + 
                result.getTotalShapesChecked() + " zones");
        System.out.println("Results:");
        for (GeofenceMatch match : result.getMatches()) {
            System.out.println("  Zone '" + match.getShapeId() + "': " + 
                    match.getPointsInside().size() + " vehicles inside");
        }
        System.out.println();
    }
    
    private static void checkSinglePoint(GeofencingService geofencing) {
        System.out.println("--- Example 4: Check if Single Point is Inside Any Zone ---");
        System.out.println("Use Case: Quick zone lookup\n");
        
        Coordinate checkPoint = new Coordinate(41.3874, 2.1686); // Plaça Catalunya
        
        List<GeofenceShape> allZones = Arrays.asList(
                geofencing.createCircle("downtown", new Coordinate(41.3874, 2.1686), 1000),
                geofencing.createCircle("sagrada-familia", new Coordinate(41.4036, 2.1744), 500),
                new GeofenceShape(
                        "eixample-polygon",
                        "POLYGON((2.15 41.38, 2.18 41.38, 2.18 41.41, 2.15 41.41, 2.15 41.38))"
                )
        );
        
        List<String> matchingZones = geofencing.checkPoint(allZones, checkPoint);
        
        System.out.println("Point: (" + checkPoint.getLat() + ", " + checkPoint.getLng() + ")");
        System.out.println("Matching zones: " + matchingZones);
        System.out.println();
    }
    
    private static void isInsideCircleConvenience(GeofencingService geofencing) {
        System.out.println("--- Example 5: Is Point Inside Circle (Convenience) ---");
        System.out.println("Use Case: Proximity check\n");
        
        Coordinate warehouse = new Coordinate(41.3874, 2.1686);
        Coordinate delivery = new Coordinate(41.3880, 2.1690);
        
        boolean isNearWarehouse = geofencing.isInsideCircle(warehouse, 500, delivery);  // 500m radius
        
        System.out.println("Warehouse: (" + warehouse.getLat() + ", " + warehouse.getLng() + ")");
        System.out.println("Delivery: (" + delivery.getLat() + ", " + delivery.getLng() + ")");
        System.out.println("Is within 500m radius: " + isNearWarehouse);
        System.out.println();
    }
    
    private static void filterPointsInShape(GeofencingService geofencing) {
        System.out.println("--- Example 6: Filter Points to Only Those Inside Zone ---");
        System.out.println("Use Case: Customer filtering for delivery eligibility\n");
        
        GeofenceShape deliveryZone = geofencing.createCircle(
                "delivery-zone",
                new Coordinate(41.3874, 2.1686),
                2000  // 2km radius
        );
        
        List<GeofencePoint> allCustomers = Arrays.asList(
                new GeofencePoint("customer-1", new Coordinate(41.3874, 2.1686)),  // Downtown - Inside
                new GeofencePoint("customer-2", new Coordinate(41.3880, 2.1690)),  // Nearby - Inside
                new GeofencePoint("customer-3", new Coordinate(41.4036, 2.1744)),  // Sagrada Família - Inside
                new GeofencePoint("customer-4", new Coordinate(42.0, 3.0)),         // Far away - Outside
                new GeofencePoint("customer-5", new Coordinate(41.3809, 2.1228))   // Camp Nou - Outside
        );
        
        List<GeofencePoint> eligibleCustomers = geofencing.filterPointsInShape(deliveryZone, allCustomers);
        
        System.out.println("Total customers: " + allCustomers.size());
        System.out.println("Eligible customers: " + eligibleCustomers.size());
        for (GeofencePoint customer : eligibleCustomers) {
            System.out.println("  - " + customer.getId());
        }
        System.out.println();
    }
    
    private static void rectangleGeofence(GeofencingService geofencing) {
        System.out.println("--- Example 7: Rectangle Geofence ---");
        System.out.println("Use Case: Rectangular service area\n");
        
        GeofenceShape rect = geofencing.createRectangle(
                "service-area",
                new Coordinate(41.37, 2.15),   // Southwest corner
                new Coordinate(41.40, 2.19)    // Northeast corner
        );
        
        List<GeofencePoint> testPoints = Arrays.asList(
                new GeofencePoint("point-1", new Coordinate(41.3874, 2.1686)),  // Inside (Plaça Catalunya)
                new GeofencePoint("point-2", new Coordinate(41.4036, 2.1744)),  // Outside (too north)
                new GeofencePoint("point-3", new Coordinate(41.3800, 2.1700))   // Inside
        );
        
        GeofenceResult result = geofencing.check(Arrays.asList(rect), testPoints, GeofenceOptions.defaults());
        
        System.out.println("Rectangle WKT: " + rect.getWkt());
        System.out.println("Results:");
        for (GeofenceMatch match : result.getMatches()) {
            System.out.println("  Zone '" + match.getShapeId() + "' contains " + 
                    match.getPointsInside().size() + " points:");
            for (GeofenceMatch.MatchedPoint p : match.getPointsInside()) {
                System.out.println("    - " + p.getId());
            }
        }
        System.out.println();
    }
}
