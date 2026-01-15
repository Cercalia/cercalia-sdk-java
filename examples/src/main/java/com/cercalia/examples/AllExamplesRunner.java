package com.cercalia.examples;

/**
 * Main runner for all Cercalia SDK examples.
 * 
 * Runs all service examples in sequence.
 */
public class AllExamplesRunner {
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║            CERCALIA SDK FOR JAVA - ALL EXAMPLES                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        boolean runAll = args.length == 0 || "all".equalsIgnoreCase(args[0]);
        String selectedExample = args.length > 0 ? args[0].toLowerCase() : "";
        
        try {
            // Geocoding
            if (runAll || "geocoding".equals(selectedExample)) {
                printSectionHeader("GEOCODING");
                GeocodingExample.main(new String[]{});
                printSeparator();
            }
            
            // Reverse Geocoding
            if (runAll || "reversegeocoding".equals(selectedExample)) {
                printSectionHeader("REVERSE GEOCODING");
                ReverseGeocodingExample.main(new String[]{});
                printSeparator();
            }
            
            // Suggest
            if (runAll || "suggest".equals(selectedExample)) {
                printSectionHeader("SUGGEST");
                SuggestExample.main(new String[]{});
                printSeparator();
            }
            
            // Routing
            if (runAll || "routing".equals(selectedExample)) {
                printSectionHeader("ROUTING");
                RoutingExample.main(new String[]{});
                printSeparator();
            }
            
            // POI
            if (runAll || "poi".equals(selectedExample)) {
                printSectionHeader("POI (POINTS OF INTEREST)");
                PoiExample.main(new String[]{});
                printSeparator();
            }
            
            // Isochrone
            if (runAll || "isochrone".equals(selectedExample)) {
                printSectionHeader("ISOCHRONE");
                IsochroneExample.main(new String[]{});
                printSeparator();
            }
            
            // Proximity
            if (runAll || "proximity".equals(selectedExample)) {
                printSectionHeader("PROXIMITY");
                ProximityExample.main(new String[]{});
                printSeparator();
            }
            
            // Geoment
            if (runAll || "geoment".equals(selectedExample)) {
                printSectionHeader("GEOMENT (GEOGRAPHIC ELEMENT GEOMETRY)");
                GeomentExample.main(new String[]{});
                printSeparator();
            }
            
            // Static Maps
            if (runAll || "staticmaps".equals(selectedExample)) {
                printSectionHeader("STATIC MAPS");
                StaticMapsExample.main(new String[]{});
                printSeparator();
            }
            
            // Snap to Road
            if (runAll || "snaptoroad".equals(selectedExample)) {
                printSectionHeader("SNAP TO ROAD");
                SnapToRoadExample.main(new String[]{});
                printSeparator();
            }
            
            // Geofencing
            if (runAll || "geofencing".equals(selectedExample)) {
                printSectionHeader("GEOFENCING");
                GeofencingExample.main(new String[]{});
                printSeparator();
            }
            
            System.out.println();
            System.out.println("╔══════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    ALL EXAMPLES COMPLETED                        ║");
            System.out.println("╚══════════════════════════════════════════════════════════════════╝");
            
        } catch (Exception e) {
            System.err.println("\n❌ Example runner failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void printSectionHeader(String title) {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────────────────────┐");
        System.out.println("│  " + title + spaces(64 - title.length()) + "│");
        System.out.println("└──────────────────────────────────────────────────────────────────┘");
        System.out.println();
    }
    
    private static void printSeparator() {
        System.out.println("\n" + "─".repeat(70) + "\n");
    }
    
    private static String spaces(int count) {
        return " ".repeat(Math.max(0, count));
    }
}
