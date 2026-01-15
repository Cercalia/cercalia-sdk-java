package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.staticmaps.*;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Static Maps Service Integration Tests with Real API Data.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StaticMapsServiceTest {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY");
    private static StaticMapsService service;
    
    @BeforeAll
    static void setUp() {
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        service = new StaticMapsService(config);
    }
    
    @Nested
    @DisplayName("Basic map generation")
    class BasicMapGeneration {
        
        @Test
        @Order(1)
        @DisplayName("should generate static map for Girona city")
        void shouldGenerateStaticMapForGironaCity() {
            StaticMapResult result = service.generateCityMap("girona", "ESP", 350, 250);
            
            assertThat(result.getImageUrl()).isNotNull();
            assertThat(result.getImageUrl()).contains("lb.cercalia.com");
            assertThat(result.getImageUrl()).contains("/MapesNG/Cercalia/map/");
            assertThat(result.getWidth()).isEqualTo(350);
            assertThat(result.getHeight()).isEqualTo(250);
            assertThat(result.getFormat()).isNotNull();
            assertThat(result.getCenter()).isNotNull();
            assertThat(result.getLabel()).isNotNull();
        }
        
        @Test
        @Order(2)
        @DisplayName("should generate map for Barcelona")
        void shouldGenerateMapForBarcelona() {
            StaticMapResult result = service.generateCityMap("Barcelona", "ESP", 500, 400);
            
            assertThat(result.getImageUrl()).isNotNull();
            assertThat(result.getCenter()).isNotNull();
            assertThat(result.getCenter().getLat()).isGreaterThan(41);
            assertThat(result.getCenter().getLng()).isGreaterThan(2);
        }
        
        @Test
        @Order(3)
        @DisplayName("should generate map for Madrid")
        void shouldGenerateMapForMadrid() {
            StaticMapResult result = service.generateCityMap("Madrid", "ESP", 500, 400);
            
            assertThat(result.getImageUrl()).isNotNull();
            assertThat(result.getCenter()).isNotNull();
        }
    }
    
    @Nested
    @DisplayName("Shape rendering")
    class ShapeRendering {
        
        @Test
        @Order(1)
        @DisplayName("should generate map with circle and polyline shapes")
        void shouldGenerateMapWithCircleAndPolylineShapes() {
            StaticMapExtent extent = StaticMapExtent.of(
                    new Coordinate(41.439132726, 2.003108336),
                    new Coordinate(41.390497829, 2.197135455)
            );
            
            StaticMapCircle circle = StaticMapCircle.builder(
                    new Coordinate(41.439132726, 2.003108336), 2000)
                    .outlineColor(RGBAColor.rgba(255, 0, 0, 128))
                    .outlineSize(2)
                    .fillColor(RGBAColor.rgba(0, 255, 0, 128))
                    .build();
            
            StaticMapPolyline polyline = StaticMapPolyline.builder()
                    .coordinates(
                            new Coordinate(41.401902461, 2.142455003),
                            new Coordinate(41.404628181, 2.155965665),
                            new Coordinate(41.433339308, 2.179860852)
                    )
                    .outlineColor(RGBAColor.red())
                    .outlineSize(2)
                    .fillColor(RGBAColor.red())
                    .build();
            
            StaticMapResult result = service.generateMap(StaticMapOptions.builder()
                    .dimensions(400, 300)
                    .labelOp(0)
                    .coordinateSystem("gdd")
                    .extent(extent)
                    .shapes(circle, polyline)
                    .build());
            
            assertThat(result.getImageUrl()).isNotNull();
            assertThat(result.getImageUrl()).contains("lb.cercalia.com");
            assertThat(result.getWidth()).isEqualTo(400);
            assertThat(result.getHeight()).isEqualTo(300);
        }
        
        @Test
        @Order(2)
        @DisplayName("should generate map with rectangle and circle shapes")
        void shouldGenerateMapWithRectangleAndCircleShapes() {
            StaticMapRectangle rectangle = StaticMapRectangle.builder(
                    new Coordinate(41.98, 2.82),
                    new Coordinate(41.96, 2.84))
                    .outlineColor(RGBAColor.red())
                    .outlineSize(3)
                    .fillColor(RGBAColor.green(128))
                    .build();
            
            StaticMapCircle circle = StaticMapCircle.builder(
                    new Coordinate(41.96, 2.84), 1000)
                    .outlineColor(RGBAColor.rgba(255, 0, 0, 128))
                    .outlineSize(10)
                    .fillColor(RGBAColor.green(128))
                    .build();
            
            StaticMapResult result = service.generateMap(StaticMapOptions.builder()
                    .cityName("Girona")
                    .shapes(rectangle, circle)
                    .build());
            
            assertThat(result.getImageUrl()).isNotNull();
            assertThat(result.getImageUrl()).contains("lb.cercalia.com");
        }
        
        @Test
        @Order(3)
        @DisplayName("should generate map with circle shape helper")
        void shouldGenerateMapWithCircleShapeHelper() {
            StaticMapResult result = service.generateMapWithCircle(
                    new Coordinate(41.3851, 2.1734),
                    2000);
            
            assertThat(result.getImageUrl()).isNotNull();
        }
        
        @Test
        @Order(4)
        @DisplayName("should generate map with polyline helper")
        void shouldGenerateMapWithPolylineHelper() {
            List<Coordinate> coords = Arrays.asList(
                    new Coordinate(41.3851, 2.1734),
                    new Coordinate(41.4034, 2.1741),
                    new Coordinate(41.4100, 2.1900)
            );
            
            StaticMapResult result = service.generateMapWithPolyline(coords, null, 400, 300);
            
            assertThat(result.getImageUrl()).isNotNull();
        }
        
        @Test
        @Order(5)
        @DisplayName("should generate map with line between two points")
        void shouldGenerateMapWithLineBetweenTwoPoints() {
            StaticMapResult result = service.generateMapWithLine(
                    new Coordinate(41.3851, 2.1734),
                    new Coordinate(41.4034, 2.1741),
                    null, 400, 300);
            
            assertThat(result.getImageUrl()).isNotNull();
        }
    }
    
    @Nested
    @DisplayName("Markers")
    class Markers {
        
        @Test
        @Order(1)
        @DisplayName("should generate map with markers")
        void shouldGenerateMapWithMarkers() {
            List<StaticMapMarker> markers = Arrays.asList(
                    StaticMapMarker.at(new Coordinate(41.3851, 2.1734), 1),
                    StaticMapMarker.at(new Coordinate(41.4034, 2.1741), 2)
            );
            
            StaticMapResult result = service.generateMapWithMarkers(markers, 400, 300);
            
            assertThat(result.getImageUrl()).isNotNull();
        }
        
        @Test
        @Order(2)
        @DisplayName("should generate map with single marker")
        void shouldGenerateMapWithSingleMarker() {
            List<StaticMapMarker> markers = Arrays.asList(
                    StaticMapMarker.at(new Coordinate(40.4168, -3.7038), 1)
            );
            
            StaticMapResult result = service.generateMapWithMarkers(markers, 400, 300);
            
            assertThat(result.getImageUrl()).isNotNull();
        }
    }
    
    @Nested
    @DisplayName("Image download")
    class ImageDownload {
        
        @Test
        @Order(1)
        @DisplayName("should download map image")
        void shouldDownloadMapImage() {
            StaticMapResult result = service.generateCityMap("Madrid", "ESP", 200, 150);
            
            assertThat(result.getImageUrl()).isNotNull();
            
            byte[] imageData = service.downloadImage(result.getImageUrl());
            
            assertThat(imageData).isNotNull();
            assertThat(imageData.length).isGreaterThan(0);
        }
        
        @Test
        @Order(2)
        @DisplayName("should generate map and return image data directly")
        void shouldGenerateMapAndReturnImageDataDirectly() {
            StaticMapResult result = service.generateMapAsImage(StaticMapOptions.builder()
                    .cityName("Valencia")
                    .countryCode("ESP")
                    .dimensions(200, 150)
                    .build());
            
            assertThat(result.getImageData()).isNotNull();
            assertThat(result.getImageData().length).isGreaterThan(0);
        }
    }
    
    @Nested
    @DisplayName("Multiple cities")
    class MultipleCities {
        
        @Test
        @Order(1)
        @DisplayName("should generate maps for different Spanish cities")
        void shouldGenerateMapsForDifferentSpanishCities() {
            String[] cities = {"Sevilla", "Bilbao", "Zaragoza"};
            
            for (String city : cities) {
                StaticMapResult result = service.generateCityMap(city, "ESP");
                assertThat(result.getImageUrl()).isNotNull();
                assertThat(result.getLabel()).isNotNull();
            }
        }
    }
    
    @Nested
    @DisplayName("Different dimensions")
    class DifferentDimensions {
        
        @Test
        @Order(1)
        @DisplayName("should respect custom dimensions")
        void shouldRespectCustomDimensions() {
            StaticMapResult result = service.generateCityMap("Barcelona", "ESP", 800, 600);
            
            assertThat(result.getWidth()).isEqualTo(800);
            assertThat(result.getHeight()).isEqualTo(600);
        }
        
        @Test
        @Order(2)
        @DisplayName("should handle small dimensions")
        void shouldHandleSmallDimensions() {
            StaticMapResult result = service.generateCityMap("Madrid", "ESP", 200, 150);
            
            assertThat(result.getWidth()).isEqualTo(200);
            assertThat(result.getHeight()).isEqualTo(150);
        }
    }
    
    @Nested
    @DisplayName("Helper methods")
    class HelperMethods {
        
        @Test
        @Order(1)
        @DisplayName("should generate map with rectangle using helper method")
        void shouldGenerateMapWithRectangleUsingHelperMethod() {
            StaticMapResult result = service.generateMapWithRectangle(
                    new Coordinate(41.98, 2.82),
                    new Coordinate(41.96, 2.84),
                    null, "Girona", null, null);
            
            assertThat(result.getImageUrl()).isNotNull();
        }
        
        @Test
        @Order(2)
        @DisplayName("should generate map with label using helper method")
        void shouldGenerateMapWithLabelUsingHelperMethod() {
            StaticMapResult result = service.generateMapWithLabel(
                    new Coordinate(41.3851, 2.1734),
                    "Barcelona",
                    null, null);
            
            assertThat(result.getImageUrl()).isNotNull();
        }
        
        @Test
        @Order(3)
        @DisplayName("should generate map with sector using helper method")
        void shouldGenerateMapWithSectorUsingHelperMethod() {
            StaticMapResult result = service.generateMapWithSector(
                    new Coordinate(41.3851, 2.1734),
                    500, 1000, 0, 90,
                    null, null);
            
            assertThat(result.getImageUrl()).isNotNull();
        }
    }
}
