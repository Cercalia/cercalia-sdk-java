package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaClient;
import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.exception.CercaliaException;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.poi.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.cercalia.sdk.util.CercaliaResponseParser.*;

/**
 * Service for searching Points of Interest (POI) using the Cercalia API.
 * <p>
 * Supports multiple search modes:
 * <ul>
 *   <li>Nearest POIs by straight-line distance (cmd=prox)</li>
 *   <li>Nearest POIs with routing (cmd=prox with weight)</li>
 *   <li>POIs along a route (cmd=geom)</li>
 *   <li>POIs inside a map extent (cmd=map)</li>
 *   <li>POIs inside a polygon (cmd=prox with wkt)</li>
 *   <li>Weather forecast (cmd=prox with D00M05 category)</li>
 * </ul>
 *
 * <pre>{@code
 * PoiService service = new PoiService(config);
 * 
 * // 1. Search POIs inside a polygon (WKT)
 * List<Poi> pois = service.searchInPolygon(PoiInPolygonOptions.builder()
 *     .wkt("POLYGON((2.17 41.38, 2.18 41.38, 2.18 41.39, 2.17 41.39, 2.17 41.38))")
 *     .categories("D00GASP")
 *     .build());
 *
 * // 2. Search POIs along a route
 * List<Poi> routePois = service.searchAlongRoute(PoiAlongRouteOptions.builder()
 *     .routeId("ROUTE_ID_FROM_ROUTING_SERVICE")
 *     .categories("D00GASP")
 *     .buffer(100) // 100 meters around the route
 *     .build());
 *
 * // 3. Get weather forecast
 * WeatherForecast weather = service.getWeatherForecast(new Coordinate(41.3851, 2.1734));
 * }</pre>
 *
 * @see <a href="https://docs.cercalia.com/docs/cercalia-webservices/points-of-interest/">Cercalia POI API</a>
 */
public class PoiService extends CercaliaClient {
    
    /**
     * Creates a new PoiService with the specified configuration.
     *
     * @param config the Cercalia configuration
     */
    public PoiService(@NotNull CercaliaConfig config) {
        super(config);
    }
    
    /**
     * Get the nearest POIs by straight-line distance.
     *
     * @param center  the search center coordinate
     * @param options the search options (categories, limit, radius)
     * @return list of POI results ordered by proximity
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<Poi> searchNearest(@NotNull Coordinate center, @NotNull PoiNearestOptions options) {
        Map<String, String> params = newParams("prox");
        params.put("mocs", "gdd");
        params.put("mo", center.getLat() + "," + center.getLng());
        params.put("rqpoicats", String.join(",", options.getCategories()));
        
        addIfPresent(params, "num", options.getLimit());
        addIfPresent(params, "rad", options.getRadius());
        
        try {
            JsonNode response = request(params, "POI Nearest");
            return parseProximityPoiResponse(response);
        } catch (CercaliaException e) {
            if (e.isNoResultsFound()) {
                return Collections.emptyList();
            }
            throw e;
        }
    }
    
    /**
     * Get the nearest POIs by straight-line distance asynchronously.
     *
     * @param center  the search center coordinate
     * @param options the search options
     * @return CompletableFuture with list of POI results
     */
    @NotNull
    public CompletableFuture<List<Poi>> searchNearestAsync(@NotNull Coordinate center, @NotNull PoiNearestOptions options) {
        return CompletableFuture.supplyAsync(() -> searchNearest(center, options));
    }
    
