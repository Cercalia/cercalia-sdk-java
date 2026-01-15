package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaClient;
import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.exception.CercaliaException;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.isochrone.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.cercalia.sdk.util.CercaliaResponseParser.*;

/**
 * Service for calculating isochrones (service areas) using the Cercalia API.
 * <p>
 * An isochrone is a polygon representing the area reachable from a center point
 * within a given time or distance constraint.
 * 
 * <pre>{@code
 * IsochroneService service = new IsochroneService(config);
 * Coordinate center = new Coordinate(41.3851, 2.1734);
 * 
 * // 1. Calculate a single isochrone (e.g. 10 minutes)
 * IsochroneResult result = service.calculate(center, IsochroneOptions.builder()
 *     .value(10)
 *     .weight(IsochroneWeight.TIME)
 *     .method(IsochroneMethod.CONCAVEHULL)
 *     .build());
 *
 * // 2. Calculate multiple concentric isochrones (e.g. 5, 10, 15 minutes)
 * List<IsochroneResult> results = service.calculateMultiple(center, 
 *     new int[]{5, 10, 15}, IsochroneWeight.TIME);
 * }</pre>
 *
 * @see <a href="https://www.cercalia.com/docs/webservices/">Cercalia API</a>
 */
public class IsochroneService extends CercaliaClient {
    
    /**
     * Creates a new IsochroneService with the specified configuration.
     *
     * @param config the Cercalia configuration
     */
    public IsochroneService(@NotNull CercaliaConfig config) {
        super(config);
    }
    
    /**
     * Calculate a single isochrone (service area) from a center point.
     *
     * @param center  the center coordinate for the isochrone (WGS84)
     * @param options the isochrone calculation options (value, weight, method)
     * @return IsochroneResult with WKT polygon geometry
     * @throws CercaliaException    if the request fails
     * @throws IllegalArgumentException if value is not greater than zero
     */
    @NotNull
    public IsochroneResult calculate(@NotNull Coordinate center, @NotNull IsochroneOptions options) {
        if (options.getValue() <= 0) {
            throw new IllegalArgumentException("Isochrone value must be greater than zero");
        }
        
        IsochroneWeight weight = options.getWeight();
        
        // Convert value to API format:
        // - time: minutes -> milliseconds
        // - distance: meters (no conversion)
        long apiValue = weight == IsochroneWeight.TIME 
                ? (long) options.getValue() * 60 * 1000 
                : options.getValue();
        
        Map<String, String> params = newParams("isochrone");
        params.put("mo", center.getLng() + "," + center.getLat());
        params.put("isolevels", String.valueOf(apiValue));
        params.put("weight", weight.getValue());
        params.put("method", options.getMethod().getValue());
        params.put("mocs", "4326");
        params.put("ocs", "4326");
        
        JsonNode response = request(params, "Isochrone");
        
        List<IsochroneResult> results = parseIsochroneResponse(response, center, 
                new int[]{options.getValue()}, weight);
        
        if (results.isEmpty()) {
            throw new CercaliaException("No isochrone data found in response");
        }
        
        return results.get(0);
    }
    
    /**
     * Calculate a single isochrone asynchronously.
     *
     * @param center  the center coordinate
     * @param options the isochrone calculation options
     * @return CompletableFuture with the IsochroneResult
     */
    @NotNull
    public CompletableFuture<IsochroneResult> calculateAsync(@NotNull Coordinate center, 
                                                              @NotNull IsochroneOptions options) {
        return CompletableFuture.supplyAsync(() -> calculate(center, options));
    }
    
