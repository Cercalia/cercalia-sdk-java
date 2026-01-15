package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.exception.CercaliaException;
import com.cercalia.sdk.model.geoment.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Geoment Service Integration Tests with Real API Data.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GeomentServiceTest {
    
    private static final String API_KEY = System.getenv("CERCALIA_API_KEY");
    private static GeomentService service;
    
    @BeforeAll
    static void setUp() {
        CercaliaConfig config = new CercaliaConfig(API_KEY);
        service = new GeomentService(config);
    }
    
    @Nested
    @DisplayName("getMunicipalityGeometry")
    class GetMunicipalityGeometry {
        
        @Test
        @Order(1)
        @DisplayName("should fetch municipality geometry by code (Madrid)")
        void shouldFetchMunicipalityGeometryByCodeMadrid() {
            GeographicElementResult result = service.getMunicipalityGeometry(
                    GeomentMunicipalityOptions.municipality("ESP280796"));
            
            assertThat(result.getCode()).isEqualTo("ESP280796");
            assertThat(result.getName()).isEqualTo("Madrid");
            assertThat(result.getWkt()).isNotNull();
            assertThat(result.getWkt()).contains("POLYGON");
            assertThat(result.getType()).isEqualTo(GeographicElementType.MUNICIPALITY);
            assertThat(result.getLevel()).isEqualTo("mun");
        }
        
        @Test
        @Order(2)
        @DisplayName("should fetch municipality geometry by code (Zaragoza)")
        void shouldFetchMunicipalityGeometryByCodeZaragoza() {
            GeographicElementResult result = service.getMunicipalityGeometry(
                    GeomentMunicipalityOptions.municipality("ESP502973"));
            
            assertThat(result.getCode()).isEqualTo("ESP502973");
            assertThat(result.getName()).isEqualTo("Zaragoza");
            assertThat(result.getWkt()).isNotNull();
            assertThat(result.getWkt()).contains("POLYGON");
            assertThat(result.getType()).isEqualTo(GeographicElementType.MUNICIPALITY);
            assertThat(result.getLevel()).isEqualTo("mun");
        }
        
        @Test
        @Order(3)
        @DisplayName("should fetch region geometry by subregc (Comunidad de Madrid)")
        void shouldFetchRegionGeometryBySubregc() {
            GeographicElementResult result = service.getMunicipalityGeometry(
                    GeomentMunicipalityOptions.region("ESP28"));
            
            assertThat(result.getCode()).isEqualTo("ESP28");
            assertThat(result.getWkt()).isNotNull();
            assertThat(result.getWkt()).contains("POLYGON");
            assertThat(result.getType()).isEqualTo(GeographicElementType.REGION);
            assertThat(result.getLevel()).isEqualTo("subreg");
        }
        
        @Test
        @Order(4)
        @DisplayName("should throw error for invalid municipality code")
        void shouldThrowErrorForInvalidMunicipalityCode() {
            assertThatThrownBy(() -> service.getMunicipalityGeometry(
                    GeomentMunicipalityOptions.municipality("INVALID999")))
                    .isInstanceOf(Exception.class);
        }
        
        @Test
        @Order(5)
        @DisplayName("should use builder pattern with tolerance")
        void shouldUseBuilderPatternWithTolerance() {
            GeographicElementResult result = service.getMunicipalityGeometry(
                    GeomentMunicipalityOptions.builder()
                            .munc("ESP280796")
                            .tolerance(0)
                            .build());
            
            assertThat(result.getCode()).isEqualTo("ESP280796");
            assertThat(result.getWkt()).isNotNull();
            assertThat(result.getWkt()).contains("POLYGON");
        }
    }
    
    @Nested
    @DisplayName("getPostalCodeGeometry")
    class GetPostalCodeGeometry {
        
        @Test
        @Order(1)
        @DisplayName("should fetch postal code geometry (Spanish postal code Madrid)")
        void shouldFetchPostalCodeGeometryMadrid() {
            // 28001 = Madrid centro
            GeographicElementResult result = service.getPostalCodeGeometry(
                    GeomentPostalCodeOptions.of("28001", "ESP"));
            
            assertThat(result.getCode()).isEqualTo("ESP-28001");
            assertThat(result.getName()).isEqualTo("28001");
            assertThat(result.getWkt()).isNotNull();
            assertThat(result.getWkt()).contains("POLYGON");
            assertThat(result.getType()).isEqualTo(GeographicElementType.POSTAL_CODE);
            assertThat(result.getLevel()).isEqualTo("pc");
        }
        
        @Test
        @Order(2)
        @DisplayName("should fetch another postal code geometry (Barcelona)")
        void shouldFetchPostalCodeGeometryBarcelona() {
            // 08001 = Barcelona centro
            GeographicElementResult result = service.getPostalCodeGeometry(
                    GeomentPostalCodeOptions.of("08001", "ESP"));
            
            assertThat(result.getCode()).isEqualTo("ESP-08001");
            assertThat(result.getName()).isEqualTo("08001");
            assertThat(result.getWkt()).isNotNull();
            assertThat(result.getWkt()).contains("POLYGON");
            assertThat(result.getType()).isEqualTo(GeographicElementType.POSTAL_CODE);
            assertThat(result.getLevel()).isEqualTo("pc");
        }
        
        @Test
        @Order(3)
        @DisplayName("should use builder pattern with tolerance")
        void shouldUseBuilderPatternWithTolerance() {
            GeographicElementResult result = service.getPostalCodeGeometry(
                    GeomentPostalCodeOptions.builder("28001")
                            .ctryc("ESP")
                            .tolerance(0)
                            .build());
            
            assertThat(result.getCode()).isEqualTo("ESP-28001");
            assertThat(result.getWkt()).isNotNull();
        }
    }
    
    @Nested
    @DisplayName("getPoiGeometry")
    class GetPoiGeometry {
        
        @Test
        @Order(1)
        @DisplayName("should handle POI geometry request (may throw if POI not found)")
        void shouldHandlePoiGeometryRequest() {
            // Using a test POI code - this might not exist
            try {
                GeographicElementResult result = service.getPoiGeometry(
                        GeomentPoiOptions.of("POI_TEST_123"));
                
                assertThat(result.getCode()).isNotNull();
                assertThat(result.getName()).isNotNull();
                assertThat(result.getWkt()).isNotNull();
                assertThat(result.getType()).isEqualTo(GeographicElementType.POI);
                assertThat(result.getLevel()).isEqualTo("poi");
            } catch (CercaliaException e) {
                // If POI doesn't exist, we should get a Cercalia error - this is expected
                assertThat(e.getMessage()).isNotNull();
            }
        }
    }
    
    @Nested
    @DisplayName("Async operations")
    class AsyncOperations {
        
        @Test
        @Order(1)
        @DisplayName("should fetch municipality geometry asynchronously")
        void shouldFetchMunicipalityGeometryAsync() {
            GeographicElementResult result = service.getMunicipalityGeometryAsync(
                    GeomentMunicipalityOptions.municipality("ESP280796"))
                    .join();
            
            assertThat(result.getCode()).isEqualTo("ESP280796");
            assertThat(result.getName()).isEqualTo("Madrid");
            assertThat(result.getWkt()).contains("POLYGON");
        }
        
        @Test
        @Order(2)
        @DisplayName("should fetch postal code geometry asynchronously")
        void shouldFetchPostalCodeGeometryAsync() {
            GeographicElementResult result = service.getPostalCodeGeometryAsync(
                    GeomentPostalCodeOptions.of("28001", "ESP"))
                    .join();
            
            assertThat(result.getCode()).isEqualTo("ESP-28001");
            assertThat(result.getWkt()).contains("POLYGON");
        }
    }
    
    @Nested
    @DisplayName("Options validation")
    class OptionsValidation {
        
        @Test
        @Order(1)
        @DisplayName("should throw when neither munc nor subregc is provided")
        void shouldThrowWhenNeitherMuncNorSubregcProvided() {
            assertThatThrownBy(() -> GeomentMunicipalityOptions.builder().build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("munc or subregc");
        }
    }
}
