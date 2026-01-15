package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.routing.*;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@DisplayName("RoutingService Integration Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RoutingServiceTest {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY");
    private static final String BASE_URL = "https://lb.cercalia.com/services/v2/json";
    
    private RoutingService service;
    
    private final Coordinate barcelona = new Coordinate(41.3851, 2.1734);
    private final Coordinate madrid = new Coordinate(40.4168, -3.7038);
    private final Coordinate zaragoza = new Coordinate(41.6488, -0.8891);
    private final Coordinate valencia = new Coordinate(39.4699, -0.3763);
    
    @BeforeAll
    void setUp() {
        CercaliaConfig config = new CercaliaConfig(API_KEY, BASE_URL);
        service = new RoutingService(config);
    }
    
    @Test
    @DisplayName("should calculate a car route between Barcelona and Madrid")
    @Timeout(15)
    void shouldCalculateCarRoute() {
        RouteResult result = service.calculateRoute(barcelona, madrid, null);
        
        assertThat(result.getWkt()).isNotEmpty();
        assertThat(result.getDistance()).isGreaterThan(600000); // > 600km
        assertThat(result.getDuration()).isGreaterThan(18000);  // > 5 hours
    }
    
    @Test
    @DisplayName("should calculate a route with multiple waypoints")
    @Timeout(15)
    void shouldCalculateRouteWithWaypoints() {
        RouteResult result = service.calculateRoute(barcelona, madrid, 
                RoutingOptions.builder()
                        .waypoints(Arrays.asList(zaragoza, valencia))
                        .build());
        
        assertThat(result).isNotNull();
        assertThat(result.getWaypoints()).hasSize(2);
        assertThat(result.getDistance()).isGreaterThan(800000); // Route via waypoints is longer
        assertThat(result.getWkt()).contains("LINESTRING");
    }
    
    @Test
    @DisplayName("should handle avoidTolls option")
    @Timeout(15)
    void shouldHandleAvoidTolls() {
        RouteResult withTolls = service.calculateRoute(barcelona, madrid, 
                RoutingOptions.builder().avoidTolls(false).build());
        RouteResult withoutTolls = service.calculateRoute(barcelona, madrid, 
                RoutingOptions.builder().avoidTolls(true).build());
        
        assertThat(withTolls.getDistance()).isGreaterThan(0);
        assertThat(withoutTolls.getDistance()).isGreaterThan(0);
    }
    
    @Test
    @DisplayName("should calculate a short car route")
    @Timeout(15)
    void shouldCalculateShortCarRoute() {
        Coordinate start = new Coordinate(41.3887, 2.1734); // Plaza Cataluña
        Coordinate end = new Coordinate(41.3809, 2.1734);   // Las Ramblas
        
        RouteResult result = service.calculateRoute(start, end, 
                RoutingOptions.builder().vehicleType(VehicleType.CAR).build());
        
        assertThat(result.getDistance()).isLessThan(5000);
        assertThat(result.getDuration()).isGreaterThan(0);
    }
    
    @Test
    @DisplayName("should throw error for invalid coordinates")
    @Timeout(15)
    void shouldThrowErrorForInvalidCoordinates() {
        Coordinate invalid = new Coordinate(999, 999);
        
        assertThatThrownBy(() -> service.calculateRoute(barcelona, invalid, null))
                .isInstanceOf(Exception.class);
    }
    
    @Test
    @DisplayName("should handle truck restrictions")
    @Timeout(30)
    void shouldHandleTruckRestrictions() {
        RouteResult result = service.calculateRoute(barcelona, madrid, 
                RoutingOptions.builder()
                        .vehicleType(VehicleType.TRUCK)
                        .truckWeight(40000)
                        .truckHeight(450)
                        .truckWidth(250)
                        .truckLength(1800)
                        .build());
        
        assertThat(result.getDistance()).isGreaterThan(0);
        assertThat(result.getDuration()).isGreaterThan(0);
    }
    
    @Nested
    @DisplayName("Logistics Truck Routing")
    class LogisticsTruckRouting {
        
        @Test
        @DisplayName("should calculate route for heavy truck (40t)")
        @Timeout(15)
        void shouldCalculateRouteForHeavyTruck() {
            RouteResult result = service.calculateRoute(barcelona, madrid, 
                    RoutingOptions.builder()
                            .vehicleType(VehicleType.TRUCK)
                            .truckWeight(40000)
                            .build());
            
            assertThat(result.getDistance()).isGreaterThan(0);
        }
        
        @Test
        @DisplayName("should calculate route for high truck (4.5m)")
        @Timeout(15)
        void shouldCalculateRouteForHighTruck() {
            RouteResult result = service.calculateRoute(barcelona, madrid, 
                    RoutingOptions.builder()
                            .vehicleType(VehicleType.TRUCK)
                            .truckHeight(450)
                            .build());
            
            assertThat(result.getDistance()).isGreaterThan(0);
        }
        
        @Test
        @DisplayName("should handle all logistics parameters together")
        @Timeout(15)
        void shouldHandleAllLogisticsParameters() {
            RouteResult result = service.calculateRoute(barcelona, madrid, 
                    RoutingOptions.builder()
                            .vehicleType(VehicleType.TRUCK)
                            .truckWeight(38000)
                            .truckHeight(400)
                            .truckWidth(255)
                            .truckLength(1650)
                            .build());
            
            assertThat(result.getDistance()).isGreaterThan(0);
        }
    }
    
    @Nested
    @DisplayName("Multi-stage Routing")
    class MultiStageRouting {
        
        @Test
        @DisplayName("should combine WKTs from multiple stages")
        @Timeout(15)
        void shouldCombineWkts() {
            RouteResult result = service.calculateRoute(barcelona, valencia, 
                    RoutingOptions.builder()
                            .waypoints(Arrays.asList(zaragoza))
                            .build());
            
            assertThat(result.getWaypoints()).isNotNull();
            assertThat(result.getWaypoints()).hasSize(1);
            assertThat(result.getWkt()).contains("LINESTRING");
            assertThat(result.getDistance()).isGreaterThan(0);
            assertThat(result.getDuration()).isGreaterThan(0);
        }
    }
    
    @Nested
    @DisplayName("Distance/Time Only")
    class DistanceTimeOnly {
        
        @Test
        @DisplayName("should get distance and time without geometry")
        @Timeout(15)
        void shouldGetDistanceTime() {
            RoutingService.DistanceTime result = service.getDistanceTime(barcelona, madrid, null);
            
            assertThat(result.getDistance()).isGreaterThan(600000);
            assertThat(result.getDuration()).isGreaterThan(18000);
        }
    }
}
