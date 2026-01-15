package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaClient;
import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.exception.CercaliaException;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.poi.PoiGeographicElement;
import com.cercalia.sdk.model.proximity.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.cercalia.sdk.util.CercaliaResponseParser.*;

/**
 * Service for finding nearby points of interest using the Cercalia Proximity API.
 * <p>
 * This service provides methods to find POIs near a center point, optionally
 * filtered by category and with routing distance/time calculations.
 * 
 * <pre>{@code
 * ProximityService service = new ProximityService(config);
 * Coordinate center = new Coordinate(41.3851, 2.1734);
 * 
 * // 1. Find nearest POIs (e.g. gas stations)
 * ProximityResult result = service.findNearest(ProximityOptions.builder(center)
 *     .categories("D00GASP")
 *     .count(5)
 *     .maxRadius(10000) // 10km
 *     .build());
 *
 * // 2. Find nearest POIs with routing info (distance/time)
 * ProximityResult routingResult = service.findNearestWithRouting(center, "D00GASP", 
 *     ProximityRouteWeight.TIME, 5);
 * }</pre>
 * 
 * @see ProximityOptions
 * @see ProximityResult
 */
public class ProximityService extends CercaliaClient {
    
    /**
     * Creates a new ProximityService with the specified configuration.
     *
     * @param config the Cercalia configuration
     */
    public ProximityService(@NotNull CercaliaConfig config) {
        super(config);
    }
    
    /**
     * Find nearest POIs from a center point.
     *
     * @param options the search options
     * @return the proximity search result
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public ProximityResult findNearest(@NotNull ProximityOptions options) {
        Map<String, String> params = newParams("prox");
        params.put("mocs", "gdd");
        params.put("mo", options.getCenter().getLat() + "," + options.getCenter().getLng());
        
        addIfPresent(params, "num", options.getCount());
        addIfPresent(params, "rad", options.getMaxRadius());
        
        if (options.getCategories() != null && !options.getCategories().isEmpty()) {
            params.put("rqpoicats", String.join(",", options.getCategories()));
        }
        
        if (Boolean.TRUE.equals(options.getIncludeRouting()) && options.getRouteWeight() != null) {
            params.put("weight", options.getRouteWeight().getValue());
        }
        
        try {
            JsonNode response = request(params, "Proximity");
            return parseResponse(response, options.getCenter());
        } catch (CercaliaException e) {
            if (e.isNoResultsFound()) {
                return new ProximityResult(Collections.emptyList(), options.getCenter(), 0);
            }
            throw e;
        }
    }
    
    /**
     * Find nearest POIs asynchronously.
     *
     * @param options the search options
     * @return CompletableFuture with the proximity result
     */
    @NotNull
    public CompletableFuture<ProximityResult> findNearestAsync(@NotNull ProximityOptions options) {
        return CompletableFuture.supplyAsync(() -> findNearest(options));
    }
    
