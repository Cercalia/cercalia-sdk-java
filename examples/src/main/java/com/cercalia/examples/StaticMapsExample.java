package com.cercalia.examples;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.staticmaps.*;
import com.cercalia.sdk.services.StaticMapsService;
import com.cercalia.sdk.util.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Static Maps examples demonstrating the Cercalia SDK.
 * 
 * Generate static map images with markers, shapes and labels.
 * 
 * Includes:
 * - City map
 * - Map with markers
 * - Circle shape
 * - Rectangle shape
 * - Polyline
 * - Map with label
 */
public class StaticMapsExample {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY") != null ? System.getenv("CERCALIA_API_KEY") : "YOUR_API_KEY";
    
    public static void main(String[] args) {
        // Enable debug logging
        Logger.setDebugEnabled(true);
        
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        StaticMapsService staticMaps = new StaticMapsService(config);
        
        System.out.println("=== Cercalia SDK - Static Maps Examples ===\n");
        
        try {
            // Example 1: City Map
            cityMap(staticMaps);
            
            // Example 2: Map with Markers
            mapWithMarkers(staticMaps);
            
            // Example 3: Circle Shape
            circleShape(staticMaps);
            
            // Example 4: Rectangle Shape
            rectangleShape(staticMaps);
            
            // Example 5: Polyline
            polyline(staticMaps);
            
            // Example 6: Map with Label
            mapWithLabel(staticMaps);
            
            System.out.println("=== All static maps examples completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Example failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void cityMap(StaticMapsService staticMaps) {
        System.out.println("1. CITY MAP");
        System.out.println("   Generate a static map centered on a city\n");
        
        StaticMapResult result = staticMaps.generateCityMap("Girona", "ESP", 350, 250);
        
        System.out.println("   Image URL: " + result.getImageUrl());
        System.out.println("   Dimensions: " + result.getWidth() + "x" + result.getHeight() + "\n");
    }
    
    private static void mapWithMarkers(StaticMapsService staticMaps) {
        System.out.println("2. MAP WITH MARKERS");
        System.out.println("   Place multiple markers on the map\n");
        
        List<StaticMapMarker> markers = Arrays.asList(
                StaticMapMarker.at(new Coordinate(41.3851, 2.1734), 1),
                StaticMapMarker.at(new Coordinate(41.4034, 2.1741), 2)
        );
        
        StaticMapResult result = staticMaps.generateMapWithMarkers(markers, 400, 300);
        
        System.out.println("   Image URL: " + result.getImageUrl());
        System.out.println("   Number of markers: 2\n");
    }
    
    private static void circleShape(StaticMapsService staticMaps) {
        System.out.println("3. CIRCLE SHAPE");
        System.out.println("   Draw a circle with custom colors\n");
        
        Coordinate center = new Coordinate(41.439132726, 2.003108336);
        
        StaticMapCircle circle = StaticMapCircle.builder(center, 2000)
                .outlineColor(RGBAColor.rgba(255, 0, 0, 128))
                .outlineSize(2)
                .fillColor(RGBAColor.rgba(0, 255, 0, 128))
                .build();
        
        StaticMapResult result = staticMaps.generateMapWithCircle(center, 2000, circle, 400, 300);
        
        System.out.println("   Image URL: " + result.getImageUrl());
        System.out.println("   Circle radius: 2000 meters\n");
    }
    
    private static void rectangleShape(StaticMapsService staticMaps) {
        System.out.println("4. RECTANGLE SHAPE");
        System.out.println("   Draw a rectangle defined by two corners\n");
        
        Coordinate upperLeft = new Coordinate(41.98, 2.82);
        Coordinate lowerRight = new Coordinate(41.96, 2.84);
        
        StaticMapRectangle rectangle = StaticMapRectangle.builder(upperLeft, lowerRight)
                .outlineColor(RGBAColor.rgb(255, 0, 0))
                .outlineSize(3)
                .fillColor(RGBAColor.rgba(0, 255, 0, 128))
                .build();
        
        StaticMapResult result = staticMaps.generateMapWithRectangle(upperLeft, lowerRight, rectangle, "Girona", 400, 300);
        
        System.out.println("   Image URL: " + result.getImageUrl());
        System.out.println("   Rectangle corners: (41.98, 2.82) to (41.96, 2.84)\n");
    }
    
    private static void polyline(StaticMapsService staticMaps) {
        System.out.println("5. POLYLINE");
        System.out.println("   Draw a multi-segment line connecting points\n");
        
        List<Coordinate> coordinates = Arrays.asList(
                new Coordinate(41.401902461, 2.142455003),
                new Coordinate(41.404628181, 2.155965665),
                new Coordinate(41.433339308, 2.179860852)
        );
        
        StaticMapPolyline polyline = StaticMapPolyline.builder(coordinates)
                .outlineColor(RGBAColor.rgb(255, 0, 0))
                .outlineSize(2)
                .build();
        
        StaticMapResult result = staticMaps.generateMapWithPolyline(coordinates, polyline, 400, 300);
        
        System.out.println("   Image URL: " + result.getImageUrl());
        System.out.println("   Number of line segments: 3\n");
    }
    
    private static void mapWithLabel(StaticMapsService staticMaps) {
        System.out.println("6. MAP WITH LABEL");
        System.out.println("   Add a text label to the map\n");
        
        Coordinate center = new Coordinate(41.3851, 2.1734);
        
        StaticMapResult result = staticMaps.generateMapWithLabel(center, "Barcelona", 400, 300);
        
        System.out.println("   Image URL: " + result.getImageUrl());
        System.out.println("   Label text: \"Barcelona\"\n");
    }
}