    /**
     * Get the nearest POIs using routing distance/time.
     *
     * @param center  the search center coordinate
     * @param options the search options including routing weight
     * @return list of POI results with routing info
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<Poi> searchNearestWithRouting(@NotNull Coordinate center, @NotNull PoiNearestWithRoutingOptions options) {
        Map<String, String> params = newParams("prox");
        params.put("mocs", "gdd");
        params.put("mo", center.getLat() + "," + center.getLng());
        params.put("rqpoicats", String.join(",", options.getCategories()));
        params.put("weight", options.getWeight().getValue());
        
        addIfPresent(params, "num", options.getLimit());
        addIfPresent(params, "rad", options.getRadius());
        addIfPresent(params, "inverse", options.getInverse());
        
        if (Boolean.TRUE.equals(options.getIncludeRealtime())) {
            params.put("iweight", "realtime");
        }
        addIfPresent(params, "departuretime", options.getDepartureTime());
        
        try {
            JsonNode response = request(params, "POI Nearest With Routing");
            return parseProximityPoiResponse(response);
        } catch (CercaliaException e) {
            if (e.isNoResultsFound()) {
                return Collections.emptyList();
            }
            throw e;
        }
    }
    
    /**
     * Get the nearest POIs using routing distance/time asynchronously.
     *
     * @param center  the search center coordinate
     * @param options the search options
     * @return CompletableFuture with list of POI results
     */
    @NotNull
    public CompletableFuture<List<Poi>> searchNearestWithRoutingAsync(@NotNull Coordinate center, 
                                                                       @NotNull PoiNearestWithRoutingOptions options) {
        return CompletableFuture.supplyAsync(() -> searchNearestWithRouting(center, options));
    }
    
    /**
     * Get POIs along a route.
     *
     * @param options the route and search options
     * @return list of POIs along the route
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<Poi> searchAlongRoute(@NotNull PoiAlongRouteOptions options) {
        Map<String, String> params = newParams("geom");
        params.put("routeid", options.getRouteId());
        params.put("routeweight", options.getRouteWeight().getValue());
        params.put("getpoicats", String.join(",", options.getCategories()));
        
        addIfPresent(params, "buffer", options.getBuffer());
        addIfPresent(params, "tolerance", options.getTolerance());
        
        try {
            JsonNode response = request(params, "POI Along Route");
            return parseGeomPoiResponse(response);
        } catch (CercaliaException e) {
            if (e.isNoResultsFound()) {
                return Collections.emptyList();
            }
            throw e;
        }
    }
    
    /**
     * Get POIs along a route asynchronously.
     *
     * @param options the route and search options
     * @return CompletableFuture with list of POIs
     */
    @NotNull
    public CompletableFuture<List<Poi>> searchAlongRouteAsync(@NotNull PoiAlongRouteOptions options) {
        return CompletableFuture.supplyAsync(() -> searchAlongRoute(options));
    }
    
    /**
     * Get POIs inside a map extent.
     *
     * @param extent  the map extent (upper-left and lower-right corners)
     * @param options the search options
     * @return list of POIs in the extent
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<Poi> searchInExtent(@NotNull MapExtent extent, @NotNull PoiInExtentOptions options) {
        Map<String, String> params = newParams("map");
        params.put("map", Boolean.TRUE.equals(options.getIncludeMap()) ? "1" : "0");
        params.put("extent", extent.toCercaliaString());
        params.put("cs", "gdd");
        params.put("mocs", "gdd");
        
        boolean useGridFilter = options.getGridSize() != null;
        
        if (useGridFilter) {
            params.put("gpoicats", String.join(",", options.getCategories()));
            params.put("gridsize", String.valueOf(options.getGridSize()));
        } else {
            params.put("getpoicats", String.join(",", options.getCategories()));
        }
        
        try {
            JsonNode response = request(params, "POI In Extent");
            return parseMapPoiResponse(response, useGridFilter);
        } catch (CercaliaException e) {
            if (e.isNoResultsFound()) {
                return Collections.emptyList();
            }
            throw e;
        }
    }
    
    /**
     * Get POIs inside a map extent asynchronously.
     *
     * @param extent  the map extent
     * @param options the search options
     * @return CompletableFuture with list of POIs
     */
    @NotNull
    public CompletableFuture<List<Poi>> searchInExtentAsync(@NotNull MapExtent extent, @NotNull PoiInExtentOptions options) {
        return CompletableFuture.supplyAsync(() -> searchInExtent(extent, options));
    }
    
