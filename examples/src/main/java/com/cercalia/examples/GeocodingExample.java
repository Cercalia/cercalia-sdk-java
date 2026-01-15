package com.cercalia.examples;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.geocoding.*;
import com.cercalia.sdk.services.GeocodingService;
import com.cercalia.sdk.util.Logger;

import java.util.List;

/**
 * Geocoding examples demonstrating the Cercalia SDK.
 */
public class GeocodingExample {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY") != null ? System.getenv("CERCALIA_API_KEY") : "YOUR_API_KEY";
    
    public static void main(String[] args) {
        // Enable debug logging
        Logger.setDebugEnabled(true);
        
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        GeocodingService geocoding = new GeocodingService(config);
        
        System.out.println("=== Cercalia SDK - Geocoding Examples ===\n");
        
        try {
            // Example 1: Structured Search
            structuredAddressSearch(geocoding);
            
            // Example 2: Single Parameter Search
            singleParameterSearch(geocoding);
            
            // Example 3: Locality Only Search
            localitySearch(geocoding);
            
            // Example 4: Postal Code Search
            postalCodeSearch(geocoding);
            
            // Example 5: Road Milestone
            roadMilestoneSearch(geocoding);
            
            // Example 6: Ambiguous Road Milestone
            ambiguousRoadSearch(geocoding);
            
            // Example 7: Cities by Postal Code
            citiesByPostalCode(geocoding);
            
            // Example 8: Advanced Search
            advancedSearch(geocoding);
            
            System.out.println("=== All examples completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Example failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void structuredAddressSearch(GeocodingService geocoding) {
        System.out.println("1. STRUCTURED ADDRESS SEARCH");
        System.out.println("   Example from docs: \"diagonal 22, barcelona\"\n");
        
        List<GeocodingCandidate> results = geocoding.geocode(
                GeocodingOptions.builder()
                        .street("diagonal 22")
                        .locality("barcelona")
                        .countryCode("ESP")
                        .build()
        );
        
        System.out.println("   Found " + results.size() + " results");
        if (!results.isEmpty()) {
            GeocodingCandidate r = results.get(0);
            System.out.println("   Best match: " + r.getName());
            System.out.println("   Locality: " + r.getLocality() + " (ID: " + r.getLocalityCode() + ")");
            System.out.println("   Municipality: " + r.getMunicipality() + " (ID: " + r.getMunicipalityCode() + ")");
            System.out.println("   Coordinates: " + r.getCoord().getLat() + ", " + r.getCoord().getLng());
            System.out.println("   Type: " + r.getType() + ", Level: " + r.getLevel() + "\n");
        }
    }
    
    private static void singleParameterSearch(GeocodingService geocoding) {
        System.out.println("2. SINGLE PARAMETER SEARCH");
        System.out.println("   Example from docs: \"provença 589, 08026 barcelona\"\n");
        
        List<GeocodingCandidate> results = geocoding.geocode(
                GeocodingOptions.builder()
                        .street("provença 589, 08026 barcelona")
                        .countryCode("ESP")
                        .build()
        );
        
        System.out.println("   Found " + results.size() + " results");
        if (!results.isEmpty()) {
            GeocodingCandidate r = results.get(0);
            System.out.println("   Address: " + r.getName());
            System.out.println("   Postal Code: " + r.getPostalCode());
            System.out.println("   District: " + r.getDistrict() + " (ID: " + r.getDistrictCode() + ")");
            System.out.println("   Subregion: " + r.getSubregion() + " (ID: " + r.getSubregionCode() + ")");
            System.out.println("   Region: " + r.getRegion() + " (ID: " + r.getRegionCode() + ")\n");
        }
    }
    
    private static void localitySearch(GeocodingService geocoding) {
        System.out.println("3. LOCALITY SEARCH");
        System.out.println("   Searching for: Madrid\n");
        
        List<GeocodingCandidate> results = geocoding.geocode(
                GeocodingOptions.builder()
                        .locality("Madrid")
                        .countryCode("ESP")
                        .build()
        );
        
        System.out.println("   Found " + results.size() + " results");
        results.stream()
                .filter(r -> r.getRegion() != null && r.getRegion().contains("Comunidad de Madrid"))
                .findFirst()
                .ifPresent(madridCity -> {
                    System.out.println("   Madrid center: " + madridCity.getCoord().getLat() + ", " + madridCity.getCoord().getLng());
                    System.out.println("   Municipality ID: " + madridCity.getMunicipalityCode());
                    System.out.println("   Region ID: " + madridCity.getRegionCode() + "\n");
                });
    }
    
    private static void postalCodeSearch(GeocodingService geocoding) {
        System.out.println("4. POSTAL CODE SEARCH");
        System.out.println("   Searching for: 08025\n");
        
        List<GeocodingCandidate> results = geocoding.geocode(
                GeocodingOptions.builder()
                        .postalCode("08025")
                        .countryCode("ESP")
                        .build()
        );
        
        System.out.println("   Found " + results.size() + " results");
        if (!results.isEmpty()) {
            System.out.println("   Postal code center: " + results.get(0).getCoord().getLat() + 
                    ", " + results.get(0).getCoord().getLng() + "\n");
        }
    }
    
    private static void roadMilestoneSearch(GeocodingService geocoding) {
        System.out.println("5. ROAD MILESTONE GEOCODING - SINGLE RESULT");
        System.out.println("   Example from docs: M-45, KM 12 (Madrid)\n");
        
        List<GeocodingCandidate> results = geocoding.geocodeRoad("M-45", 12,
                GeocodingOptions.builder()
                        .subregion("Madrid")
                        .countryCode("ESP")
                        .build()
        );
        
        System.out.println("   Found " + results.size() + " results");
        if (!results.isEmpty()) {
            GeocodingCandidate r = results.get(0);
            System.out.println("   Location: " + r.getName());
            System.out.println("   Type: " + r.getType());
            System.out.println("   Geometry level: " + r.getLevel());
            System.out.println("   Municipality: " + r.getMunicipality() + " (ID: " + r.getMunicipalityCode() + ")");
            System.out.println("   Coordinates: " + r.getCoord().getLat() + ", " + r.getCoord().getLng() + "\n");
        }
    }
    
    private static void ambiguousRoadSearch(GeocodingService geocoding) {
        System.out.println("6. ROAD MILESTONE - AMBIGUOUS (MULTIPLE CANDIDATES)");
        System.out.println("   Example from docs: A-231, KM 13\n");
        
        List<GeocodingCandidate> results = geocoding.geocodeRoad("A-231", 13,
                GeocodingOptions.builder()
                        .countryCode("ESP")
                        .build()
        );
        
        System.out.println("   Found " + results.size() + " candidates:\n");
        for (int i = 0; i < results.size(); i++) {
            GeocodingCandidate r = results.get(i);
            System.out.println("   Candidate " + (i + 1) + ":");
            System.out.println("     Municipality: " + r.getMunicipality() + " (" + r.getMunicipalityCode() + ")");
            System.out.println("     Subregion: " + r.getSubregion() + " (" + r.getSubregionCode() + ")");
            System.out.println("     Region: " + r.getRegion());
            System.out.println("     Coordinates: " + r.getCoord().getLat() + ", " + r.getCoord().getLng() + "\n");
        }
    }
    
    private static void citiesByPostalCode(GeocodingService geocoding) {
        System.out.println("7. LIST CITIES BY POSTAL CODE");
        System.out.println("   Example from docs: Postal code 40160\n");
        
        List<PostalCodeCity> cities = geocoding.geocodeCitiesByPostalCode("40160", "ESP");
        
        System.out.println("   Found " + cities.size() + " cities:\n");
        for (PostalCodeCity city : cities) {
            System.out.println("   - " + city.getName());
            System.out.println("     Municipality: " + city.getMunicipality() + " (" + city.getMunicipalityCode() + ")");
            System.out.println("     Subregion: " + city.getSubregion() + " (" + city.getSubregionCode() + ")");
            System.out.println("     Region: " + city.getRegion() + " (" + city.getRegionCode() + ")");
            System.out.println("     Coordinates: " + city.getCoord().getLat() + ", " + city.getCoord().getLng() + "\n");
        }
    }
    
    private static void advancedSearch(GeocodingService geocoding) {
        System.out.println("8. ADVANCED SEARCH WITH ADMINISTRATIVE LEVELS");
        System.out.println("   Searching: Sabadell, Cataluña, Barcelona\n");
        
        List<GeocodingCandidate> results = geocoding.geocode(
                GeocodingOptions.builder()
                        .locality("Sabadell")
                        .region("Cataluña")
                        .subregion("Barcelona")
                        .countryCode("ESP")
                        .build()
        );
        
        System.out.println("   Found " + results.size() + " results");
        if (!results.isEmpty()) {
            GeocodingCandidate r = results.get(0);
            System.out.println("   Location: " + r.getName());
            System.out.println("   Municipality: " + r.getMunicipality() + " (" + r.getMunicipalityCode() + ")");
            System.out.println("   Subregion: " + r.getSubregion() + " (" + r.getSubregionCode() + ")");
            System.out.println("   Region: " + r.getRegion() + " (" + r.getRegionCode() + ")");
            System.out.println("   Country: " + r.getCountry() + " (ID: " + r.getCountryCode() + ")\n");
        }
    }
}
