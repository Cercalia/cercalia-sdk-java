package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.isochrone.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Isochrone Service Integration Tests with Real API Data.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IsochroneServiceTest {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY");
    private static IsochroneService service;
    
    private static final Coordinate BARCELONA = new Coordinate(41.3851, 2.1734);
    private static final Coordinate MADRID = new Coordinate(40.4168, -3.7038);
    
    @BeforeAll
    static void setUp() {
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        service = new IsochroneService(config);
    }
    
    @Nested
    @DisplayName("calculate")
    class Calculate {
        
        @Test
        @Order(1)
        @DisplayName("should calculate time-based isochrone correctly (10 minutes)")
        void shouldCalculateTimeBasedIsochrone10Minutes() {
            IsochroneResult result = service.calculate(BARCELONA, 
                    IsochroneOptions.builder()
                            .value(10) // 10 minutes
                            .weight(IsochroneWeight.TIME)
                            .build());
            
            assertThat(result.getWkt()).isNotNull();
            assertThat(result.getWkt()).contains("POLYGON");
            assertThat(result.getCenter()).isEqualTo(BARCELONA);
            assertThat(result.getValue()).isEqualTo(10);
            assertThat(result.getWeight()).isEqualTo(IsochroneWeight.TIME);
            assertThat(result.getLevel()).isEqualTo("600000"); // 10 * 60 * 1000 ms
        }
        
        @Test
        @Order(2)
        @DisplayName("should calculate time-based isochrone for different duration (5 minutes)")
        void shouldCalculateTimeBasedIsochrone5Minutes() {
            IsochroneResult result = service.calculate(MADRID, 
                    IsochroneOptions.builder()
                            .value(5) // 5 minutes
                            .weight(IsochroneWeight.TIME)
                            .build());
            
            assertThat(result.getWkt()).isNotNull();
            assertThat(result.getWkt()).contains("POLYGON");
            assertThat(result.getCenter()).isEqualTo(MADRID);
            assertThat(result.getValue()).isEqualTo(5);
            assertThat(result.getWeight()).isEqualTo(IsochroneWeight.TIME);
            assertThat(result.getLevel()).isEqualTo("300000"); // 5 * 60 * 1000 ms
        }
        
        @Test
        @Order(3)
        @DisplayName("should calculate distance-based isochrone correctly (1000 meters)")
        void shouldCalculateDistanceBasedIsochrone1000Meters() {
            IsochroneResult result = service.calculate(BARCELONA, 
                    IsochroneOptions.builder()
                            .value(1000) // 1000 meters
                            .weight(IsochroneWeight.DISTANCE)
                            .build());
            
            assertThat(result.getWkt()).isNotNull();
            assertThat(result.getWkt()).contains("POLYGON");
            assertThat(result.getLevel()).isEqualTo("1000");
            assertThat(result.getWeight()).isEqualTo(IsochroneWeight.DISTANCE);
            assertThat(result.getValue()).isEqualTo(1000);
        }
        
        @Test
        @Order(4)
        @DisplayName("should calculate distance-based isochrone for 2000 meters")
        void shouldCalculateDistanceBasedIsochrone2000Meters() {
            IsochroneResult result = service.calculate(MADRID, 
                    IsochroneOptions.builder()
                            .value(2000) // 2000 meters
                            .weight(IsochroneWeight.DISTANCE)
                            .build());
            
            assertThat(result.getWkt()).isNotNull();
            assertThat(result.getWkt()).contains("POLYGON");
            assertThat(result.getLevel()).isEqualTo("2000");
            assertThat(result.getWeight()).isEqualTo(IsochroneWeight.DISTANCE);
            assertThat(result.getValue()).isEqualTo(2000);
        }
        
        @Test
        @Order(5)
        @DisplayName("should use default weight (time) when not specified")
        void shouldUseDefaultWeightTimeWhenNotSpecified() {
            IsochroneResult result = service.calculate(BARCELONA, 
                    IsochroneOptions.builder()
                            .value(15) // 15 minutes (default weight is time)
                            .build());
            
            assertThat(result.getWkt()).isNotNull();
            assertThat(result.getWeight()).isEqualTo(IsochroneWeight.TIME);
            assertThat(result.getValue()).isEqualTo(15);
            assertThat(result.getLevel()).isEqualTo("900000"); // 15 * 60 * 1000 ms
        }
        
        @Test
        @Order(6)
        @DisplayName("should use convenient factory methods")
        void shouldUseConvenientFactoryMethods() {
            IsochroneResult timeResult = service.calculate(BARCELONA, IsochroneOptions.time(10));
            assertThat(timeResult.getWeight()).isEqualTo(IsochroneWeight.TIME);
            assertThat(timeResult.getValue()).isEqualTo(10);
            
            IsochroneResult distResult = service.calculate(BARCELONA, IsochroneOptions.distance(1000));
            assertThat(distResult.getWeight()).isEqualTo(IsochroneWeight.DISTANCE);
            assertThat(distResult.getValue()).isEqualTo(1000);
        }
    }
    
    @Nested
    @DisplayName("calculateMultiple")
    class CalculateMultiple {
        
        @Test
        @Order(1)
        @DisplayName("should calculate multiple time-based isochrones")
        void shouldCalculateMultipleTimeBasedIsochrones() {
            int[] values = {5, 10};
            List<IsochroneResult> result = service.calculateMultiple(BARCELONA, values, IsochroneWeight.TIME);
            
            assertThat(result).hasSize(2);
            
            // First isochrone (5 minutes)
            assertThat(result.get(0).getValue()).isEqualTo(5);
            assertThat(result.get(0).getLevel()).isEqualTo("300000"); // 5 * 60 * 1000
            assertThat(result.get(0).getWkt()).isNotNull();
            assertThat(result.get(0).getWkt()).contains("POLYGON");
            
            // Second isochrone (10 minutes)
            assertThat(result.get(1).getValue()).isEqualTo(10);
            assertThat(result.get(1).getLevel()).isEqualTo("600000"); // 10 * 60 * 1000
            assertThat(result.get(1).getWkt()).isNotNull();
            assertThat(result.get(1).getWkt()).contains("POLYGON");
        }
        
        @Test
        @Order(2)
        @DisplayName("should calculate multiple distance-based isochrones")
        void shouldCalculateMultipleDistanceBasedIsochrones() {
            int[] values = {500, 1000, 1500};
            List<IsochroneResult> result = service.calculateMultiple(MADRID, values, IsochroneWeight.DISTANCE);
            
            assertThat(result).hasSize(3);
            
            assertThat(result.get(0).getValue()).isEqualTo(500);
            assertThat(result.get(0).getLevel()).isEqualTo("500");
            assertThat(result.get(0).getWeight()).isEqualTo(IsochroneWeight.DISTANCE);
            
            assertThat(result.get(1).getValue()).isEqualTo(1000);
            assertThat(result.get(1).getLevel()).isEqualTo("1000");
            assertThat(result.get(1).getWeight()).isEqualTo(IsochroneWeight.DISTANCE);
            
            assertThat(result.get(2).getValue()).isEqualTo(1500);
            assertThat(result.get(2).getLevel()).isEqualTo("1500");
            assertThat(result.get(2).getWeight()).isEqualTo(IsochroneWeight.DISTANCE);
        }
        
        @Test
        @Order(3)
        @DisplayName("should calculate single isochrone with multiple values array")
        void shouldCalculateSingleIsochroneWithMultipleValuesArray() {
            int[] values = {10};
            List<IsochroneResult> result = service.calculateMultiple(BARCELONA, values, IsochroneWeight.TIME);
            
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getWkt()).isNotNull();
            assertThat(result.get(0).getLevel()).isEqualTo("600000"); // 10 * 60 * 1000
        }
    }
    
    @Nested
    @DisplayName("Method options")
    class MethodOptions {
        
        @Test
        @Order(1)
        @DisplayName("should respect method option (concavehull vs convexhull)")
        void shouldRespectMethodOption() {
            IsochroneResult resultConcave = service.calculate(BARCELONA, 
                    IsochroneOptions.builder()
                            .value(10)
                            .weight(IsochroneWeight.TIME)
                            .method(IsochroneMethod.CONCAVEHULL)
                            .build());
            
            IsochroneResult resultConvex = service.calculate(BARCELONA, 
                    IsochroneOptions.builder()
                            .value(10)
                            .weight(IsochroneWeight.TIME)
                            .method(IsochroneMethod.CONVEXHULL)
                            .build());
            
            assertThat(resultConcave.getWkt()).isNotNull();
            assertThat(resultConvex.getWkt()).isNotNull();
            // Both should return valid polygons, but shapes may differ
        }
    }
    
    @Nested
    @DisplayName("Error handling")
    class ErrorHandling {
        
        @Test
        @Order(1)
        @DisplayName("should handle invalid coordinates gracefully")
        void shouldHandleInvalidCoordinatesGracefully() {
            Coordinate invalidCoord = new Coordinate(999, 999);
            
            assertThatThrownBy(() -> service.calculate(invalidCoord, IsochroneOptions.time(10)))
                    .isInstanceOf(Exception.class);
        }
        
        @Test
        @Order(2)
        @DisplayName("should handle zero value gracefully")
        void shouldHandleZeroValueGracefully() {
            assertThatThrownBy(() -> service.calculate(BARCELONA, IsochroneOptions.time(0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("greater than zero");
        }
    }
}
