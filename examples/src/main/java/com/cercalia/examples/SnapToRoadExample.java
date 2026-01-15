package com.cercalia.examples;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.snaptoroad.*;
import com.cercalia.sdk.services.SnapToRoadService;
import com.cercalia.sdk.util.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Snap to Road examples demonstrating the Cercalia SDK.
 * 
 * GPS Track Map Matching - match raw GPS coordinates to the road network.
 * 
 * Based on official documentation example:
 * https://docs.cercalia.com/docs/cercalia-webservices/snap-to-road/
 * 
 * Includes:
 * - Basic map matching
 * - Map matching with speeding detection
 */
public class SnapToRoadExample {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY") != null ? System.getenv("CERCALIA_API_KEY") : "YOUR_API_KEY";
    
    public static void main(String[] args) {
        // Enable debug logging
        Logger.setDebugEnabled(true);
        
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        SnapToRoadService snapToRoad = new SnapToRoadService(config);
        
        System.out.println("=== Cercalia SDK - Snap to Road Example ===\n");
        System.out.println("Based on official documentation example:");
        System.out.println("https://docs.cercalia.com/docs/cercalia-webservices/snap-to-road/\n");
        
        try {
            // Main example: Official documentation example
            officialDocExample(snapToRoad);
            
            System.out.println("=== Snap to Road example completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Example failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void officialDocExample(SnapToRoadService snapToRoad) {
        // OFFICIAL DOCUMENTATION EXAMPLE
        // Request URL from docs:
        // https://lb.cercalia.com/services/v2/json?cmd=geomtrack&mode=regular&
        // TRACK=[2.825850,41.969279@0,45@@70@@@A],[2.822355,41.965995@0,45@@10@@@A],[2.828963,41.968589@0,45@@10@@@B],[2.828278,41.965013@0,45@@10@@@B]&
        // SPEEDING=true&SPEEDTOLERANCE=10&SRS=EPSG:4326&key=YOUR_API_KEY
        
        System.out.println("Track Format: [lng,lat@compass,angle@@speed@@@attribute]");
        System.out.println("Example: [2.825850,41.969279@0,45@@70@@@A]\n");
        
        // Exact GPS track from documentation
        List<SnapToRoadPoint> track = Arrays.asList(
                SnapToRoadPoint.builder()
                        .coord(new Coordinate(41.969279, 2.825850))
                        .compass(0).angle(45).speed(70).attribute("A").build(),
                SnapToRoadPoint.builder()
                        .coord(new Coordinate(41.965995, 2.822355))
                        .compass(0).angle(45).speed(10).attribute("A").build(),
                SnapToRoadPoint.builder()
                        .coord(new Coordinate(41.968589, 2.828963))
                        .compass(0).angle(45).speed(10).attribute("B").build(),
                SnapToRoadPoint.builder()
                        .coord(new Coordinate(41.965013, 2.828278))
                        .compass(0).angle(45).speed(10).attribute("B").build()
        );
        
        // Options from documentation
        SnapToRoadOptions options = SnapToRoadOptions.builder()
                .speeding(true)
                .speedTolerance(10)
                .build();
        
        System.out.println("Parameters:");
        System.out.println("  - 4 GPS points with speed and direction data");
        System.out.println("  - speeding: true (detect speed violations)");
        System.out.println("  - speedTolerance: 10 km/h");
        System.out.println("  - Segments grouped by attribute (A, B)\n");
        
        System.out.println("Making request to Cercalia API...\n");
        SnapToRoadResult result = snapToRoad.match(track, options);
        
        System.out.println("Result Summary:");
        System.out.println("  Total Distance: " + String.format("%.2f", result.getTotalDistance()) + " km");
        System.out.println("  Total Segments: " + result.getSegments().size() + "\n");
        
        System.out.println("Segments Detail:");
        int idx = 1;
        for (SnapToRoadSegment seg : result.getSegments()) {
            System.out.println("  Segment " + idx + ":");
            System.out.println("    Attribute: " + (seg.getAttribute() != null ? seg.getAttribute() : "N/A"));
            System.out.println("    Distance: " + String.format("%.2f", seg.getDistance()) + " km");
            if (seg.getSpeeding() != null) {
                System.out.println("    Speeding: " + seg.getSpeeding());
            }
            if (seg.getSpeedingLevel() != null) {
                System.out.println("    Speeding Level: " + seg.getSpeedingLevel());
            }
            String wktPreview = seg.getWkt().length() > 80 ? 
                    seg.getWkt().substring(0, 80) + "..." : seg.getWkt();
            System.out.println("    WKT (first 80 chars): " + wktPreview);
            System.out.println();
            idx++;
        }
    }
}
