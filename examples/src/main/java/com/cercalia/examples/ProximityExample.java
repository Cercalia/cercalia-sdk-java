package com.cercalia.examples;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.proximity.*;
import com.cercalia.sdk.services.ProximityService;
import com.cercalia.sdk.util.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Proximity examples demonstrating the Cercalia SDK.
 * 
 * Find nearby points of interest using the Cercalia Proximity API.
 * 
 * Includes:
 * - Get the N nearest POIs
 * - Get nearest POIs with routing
 * - Find nearest by category (simplified)
 * - Multiple categories search
 */
public class ProximityExample {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY") != null ? System.getenv("CERCALIA_API_KEY") : "YOUR_API_KEY";
    
    public static void main(String[] args) {
        // Enable debug logging
        Logger.setDebugEnabled(true);
        
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        ProximityService proximity = new ProximityService(config);
        
        System.out.println("=== Cercalia SDK - Proximity Service Examples ===\n");
        
        try {
            // Example 1: Get the N nearest POIs
            nearestPois(proximity);
            
            // Example 2: Get nearest POIs with routing
            nearestPoisWithRouting(proximity);
            
            // Example 3: Find nearest by category (simplified)
            nearestByCategory(proximity);
            
            // Example 4: Multiple categories search
            multipleCategoriesSearch(proximity);
            
            System.out.println("=== All proximity examples completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Example failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void nearestPois(ProximityService proximity) {
        System.out.println("--- Example 1: Get the Nearest POIs ---");
        System.out.println("Finding nearest gas stations (C001) near Madrid center...\n");
        
        Coordinate madridCenter = new Coordinate(40.3691, -3.589);
        
        ProximityOptions options = ProximityOptions.builder(madridCenter)
                .categories("C001")  // Gas stations
                .count(2)
                .maxRadius(10000)    // 10km radius
                .build();
        
        ProximityResult result = proximity.findNearest(options);
        
        System.out.println("Found " + result.getTotalFound() + " gas stations:");
        int index = 1;
        for (ProximityItem item : result.getItems()) {
            System.out.println("\n" + index + ". " + item.getName());
            System.out.println("   ID: " + item.getId());
            System.out.println("   Category: " + item.getCategoryCode());
            System.out.println("   Distance: " + item.getDistance() + "m");
            if (item.getPosition() != null) {
                System.out.println("   Position: " + item.getPosition());
            }
            System.out.println("   Coordinates: (" + item.getCoord().getLat() + ", " + item.getCoord().getLng() + ")");
            if (item.getGeometry() != null) {
                System.out.println("   Geometry Type: " + item.getGeometry());
            }
            
            if (item.getGe() != null) {
                System.out.println("   Address:");
                if (item.getGe().getStreet() != null) {
                    System.out.println("     Street: " + item.getGe().getStreet());
                }
                if (item.getGe().getLocality() != null) {
                    System.out.println("     Locality: " + item.getGe().getLocality() + 
                            " (" + item.getGe().getLocalityCode() + ")");
                }
                if (item.getGe().getMunicipality() != null) {
                    System.out.println("     Municipality: " + item.getGe().getMunicipality() + 
                            " (" + item.getGe().getMunicipalityCode() + ")");
                }
                if (item.getGe().getRegion() != null) {
                    System.out.println("     Region: " + item.getGe().getRegion() + 
                            " (" + item.getGe().getRegionCode() + ")");
                }
                if (item.getGe().getCountry() != null) {
                    System.out.println("     Country: " + item.getGe().getCountry() + 
                            " (" + item.getGe().getCountryCode() + ")");
                }
            }
            
            if (item.getInfo() != null && !item.getInfo().isEmpty()) {
                String info = item.getInfo().length() > 100 ? 
                        item.getInfo().substring(0, 100) + "..." : item.getInfo();
                System.out.println("   Info: " + info);
            }
            index++;
        }
        System.out.println();
    }
    
    private static void nearestPoisWithRouting(ProximityService proximity) {
        System.out.println("\n--- Example 2: Get the Nearest POIs with Routing ---");
        System.out.println("Finding nearest gas stations with routing information (by time)...\n");
        
        Coordinate madridCenter = new Coordinate(40.3691, -3.589);
        
        ProximityResult result = proximity.findNearestWithRouting(
                madridCenter,
                "C001",  // Gas station
                ProximityRouteWeight.TIME,
                2
        );
        
        System.out.println("Found " + result.getTotalFound() + " gas stations with routing:");
        int index = 1;
        for (ProximityItem item : result.getItems()) {
            System.out.println("\n" + index + ". " + item.getName());
            System.out.println("   Straight-line distance: " + item.getDistance() + "m");
            if (item.getRouteDistance() != null) {
                System.out.println("   Route distance: " + item.getRouteDistance() + "m");
            }
            if (item.getRouteTime() != null) {
                System.out.println("   Route time: " + (item.getRouteTime() / 1000) + "s");
            }
            if (item.getRouteRealtime() != null) {
                System.out.println("   Route time (realtime): " + (item.getRouteRealtime() / 1000) + "s");
            }
            if (item.getRouteWeight() != null) {
                System.out.println("   Route weight: " + item.getRouteWeight());
            }
            index++;
        }
        System.out.println();
    }
    
    private static void nearestByCategory(ProximityService proximity) {
        System.out.println("\n--- Example 3: Find Nearest by Category (Simplified) ---");
        System.out.println("Finding nearest restaurants (C014) in Barcelona...\n");
        
        Coordinate barcelonaCenter = new Coordinate(41.3851, 2.1734);
        
        ProximityResult result = proximity.findNearestByCategory(barcelonaCenter, "C014", 5);
        
        System.out.println("Found " + result.getTotalFound() + " restaurants:");
        int index = 1;
        for (ProximityItem item : result.getItems()) {
            System.out.println(index + ". " + item.getName() + " - " + item.getDistance() + "m away");
            index++;
        }
        System.out.println();
    }
    
    private static void multipleCategoriesSearch(ProximityService proximity) {
        System.out.println("\n--- Example 4: Multiple Categories Search ---");
        System.out.println("Finding gas stations (C001) and pharmacies (C026) in Barcelona...\n");
        
        Coordinate barcelonaCenter = new Coordinate(41.3851, 2.1734);
        
        ProximityOptions options = ProximityOptions.builder(barcelonaCenter)
                .categories("C001", "C026")
                .count(10)
                .build();
        
        ProximityResult result = proximity.findNearest(options);
        
        System.out.println("Found " + result.getTotalFound() + " POIs:");
        
        // Category counts
        Map<String, Integer> categoryCounts = new HashMap<>();
        for (ProximityItem item : result.getItems()) {
            String cat = item.getCategoryCode() != null ? item.getCategoryCode() : "unknown";
            categoryCounts.put(cat, categoryCounts.getOrDefault(cat, 0) + 1);
        }
        
        System.out.println("Category breakdown:");
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            String name = "C001".equals(entry.getKey()) ? "Gas Stations" : 
                          "C026".equals(entry.getKey()) ? "Pharmacies" : entry.getKey();
            System.out.println("  " + name + ": " + entry.getValue());
        }
        
        System.out.println("\nFirst 3 results:");
        int count = 0;
        for (ProximityItem item : result.getItems()) {
            if (count >= 3) break;
            String catName = "C001".equals(item.getCategoryCode()) ? "Gas Station" : 
                             "C026".equals(item.getCategoryCode()) ? "Pharmacy" : 
                             item.getCategoryCode() != null ? item.getCategoryCode() : "Unknown";
            System.out.println((count + 1) + ". [" + catName + "] " + item.getName() + " - " + item.getDistance() + "m");
            count++;
        }
        System.out.println();
    }
}
