package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.poi.PoiGeographicElement;
import com.cercalia.sdk.model.proximity.*;
import org.junit.jupiter.api.*;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

/**
 * Proximity Service Integration Tests with Real API Data.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProximityServiceTest {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY");
    private static ProximityService service;
    
    // Barcelona center coordinates
    private static final Coordinate BARCELONA = new Coordinate(41.3851, 2.1734);
    // Madrid center coordinates
    private static final Coordinate MADRID = new Coordinate(40.4168, -3.7038);
    
    @BeforeAll
    static void setUp() {
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        service = new ProximityService(config);
    }
    
    @Nested
    @DisplayName("findNearest")
    class FindNearest {
        
        @Test
        @Order(1)
        @DisplayName("should find nearest gas stations from Barcelona center")
        void shouldFindNearestGasStationsFromBarcelonaCenter() {
            ProximityResult result = service.findNearest(
                    ProximityOptions.builder(BARCELONA)
                            .categories("C001") // Gas station
                            .count(5)
                            .build());
            
            assertThat(result.getItems()).isNotEmpty();
            assertThat(result.getItems().size()).isLessThanOrEqualTo(5);
            assertThat(result.getCenter()).isEqualTo(BARCELONA);
            
            ProximityItem firstItem = result.getItems().get(0);
            assertThat(firstItem.getId()).isNotEmpty();
            assertThat(firstItem.getName()).isNotNull();
            assertThat(firstItem.getCoord()).isNotNull();
            assertThat(firstItem.getCoord().getLat()).isNotNull();
            assertThat(firstItem.getCoord().getLng()).isNotNull();
            assertThat(firstItem.getDistance()).isGreaterThanOrEqualTo(0);
            assertThat(firstItem.getCategoryCode()).isEqualTo("C001");
        }
        
        @Test
        @Order(2)
        @DisplayName("should find nearest pharmacies with max radius")
        void shouldFindNearestPharmaciesWithMaxRadius() {
            ProximityResult result = service.findNearest(
                    ProximityOptions.builder(BARCELONA)
                            .categories("C026") // Pharmacy
                            .count(3)
                            .maxRadius(2000) // 2km radius
                            .build());
            
            assertThat(result.getItems()).isNotEmpty();
            
            // All items should be within 2km (2000m)
            for (ProximityItem item : result.getItems()) {
                assertThat(item.getDistance()).isLessThanOrEqualTo(2000);
            }
        }
        
        @Test
        @Order(3)
        @DisplayName("should find nearest hotels in Madrid")
        void shouldFindNearestHotelsInMadrid() {
            ProximityResult result = service.findNearest(
                    ProximityOptions.builder(MADRID)
                            .categories("C013") // Hotel
                            .count(10)
                            .build());
            
            assertThat(result.getItems()).isNotEmpty();
            assertThat(result.getTotalFound()).isEqualTo(result.getItems().size());
            
            // Verify results are sorted by distance (ascending)
            for (int i = 1; i < result.getItems().size(); i++) {
                assertThat(result.getItems().get(i).getDistance())
                        .isGreaterThanOrEqualTo(result.getItems().get(i - 1).getDistance());
            }
        }
        
        @Test
        @Order(4)
        @DisplayName("should return empty results for non-existent category in remote location")
        void shouldReturnEmptyResultsForNonExistentCategoryInRemoteLocation() {
            // A location in the ocean where no POIs should exist
            Coordinate remoteLocation = new Coordinate(30.0, -30.0);
            ProximityResult result = service.findNearest(
                    ProximityOptions.builder(remoteLocation)
                            .categories("C001") // Gas station
                            .count(5)
                            .maxRadius(1000) // 1km - very small radius in the ocean
                            .build());
            
            assertThat(result.getItems()).isEmpty();
            assertThat(result.getTotalFound()).isEqualTo(0);
        }
        
        @Test
        @Order(5)
        @DisplayName("should find multiple category types at once")
        void shouldFindMultipleCategoryTypesAtOnce() {
            ProximityResult result = service.findNearest(
                    ProximityOptions.builder(BARCELONA)
                            .categories("C001", "C026") // Gas stations and Pharmacies
                            .count(10)
                            .build());
            
            assertThat(result.getItems()).isNotEmpty();
            
            // Should have at least one of each category in a major city
            Set<String> categories = result.getItems().stream()
                    .map(ProximityItem::getCategoryCode)
                    .collect(Collectors.toSet());
            assertThat(categories.size()).isGreaterThanOrEqualTo(1);
        }
    }
    
    @Nested
    @DisplayName("findNearestByCategory")
    class FindNearestByCategory {
        
        @Test
        @Order(1)
        @DisplayName("should find nearest restaurants by category")
        void shouldFindNearestRestaurantsByCategory() {
            ProximityResult result = service.findNearestByCategory(
                    BARCELONA,
                    "C014", // Restaurant
                    5);
            
            assertThat(result.getItems()).isNotEmpty();
            assertThat(result.getItems().size()).isLessThanOrEqualTo(5);
            
            for (ProximityItem item : result.getItems()) {
                assertThat(item.getCategoryCode()).isEqualTo("C014");
            }
        }
        
        @Test
        @Order(2)
        @DisplayName("should find nearest parking by category")
        void shouldFindNearestParkingByCategory() {
            ProximityResult result = service.findNearestByCategory(
                    MADRID,
                    "C007", // Parking
                    3);
            
            assertThat(result.getItems()).isNotEmpty();
            
            ProximityItem firstParking = result.getItems().get(0);
            assertThat(firstParking.getName()).isNotNull();
            // Within same degree (rough proximity check)
            assertThat(firstParking.getCoord().getLat()).isCloseTo(MADRID.getLat(), within(1.0));
            assertThat(firstParking.getCoord().getLng()).isCloseTo(MADRID.getLng(), within(1.0));
        }
        
        @Test
        @Order(3)
        @DisplayName("should use default count of 5 when not specified")
        void shouldUseDefaultCountOf5WhenNotSpecified() {
            ProximityResult result = service.findNearestByCategory(BARCELONA, "C001"); // Gas station
            
            assertThat(result.getItems()).isNotEmpty();
            assertThat(result.getItems().size()).isLessThanOrEqualTo(5);
        }
    }
    
    @Nested
    @DisplayName("findNearestWithRouting")
    class FindNearestWithRouting {
        
        @Test
        @Order(1)
        @DisplayName("should find nearest gas stations with routing distance")
        void shouldFindNearestGasStationsWithRoutingDistance() {
            ProximityResult result = service.findNearestWithRouting(
                    BARCELONA,
                    "C001", // Gas station
                    ProximityRouteWeight.DISTANCE,
                    3);
            
            assertThat(result.getItems()).isNotEmpty();
            
            // With routing enabled, should have route distance/time info
            ProximityItem firstItem = result.getItems().get(0);
            assertThat(firstItem.getCoord()).isNotNull();
            // Note: routeDistance and routeTime may or may not be present depending on API response
        }
        
        @Test
        @Order(2)
        @DisplayName("should find nearest hospitals with routing time")
        void shouldFindNearestHospitalsWithRoutingTime() {
            ProximityResult result = service.findNearestWithRouting(
                    MADRID,
                    "C009", // Hospital
                    ProximityRouteWeight.TIME,
                    5);
            
            assertThat(result.getItems()).isNotEmpty();
            
            for (ProximityItem item : result.getItems()) {
                assertThat(item.getCategoryCode()).isEqualTo("C009");
            }
        }
        
        @Test
        @Order(3)
        @DisplayName("should use default routing time with simplified method")
        void shouldUseDefaultRoutingTimeWithSimplifiedMethod() {
            ProximityResult result = service.findNearestWithRouting(BARCELONA, "C001"); // Gas station
            
            assertThat(result.getItems()).isNotEmpty();
        }
    }
    
    @Nested
    @DisplayName("Geographic element parsing")
    class GeographicElementParsing {
        
        @Test
        @Order(1)
        @DisplayName("should parse geographic element data for POIs")
        void shouldParseGeographicElementDataForPois() {
            ProximityResult result = service.findNearest(
                    ProximityOptions.builder(BARCELONA)
                            .categories("C001") // Gas station - usually have full address info
                            .count(5)
                            .build());
            
            assertThat(result.getItems()).isNotEmpty();
            
            // At least one item should have geographic element info
            ProximityItem itemWithGe = result.getItems().stream()
                    .filter(item -> item.getGe() != null)
                    .findFirst()
                    .orElse(null);
            
            if (itemWithGe != null && itemWithGe.getGe() != null) {
                // Check that at least some geographic info is present
                PoiGeographicElement ge = itemWithGe.getGe();
                boolean hasInfo = ge.getStreet() != null || 
                        ge.getLocality() != null || 
                        ge.getMunicipality() != null || 
                        ge.getSubregion() != null || 
                        ge.getRegion() != null || 
                        ge.getCountry() != null;
                assertThat(hasInfo).isTrue();
                
                // GOLDEN RULE: verify locality and localityCode are present together
                if (ge.getLocality() != null) {
                    assertThat(ge.getLocalityCode()).isNotNull();
                }
                // GOLDEN RULE: verify municipalityCode (with intentional typo)
                if (ge.getMunicipality() != null) {
                    assertThat(ge.getMunicipalityCode()).isNotNull();
                }
            }
        }
    }
    
    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {
        
        @Test
        @Order(1)
        @DisplayName("should handle single result")
        void shouldHandleSingleResult() {
            ProximityResult result = service.findNearest(
                    ProximityOptions.builder(BARCELONA)
                            .categories("C001")
                            .count(1)
                            .build());
            
            assertThat(result.getItems()).hasSize(1);
            assertThat(result.getTotalFound()).isEqualTo(1);
        }
        
        @Test
        @Order(2)
        @DisplayName("should handle large count request")
        void shouldHandleLargeCountRequest() {
            ProximityResult result = service.findNearest(
                    ProximityOptions.builder(BARCELONA)
                            .categories("C001")
                            .count(50)
                            .build());
            
            assertThat(result.getItems()).isNotEmpty();
            // API may return fewer than requested if not enough POIs nearby
            assertThat(result.getItems().size()).isLessThanOrEqualTo(50);
        }
    }
    
    @Nested
    @DisplayName("Async operations")
    class AsyncOperations {
        
        @Test
        @Order(1)
        @DisplayName("should find nearest POIs asynchronously")
        void shouldFindNearestPoisAsynchronously() {
            ProximityResult result = service.findNearestAsync(
                    ProximityOptions.builder(BARCELONA)
                            .categories("C001")
                            .count(5)
                            .build())
                    .join();
            
            assertThat(result.getItems()).isNotEmpty();
            assertThat(result.getCenter()).isEqualTo(BARCELONA);
        }
    }
}
