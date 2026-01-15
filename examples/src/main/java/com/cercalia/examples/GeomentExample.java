package com.cercalia.examples;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.geoment.*;
import com.cercalia.sdk.services.GeomentService;
import com.cercalia.sdk.util.Logger;

/**
 * Geoment (Geographic Element Geometry) examples demonstrating the Cercalia SDK.
 * 
 * Download polygon geometries of administrative/geographic elements.
 * 
 * Includes:
 * - Municipality geometry download
 * - Foreign municipality geometry
 * - Region geometry download
 * - Postal code geometry download
 * - Simplified geometry (with tolerance)
 */
public class GeomentExample {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY") != null ? System.getenv("CERCALIA_API_KEY") : "YOUR_API_KEY";
    
    public static void main(String[] args) {
        // Enable debug logging
        Logger.setDebugEnabled(true);
        
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        GeomentService geoment = new GeomentService(config);
        
        System.out.println("=== Cercalia SDK - Geoment (Geographic Element Geometry) Examples ===\n");
        
        try {
            // Example 1: Municipality Geometry (Madrid)
            municipalityGeometry(geoment);
            
            // Example 2: Foreign Municipality (London Westminster)
            foreignMunicipalityGeometry(geoment);
            
            // Example 3: Region Geometry (Madrid Region)
            regionGeometry(geoment);
            
            // Example 4: Postal Code Geometry (USA)
            postalCodeGeometry(geoment);
            
            // Example 5: Simplified Geometry (higher tolerance)
            simplifiedGeometry(geoment);
            
            printUsageNotes();
            
            System.out.println("=== All geoment examples completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Example failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void municipalityGeometry(GeomentService geoment) {
        System.out.println("1. MUNICIPALITY GEOMETRY DOWNLOAD");
        System.out.println("   Example from docs: Madrid municipality (ESP280796)\n");
        
        GeomentMunicipalityOptions options = GeomentMunicipalityOptions.builder()
                .munc("ESP280796")
                .tolerance(0)
                .build();
        
        GeographicElementResult result = geoment.getMunicipalityGeometry(options);
        
        System.out.println("   Code: " + result.getCode());
        System.out.println("   Name: " + result.getName());
        System.out.println("   Type: " + result.getType());
        System.out.println("   Geometry Level: " + result.getLevel());
        System.out.println("   WKT length: " + result.getWkt().length() + " characters\n");
    }
    
    private static void foreignMunicipalityGeometry(GeomentService geoment) {
        System.out.println("2. FOREIGN MUNICIPALITY GEOMETRY");
        System.out.println("   Example from docs: London Westminster (GBRE02AP)\n");
        
        GeomentMunicipalityOptions options = GeomentMunicipalityOptions.builder()
                .munc("GBRE02AP")
                .tolerance(0)
                .build();
        
        GeographicElementResult result = geoment.getMunicipalityGeometry(options);
        
        System.out.println("   Code: " + result.getCode());
        System.out.println("   Name: " + result.getName());
        String wktPreview = result.getWkt().length() > 100 ? 
                result.getWkt().substring(0, 100) + "..." : result.getWkt();
        System.out.println("   WKT preview: " + wktPreview + "\n");
    }
    
    private static void regionGeometry(GeomentService geoment) {
        System.out.println("3. REGION GEOMETRY DOWNLOAD");
        System.out.println("   Example from docs: Madrid region (ESP28)\n");
        
        GeomentMunicipalityOptions options = GeomentMunicipalityOptions.builder()
                .subregc("ESP28")
                .tolerance(0)
                .build();
        
        GeographicElementResult result = geoment.getMunicipalityGeometry(options);
        
        System.out.println("   Code: " + result.getCode());
        System.out.println("   Name: " + result.getName());
        System.out.println("   Type: " + result.getType());
        System.out.println("   WKT length: " + result.getWkt().length() + " characters\n");
    }
    
    private static void postalCodeGeometry(GeomentService geoment) {
        System.out.println("4. POSTAL CODE GEOMETRY DOWNLOAD");
        System.out.println("   Example from docs: Postal code 06405 (USA)\n");
        
        GeomentPostalCodeOptions options = GeomentPostalCodeOptions.builder("06405")
                .ctryc("USA")
                .tolerance(0)
                .build();
        
        GeographicElementResult result = geoment.getPostalCodeGeometry(options);
        
        System.out.println("   Code: " + result.getCode());
        System.out.println("   Name: " + result.getName());
        System.out.println("   Type: " + result.getType());
        System.out.println("   Geometry Level: " + result.getLevel());
        String wktPreview = result.getWkt().length() > 100 ? 
                result.getWkt().substring(0, 100) + "..." : result.getWkt();
        System.out.println("   WKT preview: " + wktPreview + "\n");
    }
    
    private static void simplifiedGeometry(GeomentService geoment) {
        System.out.println("5. SIMPLIFIED GEOMETRY (with tolerance)");
        System.out.println("   Comparing original vs. simplified municipality geometry\n");
        
        // Original geometry
        GeomentMunicipalityOptions originalOptions = GeomentMunicipalityOptions.builder()
                .munc("ESP502973")
                .tolerance(0)
                .build();
        GeographicElementResult original = geoment.getMunicipalityGeometry(originalOptions);
        
        // Simplified geometry
        GeomentMunicipalityOptions simplifiedOptions = GeomentMunicipalityOptions.builder()
                .munc("ESP502973")
                .tolerance(100)  // 100 meters simplification
                .build();
        GeographicElementResult simplified = geoment.getMunicipalityGeometry(simplifiedOptions);
        
        System.out.println("   Original geometry:");
        System.out.println("     WKT length: " + original.getWkt().length() + " characters");
        System.out.println("   Simplified geometry (100m tolerance):");
        System.out.println("     WKT length: " + simplified.getWkt().length() + " characters");
        
        double reduction = (1.0 - (double) simplified.getWkt().length() / original.getWkt().length()) * 100;
        System.out.println("     Reduction: " + String.format("%.1f", reduction) + "%\n");
    }
    
    private static void printUsageNotes() {
        System.out.println("Note: WKT (Well-Known Text) geometries can be used with spatial libraries");
        System.out.println("like JTS (Java Topology Suite), PostGIS, or displayed on maps with Leaflet/Mapbox.\n");
        System.out.println("Supported geographic elements:");
        System.out.println("  - Municipalities (munc parameter)");
        System.out.println("  - Regions/Subregions (subregc parameter)");
        System.out.println("  - Postal Codes (pcode + ctryc parameters)");
        System.out.println("  - POIs (poic parameter - if available)\n");
    }
}
