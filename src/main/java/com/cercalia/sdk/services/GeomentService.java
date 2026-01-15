package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaClient;
import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.exception.CercaliaException;
import com.cercalia.sdk.model.geoment.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.cercalia.sdk.util.CercaliaResponseParser.*;

/**
 * GeomentService - Retrieves geographic and administrative geometries.
 * <p>
 * This service provides WKT (Well-Known Text) polygons for various administrative
 * and geographic elements like localities, subregions, postal codes, and POIs.
 * <p>
 * Example usage:
 * <pre>{@code
 * GeomentService service = new GeomentService(config);
 * 
 * // Get locality geometry (municipality)
 * GeographicElementResult locality = service.getMunicipalityGeometry(
 *     new GeomentMunicipalityOptions("28079", null, 0.001) // Madrid ID
 * );
 * String wkt = locality.getWkt();
 * 
 * // Get postal code geometry
 * GeographicElementResult postalCode = service.getPostalCodeGeometry(
 *     new GeomentPostalCodeOptions("08001", "ESP")
 * );
 * }</pre>
 *
 * @see <a href="https://docs.cercalia.com/docs/cercalia-webservices/geoment/">Cercalia Geoment API</a>
 */
public class GeomentService extends CercaliaClient {
    
    /**
     * Creates a new GeomentService with the specified configuration.
     *
     * @param config the Cercalia configuration
     */
    public GeomentService(@NotNull CercaliaConfig config) {
        super(config);
    }
    
    /**
     * Get geometry for a municipality or region.
     *
     * @param options Municipality/Region options (munc OR subregc, tolerance)
     * @return GeographicElementResult with WKT polygon
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public GeographicElementResult getMunicipalityGeometry(@NotNull GeomentMunicipalityOptions options) {
        Map<String, String> params = newParams("geoment");
        params.put("cs", "4326"); // Always use Lat/Lng WGS84
        
        addIfPresent(params, "munc", options.getMunc());
        addIfPresent(params, "subregc", options.getSubregc());
        addIfPresent(params, "tolerance", options.getTolerance());
        
        GeographicElementType type = options.getSubregc() != null 
                ? GeographicElementType.REGION 
                : GeographicElementType.MUNICIPALITY;
        
        return fetchGeometry(params, type);
    }
    
    /**
     * Get geometry for a municipality or region asynchronously.
     *
     * @param options Municipality/Region options
     * @return CompletableFuture with the result
     */
    @NotNull
    public CompletableFuture<GeographicElementResult> getMunicipalityGeometryAsync(
            @NotNull GeomentMunicipalityOptions options) {
        return CompletableFuture.supplyAsync(() -> getMunicipalityGeometry(options));
    }
    
    /**
     * Get geometry for a postal code.
     *
     * @param options Postal code options (pcode, ctryc, tolerance)
     * @return GeographicElementResult with WKT polygon
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public GeographicElementResult getPostalCodeGeometry(@NotNull GeomentPostalCodeOptions options) {
        Map<String, String> params = newParams("geoment");
        params.put("cs", "4326"); // Always use Lat/Lng WGS84
        
        params.put("pcode", options.getPcode());
        addIfPresent(params, "ctryc", options.getCtryc());
        addIfPresent(params, "tolerance", options.getTolerance());
        
        return fetchGeometry(params, GeographicElementType.POSTAL_CODE);
    }
    
    /**
     * Get geometry for a postal code asynchronously.
     *
     * @param options Postal code options
     * @return CompletableFuture with the result
     */
    @NotNull
    public CompletableFuture<GeographicElementResult> getPostalCodeGeometryAsync(
            @NotNull GeomentPostalCodeOptions options) {
        return CompletableFuture.supplyAsync(() -> getPostalCodeGeometry(options));
    }
    