    /**
     * Find nearest POIs by category.
     *
     * @param center       the center coordinate
     * @param categoryCode the category code
     * @param count        the number of results to return (default: 5)
     * @return the proximity search result
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public ProximityResult findNearestByCategory(@NotNull Coordinate center, 
                                                  @NotNull String categoryCode, 
                                                  int count) {
        return findNearest(ProximityOptions.builder(center)
                .categories(categoryCode)
                .count(count)
                .build());
    }
    
    /**
     * Find nearest POIs by category with default count of 5.
     *
     * @param center       the center coordinate
     * @param categoryCode the category code
     * @return the proximity search result
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public ProximityResult findNearestByCategory(@NotNull Coordinate center, @NotNull String categoryCode) {
        return findNearestByCategory(center, categoryCode, 5);
    }
    
    /**
     * Find nearest POIs with routing distance/time.
     *
     * @param center       the center coordinate
     * @param categoryCode the category code
     * @param weight       the routing weight (time or distance)
     * @param count        the number of results to return
     * @return the proximity search result
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public ProximityResult findNearestWithRouting(@NotNull Coordinate center,
                                                   @NotNull String categoryCode,
                                                   @NotNull ProximityRouteWeight weight,
                                                   int count) {
        return findNearest(ProximityOptions.builder(center)
                .categories(categoryCode)
                .count(count)
                .includeRouting(true)
                .routeWeight(weight)
                .build());
    }
    
    /**
     * Find nearest POIs with routing time (default count of 5).
     *
     * @param center       the center coordinate
     * @param categoryCode the category code
     * @return the proximity search result
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public ProximityResult findNearestWithRouting(@NotNull Coordinate center, @NotNull String categoryCode) {
        return findNearestWithRouting(center, categoryCode, ProximityRouteWeight.TIME, 5);
    }
    
    // ========== Private parsing methods ==========
    
    private ProximityResult parseResponse(JsonNode response, Coordinate center) {
        JsonNode proximityNode = response.get("proximity");
        if (proximityNode == null || proximityNode.isNull()) {
            return new ProximityResult(Collections.emptyList(), center, 0);
        }
        
        JsonNode poilistNode = proximityNode.get("poilist");
        if (poilistNode == null || poilistNode.isNull()) {
            return new ProximityResult(Collections.emptyList(), center, 0);
        }
        
        JsonNode poiArray = poilistNode.get("poi");
        if (poiArray == null || poiArray.isNull()) {
            return new ProximityResult(Collections.emptyList(), center, 0);
        }
        
        List<ProximityItem> items = new ArrayList<>();
        int size = getArraySize(poiArray);
        
        for (int i = 0; i < size; i++) {
            JsonNode poiNode = getArrayElement(poiArray, i);
            if (poiNode == null) continue;
            
            try {
                ProximityItem item = parseItem(poiNode);
                items.add(item);
            } catch (Exception e) {
                logger.warn("[Proximity] Failed to parse item: %s", e.getMessage());
            }
        }
        
        return new ProximityResult(items, center, items.size());
    }
    
    private ProximityItem parseItem(JsonNode poiNode) {
        // Parse coordinates (Golden Rule 3: Strict coordinates)
        JsonNode coordNode = poiNode.get("coord");
        if (coordNode == null || coordNode.isNull()) {
            throw new IllegalArgumentException("Invalid POI: missing coordinates");
        }
        
        String coordX = getCercaliaAttr(coordNode, "x");
        String coordY = getCercaliaAttr(coordNode, "y");
        if (coordX == null || coordY == null) {
            throw new IllegalArgumentException("Invalid POI: missing coordinates");
        }
        
        double lat = parseCoordinate(coordY, "latitude");
        double lng = parseCoordinate(coordX, "longitude");
        Coordinate coord = new Coordinate(lat, lng);
        
        // Parse required fields
        String id = getCercaliaAttr(poiNode, "id");
        if (id == null) id = "";
        
        String name = getCercaliaValue(poiNode.get("name"));
        if (name == null) name = "";
        
        String distStr = getCercaliaAttr(poiNode, "dist");
        int distance = distStr != null ? Integer.parseInt(distStr) : 0;
        
        ProximityItem.Builder builder = ProximityItem.builder()
                .id(id)
                .name(name)
                .coord(coord)
                .distance(distance);
        
        // Optional fields
        Integer position = parseIntOrNull(getCercaliaAttr(poiNode, "pos"));
        if (position != null) {
            builder.position(position);
        }
        
        String categoryCode = getCercaliaAttr(poiNode, "category_id");
        if (categoryCode != null) {
            builder.categoryCode(categoryCode);
        }
        
        String subcategoryCode = getCercaliaAttr(poiNode, "subcategory_id");
        if (subcategoryCode != null && !"-1".equals(subcategoryCode)) {
            builder.subcategoryCode(subcategoryCode);
        }
        
        String geometry = getCercaliaAttr(poiNode, "geometry");
        if (geometry != null) {
            builder.geometry(geometry);
        }
        
        String info = getCercaliaValue(poiNode.get("info"));
        if (info != null) {
            builder.info(info);
        }
        
        // Parse geographic element
        JsonNode geNode = poiNode.get("ge");
        if (geNode != null && !geNode.isNull()) {
            builder.ge(parseGeographicElement(geNode));
        }
        
        // Routing fields
        Integer routeDistance = parseIntOrNull(getCercaliaAttr(poiNode, "routedist"));
        if (routeDistance != null) {
            builder.routeDistance(routeDistance);
        }
        
        Integer routeTime = parseIntOrNull(getCercaliaAttr(poiNode, "routetime"));
        if (routeTime != null) {
            builder.routeTime(routeTime);
        }
        
        Integer routeRealtime = parseIntOrNull(getCercaliaAttr(poiNode, "routerealtime"));
        if (routeRealtime != null) {
            builder.routeRealtime(routeRealtime);
        }
        
        Integer routeWeight = parseIntOrNull(getCercaliaAttr(poiNode, "routeweight"));
        if (routeWeight != null) {
            builder.routeWeight(routeWeight);
        }
        
        return builder.build();
    }
    
    private PoiGeographicElement parseGeographicElement(JsonNode geNode) {
        PoiGeographicElement.Builder builder = PoiGeographicElement.builder();
        
        // House number
        JsonNode houseNumberNode = geNode.get("housenumber");
        if (houseNumberNode != null) {
            builder.houseNumber(getCercaliaValue(houseNumberNode));
        }
        
        // Street
        JsonNode streetNode = geNode.get("street");
        if (streetNode != null) {
            String street = getCercaliaValue(streetNode);
            if (street == null) {
                street = getCercaliaAttr(streetNode, "name");
            }
            builder.street(street);
            builder.streetCode(getCercaliaAttr(streetNode, "id"));
        }
        
        // City -> locality (Golden Rule: use locality instead of city)
        JsonNode cityNode = geNode.get("city");
        if (cityNode != null) {
            builder.locality(getCercaliaValue(cityNode));
            builder.localityCode(getCercaliaAttr(cityNode, "id"));
        }
        
        // Municipality (Golden Rule: intentional typo municipalityCode)
        JsonNode municipalityNode = geNode.get("municipality");
        if (municipalityNode != null) {
            builder.municipality(getCercaliaValue(municipalityNode));
            builder.municipalityCode(getCercaliaAttr(municipalityNode, "id"));
        }
        
        // Subregion
        JsonNode subregionNode = geNode.get("subregion");
        if (subregionNode != null) {
            builder.subregion(getCercaliaValue(subregionNode));
            builder.subregionCode(getCercaliaAttr(subregionNode, "id"));
        }
        
        // Region
        JsonNode regionNode = geNode.get("region");
        if (regionNode != null) {
            builder.region(getCercaliaValue(regionNode));
            builder.regionCode(getCercaliaAttr(regionNode, "id"));
        }
        
        // Country
        JsonNode countryNode = geNode.get("country");
        if (countryNode != null) {
            builder.country(getCercaliaValue(countryNode));
            builder.countryCode(getCercaliaAttr(countryNode, "id"));
        }
        
        return builder.build();
    }
}
