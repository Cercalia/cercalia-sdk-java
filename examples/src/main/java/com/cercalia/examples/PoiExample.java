package com.cercalia.examples;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.poi.*;
import com.cercalia.sdk.services.PoiService;
import com.cercalia.sdk.util.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * POI (Points of Interest) examples demonstrating the Cercalia SDK.
 * 
 * Includes:
 * - Nearest POIs by straight-line distance
 * - Nearest POIs with routing
 * - POIs inside map extent
 * - POIs with zoom filtering (gridsize)
 * - POIs inside polygon (WKT)
 * - Weather forecast
 */
public class PoiExample {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY") != null ? System.getenv("CERCALIA_API_KEY") : "YOUR_API_KEY";
    
    public static void main(String[] args) {
        // Enable debug logging
        Logger.setDebugEnabled(true);
        
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        PoiService poi = new PoiService(config);
        
        System.out.println("=== Cercalia SDK - POI Examples ===\n");
        
        try {
            // Example 1: Get nearest POIs (straight-line distance)
            nearestPois(poi);
            
            // Example 2: Get nearest POIs with routing
            nearestPoisWithRouting(poi);
            
            // Example 3: Get POIs inside map extent
            poisInExtent(poi);
            
            // Example 4: Get POIs with zoom filtering (gridsize)
            poisWithGridFilter(poi);
            
            // Example 5: Get POIs inside a polygon
            poisInPolygon(poi);
            
            // Example 6: Weather forecast
            weatherForecast(poi);
            
            System.out.println("=== All POI examples completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Example failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void nearestPois(PoiService poi) {
        System.out.println("1. NEAREST POIs - STRAIGHT-LINE DISTANCE");
        System.out.println("   Example from docs: Gas stations (C001) near Madrid (40.3691,-3.589)\n");
        
        Coordinate madridCenter = new Coordinate(40.3691, -3.589);
        
        PoiNearestOptions options = PoiNearestOptions.builder()
                .categories(Collections.singletonList("C001"))
                .limit(2)
                .radius(10000)
                .build();
        
        List<Poi> results = poi.searchNearest(madridCenter, options);
        
        System.out.println("   Found " + results.size() + " POIs");
        if (!results.isEmpty()) {
            Poi p = results.get(0);
            System.out.println("   Closest: " + p.getName());
            System.out.println("   Category: " + p.getCategoryCode() + 
                    (p.getSubcategoryCode() != null ? ", Subcategory: " + p.getSubcategoryCode() : ""));
            if (p.getGe() != null) {
                System.out.println("   Locality: " + p.getGe().getLocality() + 
                        " (Code: " + p.getGe().getLocalityCode() + ")");
                System.out.println("   Municipality: " + p.getGe().getMunicipality() + 
                        " (Code: " + p.getGe().getMunicipalityCode() + ")");
            }
            System.out.println("   Distance: " + p.getDistance() + "m");
            System.out.println("   Coordinates: " + p.getCoord().getLat() + ", " + p.getCoord().getLng() + "\n");
        }
    }
    
    private static void nearestPoisWithRouting(PoiService poi) {
        System.out.println("2. NEAREST POIs - USING ROUTING");
        System.out.println("   Example from docs: Gas stations (C001) near Madrid, ordered by route time\n");
        
        Coordinate madridCenter = new Coordinate(40.3691, -3.589);
        
        PoiNearestWithRoutingOptions options = PoiNearestWithRoutingOptions.builder()
                .categories(Collections.singletonList("C001"))
                .limit(2)
                .weight(PoiRouteWeight.TIME)
                .inverse(0)
                .build();
        
        List<Poi> results = poi.searchNearestWithRouting(madridCenter, options);
        
        System.out.println("   Found " + results.size() + " POIs");
        if (!results.isEmpty()) {
            Poi p = results.get(0);
            System.out.println("   Closest by route: " + p.getName());
            System.out.println("   Straight distance: " + p.getDistance() + "m");
            if (p.getRouteDistance() != null) {
                System.out.println("   Route distance: " + p.getRouteDistance() + "m");
            }
            if (p.getRouteTime() != null) {
                System.out.println("   Route time: " + p.getRouteTime() + "ms (" + 
                        Math.round(p.getRouteTime() / 1000.0) + "s)");
            }
            if (p.getRouteWeight() != null) {
                System.out.println("   Route weight: " + p.getRouteWeight());
            }
            System.out.println();
        }
    }
    
    private static void poisInExtent(PoiService poi) {
        System.out.println("3. POIs INSIDE MAP EXTENT");
        System.out.println("   Example from docs: Gas stations in Huesca map extent\n");
        
        MapExtent extent = new MapExtent(
                new Coordinate(42.144102962, -0.414886914),  // Upper-left
                new Coordinate(42.139342832, -0.407628526)   // Lower-right
        );
        
        PoiInExtentOptions options = PoiInExtentOptions.builder()
                .categories(Collections.singletonList("D00GAS"))
                .build();
        
        List<Poi> results = poi.searchInExtent(extent, options);
        
        System.out.println("   Found " + results.size() + " POIs in extent");
        if (!results.isEmpty()) {
            Poi p = results.get(0);
            System.out.println("   First POI: " + p.getName());
            System.out.println("   Category: " + p.getCategoryCode() + 
                    (p.getSubcategoryCode() != null ? ", Subcategory: " + p.getSubcategoryCode() : ""));
            if (p.getInfo() != null) {
                System.out.println("   Info: " + p.getInfo().substring(0, Math.min(50, p.getInfo().length())) + "...");
            }
            if (p.getGe() != null && p.getGe().getSubregion() != null) {
                System.out.println("   Subregion: " + p.getGe().getSubregion() + 
                        " (Code: " + p.getGe().getSubregionCode() + ")");
            }
            System.out.println();
        }
    }
    
    private static void poisWithGridFilter(PoiService poi) {
        System.out.println("4. POIs WITH ZOOM FILTERING (GRIDSIZE)");
        System.out.println("   Example from docs: Gas stations in Huesca, filtered by grid\n");
        
        MapExtent extent = new MapExtent(
                new Coordinate(42.144102962, -0.414886914),  // Upper-left
                new Coordinate(42.139342832, -0.407628526)   // Lower-right
        );
        
        PoiInExtentOptions options = PoiInExtentOptions.builder()
                .categories(Collections.singletonList("D00GAS"))
                .gridSize(100)
                .build();
        
        List<Poi> results = poi.searchInExtent(extent, options);
        
        System.out.println("   Found " + results.size() + " POIs (filtered by grid)");
        System.out.println("   Grid filtering reduces POI density for better map visualization\n");
    }
    
    private static void poisInPolygon(PoiService poi) {
        System.out.println("5. POIs INSIDE POLYGON (WKT)");
        System.out.println("   Example from docs: Gas stations inside Barcelona polygon\n");
        
        // Simplified polygon around Barcelona center
        String wkt = "POLYGON((2.16 41.42, 2.18 41.42, 2.18 41.39, 2.16 41.39, 2.16 41.42))";
        
        PoiInPolygonOptions options = PoiInPolygonOptions.builder()
                .wkt(wkt)
                .categories(Collections.singletonList("C001"))
                .build();
        
        List<Poi> results = poi.searchInPolygon(options);
        
        System.out.println("   Found " + results.size() + " POIs inside polygon");
        if (!results.isEmpty()) {
            Poi p = results.get(0);
            System.out.println("   First POI: " + p.getName());
            if (p.getGe() != null) {
                String street = p.getGe().getStreet();
                String houseNumber = p.getGe().getHouseNumber();
                if (street != null) {
                    System.out.println("   Street: " + street + (houseNumber != null ? " " + houseNumber : ""));
                }
                System.out.println("   Locality: " + p.getGe().getLocality());
            }
            System.out.println("   Coordinates: " + p.getCoord().getLat() + ", " + p.getCoord().getLng() + "\n");
        }
    }
    
    private static void weatherForecast(PoiService poi) {
        System.out.println("6. WEATHER FORECAST");
        System.out.println("   Example from docs: Weather for Barcelona (41.39818,2.1490287)\n");
        
        Coordinate barcelonaCenter = new Coordinate(41.39818, 2.1490287);
        
        WeatherForecast weather = poi.getWeatherForecast(barcelonaCenter);
        
        if (weather != null && !weather.getForecasts().isEmpty()) {
            System.out.println("   Weather station: " + weather.getLocationName());
            System.out.println("   Coordinates: " + weather.getCoord().getLat() + ", " + weather.getCoord().getLng());
            System.out.println("   Last update: " + weather.getLastUpdate());
            System.out.println("   Forecast days available: " + weather.getForecasts().size());
            
            System.out.println("\n   First day forecast:");
            WeatherDayForecast day1 = weather.getForecasts().get(0);
            System.out.println("     Date: " + day1.getDate());
            System.out.println("     Temperature: " + day1.getTemperatureMin() + "°C - " + day1.getTemperatureMax() + "°C");
            if (day1.getSkyConditions0012() != null) {
                System.out.println("     Sky conditions (00-12): " + day1.getSkyConditions0012());
            }
            if (day1.getSkyConditions1224() != null) {
                System.out.println("     Sky conditions (12-24): " + day1.getSkyConditions1224());
            }
            System.out.println("\n   Note: The forecast contains " + weather.getForecasts().size() + " days of weather data\n");
        } else {
            System.out.println("   Weather forecast not available for this location\n");
        }
    }
}
