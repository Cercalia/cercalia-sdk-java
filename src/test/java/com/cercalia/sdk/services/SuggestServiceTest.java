package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.suggest.*;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for SuggestService using real Cercalia Suggest API.
 * <p>
 * These tests validate:
 * <ul>
 *   <li>Correct parsing of Solr-format responses</li>
 *   <li>Proper mapping of all administrative fields with *Code nomenclature</li>
 *   <li>Compliance with Golden Rules (direct mapping, code integrity, strict coordinates)</li>
 * </ul>
 */
@DisplayName("SuggestService Integration Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SuggestServiceTest {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY");
    private static final String BASE_URL = "https://lb.cercalia.com/services/v2/json";
    
    private SuggestService service;
    
    @BeforeAll
    void setUp() {
        CercaliaConfig config = new CercaliaConfig(API_KEY, BASE_URL);
        service = new SuggestService(config);
    }
    
    @Nested
    @DisplayName("search() - Street suggestions")
    class StreetSearchTests {
        
        @Test
        @DisplayName("should return street suggestions for 'Paseo de la Castellana Madrid'")
        @Timeout(15)
        void shouldReturnStreetSuggestionsForCastellana() {
            List<SuggestResult> results = service.search(SuggestOptions.builder()
                    .text("Paseo de la Castellana 300, madrid")
                    .countryCode("ESP")
                    .geoType(SuggestGeoType.ST)
                    .build());
            
            assertThat(results).isNotEmpty();
            
            SuggestResult first = results.get(0);
            
            // Verify basic structure
            assertThat(first.getId()).isNotNull();
            assertThat(first.getDisplayText()).isNotNull();
            // When searching with a house number, type is 'address', without it's 'street'
            assertThat(first.getType()).isIn(SuggestResultType.STREET, SuggestResultType.ADDRESS);
            
            // Verify street information with *Code nomenclature
            assertThat(first.getStreet()).isNotNull();
            assertThat(first.getStreet().getCode()).isNotNull();
            assertThat(first.getStreet().getName()).isNotNull();
            assertThat(first.getStreet().getDescription()).isNotNull();
            
            // Verify the street matches our search
            String streetDesc = first.getStreet().getDescription().toLowerCase();
            assertThat(streetDesc).contains("castellana");
            
            // Verify all administrative levels are present with codes (GOLDEN RULE #2)
            assertThat(first.getCity()).isNotNull();
            assertThat(first.getCity().getCode()).isNotNull();
            assertThat(first.getCity().getName()).isNotNull();
            
            assertThat(first.getMunicipality()).isNotNull();
            assertThat(first.getMunicipality().getCode()).isNotNull();
            assertThat(first.getMunicipality().getName()).isNotNull();
            
            assertThat(first.getSubregion()).isNotNull();
            assertThat(first.getSubregion().getCode()).isNotNull();
            assertThat(first.getSubregion().getName()).isNotNull();
            
            assertThat(first.getRegion()).isNotNull();
            assertThat(first.getRegion().getCode()).isNotNull();
            assertThat(first.getRegion().getName()).isNotNull();
            
            assertThat(first.getCountry()).isNotNull();
            assertThat(first.getCountry().getCode()).isEqualTo("ESP");
            assertThat(first.getCountry().getName()).isNotNull();
            
            // Verify coordinates are present (street default coords)
            assertThat(first.getCoord()).isNotNull();
            assertThat(first.getCoord().getLat()).isBetween(40.0, 41.0);
            assertThat(first.getCoord().getLng()).isBetween(-4.0, -3.0);
        }
        
        @Test
        @DisplayName("should return complete street info for 'Carrer de Provença Barcelona'")
        @Timeout(15)
        void shouldReturnCompleteStreetInfoForProvenca() {
            List<SuggestResult> results = service.search(SuggestOptions.builder()
                    .text("Carrer de Provença Barcelona")
                    .countryCode("ESP")
                    .geoType(SuggestGeoType.ST)
                    .build());
            
            assertThat(results).isNotEmpty();
            
            Optional<SuggestResult> provenca = results.stream()
                    .filter(r -> r.getStreet() != null && 
                            (r.getStreet().getDescription() != null && r.getStreet().getDescription().toLowerCase().contains("provença") ||
                             r.getStreet().getName() != null && r.getStreet().getName().toLowerCase().contains("provença")))
                    .findFirst();
            
            assertThat(provenca).isPresent();
            
            // Verify street type field
            if (provenca.get().getStreet().getType() != null) {
                assertThat(provenca.get().getStreet().getType()).isEqualTo("Carrer");
            }
            
            // Verify city is Barcelona
            assertThat(provenca.get().getCity().getName().toLowerCase()).contains("barcelona");
            
            // Verify region is Catalunya
            assertThat(provenca.get().getRegion().getName().toLowerCase()).contains("catalu");
        }
        
        @Test
        @DisplayName("should include house number range info when available")
        @Timeout(15)
        void shouldIncludeHouseNumberRangeInfo() {
            List<SuggestResult> results = service.search(SuggestOptions.builder()
                    .text("Paseo de la Castellana 300, madrid")
                    .countryCode("ESP")
                    .geoType(SuggestGeoType.ST)
                    .build());
            
            assertThat(results).isNotEmpty();
            
            SuggestResult first = results.get(0);
            
            // For a specific address search, houseNumbers should be available
            if (first.getHouseNumbers() != null) {
                assertThat(first.getHouseNumbers().isAvailable()).isTrue();
                assertThat(first.getHouseNumbers().getMin()).isNotNull();
                assertThat(first.getHouseNumbers().getMax()).isNotNull();
                
                // Verify portal info when searching with number
                if (first.getHouseNumbers().getCurrent() != null) {
                    assertThat(first.getHouseNumbers().getCurrent()).isEqualTo(300);
                }
            }
        }
        
        @Test
        @DisplayName("should include postal code when available")
        @Timeout(15)
        void shouldIncludePostalCodeWhenAvailable() {
            List<SuggestResult> results = service.search(SuggestOptions.builder()
                    .text("Paseo de la Castellana 300, madrid")
                    .countryCode("ESP")
                    .geoType(SuggestGeoType.ST)
                    .build());
            
            assertThat(results).isNotEmpty();
            
            SuggestResult first = results.get(0);
            
            // For complete address, postal code should be present
            if (first.getPostalCode() != null) {
                assertThat(first.getPostalCode()).matches("^\\d{5}$");
            }
        }
    }
    
    @Nested
    @DisplayName("search() - City suggestions")
    class CitySearchTests {
        
        @Test
        @DisplayName("should return city suggestions for 'Barcelona'")
        @Timeout(15)
        void shouldReturnCitySuggestionsForBarcelona() {
            List<SuggestResult> results = service.search(SuggestOptions.builder()
                    .text("Barcelona")
                    .countryCode("ESP")
                    .geoType(SuggestGeoType.CT)
                    .build());
            
            assertThat(results).isNotEmpty();
            
            Optional<SuggestResult> barcelona = results.stream()
                    .filter(r -> r.getCity() != null && 
                            r.getCity().getName() != null &&
                            r.getCity().getName().toLowerCase().equals("barcelona"))
                    .findFirst();
            
            assertThat(barcelona).isPresent();
            assertThat(barcelona.get().getType()).isEqualTo(SuggestResultType.CITY);
            
            // Verify city has code
            assertThat(barcelona.get().getCity().getCode()).isNotNull();
            assertThat(barcelona.get().getCity().getCode()).startsWith("ESP");
            
            // Verify administrative hierarchy
            assertThat(barcelona.get().getSubregion().getName()).isNotNull();
            assertThat(barcelona.get().getRegion().getName().toLowerCase()).contains("catalu");
            assertThat(barcelona.get().getCountry().getCode()).isEqualTo("ESP");
        }
        
        @Test
        @DisplayName("should return city suggestions for 'Girona'")
        @Timeout(15)
        void shouldReturnCitySuggestionsForGirona() {
            List<SuggestResult> results = service.search(SuggestOptions.builder()
                    .text("Girona")
                    .countryCode("ESP")
                    .geoType(SuggestGeoType.CT)
                    .build());
            
            assertThat(results).isNotEmpty();
            
            boolean hasGirona = results.stream()
                    .anyMatch(r -> r.getCity() != null && 
                            r.getCity().getName() != null &&
                            r.getCity().getName().toLowerCase().contains("girona"));
            assertThat(hasGirona).isTrue();
        }
    }
    
    @Nested
    @DisplayName("Convenience methods")
    class ConvenienceMethodTests {
        
        @Test
        @DisplayName("searchStreets() should return only street suggestions")
        @Timeout(15)
        void searchStreetsShouldReturnOnlyStreets() {
            List<SuggestResult> results = service.searchStreets("Gran Via", "ESP");
            
            assertThat(results).isNotEmpty();
            
            // All results should be streets
            for (SuggestResult r : results) {
                assertThat(r.getStreet()).isNotNull();
                assertThat(r.getStreet().getCode()).isNotNull();
            }
            
            // Should contain Gran Via
            boolean hasGranVia = results.stream()
                    .anyMatch(r -> r.getStreet() != null &&
                            (r.getStreet().getDescription() != null && r.getStreet().getDescription().toLowerCase().contains("gran via") ||
                             r.getStreet().getName() != null && r.getStreet().getName().toLowerCase().contains("gran via")));
            assertThat(hasGranVia).isTrue();
        }
        
        @Test
        @DisplayName("searchCities() should return city suggestions")
        @Timeout(15)
        void searchCitiesShouldReturnCities() {
            List<SuggestResult> results = service.searchCities("Madrid", "ESP");
            
            assertThat(results).isNotEmpty();
            
            // Should contain Madrid
            boolean hasMadrid = results.stream()
                    .anyMatch(r -> (r.getCity() != null && r.getCity().getName() != null && 
                            r.getCity().getName().toLowerCase().contains("madrid")) ||
                            r.getDisplayText().toLowerCase().contains("madrid"));
            assertThat(hasMadrid).isTrue();
        }
    }
    
    @Nested
    @DisplayName("geocode() - Address geocoding")
    class GeocodeTests {
        
        @Test
        @DisplayName("should geocode a street with city code")
        @Timeout(15)
        void shouldGeocodeStreetWithCityCode() {
            // First search to get codes
            List<SuggestResult> suggestions = service.search(SuggestOptions.builder()
                    .text("Paseo de la Castellana madrid")
                    .countryCode("ESP")
                    .geoType(SuggestGeoType.ST)
                    .build());
            
            assertThat(suggestions).isNotEmpty();
            
            SuggestResult suggestion = suggestions.get(0);
            
            // Verify we have the codes needed for geocoding
            assertThat(suggestion.getStreet()).isNotNull();
            assertThat(suggestion.getStreet().getCode()).isNotNull();
            assertThat(suggestion.getCity()).isNotNull();
            assertThat(suggestion.getCity().getCode()).isNotNull();
            
            // Now geocode with a specific house number
            SuggestGeocodeResult result = service.geocode(SuggestGeocodeOptions.builder()
                    .streetCode(suggestion.getStreet().getCode())
                    .cityCode(suggestion.getCity().getCode())
                    .streetNumber("200")
                    .countryCode("ESP")
                    .build());
            
            // Verify geocode result
            assertThat(result.getCoord()).isNotNull();
            assertThat(result.getCoord().getLat()).isBetween(40.0, 41.0);
            assertThat(result.getCoord().getLng()).isBetween(-4.0, -3.0);
            
            assertThat(result.getFormattedAddress()).isNotNull();
            assertThat(result.getFormattedAddress()).isNotEmpty();
            
            // Verify house number and postal code
            assertThat(result.getHouseNumber()).isEqualTo("200");
            assertThat(result.getPostalCode()).isNotNull();
        }
        
        @Test
        @DisplayName("should geocode Provença street in Barcelona")
        @Timeout(15)
        void shouldGeocodeProvencaInBarcelona() {
            // First search to get codes
            List<SuggestResult> suggestions = service.search(SuggestOptions.builder()
                    .text("Carrer de Provença Barcelona")
                    .countryCode("ESP")
                    .geoType(SuggestGeoType.ST)
                    .build());
            
            assertThat(suggestions).isNotEmpty();
            
            SuggestResult suggestion = suggestions.get(0);
            assertThat(suggestion.getStreet()).isNotNull();
            assertThat(suggestion.getStreet().getCode()).isNotNull();
            assertThat(suggestion.getCity()).isNotNull();
            assertThat(suggestion.getCity().getCode()).isNotNull();
            
            // Geocode with house number
            SuggestGeocodeResult result = service.geocode(SuggestGeocodeOptions.builder()
                    .streetCode(suggestion.getStreet().getCode())
                    .cityCode(suggestion.getCity().getCode())
                    .streetNumber("589")
                    .countryCode("ESP")
                    .build());
            
            assertThat(result.getCoord()).isNotNull();
            assertThat(result.getCoord().getLat()).isBetween(41.0, 42.0);
            assertThat(result.getCoord().getLng()).isBetween(2.0, 3.0);
            
            assertThat(result.getHouseNumber()).isEqualTo("589");
        }
    }
    
    @Nested
    @DisplayName("findAndGeocode() - Combined search and geocode")
    class FindAndGeocodeTests {
        
        @Test
        @DisplayName("should find and geocode an address in one call")
        @Timeout(15)
        void shouldFindAndGeocodeAddress() {
            SuggestGeocodeResult result = service.findAndGeocode(
                    "Paseo de la Castellana 200, Madrid", "ESP", "200");
            
            assertThat(result).isNotNull();
            assertThat(result.getCoord()).isNotNull();
            assertThat(result.getCoord().getLat()).isBetween(40.0, 41.0);
            assertThat(result.getFormattedAddress()).isNotNull();
        }
        
        @Test
        @DisplayName("should return null for non-existent address")
        @Timeout(15)
        void shouldReturnNullForNonExistentAddress() {
            SuggestGeocodeResult result = service.findAndGeocode(
                    "XYZNONEXISTENT12345QWERTY", "ESP", null);
            
            assertThat(result).isNull();
        }
        
        @Test
        @DisplayName("should return suggestion coordinates when available")
        @Timeout(15)
        void shouldReturnSuggestionCoordinatesWhenAvailable() {
            // Search for a city (which should have coordinates in suggestion)
            SuggestGeocodeResult result = service.findAndGeocode("Barcelona", "ESP", null);
            
            // May return coordinates from suggestion or from geocode
            if (result != null) {
                assertThat(result.getCoord()).isNotNull();
                assertThat(result.getCoord().getLat()).isNotNull();
                assertThat(result.getCoord().getLng()).isNotNull();
            }
        }
    }
    
    @Nested
    @DisplayName("Edge cases and error handling")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("should return empty list for empty text")
        @Timeout(15)
        void shouldReturnEmptyListForEmptyText() {
            List<SuggestResult> results = service.search(SuggestOptions.builder()
                    .text("")
                    .countryCode("ESP")
                    .build());
            
            assertThat(results).isEmpty();
        }
        
        @Test
        @DisplayName("should return empty list for nonsense text")
        @Timeout(15)
        void shouldReturnEmptyListForNonsenseText() {
            List<SuggestResult> results = service.search(SuggestOptions.builder()
                    .text("XYZNONEXISTENT12345QWERTY")
                    .countryCode("ESP")
                    .build());
            
            assertThat(results).isEmpty();
        }
        
        @Test
        @DisplayName("should handle special characters in search text")
        @Timeout(15)
        void shouldHandleSpecialCharacters() {
            List<SuggestResult> results = service.search(SuggestOptions.builder()
                    .text("Plaça d'Espanya")
                    .countryCode("ESP")
                    .build());
            
            // Should not throw and return some results
            assertThat(results).isNotNull();
        }
        
        @Test
        @DisplayName("should handle accented characters")
        @Timeout(15)
        void shouldHandleAccentedCharacters() {
            List<SuggestResult> results = service.search(SuggestOptions.builder()
                    .text("Aragón")
                    .countryCode("ESP")
                    .build());
            
            assertThat(results).isNotNull();
        }
        
        @Test
        @DisplayName("should handle very short search text")
        @Timeout(15)
        void shouldHandleVeryShortSearchText() {
            List<SuggestResult> results = service.search(SuggestOptions.builder()
                    .text("Ma")
                    .countryCode("ESP")
                    .build());
            
            // May or may not return results depending on API configuration
            assertThat(results).isNotNull();
        }
    }
    
    @Nested
    @DisplayName("Response field completeness (Golden Rules)")
    class GoldenRulesTests {
        
        @Test
        @DisplayName("should use *Code nomenclature for all identifiers")
        @Timeout(15)
        void shouldUseCodeNomenclature() {
            List<SuggestResult> results = service.search(SuggestOptions.builder()
                    .text("Gran Via Madrid")
                    .countryCode("ESP")
                    .geoType(SuggestGeoType.ST)
                    .build());
            
            assertThat(results).isNotEmpty();
            
            SuggestResult first = results.get(0);
            
            // Verify *Code nomenclature (not *Id)
            if (first.getStreet() != null) {
                // SuggestStreet should have code field, not id
                assertThat(first.getStreet().getCode()).isNotNull();
            }
            if (first.getCity() != null) {
                assertThat(first.getCity().getCode()).isNotNull();
            }
            if (first.getMunicipality() != null) {
                assertThat(first.getMunicipality().getCode()).isNotNull();
            }
            if (first.getSubregion() != null) {
                assertThat(first.getSubregion().getCode()).isNotNull();
            }
            if (first.getRegion() != null) {
                assertThat(first.getRegion().getCode()).isNotNull();
            }
            if (first.getCountry() != null) {
                assertThat(first.getCountry().getCode()).isNotNull();
            }
        }
        
        @Test
        @DisplayName("should include score when present")
        @Timeout(15)
        void shouldIncludeScoreWhenPresent() {
            List<SuggestResult> results = service.search(SuggestOptions.builder()
                    .text("Paseo de la Castellana 300, madrid")
                    .countryCode("ESP")
                    .geoType(SuggestGeoType.ST)
                    .build());
            
            assertThat(results).isNotEmpty();
            
            // At least the first result should have a score
            SuggestResult first = results.get(0);
            assertThat(first.getScore()).isNotNull();
        }
        
        @Test
        @DisplayName("should include all street fields (code, name, description, type, article)")
        @Timeout(15)
        void shouldIncludeAllStreetFields() {
            List<SuggestResult> results = service.search(SuggestOptions.builder()
                    .text("Paseo de la Castellana madrid")
                    .countryCode("ESP")
                    .geoType(SuggestGeoType.ST)
                    .build());
            
            assertThat(results).isNotEmpty();
            
            SuggestResult first = results.get(0);
            assertThat(first.getStreet()).isNotNull();
            
            // Verify street fields are present
            SuggestStreet street = first.getStreet();
            assertThat(street.getCode()).isNotNull();
            assertThat(street.getName()).isNotNull();
            assertThat(street.getDescription()).isNotNull();
            // type and article may be null, but the fields should exist
        }
    }
    
    @Nested
    @DisplayName("Geocode result completeness")
    class GeocodeResultCompletenessTests {
        
        @Test
        @DisplayName("should return geocode result with coordinate strictness (GOLDEN RULE #3)")
        @Timeout(15)
        void shouldReturnGeocodeWithStrictCoordinates() {
            // First search to get codes
            List<SuggestResult> suggestions = service.search(SuggestOptions.builder()
                    .text("Paseo de la Castellana madrid")
                    .countryCode("ESP")
                    .geoType(SuggestGeoType.ST)
                    .build());
            
            assertThat(suggestions).isNotEmpty();
            SuggestResult suggestion = suggestions.get(0);
            
            SuggestGeocodeResult result = service.geocode(SuggestGeocodeOptions.builder()
                    .streetCode(suggestion.getStreet().getCode())
                    .cityCode(suggestion.getCity().getCode())
                    .streetNumber("100")
                    .countryCode("ESP")
                    .build());
            
            // Verify coordinate strictness (GOLDEN RULE #3)
            assertThat(result.getCoord()).isNotNull();
            assertThat(result.getCoord().getLat()).isNotNull();
            assertThat(result.getCoord().getLng()).isNotNull();
        }
    }
    
    @Nested
    @DisplayName("Async methods")
    class AsyncTests {
        
        @Test
        @DisplayName("searchAsync should return results")
        @Timeout(15)
        void searchAsyncShouldReturnResults() throws Exception {
            List<SuggestResult> results = service.searchAsync(SuggestOptions.builder()
                    .text("Gran Via Madrid")
                    .countryCode("ESP")
                    .build()).get();
            
            assertThat(results).isNotEmpty();
        }
        
        @Test
        @DisplayName("geocodeAsync should return result")
        @Timeout(15)
        void geocodeAsyncShouldReturnResult() throws Exception {
            // First search to get codes
            List<SuggestResult> suggestions = service.searchStreets("Gran Via", "ESP");
            assertThat(suggestions).isNotEmpty();
            
            SuggestResult suggestion = suggestions.get(0);
            
            SuggestGeocodeResult result = service.geocodeAsync(SuggestGeocodeOptions.builder()
                    .streetCode(suggestion.getStreet().getCode())
                    .cityCode(suggestion.getCity().getCode())
                    .streetNumber("1")
                    .countryCode("ESP")
                    .build()).get();
            
            assertThat(result.getCoord()).isNotNull();
        }
    }
}
