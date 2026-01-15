package com.cercalia.examples;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.geocoding.GeocodingCandidate;
import com.cercalia.sdk.model.reversegeocoding.*;
import com.cercalia.sdk.services.ReverseGeocodingService;

import java.util.Arrays;
import java.util.List;

/**
 * Cercalia SDK - Reverse Geocoding Examples
 * 
 * Examples based on the official API documentation:
 * - Basic reverse geocoding (address)
 * - Batch reverse geocoding
 * - Get timezone information
 * - Get intersecting regions (WKT polygon)
 * - Get census section (Spain only)
 * - Get SIGPAC parcel (Spain only)
 */
public class ReverseGeocodingExample {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY") != null ? System.getenv("CERCALIA_API_KEY") : "YOUR_API_KEY";
    private static final String BASE_URL = "https://lb.cercalia.com/services/v2/json";
    
    public static void main(String[] args) {
        System.out.println("--- Cercalia SDK - Reverse Geocoding Service Examples ---\n");
        
        try {
            // Initialize service
            CercaliaConfig config = new CercaliaConfig(API_KEY, BASE_URL);
            ReverseGeocodingService reverse = new ReverseGeocodingService(config);
            
            // Example 1: Basic Reverse Geocoding (Address)
            example1BasicReverseGeocoding(reverse);
            
            // Example 2: Batch Reverse Geocoding
            example2BatchReverseGeocoding(reverse);
            
            // Example 3: Get Timezone Information
            example3GetTimezone(reverse);
            
            // Example 4: Get Intersecting Regions (WKT Polygon)
            example4GetIntersectingRegions(reverse);
            
            // Example 5: Get Census Section (Spain Only)
            example5GetCensusSection(reverse);
            
            // Example 6: Get SIGPAC Parcel (Spain Only)
            example6GetSigpacParcel(reverse);
            
            System.out.println("\n--- All examples completed successfully ---");
            
        } catch (Exception e) {
            System.err.println("Example failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void example1BasicReverseGeocoding(ReverseGeocodingService reverse) {
        System.out.println("=== Example 1: Reverse Geocoding - Address ===");
        System.out.println("Reverse geocoding coordinates to get address information...\n");
        
        Coordinate jaenCoord = new Coordinate(37.777041, -3.785477);
        ReverseGeocodeOptions options = ReverseGeocodeOptions.builder()
                .level(ReverseGeocodeLevel.ADR)
                .build();
        
        ReverseGeocodeResult result = reverse.reverseGeocode(jaenCoord, options);
        
        if (result != null) {
            GeocodingCandidate ge = result.getGe();
            System.out.println("Found address:");
            System.out.println("  Name: " + ge.getName());
            System.out.println("  ID: " + ge.getId());
            
            if (ge.getStreet() != null) {
                System.out.println("  Street: " + ge.getStreet() + " (" + ge.getStreetCode() + ")");
            }
            if (ge.getHouseNumber() != null) {
                System.out.println("  House Number: " + ge.getHouseNumber());
            }
            if (ge.getLocality() != null) {
                System.out.println("  Locality: " + ge.getLocality() + " (" + ge.getLocalityCode() + ")");
            }
            if (ge.getMunicipality() != null) {
                System.out.println("  Municipality: " + ge.getMunicipality() + " (" + ge.getMunicipalityCode() + ")");
            }
            if (ge.getSubregion() != null) {
                System.out.println("  Subregion: " + ge.getSubregion() + " (" + ge.getSubregionCode() + ")");
            }
            if (ge.getRegion() != null) {
                System.out.println("  Region: " + ge.getRegion() + " (" + ge.getRegionCode() + ")");
            }
            if (ge.getCountry() != null) {
                System.out.println("  Country: " + ge.getCountry() + " (" + ge.getCountryCode() + ")");
            }
            if (ge.getPostalCode() != null) {
                System.out.println("  Postal Code: " + ge.getPostalCode());
            }
            if (result.getDistance() != null) {
                System.out.println("  Distance from input: " + result.getDistance() + "m");
            }
            if (result.getMaxSpeed() != null) {
                System.out.println("  Max Speed: " + result.getMaxSpeed() + " km/h");
            }
            System.out.println("  Type: " + ge.getType());
            System.out.println("  Level: " + ge.getLevel());
            System.out.println("  Coordinates: (" + ge.getCoord().getLat() + ", " + ge.getCoord().getLng() + ")");
        }
    }
    
    private static void example2BatchReverseGeocoding(ReverseGeocodingService reverse) {
        System.out.println("\n\n=== Example 2: Batch Reverse Geocoding ===");
        System.out.println("Reverse geocoding multiple coordinates in one request...\n");
        
        List<Coordinate> coords = Arrays.asList(
                new Coordinate(37.777041, -3.785477),
                new Coordinate(37.877041, -3.785770)
        );
        
        ReverseGeocodeOptions options = ReverseGeocodeOptions.builder()
                .level(ReverseGeocodeLevel.ADR)
                .build();
        
        List<ReverseGeocodeResult> results = reverse.reverseGeocodeBatch(coords, options);
        
        System.out.println("Found " + results.size() + " results:");
        for (int i = 0; i < results.size(); i++) {
            ReverseGeocodeResult result = results.get(i);
            GeocodingCandidate ge = result.getGe();
            System.out.println("\n" + (i + 1) + ". " + ge.getName());
            System.out.println("   Locality: " + (ge.getLocality() != null ? ge.getLocality() : "N/A"));
            System.out.println("   Municipality: " + (ge.getMunicipality() != null ? ge.getMunicipality() : "N/A") + 
                    " (" + (ge.getMunicipalityCode() != null ? ge.getMunicipalityCode() : "N/A") + ")");
            if (result.getDistance() != null) {
                System.out.println("   Distance: " + result.getDistance() + "m");
            }
        }
    }
    
    private static void example3GetTimezone(ReverseGeocodingService reverse) {
        System.out.println("\n\n=== Example 3: Get Timezone Information ===");
        System.out.println("Getting timezone info for coordinates in Poland...\n");
        
        Coordinate warsawCoord = new Coordinate(52.252025, 20.995254);
        TimezoneOptions options = TimezoneOptions.builder()
                .dateTime("2019-09-27T14:30:12Z")
                .build();
        
        TimezoneResult result = reverse.getTimezone(warsawCoord, options);
        
        if (result != null) {
            System.out.println("Timezone Information:");
            System.out.println("  ID: " + result.getId());
            System.out.println("  Name: " + result.getName());
            System.out.println("  Local DateTime: " + result.getLocalDateTime());
            System.out.println("  UTC DateTime: " + result.getUtcDateTime());
            System.out.println("  UTC Offset: " + (result.getUtcOffset() / 1000) + "s");
            System.out.println("  Daylight Saving: " + (result.getDaylightSavingTime() / 1000) + "s");
        }
    }
    
    private static void example4GetIntersectingRegions(ReverseGeocodingService reverse) {
        System.out.println("\n\n=== Example 4: Get Intersecting Regions ===");
        System.out.println("Finding municipalities that intersect a polygon...\n");
        
        String wkt = "POLYGON((2.1 41.3,2.2 41.3,2.2 41.4,2.1 41.4,2.1 41.3))";
        List<ReverseGeocodeResult> results = reverse.getIntersectingRegions(wkt, "mun");
        
        System.out.println("Found " + results.size() + " municipalities:");
        for (int i = 0; i < results.size(); i++) {
            ReverseGeocodeResult result = results.get(i);
            GeocodingCandidate ge = result.getGe();
            System.out.println((i + 1) + ". " + ge.getName() + " (" + ge.getId() + ")");
            if (ge.getSubregion() != null) {
                System.out.println("   Subregion: " + ge.getSubregion() + " (" + ge.getSubregionCode() + ")");
            }
            if (ge.getRegion() != null) {
                System.out.println("   Region: " + ge.getRegion() + " (" + ge.getRegionCode() + ")");
            }
        }
    }
    
    private static void example5GetCensusSection(ReverseGeocodingService reverse) {
        System.out.println("\n\n=== Example 5: Get Census Section (Spain Only) ===");
        System.out.println("Getting census section for a coordinate in Madrid...\n");
        
        Coordinate madridCoord = new Coordinate(40.344689, -3.653152);
        ReverseGeocodeOptions options = ReverseGeocodeOptions.builder()
                .category("d00seccen")
                .build();
        
        ReverseGeocodeResult result = reverse.reverseGeocode(madridCoord, options);
        
        if (result != null) {
            GeocodingCandidate ge = result.getGe();
            System.out.println("Census Section Information:");
            if (result.getCensusId() != null) {
                System.out.println("  Census ID: " + result.getCensusId());
            }
            System.out.println("  POI ID: " + ge.getId());
            System.out.println("  Name: " + ge.getName());
            if (ge.getLocality() != null) {
                System.out.println("  Locality: " + ge.getLocality() + " (" + ge.getLocalityCode() + ")");
            }
            if (ge.getMunicipality() != null) {
                System.out.println("  Municipality: " + ge.getMunicipality() + " (" + ge.getMunicipalityCode() + ")");
            }
        }
    }
    
    private static void example6GetSigpacParcel(ReverseGeocodingService reverse) {
        System.out.println("\n\n=== Example 6: Get SIGPAC Agricultural Parcel (Spain Only) ===");
        System.out.println("Getting SIGPAC parcel info for agricultural land...\n");
        
        Coordinate sigpacCoord = new Coordinate(41.426362, 2.038241);
        ReverseGeocodeOptions options = ReverseGeocodeOptions.builder()
                .category("d00sigpac")
                .build();
        
        ReverseGeocodeResult result = reverse.reverseGeocode(sigpacCoord, options);
        
        if (result != null) {
            GeocodingCandidate ge = result.getGe();
            SigpacInfo sigpac = result.getSigpac();
            
            if (sigpac != null) {
                System.out.println("SIGPAC Parcel Information:");
                System.out.println("  Parcel ID: " + sigpac.getId());
                System.out.println("  Municipality Code: " + sigpac.getMunicipalityCode());
                System.out.println("  Land Usage: " + sigpac.getUsage());
                System.out.println("  Extension: " + sigpac.getExtensionHa() + " Ha");
                if (sigpac.getVulnerableType() != null) {
                    System.out.println("  Vulnerable Type: " + sigpac.getVulnerableType());
                }
                if (sigpac.getVulnerableCode() != null) {
                    System.out.println("  Vulnerable Code: " + sigpac.getVulnerableCode());
                }
            }
            
            System.out.println("\nGeographic Information:");
            if (ge.getLocality() != null) {
                System.out.println("  Locality: " + ge.getLocality() + " (" + ge.getLocalityCode() + ")");
            }
            if (ge.getMunicipality() != null) {
                System.out.println("  Municipality: " + ge.getMunicipality() + " (" + ge.getMunicipalityCode() + ")");
            }
            if (ge.getRegion() != null) {
                System.out.println("  Region: " + ge.getRegion() + " (" + ge.getRegionCode() + ")");
            }
        }
    }
}
