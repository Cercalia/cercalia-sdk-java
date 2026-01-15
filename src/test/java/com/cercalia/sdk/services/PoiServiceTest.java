package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.poi.*;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

/**
 * POI Service Integration Tests with Real API Data.
 * <p>
 * Based on examples from the official Cercalia documentation:
 * <a href="https://docs.cercalia.com/docs/cercalia-webservices/points-of-interest/">POI API</a>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PoiServiceTest {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY");
    private static PoiService service;
    
    // Test coordinates
    private static final Coordinate MADRID_CENTER = new Coordinate(40.3691, -3.589);
    private static final Coordinate BARCELONA_CENTER = new Coordinate(41.39818, 2.1490287);
    private static final Coordinate GIRONA_CENTER = new Coordinate(41.9793, 2.8214);
    
    @BeforeAll
    static void setUp() {
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        service = new PoiService(config);
    }
    
    @Nested
    @DisplayName("searchNearest")
    class SearchNearest {
        
        @Test
        @Order(1)
        @DisplayName("should get nearest gas stations in Madrid")
        void shouldGetNearestGasStationsInMadrid() {
            List<Poi> results = service.searchNearest(MADRID_CENTER, 
                    PoiNearestOptions.builder()
                            .categories("C001") // Gas stations
                            .limit(2)
                            .radius(10000)
                            .build());
            
            assertThat(results).isNotNull();
            assertThat(results).isNotEmpty();
            assertThat(results.size()).isLessThanOrEqualTo(2);
            
            // Validate first result structure
            Poi poi = results.get(0);
            assertThat(poi.getId()).isNotEmpty();
            assertThat(poi.getName()).isNotNull();
            assertThat(poi.getCategoryCode()).isEqualTo("C001");
            assertThat(poi.getCoord()).isNotNull();
            assertThat(poi.getCoord().getLat()).isNotZero();
            assertThat(poi.getCoord().getLng()).isNotZero();
            assertThat(poi.getDistance()).isNotNull();
            assertThat(poi.getPosition()).isEqualTo(1);
            
            // Validate geographic element if present
            if (poi.getGe() != null) {
                PoiGeographicElement ge = poi.getGe();
                boolean hasAdminInfo = ge.getLocality() != null || 
                                       ge.getMunicipality() != null || 
                                       ge.getRegion() != null || 
                                       ge.getCountry() != null;
                assertThat(hasAdminInfo).isTrue();
            }
        }
        
        @Test
        @Order(2)
        @DisplayName("should get nearest schools in Girona")
        void shouldGetNearestSchoolsInGirona() {
            List<Poi> results = service.searchNearest(GIRONA_CENTER, 
                    PoiNearestOptions.builder()
                            .categories("D00ESC") // Schools
                            .limit(5)
                            .radius(2000)
                            .build());
            
            assertThat(results).isNotNull();
            
            if (!results.isEmpty()) {
                Poi poi = results.get(0);
                assertThat(poi.getId()).isNotEmpty();
                assertThat(poi.getName()).isNotNull();
                assertThat(poi.getCategoryCode()).isEqualTo("D00ESC");
                assertThat(poi.getCoord()).isNotNull();
                assertThat(poi.getDistance()).isNotNull();
            }
        }
        
        @Test
        @Order(3)
        @DisplayName("should handle empty results gracefully")
        void shouldHandleEmptyResultsGracefully() {
            // Search in the middle of the ocean
            Coordinate oceanCoord = new Coordinate(30.0, -30.0);
            
            List<Poi> results = service.searchNearest(oceanCoord, 
                    PoiNearestOptions.builder()
                            .categories("C001")
                            .limit(10)
                            .radius(1000)
                            .build());
            
            assertThat(results).isNotNull();
            assertThat(results).isEmpty();
        }
        
        @Test
        @Order(4)
        @DisplayName("should search for multiple POI categories")
        void shouldSearchForMultiplePoiCategories() {
            List<Poi> results = service.searchNearest(MADRID_CENTER, 
                    PoiNearestOptions.builder()
                            .categories("C001", "C009", "C024") // Gas stations, Hospitals, ATMs
                            .limit(10)
                            .radius(5000)
                            .build());
            
            assertThat(results).isNotNull();
            
            if (!results.isEmpty()) {
                // Check that we have different categories
                Set<String> categories = results.stream()
                        .map(Poi::getCategoryCode)
                        .collect(Collectors.toSet());
                assertThat(categories).isNotEmpty();
                
                // All categories should be one of the requested ones
                for (Poi poi : results) {
                    assertThat(poi.getCategoryCode()).isIn("C001", "C009", "C024");
                }
            }
        }
    }
    
    @Nested
    @DisplayName("searchNearestWithRouting")
    class SearchNearestWithRouting {
        
        @Test
        @Order(1)
        @DisplayName("should get nearest gas stations with routing in Madrid")
        void shouldGetNearestGasStationsWithRoutingInMadrid() {
            List<Poi> results = service.searchNearestWithRouting(MADRID_CENTER, 
                    PoiNearestWithRoutingOptions.builder()
                            .categories("C001")
                            .limit(2)
                            .weight(PoiRouteWeight.TIME)
                            .build());
            
            assertThat(results).isNotNull();
            assertThat(results).isNotEmpty();
            
            Poi poi = results.get(0);
            assertThat(poi.getId()).isNotEmpty();
            assertThat(poi.getName()).isNotNull();
            assertThat(poi.getCategoryCode()).isEqualTo("C001");
            assertThat(poi.getCoord()).isNotNull();
            
            // Routing-specific fields
            assertThat(poi.getRouteDistance()).isNotNull();
            assertThat(poi.getRouteTime()).isNotNull();
            assertThat(poi.getRouteWeight()).isNotNull();
            
            // Distance and position should still be present
            assertThat(poi.getDistance()).isNotNull();
            assertThat(poi.getPosition()).isNotNull();
        }
        
        @Test
        @Order(2)
        @DisplayName("should get nearest POIs with inverse routing")
        void shouldGetNearestPoisWithInverseRouting() {
            List<Poi> results = service.searchNearestWithRouting(MADRID_CENTER, 
                    PoiNearestWithRoutingOptions.builder()
                            .categories("C001")
                            .limit(2)
                            .weight(PoiRouteWeight.DISTANCE)
                            .inverse(1) // Routes from POIs to center
                            .build());
            
            assertThat(results).isNotNull();
            
            if (!results.isEmpty()) {
                Poi poi = results.get(0);
                assertThat(poi.getRouteDistance()).isNotNull();
                assertThat(poi.getRouteTime()).isNotNull();
            }
        }
        
        @Test
        @Order(3)
        @DisplayName("should handle routing with realtime traffic")
        void shouldHandleRoutingWithRealtimeTraffic() {
            List<Poi> results = service.searchNearestWithRouting(MADRID_CENTER, 
                    PoiNearestWithRoutingOptions.builder()
                            .categories("C001")
                            .limit(2)
                            .weight(PoiRouteWeight.TIME)
                            .includeRealtime(true)
                            .build());
            
            assertThat(results).isNotNull();
            
            if (!results.isEmpty()) {
                Poi poi = results.get(0);
                assertThat(poi.getRouteTime()).isNotNull();
                // routeRealtime might be present if traffic data is available
                if (poi.getRouteRealtime() != null) {
                    assertThat(poi.getRouteRealtime()).isInstanceOf(Integer.class);
                }
            }
        }
    }
    
    @Nested
    @DisplayName("searchInExtent")
    class SearchInExtent {
        
        @Test
        @Order(1)
        @DisplayName("should get gas stations in Huesca map extent")
        void shouldGetGasStationsInHuescaMapExtent() {
            MapExtent extent = new MapExtent(
                    new Coordinate(42.144102962, -0.414886914),
                    new Coordinate(42.139342832, -0.407628526)
            );
            
            List<Poi> results = service.searchInExtent(extent, 
                    PoiInExtentOptions.builder()
                            .categories("D00GAS") // Gas stations with prices (Spain)
                            .includeMap(false)
                            .build());
            
            assertThat(results).isNotNull();
            
            if (!results.isEmpty()) {
                Poi poi = results.get(0);
                assertThat(poi.getId()).isNotEmpty();
                assertThat(poi.getName()).isNotNull();
                assertThat(poi.getCategoryCode()).isEqualTo("D00GAS");
                assertThat(poi.getCoord()).isNotNull();
                
                // Coordinates should be within the extent
                assertThat(poi.getCoord().getLat()).isBetween(extent.getLowerRight().getLat(), extent.getUpperLeft().getLat());
                assertThat(poi.getCoord().getLng()).isBetween(extent.getUpperLeft().getLng(), extent.getLowerRight().getLng());
                
                // Should have pixel coordinates
                if (poi.getPixels() != null) {
                    assertThat(poi.getPixels().getX()).isNotNull();
                    assertThat(poi.getPixels().getY()).isNotNull();
                }
            }
        }
        
        @Test
        @Order(2)
        @DisplayName("should use zoom filtering with gridsize")
        void shouldUseZoomFilteringWithGridsize() {
            MapExtent extent = new MapExtent(
                    new Coordinate(42.144102962, -0.414886914),
                    new Coordinate(42.139342832, -0.407628526)
            );
            
            List<Poi> results = service.searchInExtent(extent, 
                    PoiInExtentOptions.builder()
                            .categories("D00GAS")
                            .includeMap(false)
                            .gridSize(100) // Use grid filtering
                            .build());
            
            assertThat(results).isNotNull();
            
            // Grid filtering might reduce results
            for (Poi poi : results) {
                assertThat(poi.getCategoryCode()).isEqualTo("D00GAS");
                assertThat(poi.getCoord()).isNotNull();
            }
        }
    }
    
    @Nested
    @DisplayName("searchInPolygon")
    class SearchInPolygon {
        
        @Test
        @Order(1)
        @DisplayName("should get gas stations inside Barcelona polygon")
        void shouldGetGasStationsInsideBarcelonaPolygon() {
            // Polygon around Barcelona city center
            String wkt = "POLYGON((2.149028778076172 41.39586980544921, 2.149028778076172 41.40586980544921, " +
                    "2.179028778076172 41.40586980544921, 2.179028778076172 41.39586980544921, " +
                    "2.149028778076172 41.39586980544921))";
            
            List<Poi> results = service.searchInPolygon(
                    PoiInPolygonOptions.builder()
                            .categories("C001")
                            .wkt(wkt)
                            .build());
            
            assertThat(results).isNotNull();
            
            if (!results.isEmpty()) {
                Poi poi = results.get(0);
                assertThat(poi.getId()).isNotEmpty();
                assertThat(poi.getName()).isNotNull();
                assertThat(poi.getCategoryCode()).isEqualTo("C001");
                assertThat(poi.getCoord()).isNotNull();
                
                // Coordinates should be roughly within the polygon bounds
                assertThat(poi.getCoord().getLat()).isBetween(41.39, 41.41);
                assertThat(poi.getCoord().getLng()).isBetween(2.14, 2.18);
            }
        }
    }
    
    @Nested
    @DisplayName("getWeatherForecast")
    class GetWeatherForecast {
        
        @Test
        @Order(1)
        @DisplayName("should get weather forecast for Barcelona")
        void shouldGetWeatherForecastForBarcelona() {
            WeatherForecast forecast = service.getWeatherForecast(BARCELONA_CENTER);
            
            assertThat(forecast).isNotNull();
            assertThat(forecast.getLocationName()).isNotEmpty();
            assertThat(forecast.getCoord()).isNotNull();
            assertThat(forecast.getCoord().getLat()).isNotZero();
            assertThat(forecast.getCoord().getLng()).isNotZero();
            assertThat(forecast.getForecasts()).isNotNull();
            
            if (!forecast.getForecasts().isEmpty()) {
                WeatherDayForecast dayForecast = forecast.getForecasts().get(0);
                assertThat(dayForecast.getDate()).isNotNull();
                assertThat(dayForecast.getDate()).matches("^\\d{4}-\\d{2}-\\d{2}$"); // YYYY-MM-DD format
                
                // Temperature should be defined
                assertThat(dayForecast.getTemperatureMax()).isNotNull();
                assertThat(dayForecast.getTemperatureMin()).isNotNull();
                
                // Should have forecast data
                assertThat(dayForecast.getPrecipitationChance0012()).isNotNull();
                assertThat(dayForecast.getSkyConditions0012()).isNotNull();
            }
            
            // Last update timestamp should be present
            if (forecast.getLastUpdate() != null) {
                assertThat(forecast.getLastUpdate()).matches("^\\d{4}-\\d{2}-\\d{2}.*");
            }
        }
    }
    
    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {
        
        @Test
        @Order(1)
        @DisplayName("should handle invalid coordinates gracefully")
        void shouldHandleInvalidCoordinatesGracefully() {
            Coordinate invalidCoord = new Coordinate(999, 999);
            
            List<Poi> results = service.searchNearest(invalidCoord, 
                    PoiNearestOptions.builder()
                            .categories("C001")
                            .limit(5)
                            .build());
            
            // Should return empty array or handle gracefully
            assertThat(results).isNotNull();
        }
        
        @Test
        @Order(2)
        @DisplayName("should handle very small radius")
        void shouldHandleVerySmallRadius() {
            List<Poi> results = service.searchNearest(MADRID_CENTER, 
                    PoiNearestOptions.builder()
                            .categories("C001")
                            .limit(5)
                            .radius(1) // 1 meter radius
                            .build());
            
            assertThat(results).isNotNull();
            // Likely to be empty but should not throw
        }
    }
    
    @Nested
    @DisplayName("Data Integrity")
    class DataIntegrity {
        
        @Test
        @Order(1)
        @DisplayName("should maintain coordinate precision")
        void shouldMaintainCoordinatePrecision() {
            List<Poi> results = service.searchNearest(MADRID_CENTER, 
                    PoiNearestOptions.builder()
                            .categories("C001")
                            .limit(1)
                            .radius(10000)
                            .build());
            
            if (!results.isEmpty()) {
                Poi poi = results.get(0);
                
                // Coordinates should be numbers with reasonable precision
                assertThat(poi.getCoord().getLat()).isBetween(-90.0, 90.0);
                assertThat(poi.getCoord().getLng()).isBetween(-180.0, 180.0);
            }
        }
        
        @Test
        @Order(2)
        @DisplayName("should preserve all administrative levels when present")
        void shouldPreserveAllAdministrativeLevelsWhenPresent() {
            List<Poi> results = service.searchNearest(MADRID_CENTER, 
                    PoiNearestOptions.builder()
                            .categories("C001")
                            .limit(1)
                            .radius(10000)
                            .build());
            
            if (!results.isEmpty() && results.get(0).getGe() != null) {
                PoiGeographicElement ge = results.get(0).getGe();
                
                // If an administrative level is present, its ID should also be present (Golden Rule 2)
                if (ge.getLocality() != null) {
                    assertThat(ge.getLocalityCode()).isNotNull();
                }
                if (ge.getMunicipality() != null) {
                    assertThat(ge.getMunicipalityCode()).isNotNull(); // Note: typo is intentional (municipality)
                }
                if (ge.getSubregion() != null) {
                    assertThat(ge.getSubregionCode()).isNotNull();
                }
                if (ge.getRegion() != null) {
                    assertThat(ge.getRegionCode()).isNotNull();
                }
                if (ge.getCountry() != null) {
                    assertThat(ge.getCountryCode()).isNotNull();
                }
            }
        }
        
        @Test
        @Order(3)
        @DisplayName("should include geometry type when present")
        void shouldIncludeGeometryTypeWhenPresent() {
            List<Poi> results = service.searchNearest(MADRID_CENTER, 
                    PoiNearestOptions.builder()
                            .categories("C001")
                            .limit(1)
                            .radius(10000)
                            .build());
            
            if (!results.isEmpty()) {
                Poi poi = results.get(0);
                
                // Geometry should be present (Golden Rule 4)
                assertThat(poi.getGeometry()).isNotNull();
            }
        }
    }
}
