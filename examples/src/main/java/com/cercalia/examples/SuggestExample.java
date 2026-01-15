package com.cercalia.examples;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.suggest.*;
import com.cercalia.sdk.services.SuggestService;
import com.cercalia.sdk.util.Logger;

import java.util.List;

/**
 * Suggest examples demonstrating the Cercalia SDK.
 * <p>
 * The SuggestService is designed for typeahead/autocomplete functionality.
 * It uses a different API endpoint with Solr-like JSON format.
 */
public class SuggestExample {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY") != null ? System.getenv("CERCALIA_API_KEY") : "YOUR_API_KEY";
    
    public static void main(String[] args) {
        // Enable debug logging
        Logger.setDebugEnabled(false);
        
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        SuggestService suggest = new SuggestService(config);
        
        System.out.println("=== Cercalia SDK - Suggest Examples ===\n");
        
        try {
            // Example 1: Basic Street Search
            basicStreetSearch(suggest);
            
            // Example 2: City Search
            citySearch(suggest);
            
            // Example 3: Search with House Number
            searchWithHouseNumber(suggest);
            
            // Example 4: Geocode a Suggestion
            geocodeSuggestion(suggest);
            
            // Example 5: Find and Geocode (Combined)
            findAndGeocode(suggest);
            
            // Example 6: Convenience Methods
            convenienceMethods(suggest);
            
            System.out.println("=== All examples completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Example failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void basicStreetSearch(SuggestService suggest) {
        System.out.println("1. BASIC STREET SEARCH");
        System.out.println("   Searching for \"Provença\" in Spain...\n");
        
        List<SuggestResult> results = suggest.search(SuggestOptions.builder()
                .text("Provença")
                .countryCode("ESP")
                .geoType(SuggestGeoType.ST)
                .build());
        
        System.out.println("   Found " + results.size() + " results");
        if (!results.isEmpty()) {
            SuggestResult r = results.get(0);
            System.out.println("   Best match: " + r.getDisplayText());
            System.out.println("   Type: " + r.getType());
            System.out.println("   ID: " + r.getId());
            
            if (r.getStreet() != null) {
                System.out.println("   Street:");
                System.out.println("      Code: " + r.getStreet().getCode());
                System.out.println("      Name: " + r.getStreet().getName());
                System.out.println("      Description: " + r.getStreet().getDescription());
                System.out.println("      Type: " + r.getStreet().getType());
            }
            
            if (r.getCity() != null) {
                System.out.println("   City: " + r.getCity().getName() + " (Code: " + r.getCity().getCode() + ")");
            }
            
            if (r.getMunicipality() != null) {
                System.out.println("   Municipality: " + r.getMunicipality().getName());
            }
            
            if (r.getRegion() != null) {
                System.out.println("   Region: " + r.getRegion().getName());
            }
            
            if (r.getCountry() != null) {
                System.out.println("   Country: " + r.getCountry().getName() + " (" + r.getCountry().getCode() + ")");
            }
            
            if (r.getCoord() != null) {
                System.out.println("   Coordinates: " + r.getCoord().getLat() + ", " + r.getCoord().getLng());
            }
            
            if (r.getScore() != null) {
                System.out.println("   Score: " + r.getScore());
            }
        }
        System.out.println();
    }
    
    private static void citySearch(SuggestService suggest) {
        System.out.println("2. CITY SEARCH");
        System.out.println("   Searching for \"Barcelona\" (cities only)...\n");
        
        List<SuggestResult> results = suggest.search(SuggestOptions.builder()
                .text("Barcelona")
                .countryCode("ESP")
                .geoType(SuggestGeoType.CT)
                .build());
        
        System.out.println("   Found " + results.size() + " results");
        for (int i = 0; i < Math.min(3, results.size()); i++) {
            SuggestResult r = results.get(i);
            System.out.println("   [" + (i + 1) + "] " + r.getDisplayText());
            System.out.println("       Type: " + r.getType());
            if (r.getCity() != null) {
                System.out.println("       City Code: " + r.getCity().getCode());
            }
            if (r.getSubregion() != null) {
                System.out.println("       Province: " + r.getSubregion().getName());
            }
        }
        System.out.println();
    }
    
    private static void searchWithHouseNumber(SuggestService suggest) {
        System.out.println("3. SEARCH WITH HOUSE NUMBER");
        System.out.println("   Searching for \"Paseo de la Castellana 300, Madrid\"...\n");
        
        List<SuggestResult> results = suggest.search(SuggestOptions.builder()
                .text("Paseo de la Castellana 300, Madrid")
                .countryCode("ESP")
                .geoType(SuggestGeoType.ST)
                .build());
        
        System.out.println("   Found " + results.size() + " results");
        if (!results.isEmpty()) {
            SuggestResult r = results.get(0);
            System.out.println("   Best match: " + r.getDisplayText());
            System.out.println("   Type: " + r.getType());
            
            if (r.getHouseNumbers() != null) {
                System.out.println("   House Numbers:");
                System.out.println("      Available: " + r.getHouseNumbers().isAvailable());
                System.out.println("      Range: " + r.getHouseNumbers().getMin() + " - " + r.getHouseNumbers().getMax());
                System.out.println("      Current: " + r.getHouseNumbers().getCurrent());
                System.out.println("      Adjusted: " + r.getHouseNumbers().getAdjusted());
                System.out.println("      Hint: " + r.getHouseNumbers().getHint());
            }
            
            if (r.getPostalCode() != null) {
                System.out.println("   Postal Code: " + r.getPostalCode());
            }
        }
        System.out.println();
    }
    
    private static void geocodeSuggestion(SuggestService suggest) {
        System.out.println("4. GEOCODE A SUGGESTION");
        System.out.println("   Step 1: Search for street...");
        
        // First, search to get codes
        List<SuggestResult> suggestions = suggest.search(SuggestOptions.builder()
                .text("Carrer de Provença Barcelona")
                .countryCode("ESP")
                .geoType(SuggestGeoType.ST)
                .build());
        
        if (suggestions.isEmpty()) {
            System.out.println("   No suggestions found!\n");
            return;
        }
        
        SuggestResult suggestion = suggestions.get(0);
        System.out.println("   Found: " + suggestion.getDisplayText());
        System.out.println("   Street Code: " + (suggestion.getStreet() != null ? suggestion.getStreet().getCode() : "N/A"));
        System.out.println("   City Code: " + (suggestion.getCity() != null ? suggestion.getCity().getCode() : "N/A"));
        
        System.out.println("\n   Step 2: Geocode with house number 589...");
        
        // Then geocode with specific house number
        SuggestGeocodeResult result = suggest.geocode(SuggestGeocodeOptions.builder()
                .streetCode(suggestion.getStreet() != null ? suggestion.getStreet().getCode() : null)
                .cityCode(suggestion.getCity() != null ? suggestion.getCity().getCode() : null)
                .streetNumber("589")
                .countryCode("ESP")
                .build());
        
        System.out.println("\n   Geocode Result:");
        System.out.println("   Formatted Address: " + result.getFormattedAddress());
        System.out.println("   Name: " + result.getName());
        System.out.println("   House Number: " + result.getHouseNumber());
        System.out.println("   Postal Code: " + result.getPostalCode());
        System.out.println("   Coordinates: " + result.getCoord().getLat() + ", " + result.getCoord().getLng());
        System.out.println();
    }
    
    private static void findAndGeocode(SuggestService suggest) {
        System.out.println("5. FIND AND GEOCODE (Combined in one call)");
        System.out.println("   Searching and geocoding \"Paseo de la Castellana 200, Madrid\"...\n");
        
        SuggestGeocodeResult result = suggest.findAndGeocode(
                "Paseo de la Castellana 200, Madrid", "ESP", "200");
        
        if (result != null) {
            System.out.println("   Result:");
            System.out.println("   Formatted Address: " + result.getFormattedAddress());
            System.out.println("   House Number: " + result.getHouseNumber());
            System.out.println("   Postal Code: " + result.getPostalCode());
            System.out.println("   Coordinates: " + result.getCoord().getLat() + ", " + result.getCoord().getLng());
        } else {
            System.out.println("   No result found!");
        }
        System.out.println();
    }
    
    private static void convenienceMethods(SuggestService suggest) {
        System.out.println("6. CONVENIENCE METHODS");
        
        // Search Streets
        System.out.println("\n   6a. searchStreets(\"Gran Via\", \"ESP\"):");
        List<SuggestResult> streets = suggest.searchStreets("Gran Via", "ESP");
        System.out.println("       Found " + streets.size() + " streets");
        if (!streets.isEmpty()) {
            System.out.println("       First: " + streets.get(0).getDisplayText());
        }
        
        // Search Cities
        System.out.println("\n   6b. searchCities(\"Girona\", \"ESP\"):");
        List<SuggestResult> cities = suggest.searchCities("Girona", "ESP");
        System.out.println("       Found " + cities.size() + " cities");
        if (!cities.isEmpty()) {
            System.out.println("       First: " + cities.get(0).getDisplayText());
        }
        
        // Search POIs (if available)
        System.out.println("\n   6c. searchPois(\"Aeropuerto\", \"ESP\", ...):");
        List<SuggestResult> pois = suggest.searchPois("Aeropuerto", "ESP", null, null, null);
        System.out.println("       Found " + pois.size() + " POIs");
        if (!pois.isEmpty()) {
            System.out.println("       First: " + pois.get(0).getDisplayText());
        }
        
        System.out.println();
    }
}
