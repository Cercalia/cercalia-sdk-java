package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.geocoding.*;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for GeocodingService.
 * Tests against the real Cercalia API.
 */
@DisplayName("GeocodingService Integration Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GeocodingServiceTest {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY");
    private static final String BASE_URL = "https://lb.cercalia.com/services/v2/json";
    
    private GeocodingService service;
    
    @BeforeAll
    void setUp() {
        CercaliaConfig config = new CercaliaConfig(API_KEY, BASE_URL);
        service = new GeocodingService(config);
    }
    
    @Test
    @DisplayName("should geocode a real address (Provença 589, Barcelona)")
    @Timeout(15)
    void shouldGeocodeRealAddress() {
        List<GeocodingCandidate> results = service.geocode(
                GeocodingOptions.builder()
                        .street("provença 589")
                        .locality("barcelona")
                        .countryCode("ESP")
                        .build()
        );
        
        assertThat(results).isNotEmpty();
        GeocodingCandidate bestMatch = results.get(0);
        
        assertThat(bestMatch.getMunicipality()).containsIgnoringCase("Barcelona");
        assertThat(bestMatch.getLocality()).containsIgnoringCase("Barcelona");
        assertThat(bestMatch.getLocalityCode()).isNotNull();
        assertThat(bestMatch.getMunicipalityCode()).isNotNull();
        assertThat(bestMatch.getCountryCode()).isEqualTo("ESP");
        assertThat(bestMatch.getLabel()).isNotNull();
        assertThat(bestMatch.getCoord().getLat()).isCloseTo(41.41, within(0.1));
        assertThat(bestMatch.getCoord().getLng()).isCloseTo(2.18, within(0.1));
    }
    
    @Test
    @DisplayName("should handle city only search")
    @Timeout(15)
    void shouldHandleCityOnlySearch() {
        List<GeocodingCandidate> results = service.geocode(
                GeocodingOptions.builder()
                        .locality("Girona")
                        .countryCode("ESP")
                        .build()
        );
        
        assertThat(results).isNotEmpty();
        GeocodingCandidate bestMatch = results.get(0);
        assertThat(bestMatch.getMunicipality()).containsIgnoringCase("Girona");
        assertThat(bestMatch.getLevel()).isNotNull();
    }
    
    @Test
    @DisplayName("should handle postal code search")
    @Timeout(15)
    void shouldHandlePostalCodeSearch() {
        List<GeocodingCandidate> results = service.geocode(
                GeocodingOptions.builder()
                        .postalCode("08025")
                        .countryCode("ESP")
                        .build()
        );
        
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getPostalCode()).isEqualTo("08025");
    }
    
    @Test
    @DisplayName("should handle search by locality")
    @Timeout(15)
    void shouldHandleSearchByLocality() {
        List<GeocodingCandidate> results = service.geocode(
                GeocodingOptions.builder()
                        .locality("Madrid")
                        .build()
        );
        
        assertThat(results).isNotEmpty();
        
        // The API returns multiple Madrids, but the one in Comunidad de Madrid should be in the results
        Optional<GeocodingCandidate> madridCity = results.stream()
                .filter(r -> r.getRegion() != null && r.getRegion().matches("(?i).*Comunidad de Madrid.*"))
                .findFirst();
        
        assertThat(madridCity).isPresent();
        assertThat(madridCity.get().getMunicipality()).containsIgnoringCase("Madrid");
    }
    
    @Test
    @DisplayName("should geocode a road milestone (M-45 KM 12)")
    @Timeout(15)
    void shouldGeocodeRoadMilestone() {
        List<GeocodingCandidate> results = service.geocodeRoad("M-45", 12, 
                GeocodingOptions.builder().countryCode("ESP").build());
        
        assertThat(results).isNotEmpty();
        GeocodingCandidate bestMatch = results.get(0);
        
        assertThat(bestMatch.getType()).isEqualTo(GeocodingCandidateType.MILESTONE);
        assertThat(bestMatch.getSubregionCode()).isNotNull();
        assertThat(bestMatch.getRegionCode()).isNotNull();
        assertThat(bestMatch.getLevel()).isNotNull();
        assertThat(bestMatch.getLevel().getValue()).matches("pk|rd");
        assertThat(bestMatch.getCoord().getLat()).isCloseTo(40.33, within(0.1));
        assertThat(bestMatch.getCoord().getLng()).isCloseTo(-3.66, within(0.1));
    }
    
    @Test
    @DisplayName("should return empty list for non-existent address")
    @Timeout(15)
    void shouldReturnEmptyListForNonExistentAddress() {
        List<GeocodingCandidate> results = service.geocode(
                GeocodingOptions.builder()
                        .locality("ESTOESUNADIRECCIONINEXISTENTE 123456789")
                        .build()
        );
        
        assertThat(results).isEmpty();
    }
    
    @Test
    @DisplayName("should handle multiple candidates for ambiguous address")
    @Timeout(15)
    void shouldHandleMultipleCandidates() {
        List<GeocodingCandidate> results = service.geocode(
                GeocodingOptions.builder()
                        .locality("Madrid")
                        .countryCode("ESP")
                        .build()
        );
        
        assertThat(results).isNotEmpty();
        
        Optional<GeocodingCandidate> madridCity = results.stream()
                .filter(r -> r.getRegion() != null && r.getRegion().matches("(?i).*Comunidad de Madrid.*"))
                .findFirst();
        
        assertThat(madridCity).isPresent();
    }
    
    @Test
    @DisplayName("should geocode with region and subregion (Sabadell, Cataluña, Barcelona)")
    @Timeout(15)
    void shouldGeocodeWithRegionAndSubregion() {
        List<GeocodingCandidate> results = service.geocode(
                GeocodingOptions.builder()
                        .locality("Sabadell")
                        .region("Cataluña")
                        .subregion("Barcelona")
                        .countryCode("ESP")
                        .build()
        );
        
        assertThat(results).isNotEmpty();
        GeocodingCandidate bestMatch = results.get(0);
        assertThat(bestMatch.getMunicipality()).isEqualTo("Sabadell");
        assertThat(bestMatch.getRegion()).matches("Catalu(ñ|ny)?a");
    }
    
    @Nested
    @DisplayName("Documentation Examples")
    class DocumentationExamples {
        
        @Test
        @DisplayName("should geocode diagonal 22, barcelona (structured search example)")
        @Timeout(15)
        void shouldGeocodeDiagonalBarcelona() {
            List<GeocodingCandidate> results = service.geocode(
                    GeocodingOptions.builder()
                            .street("diagonal 22")
                            .locality("barcelona")
                            .countryCode("esp")
                            .build()
            );
            
            assertThat(results).isNotEmpty();
            GeocodingCandidate bestMatch = results.get(0);
            assertThat(bestMatch.getName()).containsIgnoringCase("Diagonal");
            assertThat(bestMatch.getLocality()).isEqualTo("Barcelona");
        }
        
        @Test
        @DisplayName("should geocode road milestone M-45 KM 12 (road milestone example)")
        @Timeout(15)
        void shouldGeocodeRoadMilestoneM45() {
            List<GeocodingCandidate> results = service.geocodeRoad("M-45", 12,
                    GeocodingOptions.builder()
                            .subregion("Madrid")
                            .countryCode("ESP")
                            .build()
            );
            
            assertThat(results).isNotEmpty();
            GeocodingCandidate bestMatch = results.get(0);
            assertThat(bestMatch.getType()).isEqualTo(GeocodingCandidateType.MILESTONE);
            assertThat(bestMatch.getName()).containsIgnoringCase("M-45");
            assertThat(bestMatch.getCoord().getLat()).isCloseTo(40.33, within(0.1));
            assertThat(bestMatch.getCoord().getLng()).isCloseTo(-3.66, within(0.1));
        }
        
        @Test
        @DisplayName("should geocode A-231 KM 13 (ambiguous road example)")
        @Timeout(15)
        void shouldGeocodeAmbiguousRoad() {
            List<GeocodingCandidate> results = service.geocodeRoad("A-231", 13,
                    GeocodingOptions.builder()
                            .countryCode("ESP")
                            .build()
            );
            
            assertThat(results).hasSizeGreaterThan(1);
            
            // Example shows 2 candidates: La Fresneda and Villanueva de las Manzanas
            Optional<GeocodingCandidate> fresneda = results.stream()
                    .filter(r -> "La Fresneda".equals(r.getMunicipality()))
                    .findFirst();
            Optional<GeocodingCandidate> villanueva = results.stream()
                    .filter(r -> "Villanueva de las Manzanas".equals(r.getMunicipality()))
                    .findFirst();
            
            assertThat(fresneda).isPresent();
            assertThat(villanueva).isPresent();
        }
        
        @Test
        @DisplayName("should get cities by postal code 40160 (Torrecaballeros example)")
        @Timeout(15)
        void shouldGetCitiesByPostalCode() {
            List<PostalCodeCity> cities = service.geocodeCitiesByPostalCode("40160", "ESP");
            
            assertThat(cities).isNotEmpty();
            
            // According to documentation, should return Torrecaballeros and Cabanillas del Monte
            Optional<PostalCodeCity> torrecaballeros = cities.stream()
                    .filter(c -> "Torrecaballeros".equals(c.getName()))
                    .findFirst();
            Optional<PostalCodeCity> cabanillas = cities.stream()
                    .filter(c -> "Cabanillas del Monte".equals(c.getName()))
                    .findFirst();
            
            assertThat(torrecaballeros).isPresent();
            assertThat(torrecaballeros.get().getMunicipalityCode()).isNotNull();
            assertThat(torrecaballeros.get().getSubregion()).isEqualTo("Segovia");
            assertThat(torrecaballeros.get().getSubregionCode()).isNotNull();
            assertThat(torrecaballeros.get().getRegion()).isEqualTo("Castilla y León");
            assertThat(torrecaballeros.get().getRegionCode()).isNotNull();
            assertThat(torrecaballeros.get().getCountryCode()).isEqualTo("ESP");
            
            assertThat(cabanillas).isPresent();
        }
    }
}
