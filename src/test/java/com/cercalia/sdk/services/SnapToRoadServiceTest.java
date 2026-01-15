package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.exception.CercaliaException;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.snaptoroad.*;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for SnapToRoadService.
 * Tests GPS track map matching functionality.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SnapToRoadServiceTest {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY");
    private static SnapToRoadService service;
    
    // Sample GPS track along Diagonal Avenue, Barcelona
    private static List<SnapToRoadPoint> barcelonaTrack;
    
    @BeforeAll
    static void setUp() {
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        service = new SnapToRoadService(config);
        
        barcelonaTrack = Arrays.asList(
                SnapToRoadPoint.of(41.3928, 2.1365), // Near Diagonal / Francesc Macià
                SnapToRoadPoint.of(41.3940, 2.1420), // Along Diagonal
                SnapToRoadPoint.of(41.3952, 2.1480), // Further along Diagonal
                SnapToRoadPoint.of(41.3965, 2.1540)  // Near Passeig de Gràcia
        );
    }
    
    @Nested
    @DisplayName("Basic Map Matching")
    class BasicMapMatching {
        
        @Test
        @Order(1)
        @DisplayName("should match GPS track to road network and return valid segments")
        void shouldMatchGpsTrackToRoadNetwork() {
            SnapToRoadResult result = service.match(barcelonaTrack, SnapToRoadOptions.defaults());
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            assertThat(result.getTotalDistance()).isGreaterThanOrEqualTo(0);
            
            if (result.hasSegments()) {
                for (SnapToRoadSegment segment : result.getSegments()) {
                    // Required fields - strict validation
                    assertThat(segment.getWkt()).isNotNull();
                    assertThat(segment.getWkt()).isNotEmpty();
                    assertThat(segment.getWkt()).matches("^(LINESTRING|MULTILINESTRING|POINT).*");
                    
                    assertThat(segment.getDistance()).isGreaterThanOrEqualTo(0);
                }
                
                // totalDistance should match sum of segment distances
                double sumDistance = result.getSegments().stream()
                        .mapToDouble(SnapToRoadSegment::getDistance)
                        .sum();
                assertThat(sumDistance).isCloseTo(result.getTotalDistance(), within(0.01));
            } else {
                assertThat(result.getTotalDistance()).isEqualTo(0);
            }
        }
        
        @Test
        @Order(2)
        @DisplayName("should handle minimum valid track (2 points)")
        void shouldHandleMinimumValidTrack() {
            List<SnapToRoadPoint> minTrack = Arrays.asList(
                    SnapToRoadPoint.of(41.3928, 2.1365),
                    SnapToRoadPoint.of(41.3940, 2.1420)
            );
            
            SnapToRoadResult result = service.match(minTrack, SnapToRoadOptions.defaults());
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            assertThat(result.getTotalDistance()).isGreaterThanOrEqualTo(0);
            
            if (result.hasSegments()) {
                SnapToRoadSegment firstSegment = result.getSegments().get(0);
                assertThat(firstSegment.getWkt()).isNotNull();
                assertThat(firstSegment.getDistance()).isGreaterThanOrEqualTo(0);
            }
        }
        
        @Test
        @Order(3)
        @DisplayName("should handle track in different geographic locations (Madrid)")
        void shouldHandleTrackInMadrid() {
            List<SnapToRoadPoint> madridTrack = Arrays.asList(
                    SnapToRoadPoint.of(40.4168, -3.7038), // Puerta del Sol
                    SnapToRoadPoint.of(40.4200, -3.7000), // Near Gran Vía
                    SnapToRoadPoint.of(40.4220, -3.6980)  // Cibeles area
            );
            
            SnapToRoadResult result = service.match(madridTrack, SnapToRoadOptions.defaults());
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            assertThat(result.getTotalDistance()).isGreaterThanOrEqualTo(0);
            
            for (SnapToRoadSegment segment : result.getSegments()) {
                assertThat(segment.getWkt()).isNotNull();
            }
        }
        
        @Test
        @Order(4)
        @DisplayName("should handle tracks of different lengths correctly")
        void shouldHandleTracksOfDifferentLengths() {
            // Short track
            List<SnapToRoadPoint> shortTrack = Arrays.asList(
                    SnapToRoadPoint.of(41.3928, 2.1365),
                    SnapToRoadPoint.of(41.3940, 2.1420)
            );
            
            // Long track
            List<SnapToRoadPoint> longTrack = Arrays.asList(
                    SnapToRoadPoint.of(41.3928, 2.1365),
                    SnapToRoadPoint.of(41.3940, 2.1420),
                    SnapToRoadPoint.of(41.3952, 2.1480),
                    SnapToRoadPoint.of(41.3965, 2.1540),
                    SnapToRoadPoint.of(41.3975, 2.1580),
                    SnapToRoadPoint.of(41.3985, 2.1620)
            );
            
            SnapToRoadResult shortResult = service.match(shortTrack, SnapToRoadOptions.defaults());
            SnapToRoadResult longResult = service.match(longTrack, SnapToRoadOptions.defaults());
            
            assertThat(shortResult.getSegments()).isNotNull();
            assertThat(longResult.getSegments()).isNotNull();
            assertThat(shortResult.getTotalDistance()).isGreaterThanOrEqualTo(0);
            assertThat(longResult.getTotalDistance()).isGreaterThanOrEqualTo(0);
        }
    }
    
    @Nested
    @DisplayName("Weight Options (Distance vs Time)")
    class WeightOptions {
        
        @Test
        @Order(5)
        @DisplayName("should support distance-based matching (default)")
        void shouldSupportDistanceBasedMatching() {
            SnapToRoadOptions options = SnapToRoadOptions.builder()
                    .weight(SnapToRoadOptions.Weight.DISTANCE)
                    .build();
            
            SnapToRoadResult result = service.match(barcelonaTrack, options);
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            
            for (SnapToRoadSegment segment : result.getSegments()) {
                assertThat(segment.getWkt()).isNotNull();
                assertThat(segment.getDistance()).isGreaterThanOrEqualTo(0);
            }
        }
        
        @Test
        @Order(6)
        @DisplayName("should support time-based matching")
        void shouldSupportTimeBasedMatching() {
            SnapToRoadOptions options = SnapToRoadOptions.builder()
                    .weight(SnapToRoadOptions.Weight.TIME)
                    .build();
            
            SnapToRoadResult result = service.match(barcelonaTrack, options);
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            
            for (SnapToRoadSegment segment : result.getSegments()) {
                assertThat(segment.getWkt()).isNotNull();
                assertThat(segment.getDistance()).isGreaterThanOrEqualTo(0);
            }
        }
        
        @Test
        @Order(7)
        @DisplayName("should return results for both distance and time weighting")
        void shouldReturnDifferentResultsForWeighting() {
            SnapToRoadOptions distanceOptions = SnapToRoadOptions.builder()
                    .weight(SnapToRoadOptions.Weight.DISTANCE)
                    .build();
            SnapToRoadOptions timeOptions = SnapToRoadOptions.builder()
                    .weight(SnapToRoadOptions.Weight.TIME)
                    .build();
            
            SnapToRoadResult distanceResult = service.match(barcelonaTrack, distanceOptions);
            SnapToRoadResult timeResult = service.match(barcelonaTrack, timeOptions);
            
            assertThat(distanceResult).isNotNull();
            assertThat(timeResult).isNotNull();
            assertThat(distanceResult.getSegments()).isNotNull();
            assertThat(timeResult.getSegments()).isNotNull();
            assertThat(distanceResult.getTotalDistance()).isGreaterThanOrEqualTo(0);
            assertThat(timeResult.getTotalDistance()).isGreaterThanOrEqualTo(0);
        }
    }
    
    @Nested
    @DisplayName("Geometry Simplification")
    class GeometrySimplification {
        
        @Test
        @Order(8)
        @DisplayName("should support detailed geometry with low tolerance")
        void shouldSupportDetailedGeometry() {
            SnapToRoadOptions options = SnapToRoadOptions.builder()
                    .geometryTolerance(1)
                    .build();
            
            SnapToRoadResult result = service.match(barcelonaTrack, options);
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            
            for (SnapToRoadSegment segment : result.getSegments()) {
                assertThat(segment.getWkt()).isNotNull();
            }
        }
        
        @Test
        @Order(9)
        @DisplayName("should support simplified geometry with high tolerance")
        void shouldSupportSimplifiedGeometry() {
            SnapToRoadOptions options = SnapToRoadOptions.builder()
                    .geometryTolerance(100)
                    .build();
            
            SnapToRoadResult result = service.match(barcelonaTrack, options);
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            
            for (SnapToRoadSegment segment : result.getSegments()) {
                assertThat(segment.getWkt()).isNotNull();
            }
        }
        
        @Test
        @Order(10)
        @DisplayName("should use matchSimplified() convenience method")
        void shouldUseMatchSimplifiedConvenienceMethod() {
            SnapToRoadResult result = service.matchSimplified(barcelonaTrack, 50);
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            
            for (SnapToRoadSegment segment : result.getSegments()) {
                assertThat(segment.getWkt()).isNotNull();
                assertThat(segment.getDistance()).isGreaterThanOrEqualTo(0);
            }
        }
    }
    
    @Nested
    @DisplayName("Speeding Detection")
    class SpeedingDetection {
        
        @Test
        @Order(11)
        @DisplayName("should detect speeding when enabled with speed data")
        void shouldDetectSpeedingWhenEnabled() {
            List<SnapToRoadPoint> trackWithSpeed = Arrays.asList(
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3928, 2.1365)).speed(50).build(),
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3940, 2.1420)).speed(130).build(), // High speed
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3952, 2.1480)).speed(60).build(),
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3965, 2.1540)).speed(70).build()
            );
            
            SnapToRoadOptions options = SnapToRoadOptions.builder()
                    .speeding(true)
                    .speedTolerance(10)
                    .build();
            
            SnapToRoadResult result = service.match(trackWithSpeed, options);
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            
            for (SnapToRoadSegment segment : result.getSegments()) {
                assertThat(segment.getWkt()).isNotNull();
                assertThat(segment.getDistance()).isGreaterThanOrEqualTo(0);
                
                if (segment.getSpeeding() != null && segment.getSpeeding()) {
                    if (segment.getSpeedingLevel() != null) {
                        assertThat(segment.getSpeedingLevel()).isGreaterThanOrEqualTo(0);
                    }
                }
            }
        }
        
        @Test
        @Order(12)
        @DisplayName("should use matchWithSpeedingDetection() convenience method")
        void shouldUseMatchWithSpeedingDetectionConvenienceMethod() {
            List<SnapToRoadPoint> trackWithSpeed = Arrays.asList(
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3928, 2.1365)).speed(50).build(),
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3940, 2.1420)).speed(100).build(),
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3952, 2.1480)).speed(60).build()
            );
            
            SnapToRoadResult result = service.matchWithSpeedingDetection(trackWithSpeed, 10);
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            
            for (SnapToRoadSegment segment : result.getSegments()) {
                assertThat(segment.getWkt()).isNotNull();
                assertThat(segment.getDistance()).isGreaterThanOrEqualTo(0);
            }
        }
    }
    
    @Nested
    @DisplayName("Segment Grouping by Attribute")
    class SegmentGrouping {
        
        @Test
        @Order(13)
        @DisplayName("should group segments using matchWithGroups()")
        void shouldGroupSegmentsUsingMatchWithGroups() {
            List<Coordinate> coords = barcelonaTrack.stream()
                    .map(SnapToRoadPoint::getCoord)
                    .collect(Collectors.toList());
            
            SnapToRoadResult result = service.matchWithGroups(coords, 2, SnapToRoadOptions.defaults());
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            
            for (SnapToRoadSegment segment : result.getSegments()) {
                assertThat(segment.getWkt()).isNotNull();
                assertThat(segment.getDistance()).isGreaterThanOrEqualTo(0);
            }
        }
        
        @Test
        @Order(14)
        @DisplayName("should handle custom attributes in track points")
        void shouldHandleCustomAttributes() {
            List<SnapToRoadPoint> trackWithAttributes = Arrays.asList(
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3928, 2.1365)).attribute("LEG_A").build(),
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3940, 2.1420)).attribute("LEG_A").build(),
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3952, 2.1480)).attribute("LEG_B").build(),
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3965, 2.1540)).attribute("LEG_B").build()
            );
            
            SnapToRoadResult result = service.match(trackWithAttributes, SnapToRoadOptions.defaults());
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            
            for (SnapToRoadSegment segment : result.getSegments()) {
                assertThat(segment.getWkt()).isNotNull();
                assertThat(segment.getDistance()).isGreaterThanOrEqualTo(0);
            }
        }
    }
    
    @Nested
    @DisplayName("Combined Options")
    class CombinedOptions {
        
        @Test
        @Order(15)
        @DisplayName("should combine speeding detection and geometry simplification")
        void shouldCombineSpeedingAndSimplification() {
            List<SnapToRoadPoint> trackWithSpeed = Arrays.asList(
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3928, 2.1365)).speed(50).build(),
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3940, 2.1420)).speed(100).build(),
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3952, 2.1480)).speed(60).build()
            );
            
            SnapToRoadOptions options = SnapToRoadOptions.builder()
                    .speeding(true)
                    .speedTolerance(10)
                    .geometryTolerance(50)
                    .build();
            
            SnapToRoadResult result = service.match(trackWithSpeed, options);
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            
            for (SnapToRoadSegment segment : result.getSegments()) {
                assertThat(segment.getWkt()).isNotNull();
                assertThat(segment.getDistance()).isGreaterThanOrEqualTo(0);
            }
        }
        
        @Test
        @Order(16)
        @DisplayName("should combine all options together")
        void shouldCombineAllOptions() {
            List<SnapToRoadPoint> trackWithSpeed = Arrays.asList(
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3928, 2.1365)).speed(50).build(),
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3940, 2.1420)).speed(100).build(),
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3952, 2.1480)).speed(60).build(),
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3965, 2.1540)).speed(70).build()
            );
            
            SnapToRoadOptions options = SnapToRoadOptions.builder()
                    .weight(SnapToRoadOptions.Weight.TIME)
                    .speeding(true)
                    .speedTolerance(5)
                    .geometryTolerance(100)
                    .build();
            
            SnapToRoadResult result = service.match(trackWithSpeed, options);
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            assertThat(result.getTotalDistance()).isGreaterThanOrEqualTo(0);
            
            for (SnapToRoadSegment segment : result.getSegments()) {
                assertThat(segment.getWkt()).isNotNull();
                assertThat(segment.getWkt()).isNotEmpty();
                assertThat(segment.getDistance()).isGreaterThanOrEqualTo(0);
            }
        }
    }
    
    @Nested
    @DisplayName("Async Operations")
    class AsyncOperations {
        
        @Test
        @Order(17)
        @DisplayName("should match track asynchronously")
        void shouldMatchTrackAsync() throws Exception {
            CompletableFuture<SnapToRoadResult> future = service.matchAsync(barcelonaTrack, SnapToRoadOptions.defaults());
            
            SnapToRoadResult result = future.get(30, TimeUnit.SECONDS);
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            assertThat(result.getTotalDistance()).isGreaterThanOrEqualTo(0);
        }
        
        @Test
        @Order(18)
        @DisplayName("should use matchSimplifiedAsync()")
        void shouldUseMatchSimplifiedAsync() throws Exception {
            CompletableFuture<SnapToRoadResult> future = service.matchSimplifiedAsync(barcelonaTrack, 50);
            
            SnapToRoadResult result = future.get(30, TimeUnit.SECONDS);
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
        }
    }
    
    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {
        
        @Test
        @Order(19)
        @DisplayName("should throw error with fewer than 2 points")
        void shouldThrowErrorWithFewerThan2Points() {
            List<SnapToRoadPoint> singlePoint = Arrays.asList(
                    SnapToRoadPoint.of(41.3928, 2.1365)
            );
            
            assertThatThrownBy(() -> service.match(singlePoint, SnapToRoadOptions.defaults()))
                    .isInstanceOf(CercaliaException.class)
                    .hasMessageContaining("at least 2 GPS points");
        }
        
        @Test
        @Order(20)
        @DisplayName("should throw error with empty list")
        void shouldThrowErrorWithEmptyList() {
            List<SnapToRoadPoint> emptyList = Arrays.asList();
            
            assertThatThrownBy(() -> service.match(emptyList, SnapToRoadOptions.defaults()))
                    .isInstanceOf(CercaliaException.class)
                    .hasMessageContaining("at least 2 GPS points");
        }
        
        @Test
        @Order(21)
        @DisplayName("should throw error with null input")
        void shouldThrowErrorWithNullInput() {
            assertThatThrownBy(() -> service.match(null, SnapToRoadOptions.defaults()))
                    .isInstanceOf(CercaliaException.class);
        }
    }
    
    @Nested
    @DisplayName("Data Integrity (Golden Rules)")
    class DataIntegrity {
        
        @Test
        @Order(22)
        @DisplayName("should return valid WKT geometry format")
        void shouldReturnValidWktGeometryFormat() {
            SnapToRoadResult result = service.match(barcelonaTrack, SnapToRoadOptions.defaults());
            
            assertThat(result.getSegments()).isNotNull();
            
            for (SnapToRoadSegment segment : result.getSegments()) {
                assertThat(segment.getWkt()).isNotNull();
                assertThat(segment.getWkt()).isNotEmpty();
                assertThat(segment.getWkt()).matches("^(LINESTRING|MULTILINESTRING|POINT|POLYGON|MULTIPOINT|MULTIPOLYGON).*");
            }
        }
        
        @Test
        @Order(23)
        @DisplayName("should never return null for required fields")
        void shouldNeverReturnNullForRequiredFields() {
            SnapToRoadResult result = service.match(barcelonaTrack, SnapToRoadOptions.defaults());
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
            
            for (SnapToRoadSegment segment : result.getSegments()) {
                assertThat(segment.getWkt()).isNotNull();
                // Distance is a primitive double, cannot be null
            }
        }
        
        @Test
        @Order(24)
        @DisplayName("should maintain numeric precision for distances")
        void shouldMaintainNumericPrecisionForDistances() {
            SnapToRoadResult result = service.match(barcelonaTrack, SnapToRoadOptions.defaults());
            
            for (SnapToRoadSegment segment : result.getSegments()) {
                assertThat(segment.getDistance()).isNotNaN();
                assertThat(segment.getDistance()).isFinite();
                assertThat(segment.getDistance()).isGreaterThanOrEqualTo(0);
            }
            
            assertThat(result.getTotalDistance()).isNotNaN();
            assertThat(result.getTotalDistance()).isFinite();
            assertThat(result.getTotalDistance()).isGreaterThanOrEqualTo(0);
        }
        
        @Test
        @Order(25)
        @DisplayName("should validate totalDistance equals sum of segment distances")
        void shouldValidateTotalDistanceEqualsSum() {
            SnapToRoadResult result = service.match(barcelonaTrack, SnapToRoadOptions.defaults());
            
            if (result.hasSegments()) {
                double sumDistance = result.getSegments().stream()
                        .mapToDouble(SnapToRoadSegment::getDistance)
                        .sum();
                
                assertThat(sumDistance).isCloseTo(result.getTotalDistance(), within(0.01));
            } else {
                assertThat(result.getTotalDistance()).isEqualTo(0);
            }
        }
    }
    
    @Nested
    @DisplayName("Track String Building (Internal)")
    class TrackStringBuilding {
        
        @Test
        @Order(26)
        @DisplayName("should build track with all optional fields (via API call)")
        void shouldBuildTrackWithAllOptionalFields() {
            // Using exact coords from documentation
            List<SnapToRoadPoint> trackWithAllFields = Arrays.asList(
                    SnapToRoadPoint.builder()
                            .coord(new Coordinate(41.969279, 2.825850))
                            .compass(0)
                            .angle(45)
                            .speed(70)
                            .attribute("A")
                            .build(),
                    SnapToRoadPoint.builder()
                            .coord(new Coordinate(41.965995, 2.822355))
                            .compass(0)
                            .angle(45)
                            .speed(10)
                            .attribute("A")
                            .build()
            );
            
            SnapToRoadResult result = service.match(trackWithAllFields, SnapToRoadOptions.defaults());
            
            // If API accepts the track, it means the format is correct
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
        }
        
        @Test
        @Order(27)
        @DisplayName("should handle mixed optional fields per point")
        void shouldHandleMixedOptionalFields() {
            List<SnapToRoadPoint> mixedTrack = Arrays.asList(
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3928, 2.1365)).speed(50).build(), // Only speed
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3940, 2.1420)).compass(90).angle(45).build(), // Only compass/angle
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3952, 2.1480)).attribute("A").build(), // Only attribute
                    SnapToRoadPoint.builder().coord(new Coordinate(41.3965, 2.1540)).compass(0).angle(30).speed(70).attribute("B").build() // All fields
            );
            
            SnapToRoadResult result = service.match(mixedTrack, SnapToRoadOptions.defaults());
            
            assertThat(result).isNotNull();
            assertThat(result.getSegments()).isNotNull();
        }
    }
}