    /**
     * Calculate multiple isochrones (concentric service areas) in a single API request.
     *
     * @param center  the center coordinate (WGS84)
     * @param values  array of values (e.g., [5, 10, 15] for 5, 10, 15 minutes)
     * @param weight  the weight type (time or distance)
     * @return list of IsochroneResult, one for each value
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<IsochroneResult> calculateMultiple(@NotNull Coordinate center, 
                                                    @NotNull int[] values, 
                                                    @NotNull IsochroneWeight weight) {
        return calculateMultiple(center, values, weight, IsochroneMethod.CONCAVEHULL);
    }
    
    /**
     * Calculate multiple isochrones (concentric service areas) in a single API request.
     *
     * @param center the center coordinate (WGS84)
     * @param values array of values (e.g., [5, 10, 15] for 5, 10, 15 minutes)
     * @param weight the weight type (time or distance)
     * @param method the method for polygon calculation
     * @return list of IsochroneResult, one for each value
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<IsochroneResult> calculateMultiple(@NotNull Coordinate center, 
                                                    @NotNull int[] values, 
                                                    @NotNull IsochroneWeight weight,
                                                    @NotNull IsochroneMethod method) {
        if (values.length == 0) {
            throw new IllegalArgumentException("At least one value is required");
        }
        
        // Convert values to API format
        StringBuilder isolevels = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) isolevels.append(',');
            long apiValue = weight == IsochroneWeight.TIME 
                    ? (long) values[i] * 60 * 1000 
                    : values[i];
            isolevels.append(apiValue);
        }
        
        Map<String, String> params = newParams("isochrone");
        params.put("mo", center.getLng() + "," + center.getLat());
        params.put("isolevels", isolevels.toString());
        params.put("weight", weight.getValue());
        params.put("method", method.getValue());
        params.put("mocs", "4326");
        params.put("ocs", "4326");
        
        JsonNode response = request(params, "Multi-Isochrone");
        
        return parseIsochroneResponse(response, center, values, weight);
    }
    
    /**
     * Calculate multiple isochrones asynchronously.
     *
     * @param center the center coordinate
     * @param values array of values
     * @param weight the weight type
     * @return CompletableFuture with list of IsochroneResult
     */
    @NotNull
    public CompletableFuture<List<IsochroneResult>> calculateMultipleAsync(@NotNull Coordinate center, 
                                                                            @NotNull int[] values, 
                                                                            @NotNull IsochroneWeight weight) {
        return CompletableFuture.supplyAsync(() -> calculateMultiple(center, values, weight));
    }
    
    // ========== Private parsing methods ==========
    
    private List<IsochroneResult> parseIsochroneResponse(JsonNode response, Coordinate center, 
                                                          int[] values, IsochroneWeight weight) {
        JsonNode isochronesNode = response.get("isochrones");
        if (isochronesNode == null || isochronesNode.isNull()) {
            throw new CercaliaException("No isochrones data found in response");
        }
        
        JsonNode isochroneArray = isochronesNode.get("isochrone");
        if (isochroneArray == null || isochroneArray.isNull()) {
            throw new CercaliaException("No isochrone data found in response");
        }
        
        List<IsochroneResult> results = new ArrayList<>();
        int size = getArraySize(isochroneArray);
        
        for (int i = 0; i < size; i++) {
            JsonNode isochroneNode = getArrayElement(isochroneArray, i);
            if (isochroneNode == null) continue;
            
            // Get the value for this index (handle case where fewer values than isochrones)
            int value = i < values.length ? values[i] : values[values.length - 1];
            
            IsochroneResult result = parseIsochroneLevel(isochroneNode, center, value, weight);
            results.add(result);
        }
        
        return results;
    }
    
    private IsochroneResult parseIsochroneLevel(JsonNode levelNode, Coordinate center, 
                                                 int value, IsochroneWeight weight) {
        // Extract WKT polygon (direct mapping)
        String wkt = getCercaliaValue(levelNode);
        if (wkt == null || wkt.isEmpty()) {
            throw new CercaliaException("No WKT polygon found in isochrone response");
        }
        
        // Extract level attribute (transparency of geometry type)
        String levelValue = getCercaliaAttr(levelNode, "level");
        if (levelValue == null) {
            throw new CercaliaException("No level attribute found in isochrone response");
        }
        
        return IsochroneResult.builder()
                .wkt(wkt)
                .center(center)
                .value(value)
                .weight(weight)
                .level(levelValue)
                .build();
    }
}
