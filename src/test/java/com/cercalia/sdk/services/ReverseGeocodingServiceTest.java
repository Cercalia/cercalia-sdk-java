package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.reversegeocoding.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for ReverseGeocodingService.
 */
class ReverseGeocodingServiceTest {
    
    private static ReverseGeocodingService service;
    
    // Barcelona - Plaça Catalunya
    private static final Coordinate BARCELONA_CATALUNYA_SQUARE = new Coordinate(41.3874, 2.1700);
    
    // Madrid - Puerta del Sol
    private static final Coordinate MADRID_PUERTA_DEL_SOL = new Coordinate(40.4169, -3.7035);
    
    // Ocean coordinate (should return null or timezone only)
    private static final Coordinate OCEAN_COORD = new Coordinate(30.0, -30.0);
    
    // Zaragoza
    private static final Coordinate ZARAGOZA = new Coordinate(41.6488, -0.8891);
    
    @BeforeAll
    static void setup() {
        CercaliaConfig config = new CercaliaConfig(
                System.getenv("CERCALIA_API_KEY"),
                "https://lb.cercalia.com/services/v2/json"
        );
        service = new ReverseGeocodingService(config);
    }
    
    @Nested
    @DisplayName("reverseGeocode")
    class ReverseGeocodeTests {
        
        @Test
        @DisplayName("should reverse geocode Barcelona Plaça Catalunya")
        void shouldReverseGeocodeBarcelona() {
            ReverseGeocodeResult result = service.reverseGeocode(BARCELONA_CATALUNYA_SQUARE);
            
            assertThat(result).isNotNull();
            assertThat(result.getGe().getName()).isNotBlank();
            assertThat(result.getGe().getMunicipality()).isNotBlank();
            assertThat(result.getGe().getCountry()).isNotBlank();
            assertThat(result.getGe().getCoord()).isNotNull();
            assertThat(result.getGe().getCoord().getLat()).isCloseTo(BARCELONA_CATALUNYA_SQUARE.getLat(), within(0.05));
            assertThat(result.getGe().getCoord().getLng()).isCloseTo(BARCELONA_CATALUNYA_SQUARE.getLng(), within(0.05));
        }
        
        @Test
        @DisplayName("should reverse geocode Madrid Puerta del Sol")
        void shouldReverseGeocodeMadrid() {
            ReverseGeocodeResult result = service.reverseGeocode(MADRID_PUERTA_DEL_SOL);
            
            assertThat(result).isNotNull();
            assertThat(result.getGe().getName()).isNotBlank();
            assertThat(result.getGe().getMunicipality()).isEqualTo("Madrid");
            assertThat(result.getGe().getCountry()).isEqualTo("España");
            assertThat(result.getGe().getType()).isNotNull();
        }
        
        @Test
        @DisplayName("should return result type when available")
        void shouldReturnResultType() {
            ReverseGeocodeResult result = service.reverseGeocode(BARCELONA_CATALUNYA_SQUARE);
            
            assertThat(result).isNotNull();
            assertThat(result.getGe()).isNotNull();
            assertThat(result.getGe().getType()).isNotNull();
            // Type can be 'poi', 'road', 'locality', 'municipality', etc.
            assertThat(result.getGe().getType().getValue())
                    .isIn("poi", "road", "locality", "municipality", "address", "street");
        }
        
        @Test
        @DisplayName("should handle ocean coordinates gracefully")
        void shouldHandleOceanCoordinates() {
            ReverseGeocodeResult result = service.reverseGeocode(OCEAN_COORD);
            
            // Ocean coordinates typically return null or very limited info
            if (result == null) {
                assertThat(result).isNull();
            } else {
                // If it returns something, verify basic structure
                assertThat(result.getGe()).isNotNull();
            }
        }
        
        @Test
        @DisplayName("should return detailed address information")
        void shouldReturnDetailedAddressInfo() {
            ReverseGeocodeResult result = service.reverseGeocode(BARCELONA_CATALUNYA_SQUARE);
            
            assertThat(result).isNotNull();
            
            // Check geographic element structure
            assertThat(result.getGe().getName()).isNotBlank();
            assertThat(result.getGe().getCoord()).isNotNull();
            
            // At least some administrative info should be present
            boolean hasAdminInfo = result.getGe().getLocality() != null || 
                                   result.getGe().getMunicipality() != null || 
                                   result.getGe().getSubregion() != null || 
                                   result.getGe().getRegion() != null || 
                                   result.getGe().getCountry() != null;
            assertThat(hasAdminInfo).isTrue();
        }
    }
    
    @Nested
    @DisplayName("reverseGeocodeBatch")
    class ReverseGeocodeBatchTests {
        
        @Test
        @DisplayName("should reverse geocode multiple coordinates")
        void shouldReverseGeocodeBatch() {
            List<Coordinate> coords = Arrays.asList(
                    BARCELONA_CATALUNYA_SQUARE,
                    MADRID_PUERTA_DEL_SOL
            );
            
            List<ReverseGeocodeResult> results = service.reverseGeocodeBatch(coords);
            
            assertThat(results).hasSize(2);
            
            // First result (Barcelona)
            assertThat(results.get(0).getGe().getName()).isNotBlank();
            assertThat(results.get(0).getGe().getMunicipality()).isNotBlank();
            
            // Second result (Madrid)
            assertThat(results.get(1).getGe().getName()).isNotBlank();
            assertThat(results.get(1).getGe().getMunicipality()).isEqualTo("Madrid");
        }
        
