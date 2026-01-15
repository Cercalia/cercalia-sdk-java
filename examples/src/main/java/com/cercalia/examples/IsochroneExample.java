package com.cercalia.examples;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.isochrone.*;
import com.cercalia.sdk.services.IsochroneService;
import com.cercalia.sdk.util.Logger;

import java.util.List;

/**
 * Isochrone examples demonstrating the Cercalia SDK.
 * 
 * An isochrone is a polygon representing the area reachable from a center point
 * within a given time or distance constraint.
 * 
 * Includes:
 * - Time-based isochrone
 * - Distance-based isochrone
 * - Multiple isochrones (concentric)
 * - Simplified isochrone (convexhull method)
 */
public class IsochroneExample {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY") != null ? System.getenv("CERCALIA_API_KEY") : "YOUR_API_KEY";
    
    public static void main(String[] args) {
        // Enable debug logging
        Logger.setDebugEnabled(true);
        
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        IsochroneService isochrone = new IsochroneService(config);
        
        System.out.println("=== Cercalia SDK - Isochrone Examples ===\n");
        
        // Center point for all examples
        Coordinate center = new Coordinate(41.9723144, 2.8260807);
        
        try {
            // Example 1: Time-based isochrone
            timeBasedIsochrone(isochrone, center);
            
            // Example 2: Distance-based isochrone
            distanceBasedIsochrone(isochrone, center);
            
            // Example 3: Multiple isochrones (concentric)
            multipleIsochrones(isochrone, center);
            
            // Example 4: Simplified isochrone (convexhull)
            simplifiedIsochrone(isochrone, center);
            
            System.out.println("=== All isochrone examples completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Example failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void timeBasedIsochrone(IsochroneService isochrone, Coordinate center) {
        System.out.println("1. TIME-BASED ISOCHRONE");
        System.out.println("   Example from docs: 2 minutes from center point\n");
        
        IsochroneOptions options = IsochroneOptions.builder()
                .value(2)  // 2 minutes
                .weight(IsochroneWeight.TIME)
                .method(IsochroneMethod.CONCAVEHULL)
                .build();
        
        IsochroneResult result = isochrone.calculate(center, options);
        
        System.out.println("   WKT polygon length: " + result.getWkt().length() + " characters");
        System.out.println("   Center: " + result.getCenter().getLat() + ", " + result.getCenter().getLng());
        System.out.println("   Value: " + result.getValue() + " minutes");
        System.out.println("   Weight: " + result.getWeight());
        System.out.println("   Level (API raw value): " + result.getLevel() + " ms\n");
    }
    
    private static void distanceBasedIsochrone(IsochroneService isochrone, Coordinate center) {
        System.out.println("2. DISTANCE-BASED ISOCHRONE");
        System.out.println("   1000 meters radius from center point\n");
        
        IsochroneOptions options = IsochroneOptions.builder()
                .value(1000)  // 1000 meters
                .weight(IsochroneWeight.DISTANCE)
                .method(IsochroneMethod.CONCAVEHULL)
                .build();
        
        IsochroneResult result = isochrone.calculate(center, options);
        
        System.out.println("   WKT polygon length: " + result.getWkt().length() + " characters");
        System.out.println("   Value: " + result.getValue() + " meters");
        System.out.println("   Weight: " + result.getWeight());
        System.out.println("   Level (API raw value): " + result.getLevel() + " m\n");
    }
    
    private static void multipleIsochrones(IsochroneService isochrone, Coordinate center) {
        System.out.println("3. MULTIPLE ISOCHRONES (CONCENTRIC)");
        System.out.println("   Example from docs: 2, 5, and 10 minute isochrones\n");
        
        int[] values = {2, 5, 10};  // 2, 5, 10 minutes
        
        List<IsochroneResult> results = isochrone.calculateMultiple(center, values, IsochroneWeight.TIME);
        
        System.out.println("   Calculated " + results.size() + " isochrone levels:\n");
        for (int i = 0; i < results.size(); i++) {
            IsochroneResult result = results.get(i);
            System.out.println("   Level " + (i + 1) + ":");
            System.out.println("     Value: " + result.getValue() + " minutes");
            System.out.println("     API Level: " + result.getLevel() + " ms");
            System.out.println("     WKT length: " + result.getWkt().length() + " characters\n");
        }
    }
    
    private static void simplifiedIsochrone(IsochroneService isochrone, Coordinate center) {
        System.out.println("4. SIMPLIFIED ISOCHRONE (CONVEXHULL METHOD)");
        System.out.println("   Faster calculation with simpler polygon shape\n");
        
        IsochroneOptions options = IsochroneOptions.builder()
                .value(10)  // 10 minutes
                .weight(IsochroneWeight.TIME)
                .method(IsochroneMethod.CONVEXHULL)
                .build();
        
        IsochroneResult result = isochrone.calculate(center, options);
        
        System.out.println("   Method: convexhull (simplified shape)");
        System.out.println("   WKT polygon length: " + result.getWkt().length() + " characters");
        System.out.println("   Compare with concavehull for accuracy vs performance trade-off\n");
    }
}