    /**
     * Get geometry for a Point of Interest.
     *
     * @param options POI options (poic, tolerance)
     * @return GeographicElementResult with WKT polygon/point
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public GeographicElementResult getPoiGeometry(@NotNull GeomentPoiOptions options) {
        Map<String, String> params = newParams("geoment");
        params.put("cs", "4326"); // Always use Lat/Lng WGS84
        
        params.put("poic", options.getPoic());
        addIfPresent(params, "tolerance", options.getTolerance());
        
        return fetchGeometry(params, GeographicElementType.POI);
    }
    
    /**
     * Get geometry for a POI asynchronously.
     *
     * @param options POI options
     * @return CompletableFuture with the result
     */
    @NotNull
    public CompletableFuture<GeographicElementResult> getPoiGeometryAsync(@NotNull GeomentPoiOptions options) {
        return CompletableFuture.supplyAsync(() -> getPoiGeometry(options));
    }
    
    // ========== Private methods ==========
    
    private GeographicElementResult fetchGeometry(Map<String, String> params, GeographicElementType type) {
        JsonNode response = request(params, "Geoment");
        return parseResponse(response, type);
    }
    
    private GeographicElementResult parseResponse(JsonNode response, GeographicElementType type) {
        // Response can be in two formats:
        // 1. { geographic_elements: { geographic_element: [...] } }
        // 2. { ge: { ... } }
        JsonNode element = null;
        
        // Try geographic_elements path
        JsonNode geographicElements = response.get("geographic_elements");
        if (geographicElements != null && !geographicElements.isNull()) {
            JsonNode geographicElement = geographicElements.get("geographic_element");
            if (geographicElement != null && !geographicElement.isNull()) {
                element = geographicElement.isArray() 
                        ? getArrayElement(geographicElement, 0) 
                        : geographicElement;
            }
        }
        
        // Try ge path
        if (element == null) {
            JsonNode ge = response.get("ge");
            if (ge != null && !ge.isNull()) {
                element = ge.isArray() ? getArrayElement(ge, 0) : ge;
            }
        }
        
        if (element == null) {
            throw new CercaliaException("No geographic elements found in response");
        }
        
        // Extract fields following Golden Rules
        String code = getCercaliaAttr(element, "id");
        if (code == null) code = "";
        
        String name = getCercaliaAttr(element, "name");
        String level = getCercaliaAttr(element, "type"); // Transparency of geometry type
        
        // Try multiple possible paths for WKT (Cercalia API inconsistency)
        String wkt = extractWkt(element);
        
        if (wkt == null) {
            logger.error("[GeomentService] Missing WKT in element: %s", element.toString());
            throw new CercaliaException("Geometry WKT missing in response");
        }
        
        // Direct mapping - no fallbacks (Golden Rule #1)
        return GeographicElementResult.builder()
                .code(code) // Use 'code' suffix instead of 'id' (Golden Rule #2)
                .name(name) // Can be null - no fallback (Golden Rule #1)
                .wkt(wkt)
                .type(type)
                .level(level) // Transparency of geometry type (Golden Rule #6)
                .build();
    }
    
    private String extractWkt(JsonNode element) {
        // Try geometry.wkt path
        JsonNode geometry = element.get("geometry");
        if (geometry != null && !geometry.isNull()) {
            JsonNode wktNode = geometry.get("wkt");
            if (wktNode != null) {
                String wkt = getCercaliaValue(wktNode);
                if (wkt != null) return wkt;
            }
        }
        
        // Try geom path (multiple formats)
        JsonNode geom = element.get("geom");
        if (geom != null && !geom.isNull()) {
            if (geom.isTextual()) {
                return geom.asText();
            }
            // Try geom.wkt
            JsonNode wktNode = geom.get("wkt");
            if (wktNode != null) {
                String wkt = getCercaliaValue(wktNode);
                if (wkt != null) return wkt;
            }
            // Try geom.value
            String wkt = getCercaliaValue(geom);
            if (wkt != null) return wkt;
        }
        
        // Try direct wkt path
        JsonNode wktNode = element.get("wkt");
        if (wktNode != null) {
            return getCercaliaValue(wktNode);
        }
        
        return null;
    }
}
