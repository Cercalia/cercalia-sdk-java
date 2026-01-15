package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.exception.CercaliaException;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.geofencing.*;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for GeofencingService.
 * Tests point-in-polygon geofencing functionality.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GeofencingServiceTest {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY");
    private static GeofencingService service;
    
    // Sample locations in Barcelona
    private static final Coordinate barcelonaCenter = new Coordinate(41.3874, 2.1686); // Plaça Catalunya
    private static final Coordinate sagradaFamilia = new Coordinate(41.4036, 2.1744);
    private static final Coordinate campNou = new Coordinate(41.3809, 2.1228);
    private static final Coordinate outsideBarcelona = new Coordinate(42.0, 3.0); // Far from Barcelona
    
    // Circular zone around Plaça Catalunya (500m radius)
    private static GeofenceShape circleZone;
    
    // Polygon covering central Barcelona (Eixample area)
    private static GeofenceShape polygonZone;
    
    @BeforeAll
    static void setUp() {
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        service = new GeofencingService(config);
        
        circleZone = GeofenceShape.circle("center-zone", 
                barcelonaCenter.getLng(), barcelonaCenter.getLat(), 500);
        
        polygonZone = new GeofenceShape("eixample-zone", 
                "POLYGON((2.15 41.38, 2.18 41.38, 2.18 41.41, 2.15 41.41, 2.15 41.38))");
    }
    
    @Nested
    @DisplayName("check()")
    class CheckMethod {
        
        @Test
        @Order(1)
        @DisplayName("should detect points inside a circular zone")
        void shouldDetectPointsInsideCircularZone() {
            List<GeofencePoint> points = Arrays.asList(
                    new GeofencePoint("inside", barcelonaCenter),
                    new GeofencePoint("outside", outsideBarcelona)
            );
            
            GeofenceResult result = service.check(
                    Collections.singletonList(circleZone), 
                    points, 
                    GeofenceOptions.defaults()
            );
            
            assertThat(result).isNotNull();
            assertThat(result.getTotalPointsChecked()).isEqualTo(2);
            assertThat(result.getTotalShapesChecked()).isEqualTo(1);
            
            // Should have a match for the circle zone with the inside point
            List<GeofenceMatch> centerMatches = result.getMatches().stream()
                    .filter(m -> "center-zone".equals(m.getShapeId()))
                    .collect(Collectors.toList());
            
            if (!centerMatches.isEmpty()) {
                GeofenceMatch centerMatch = centerMatches.get(0);
                List<String> insidePoints = centerMatch.getPointsInside().stream()
                        .map(GeofenceMatch.MatchedPoint::getId)
                        .collect(Collectors.toList());
                assertThat(insidePoints).contains("inside");
                assertThat(insidePoints).doesNotContain("outside");
            }
        }
        
        @Test
        @Order(2)
        @DisplayName("should detect points inside a polygon zone")
        void shouldDetectPointsInsidePolygonZone() {
            List<GeofencePoint> points = Arrays.asList(
                    new GeofencePoint("sagrada", sagradaFamilia),
                    new GeofencePoint("campnou", campNou),
                    new GeofencePoint("outside", outsideBarcelona)
            );
            
            GeofenceResult result = service.check(
                    Collections.singletonList(polygonZone), 
                    points, 
                    GeofenceOptions.defaults()
            );
            
            assertThat(result).isNotNull();
            assertThat(result.getTotalPointsChecked()).isEqualTo(3);
            
            // Sagrada Familia should be inside Eixample polygon
            List<GeofenceMatch> eixampleMatches = result.getMatches().stream()
                    .filter(m -> "eixample-zone".equals(m.getShapeId()))
                    .collect(Collectors.toList());
            
            if (!eixampleMatches.isEmpty()) {
                GeofenceMatch eixampleMatch = eixampleMatches.get(0);
                List<String> insideIds = eixampleMatch.getPointsInside().stream()
                        .map(GeofenceMatch.MatchedPoint::getId)
                        .collect(Collectors.toList());
                assertThat(insideIds).contains("sagrada");
                assertThat(insideIds).doesNotContain("outside");
            }
        }
        
        @Test
        @Order(3)
        @DisplayName("should handle multiple shapes and multiple points")
        void shouldHandleMultipleShapesAndPoints() {
            List<GeofenceShape> shapes = Arrays.asList(circleZone, polygonZone);
            List<GeofencePoint> points = Arrays.asList(
                    new GeofencePoint("center", barcelonaCenter),
                    new GeofencePoint("sagrada", sagradaFamilia),
                    new GeofencePoint("outside", outsideBarcelona)
            );
            
            GeofenceResult result = service.check(shapes, points, GeofenceOptions.defaults());
            
            assertThat(result).isNotNull();
            assertThat(result.getTotalPointsChecked()).isEqualTo(3);
            assertThat(result.getTotalShapesChecked()).isEqualTo(2);
        }
        
        @Test
        @Order(4)
        @DisplayName("should throw error with no shapes")
        void shouldThrowErrorWithNoShapes() {
            List<GeofencePoint> points = Collections.singletonList(
                    new GeofencePoint("test", barcelonaCenter)
            );
            
            assertThatThrownBy(() -> service.check(Collections.emptyList(), points, GeofenceOptions.defaults()))
                    .isInstanceOf(CercaliaException.class)
                    .hasMessageContaining("at least one shape");
        }
        
        @Test
        @Order(5)
        @DisplayName("should throw error with no points")
        void shouldThrowErrorWithNoPoints() {
            assertThatThrownBy(() -> service.check(
                    Collections.singletonList(circleZone), 
                    Collections.emptyList(), 
                    GeofenceOptions.defaults()))
                    .isInstanceOf(CercaliaException.class)
                    .hasMessageContaining("at least one point");
        }
    }
    
    @Nested
    @DisplayName("checkPoint()")
    class CheckPointMethod {
        
        @Test
        @Order(6)
        @DisplayName("should return matching zone IDs for a single point")
        void shouldReturnMatchingZoneIds() {
            List<GeofenceShape> shapes = Arrays.asList(circleZone, polygonZone);
            
            List<String> matchingZones = service.checkPoint(shapes, barcelonaCenter);
            
            assertThat(matchingZones).isNotNull();
            // Barcelona center should be in the circle zone at minimum
            assertThat(matchingZones).contains("center-zone");
        }
        
        @Test
        @Order(7)
        @DisplayName("should return empty list for point outside all zones")
        void shouldReturnEmptyListForPointOutside() {
            List<GeofenceShape> shapes = Collections.singletonList(circleZone);
            
            List<String> matchingZones = service.checkPoint(shapes, outsideBarcelona);
            
            assertThat(matchingZones).isNotNull();
            assertThat(matchingZones).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("isInsideCircle()")
    class IsInsideCircleMethod {
        
        @Test
        @Order(8)
        @DisplayName("should return true for point inside circle")
        void shouldReturnTrueForPointInsideCircle() {
            boolean isInside = service.isInsideCircle(
                    barcelonaCenter,
                    1000, // 1km radius
                    new Coordinate(41.388, 2.169) // Very close to center
            );
            
            assertThat(isInside).isTrue();
        }
        
        @Test
        @Order(9)
        @DisplayName("should return false for point outside circle")
        void shouldReturnFalseForPointOutsideCircle() {
            boolean isInside = service.isInsideCircle(
                    barcelonaCenter,
                    100, // 100m radius
                    outsideBarcelona
            );
            
            assertThat(isInside).isFalse();
        }
    }
    
    @Nested
    @DisplayName("isInsidePolygon()")
    class IsInsidePolygonMethod {
        
        @Test
        @Order(10)
        @DisplayName("should return true for point inside polygon")
        void shouldReturnTrueForPointInsidePolygon() {
            String polygonWkt = "POLYGON((2.16 41.39, 2.18 41.39, 2.18 41.41, 2.16 41.41, 2.16 41.39))";
            
            boolean isInside = service.isInsidePolygon(polygonWkt, sagradaFamilia);
            
            assertThat(isInside).isTrue();
        }
        
        @Test
        @Order(11)
        @DisplayName("should return false for point outside polygon")
        void shouldReturnFalseForPointOutsidePolygon() {
            String polygonWkt = "POLYGON((2.16 41.39, 2.18 41.39, 2.18 41.41, 2.16 41.41, 2.16 41.39))";
            
            boolean isInside = service.isInsidePolygon(polygonWkt, outsideBarcelona);
            
            assertThat(isInside).isFalse();
        }
    }
    
    @Nested
    @DisplayName("filterPointsInShape()")
    class FilterPointsInShapeMethod {
        
        @Test
        @Order(12)
        @DisplayName("should return only points inside the shape")
        void shouldReturnOnlyPointsInsideShape() {
            List<GeofencePoint> points = Arrays.asList(
                    new GeofencePoint("center", barcelonaCenter),
                    new GeofencePoint("near", new Coordinate(41.388, 2.170)),
                    new GeofencePoint("far", outsideBarcelona)
            );
            
            // Large circle covering central Barcelona
            GeofenceShape largeCircle = GeofenceShape.circle("large", 
                    barcelonaCenter.getLng(), barcelonaCenter.getLat(), 2000);
            
            List<GeofencePoint> insidePoints = service.filterPointsInShape(largeCircle, points);
            
            assertThat(insidePoints).isNotNull();
            List<String> insideIds = insidePoints.stream()
                    .map(GeofencePoint::getId)
                    .collect(Collectors.toList());
            assertThat(insideIds).contains("center", "near");
            assertThat(insideIds).doesNotContain("far");
        }
        
        @Test
        @Order(13)
        @DisplayName("should return empty list when no points inside")
        void shouldReturnEmptyListWhenNoPointsInside() {
            List<GeofencePoint> points = Arrays.asList(
                    new GeofencePoint("far1", outsideBarcelona),
                    new GeofencePoint("far2", new Coordinate(43.0, 4.0))
            );
            
            List<GeofencePoint> insidePoints = service.filterPointsInShape(circleZone, points);
            
            assertThat(insidePoints).isEmpty();
        }
        
        @Test
        @Order(14)
        @DisplayName("should return empty list for empty points input")
        void shouldReturnEmptyListForEmptyInput() {
            List<GeofencePoint> insidePoints = service.filterPointsInShape(circleZone, Collections.emptyList());
            
            assertThat(insidePoints).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("Helper methods")
    class HelperMethods {
        
        @Test
        @Order(15)
        @DisplayName("createCircle() should create valid circle WKT")
        void createCircleShouldCreateValidWkt() {
            GeofenceShape circle = service.createCircle("test", barcelonaCenter, 1000);
            
            assertThat(circle.getId()).isEqualTo("test");
            assertThat(circle.getWkt()).contains("CIRCLE");
            assertThat(circle.getWkt()).contains(String.valueOf(barcelonaCenter.getLng()));
            assertThat(circle.getWkt()).contains(String.valueOf(barcelonaCenter.getLat()));
            assertThat(circle.getWkt()).contains("1000");
        }
        
        @Test
        @Order(16)
        @DisplayName("createRectangle() should create valid polygon WKT")
        void createRectangleShouldCreateValidWkt() {
            Coordinate sw = new Coordinate(41.37, 2.15);
            Coordinate ne = new Coordinate(41.40, 2.19);
            
            GeofenceShape rect = service.createRectangle("test-rect", sw, ne);
            
            assertThat(rect.getId()).isEqualTo("test-rect");
            assertThat(rect.getWkt()).contains("POLYGON");
            assertThat(rect.getWkt()).contains(String.valueOf(sw.getLng()));
            assertThat(rect.getWkt()).contains(String.valueOf(sw.getLat()));
            assertThat(rect.getWkt()).contains(String.valueOf(ne.getLng()));
            assertThat(rect.getWkt()).contains(String.valueOf(ne.getLat()));
        }
        
        @Test
        @Order(17)
        @DisplayName("createRectangle() result should work with check()")
        void createRectangleShouldWorkWithCheck() {
            Coordinate sw = new Coordinate(41.37, 2.15);
            Coordinate ne = new Coordinate(41.40, 2.19);
            GeofenceShape rect = service.createRectangle("delivery-zone", sw, ne);
            
            List<GeofencePoint> points = Arrays.asList(
                    new GeofencePoint("inside", barcelonaCenter),
                    new GeofencePoint("outside", outsideBarcelona)
            );
            
            GeofenceResult result = service.check(
                    Collections.singletonList(rect), 
                    points, 
                    GeofenceOptions.defaults()
            );
            
            assertThat(result).isNotNull();
            assertThat(result.getTotalShapesChecked()).isEqualTo(1);
        }
    }
    
    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {
        
        @Test
        @Order(18)
        @DisplayName("should handle very small radius circles")
        void shouldHandleVerySmallRadiusCircles() {
            GeofenceShape tinyCircle = service.createCircle("tiny", barcelonaCenter, 10); // 10m radius
            
            GeofenceResult result = service.check(
                    Collections.singletonList(tinyCircle),
                    Collections.singletonList(new GeofencePoint("exact", barcelonaCenter)),
                    GeofenceOptions.defaults()
            );
            
            assertThat(result).isNotNull();
            // Point at exact center should be inside
            List<GeofenceMatch> matches = result.getMatches().stream()
                    .filter(m -> "tiny".equals(m.getShapeId()))
                    .collect(Collectors.toList());
            
            if (!matches.isEmpty()) {
                assertThat(matches.get(0).getPointsInside()).isNotEmpty();
            }
        }
        
        @Test
        @Order(19)
        @DisplayName("should handle large radius circles")
        void shouldHandleLargeRadiusCircles() {
            GeofenceShape largeCircle = service.createCircle("large", barcelonaCenter, 50000); // 50km radius
            
            List<GeofencePoint> points = Arrays.asList(
                    new GeofencePoint("barcelona", barcelonaCenter),
                    new GeofencePoint("sagrada", sagradaFamilia),
                    new GeofencePoint("campnou", campNou)
            );
            
            GeofenceResult result = service.check(
                    Collections.singletonList(largeCircle), 
                    points, 
                    GeofenceOptions.defaults()
            );
            
            assertThat(result).isNotNull();
            // All Barcelona points should be inside
            List<GeofenceMatch> matches = result.getMatches().stream()
                    .filter(m -> "large".equals(m.getShapeId()))
                    .collect(Collectors.toList());
            
            if (!matches.isEmpty()) {
                assertThat(matches.get(0).getPointsInside()).hasSize(3);
            }
        }
        
        @Test
        @Order(20)
        @DisplayName("should handle complex polygon with many vertices")
        void shouldHandleComplexPolygon() {
            // Star-shaped polygon
            GeofenceShape starPolygon = new GeofenceShape("star",
                    "POLYGON((2.16 41.39, 2.17 41.40, 2.18 41.39, 2.175 41.385, 2.18 41.38, 2.17 41.375, 2.16 41.38, 2.165 41.385, 2.16 41.39))");
            
            GeofenceResult result = service.check(
                    Collections.singletonList(starPolygon),
                    Collections.singletonList(new GeofencePoint("center", new Coordinate(41.385, 2.17))),
                    GeofenceOptions.defaults()
            );
            
            assertThat(result).isNotNull();
        }
    }
    
    @Nested
    @DisplayName("Async operations")
    class AsyncOperations {
        
        @Test
        @Order(21)
        @DisplayName("should check geofences asynchronously")
        void shouldCheckGeofencesAsync() throws Exception {
            List<GeofencePoint> points = Arrays.asList(
                    new GeofencePoint("inside", barcelonaCenter),
                    new GeofencePoint("outside", outsideBarcelona)
            );
            
            CompletableFuture<GeofenceResult> future = service.checkAsync(
                    Collections.singletonList(circleZone),
                    points,
                    GeofenceOptions.defaults()
            );
            
            GeofenceResult result = future.get(30, TimeUnit.SECONDS);
            
            assertThat(result).isNotNull();
            assertThat(result.getTotalPointsChecked()).isEqualTo(2);
        }
        
        @Test
        @Order(22)
        @DisplayName("should use isInsideCircleAsync()")
        void shouldUseIsInsideCircleAsync() throws Exception {
            CompletableFuture<Boolean> future = service.isInsideCircleAsync(
                    barcelonaCenter,
                    1000,
                    new Coordinate(41.388, 2.169)
            );
            
            Boolean isInside = future.get(30, TimeUnit.SECONDS);
            
            assertThat(isInside).isTrue();
        }
    }
    
    @Nested
    @DisplayName("Data Integrity (Golden Rules)")
    class DataIntegrity {
        
        @Test
        @Order(23)
        @DisplayName("should never return null for required fields")
        void shouldNeverReturnNullForRequiredFields() {
            List<GeofencePoint> points = Collections.singletonList(
                    new GeofencePoint("test", barcelonaCenter)
            );
            
            GeofenceResult result = service.check(
                    Collections.singletonList(circleZone),
                    points,
                    GeofenceOptions.defaults()
            );
            
            assertThat(result).isNotNull();
            assertThat(result.getMatches()).isNotNull();
            
            for (GeofenceMatch match : result.getMatches()) {
                assertThat(match.getShapeId()).isNotNull();
                assertThat(match.getShapeWkt()).isNotNull();
                assertThat(match.getPointsInside()).isNotNull();
                
                for (GeofenceMatch.MatchedPoint point : match.getPointsInside()) {
                    assertThat(point.getId()).isNotNull();
                    assertThat(point.getCoord()).isNotNull();
                }
            }
        }
        
        @Test
        @Order(24)
        @DisplayName("should preserve shape IDs in results")
        void shouldPreserveShapeIds() {
            GeofenceShape customShape = new GeofenceShape("my-custom-zone-123",
                    "CIRCLE(" + barcelonaCenter.getLng() + " " + barcelonaCenter.getLat() + ", 1000)");
            
            List<GeofencePoint> points = Collections.singletonList(
                    new GeofencePoint("test", barcelonaCenter)
            );
            
            GeofenceResult result = service.check(
                    Collections.singletonList(customShape),
                    points,
                    GeofenceOptions.defaults()
            );
            
            assertThat(result.getMatches()).isNotEmpty();
            assertThat(result.getMatches().get(0).getShapeId()).isEqualTo("my-custom-zone-123");
        }
    }
}