    /**
     * Get POIs inside a polygon.
     *
     * @param options the polygon and category options
     * @return list of POIs inside the polygon
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<Poi> searchInPolygon(@NotNull PoiInPolygonOptions options) {
        Map<String, String> params = newParams("prox");
        params.put("cs", "4326");
        params.put("rqpoicats", String.join(",", options.getCategories()));
        params.put("wkt", options.getWkt());
        
        try {
            JsonNode response = request(params, "POI In Polygon");
            return parseProximityPoiResponse(response);
        } catch (CercaliaException e) {
            if (e.isNoResultsFound()) {
                return Collections.emptyList();
            }
            throw e;
        }
    }
    
    /**
     * Get POIs inside a polygon asynchronously.
     *
     * @param options the polygon and category options
     * @return CompletableFuture with list of POIs
     */
    @NotNull
    public CompletableFuture<List<Poi>> searchInPolygonAsync(@NotNull PoiInPolygonOptions options) {
        return CompletableFuture.supplyAsync(() -> searchInPolygon(options));
    }
    
    /**
     * Get weather forecast for a location.
     *
     * @param center the location coordinate
     * @return the weather forecast, or null if not available
     * @throws CercaliaException if the request fails
     */
    @Nullable
    public WeatherForecast getWeatherForecast(@NotNull Coordinate center) {
        Map<String, String> params = newParams("prox");
        params.put("mocs", "gdd");
        params.put("mo", center.getLat() + "," + center.getLng());
        params.put("rqpoicats", "D00M05");
        
        try {
            JsonNode response = request(params, "Weather Forecast");
            return parseWeatherResponse(response);
        } catch (CercaliaException e) {
            if (e.isNoResultsFound()) {
                return null;
            }
            throw e;
        }
    }
    
    /**
     * Get weather forecast for a location asynchronously.
     *
     * @param center the location coordinate
     * @return CompletableFuture with the weather forecast
     */
    @NotNull
    public CompletableFuture<WeatherForecast> getWeatherForecastAsync(@NotNull Coordinate center) {
        return CompletableFuture.supplyAsync(() -> getWeatherForecast(center));
    }
    
    // ========== Private parsing methods ==========
    
    private List<Poi> parseProximityPoiResponse(JsonNode response) {
        JsonNode proximityNode = response.get("proximity");
        if (proximityNode == null || proximityNode.isNull()) {
            return Collections.emptyList();
        }
        
        JsonNode poilistNode = proximityNode.get("poilist");
        if (poilistNode == null || poilistNode.isNull()) {
            return Collections.emptyList();
        }
        
        return parsePoiList(poilistNode);
    }
    
    private List<Poi> parseGeomPoiResponse(JsonNode response) {
        JsonNode getpoicatsNode = response.get("getpoicats");
        if (getpoicatsNode == null || getpoicatsNode.isNull()) {
            return Collections.emptyList();
        }
        
        JsonNode poilistNode = getpoicatsNode.get("poilist");
        if (poilistNode == null || poilistNode.isNull()) {
            return Collections.emptyList();
        }
        
        return parsePoiList(poilistNode);
    }
    
    private List<Poi> parseMapPoiResponse(JsonNode response, boolean useGridFilter) {
        JsonNode mapNode = response.get("map");
        if (mapNode == null || mapNode.isNull()) {
            return Collections.emptyList();
        }
        
        String containerKey = useGridFilter ? "gpoicats" : "getpoicats";
        JsonNode containerNode = mapNode.get(containerKey);
        if (containerNode == null || containerNode.isNull()) {
            return Collections.emptyList();
        }
        
        JsonNode poilistNode = containerNode.get("poilist");
        if (poilistNode == null || poilistNode.isNull()) {
            return Collections.emptyList();
        }
        
        return parsePoiList(poilistNode);
    }
    
    private List<Poi> parsePoiList(JsonNode poilistNode) {
        JsonNode poiArray = poilistNode.get("poi");
        if (poiArray == null || poiArray.isNull()) {
            return Collections.emptyList();
        }
        
        List<Poi> results = new ArrayList<>();
        int size = getArraySize(poiArray);
        
        for (int i = 0; i < size; i++) {
            JsonNode poiNode = getArrayElement(poiArray, i);
            if (poiNode == null) continue;
            
            try {
                Poi poi = parsePoi(poiNode);
                results.add(poi);
            } catch (Exception e) {
                logger.warn("[POI] Failed to parse POI: %s", e.getMessage());
            }
        }
        
        return results;
    }
    