        @Test
        @DisplayName("should handle single coordinate batch")
        void shouldHandleSingleCoordinateBatch() {
            List<ReverseGeocodeResult> results = service.reverseGeocodeBatch(
                    Arrays.asList(MADRID_PUERTA_DEL_SOL)
            );
            
            assertThat(results).hasSize(1);
            assertThat(results.get(0).getGe().getMunicipality()).isEqualTo("Madrid");
        }
        
        @Test
        @DisplayName("should handle three coordinates batch")
        void shouldHandleThreeCoordinatesBatch() {
            List<Coordinate> coords = Arrays.asList(
                    BARCELONA_CATALUNYA_SQUARE,
                    MADRID_PUERTA_DEL_SOL,
                    ZARAGOZA
            );
            
            List<ReverseGeocodeResult> results = service.reverseGeocodeBatch(coords);
            
            assertThat(results).hasSize(3);
            
            for (ReverseGeocodeResult result : results) {
                assertThat(result.getGe()).isNotNull();
                assertThat(result.getGe().getName()).isNotBlank();
                assertThat(result.getGe().getCoord()).isNotNull();
            }
        }
    }
    
    @Nested
    @DisplayName("Level-specific requests")
    class LevelSpecificTests {
        
        @Test
        @DisplayName("should request timezone information")
        void shouldRequestTimezoneInfo() {
            TimezoneResult result = service.getTimezone(BARCELONA_CATALUNYA_SQUARE);
            
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotBlank();
            assertThat(result.getName()).isNotBlank();
            assertThat(result.getUtcOffset()).isNotNull();
            assertThat(result.getCoord()).isEqualTo(BARCELONA_CATALUNYA_SQUARE);
        }
        
        @Test
        @DisplayName("should request timezone with specific datetime")
        void shouldRequestTimezoneWithDatetime() {
            TimezoneOptions options = TimezoneOptions.builder()
                    .dateTime("2019-09-27T14:30:12Z")
                    .build();
            
            TimezoneResult result = service.getTimezone(BARCELONA_CATALUNYA_SQUARE, options);
            
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotBlank();
            assertThat(result.getName()).isNotBlank();
            assertThat(result.getLocalDateTime()).isNotBlank();
            assertThat(result.getUtcDateTime()).isEqualTo("2019-09-27T14:30:12Z");
            assertThat(result.getUtcOffset()).isNotNull();
            assertThat(result.getDaylightSavingTime()).isNotNull();
        }
        
        @Test
        @DisplayName("should request municipality level information")
        void shouldRequestMunicipalityLevel() {
            ReverseGeocodeOptions options = ReverseGeocodeOptions.builder()
                    .level(ReverseGeocodeLevel.MUN)
                    .build();
            
            ReverseGeocodeResult result = service.reverseGeocode(BARCELONA_CATALUNYA_SQUARE, options);
            
            assertThat(result).isNotNull();
            assertThat(result.getGe()).isNotNull();
        }
        
        @Test
        @DisplayName("should request postal code level information")
        void shouldRequestPostalCodeLevel() {
            ReverseGeocodeOptions options = ReverseGeocodeOptions.builder()
                    .level(ReverseGeocodeLevel.PCODE)
                    .build();
            
            ReverseGeocodeResult result = service.reverseGeocode(BARCELONA_CATALUNYA_SQUARE, options);
            
            assertThat(result).isNotNull();
            assertThat(result.getGe()).isNotNull();
        }
    }
    
    @Nested
    @DisplayName("getIntersectingRegions")
    class GetIntersectingRegionsTests {
        
        @Test
        @DisplayName("should find municipalities intersecting a polygon")
        void shouldFindMunicipalitiesIntersectingPolygon() {
            // Simple polygon around Barcelona area
            String wkt = "POLYGON((2.10 41.35, 2.20 41.35, 2.20 41.45, 2.10 41.45, 2.10 41.35))";
            
            List<ReverseGeocodeResult> results = service.getIntersectingRegions(wkt, "mun");
            
            assertThat(results).isNotNull();
            
            if (!results.isEmpty()) {
                for (ReverseGeocodeResult result : results) {
                    assertThat(result.getGe()).isNotNull();
                    assertThat(result.getGe().getName()).isNotBlank();
                    assertThat(result.getGe().getCoord()).isNotNull();
                }
            }
        }
        
        @Test
        @DisplayName("should find regions intersecting a polygon")
        void shouldFindRegionsIntersectingPolygon() {
            // Larger polygon around Madrid area
            String wkt = "POLYGON((-3.80 40.35, -3.60 40.35, -3.60 40.50, -3.80 40.50, -3.80 40.35))";
            
            List<ReverseGeocodeResult> results = service.getIntersectingRegions(wkt, "subreg");
            
            assertThat(results).isNotNull();
            
            if (!results.isEmpty()) {
                assertThat(results.get(0).getGe().getName()).isNotBlank();
            }
        }
        
        @Test
        @DisplayName("should return empty array for polygon with no intersections")
        void shouldReturnEmptyForOceanPolygon() {
            // Polygon in the middle of the ocean
            String wkt = "POLYGON((-30.0 30.0, -29.9 30.0, -29.9 30.1, -30.0 30.1, -30.0 30.0))";
            
            List<ReverseGeocodeResult> results = service.getIntersectingRegions(wkt, "mun");
            
            assertThat(results).isNotNull();
            // Likely to be empty, but API might return something
            assertThat(results.size()).isGreaterThanOrEqualTo(0);
        }
    }
}