    private Poi parsePoi(JsonNode poiNode) {
        // Parse coordinates (Golden Rule 3: Strict coordinates)
        JsonNode coordNode = poiNode.get("coord");
        if (coordNode == null || coordNode.isNull()) {
            throw new IllegalArgumentException("POI coordinates are missing");
        }
        
        String coordX = getCercaliaAttr(coordNode, "x");
        String coordY = getCercaliaAttr(coordNode, "y");
        if (coordX == null || coordY == null) {
            throw new IllegalArgumentException("POI coordinates are missing");
        }
        
        double lat = parseCoordinate(coordY, "latitude");
        double lng = parseCoordinate(coordX, "longitude");
        Coordinate coord = new Coordinate(lat, lng);
        
        // Parse POI fields
        String id = getCercaliaAttr(poiNode, "id");
        if (id == null) id = "";
        
        String name = getCercaliaValue(poiNode.get("name"));
        if (name == null) name = "";
        
        String categoryCode = getCercaliaAttr(poiNode, "category_id");
        if (categoryCode == null) categoryCode = "";
        
        Poi.Builder builder = Poi.builder()
                .id(id)
                .name(name)
                .categoryCode(categoryCode)
                .coord(coord);
        
        // Optional fields
        String info = getCercaliaValue(poiNode.get("info"));
        if (info != null) {
            builder.info(info);
        }
        
        String subcategoryCode = getCercaliaAttr(poiNode, "subcategory_id");
        if (subcategoryCode != null && !"-1".equals(subcategoryCode)) {
            builder.subcategoryCode(subcategoryCode);
        }
        
        String geometry = getCercaliaAttr(poiNode, "geometry");
        if (geometry != null) {
            builder.geometry(geometry);
        }
        
        Integer distance = parseIntOrNull(getCercaliaAttr(poiNode, "dist"));
        if (distance != null) {
            builder.distance(distance);
        }
        
        Integer position = parseIntOrNull(getCercaliaAttr(poiNode, "pos"));
        if (position != null) {
            builder.position(position);
        }
        
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
        
        // Parse geographic element
        JsonNode geNode = poiNode.get("ge");
        if (geNode != null && !geNode.isNull()) {
            builder.ge(parseGeographicElement(geNode));
        }
        
        // Parse pixels
        JsonNode pixelsNode = poiNode.get("pixels");
        if (pixelsNode != null && !pixelsNode.isNull()) {
            Integer px = parseIntOrNull(getCercaliaAttr(pixelsNode, "x"));
            Integer py = parseIntOrNull(getCercaliaAttr(pixelsNode, "y"));
            if (px != null && py != null) {
                builder.pixels(new PixelCoordinate(px, py));
            }
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
        
        // City -> locality (Golden Rule 4: locality instead of city)
        JsonNode cityNode = geNode.get("city");
        if (cityNode != null) {
            builder.locality(getCercaliaValue(cityNode));
            builder.localityCode(getCercaliaAttr(cityNode, "id"));
        }
        
        // Municipality (Golden Rule 2: intentional typo municipalityCode)
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
    
    @Nullable
    private WeatherForecast parseWeatherResponse(JsonNode response) {
        JsonNode proximityNode = response.get("proximity");
        if (proximityNode == null || proximityNode.isNull()) {
            return null;
        }
        
        JsonNode poilistNode = proximityNode.get("poilist");
        if (poilistNode == null || poilistNode.isNull()) {
            return null;
        }
        
        JsonNode poiArray = poilistNode.get("poi");
        if (poiArray == null || poiArray.isNull()) {
            return null;
        }
        
        JsonNode poiNode = getArrayElement(poiArray, 0);
        if (poiNode == null) {
            return null;
        }
        
        // Parse coordinates (Golden Rule 3: Strict coordinates)
        JsonNode coordNode = poiNode.get("coord");
        if (coordNode == null || coordNode.isNull()) {
            throw new IllegalArgumentException("Weather POI coordinates are missing");
        }
        
        String coordX = getCercaliaAttr(coordNode, "x");
        String coordY = getCercaliaAttr(coordNode, "y");
        if (coordX == null || coordY == null) {
            throw new IllegalArgumentException("Weather POI coordinates are missing");
        }
        
        double lat = parseCoordinate(coordY, "latitude");
        double lng = parseCoordinate(coordX, "longitude");
        Coordinate coord = new Coordinate(lat, lng);
        
        String locationName = getCercaliaValue(poiNode.get("name"));
        if (locationName == null) locationName = "";
        
        String infoStr = getCercaliaValue(poiNode.get("info"));
        if (infoStr == null) infoStr = "";
        
        ParsedWeatherInfo weatherInfo = parseWeatherInfo(infoStr);
        
        return WeatherForecast.builder()
                .locationName(locationName)
                .coord(coord)
                .lastUpdate(weatherInfo.lastUpdate)
                .forecasts(weatherInfo.days)
                .build();
    }
    
    private ParsedWeatherInfo parseWeatherInfo(String info) {
        ParsedWeatherInfo result = new ParsedWeatherInfo();
        result.days = new ArrayList<>();
        
        if (info == null || info.isEmpty()) {
            return result;
        }
        
        String[] parts = info.split("\\|");
        if (parts.length < 2) {
            return result;
        }
        
        result.lastUpdate = parts[0];
        
        int i = 1;
        int dayNum = 1;
        
        while (i < parts.length && dayNum <= 6) {
            String date = parts[i];
            if (date == null || !date.contains("-")) {
                i++;
                continue;
            }
            
            WeatherDayForecast.Builder builder = WeatherDayForecast.builder().date(date);
            
            if (dayNum <= 2) {
                // Full format with 00-12 and 12-24 splits
                if (i + 10 <= parts.length) {
                    builder.precipitationChance0012(parseOptionalDouble(parts[i + 1]));
                    builder.precipitationChance1224(parseOptionalDouble(parts[i + 2]));
                    builder.snowLevel0012(parseOptionalDouble(parts[i + 3]));
                    builder.snowLevel1224(parseOptionalDouble(parts[i + 4]));
                    builder.skyConditions0012(parseOptionalDouble(parts[i + 5]));
                    builder.skyConditions1224(parseOptionalDouble(parts[i + 6]));
                    builder.windSpeed0012(parseOptionalDouble(parts[i + 7]));
                    builder.windSpeed1224(parseOptionalDouble(parts[i + 8]));
                    builder.temperatureMax(parseOptionalDouble(parts[i + 9]));
                    builder.temperatureMin(parseOptionalDouble(parts[i + 10]));
                    i += 11;
                } else {
                    break;
                }
            } else if (dayNum == 3) {
                // Day 3 has no wind
                if (i + 8 <= parts.length) {
                    builder.precipitationChance0012(parseOptionalDouble(parts[i + 1]));
                    builder.precipitationChance1224(parseOptionalDouble(parts[i + 2]));
                    builder.snowLevel0012(parseOptionalDouble(parts[i + 3]));
                    builder.snowLevel1224(parseOptionalDouble(parts[i + 4]));
                    builder.skyConditions0012(parseOptionalDouble(parts[i + 5]));
                    builder.skyConditions1224(parseOptionalDouble(parts[i + 6]));
                    builder.temperatureMax(parseOptionalDouble(parts[i + 7]));
                    builder.temperatureMin(parseOptionalDouble(parts[i + 8]));
                    i += 9;
                } else {
                    break;
                }
            } else {
                // Days 4-6 simplified format
                if (i + 5 <= parts.length) {
                    builder.precipitationChance0012(parseOptionalDouble(parts[i + 1]));
                    builder.snowLevel0012(parseOptionalDouble(parts[i + 2]));
                    builder.skyConditions0012(parseOptionalDouble(parts[i + 3]));
                    builder.temperatureMax(parseOptionalDouble(parts[i + 4]));
                    builder.temperatureMin(parseOptionalDouble(parts[i + 5]));
                    i += 6;
                } else {
                    break;
                }
            }
            
            result.days.add(builder.build());
            dayNum++;
        }
        
        return result;
    }
    
    @Nullable
    private Double parseOptionalDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private static class ParsedWeatherInfo {
        String lastUpdate;
        List<WeatherDayForecast> days;
    }
}
