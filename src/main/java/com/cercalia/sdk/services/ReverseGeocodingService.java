package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaClient;
import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.exception.CercaliaException;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.geocoding.GeocodingCandidate;
import com.cercalia.sdk.model.geocoding.GeocodingCandidateType;
import com.cercalia.sdk.model.geocoding.GeocodingLevel;
import com.cercalia.sdk.model.reversegeocoding.*;
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
 * Service for reverse geocoding coordinates using the Cercalia API.
 * Converts geographic coordinates into addresses and place information.
 * 
 * <pre>{@code
 * ReverseGeocodingService service = new ReverseGeocodingService(config);
 * Coordinate coord = new Coordinate(41.3851, 2.1734);
 * 
 * // 1. Reverse geocode a single coordinate to an address
 * ReverseGeocodeResult result = service.reverseGeocode(coord);
 * if (result != null) {
 *     System.out.println(result.getGe().getLabel());
 * }
 *
 * // 2. Batch reverse geocoding (max 100)
 * List<Coordinate> coords = Arrays.asList(coord, new Coordinate(40.4168, -3.7038));
 * List<ReverseGeocodeResult> batchResults = service.reverseGeocodeBatch(coords);
 *
 * // 3. Get timezone for a coordinate
 * TimezoneResult tz = service.getTimezone(coord);
 * System.out.println("Timezone: " + tz.getName() + " (Offset: " + tz.getUtcOffset() + "s)");
 * }</pre>
 * 
 * @see ReverseGeocodeOptions
 * @see ReverseGeocodeResult
 * @see TimezoneResult
 */
public class ReverseGeocodingService extends CercaliaClient {
    
    /**
     * Creates a new ReverseGeocodingService with the specified configuration.
     *
     * @param config the Cercalia configuration
     */
    public ReverseGeocodingService(@NotNull CercaliaConfig config) {
        super(config);
    }
    
    /**
     * Reverse geocode a coordinate to get address information.
     *
     * @param coord the coordinate to reverse geocode
     * @return the reverse geocode result, or null if not found
     * @throws CercaliaException if the request fails
     */
    @Nullable
    public ReverseGeocodeResult reverseGeocode(@NotNull Coordinate coord) {
        return reverseGeocode(coord, null);
    }
    
    /**
     * Reverse geocode a coordinate to get address information.
     *
     * @param coord   the coordinate to reverse geocode
     * @param options the reverse geocoding options
     * @return the reverse geocode result, or null if not found
     * @throws CercaliaException if the request fails
     */
    @Nullable
    public ReverseGeocodeResult reverseGeocode(@NotNull Coordinate coord, @Nullable ReverseGeocodeOptions options) {
        List<ReverseGeocodeResult> results = reverseGeocodeBatch(Collections.singletonList(coord), options);
        return results.isEmpty() ? null : results.get(0);
    }
    
    /**
     * Reverse geocode a coordinate asynchronously.
     *
     * @param coord the coordinate to reverse geocode
     * @return a CompletableFuture with the result
     */
    @NotNull
    public CompletableFuture<ReverseGeocodeResult> reverseGeocodeAsync(@NotNull Coordinate coord) {
        return reverseGeocodeAsync(coord, null);
    }
    
    /**
     * Reverse geocode a coordinate asynchronously.
     *
     * @param coord   the coordinate to reverse geocode
     * @param options the reverse geocoding options
     * @return a CompletableFuture with the result
     */
    @NotNull
    public CompletableFuture<ReverseGeocodeResult> reverseGeocodeAsync(@NotNull Coordinate coord, 
                                                                        @Nullable ReverseGeocodeOptions options) {
        return CompletableFuture.supplyAsync(() -> reverseGeocode(coord, options));
    }
    
    /**
     * Reverse geocode multiple coordinates in a single request (max 100).
     *
     * @param coords the coordinates to reverse geocode
     * @return list of reverse geocode results
     * @throws CercaliaException      if the request fails
     * @throws IllegalArgumentException if more than 100 coordinates are provided
     */
    @NotNull
    public List<ReverseGeocodeResult> reverseGeocodeBatch(@NotNull List<Coordinate> coords) {
        return reverseGeocodeBatch(coords, null);
    }
    
    /**
     * Reverse geocode multiple coordinates in a single request (max 100).
     *
     * @param coords  the coordinates to reverse geocode
     * @param options the reverse geocoding options
     * @return list of reverse geocode results
     * @throws CercaliaException      if the request fails
     * @throws IllegalArgumentException if more than 100 coordinates are provided
     */
    @NotNull
    public List<ReverseGeocodeResult> reverseGeocodeBatch(@NotNull List<Coordinate> coords, 
                                                          @Nullable ReverseGeocodeOptions options) {
        if (coords.isEmpty()) {
            return Collections.emptyList();
        }
        if (coords.size() > 100) {
            throw new IllegalArgumentException("Maximum 100 coordinates allowed per request");
        }
        
        Map<String, String> params = newParams("prox");
        params.put("mocs", "gdd");
        
        if (coords.size() == 1) {
            params.put("mo", coords.get(0).getLat() + "," + coords.get(0).getLng());
        } else {
            StringBuilder molist = new StringBuilder();
            for (int i = 0; i < coords.size(); i++) {
                if (i > 0) molist.append(",");
                molist.append("[").append(coords.get(i).getLat())
                      .append(",").append(coords.get(i).getLng()).append("]");
            }
            params.put("molist", molist.toString());
        }
        
        if (options != null && options.getLevel() != null) {
            params.put("rqge", options.getLevel().getValue());
        } else if (options == null || options.getCategory() == null) {
            // Default level if neither level nor category is specified
            params.put("rqge", "adr");
        }
        
        if (options != null && options.getCategory() != null) {
            params.put("rqpoicats", options.getCategory());
        }
        
        if (options != null && options.getDateTime() != null) {
            params.put("datetime", options.getDateTime());
        }
        
        try {
            JsonNode response = request(params, "ReverseGeocoding");
            return parseProximityResponse(response);
        } catch (CercaliaException e) {
            // Handle "No results found" gracefully
            if ("30006".equals(e.getErrorCode())) {
                return Collections.emptyList();
            }
            throw e;
        }
    }
    
    /**
     * Reverse geocode multiple coordinates asynchronously.
     *
     * @param coords  the coordinates to reverse geocode
     * @param options the reverse geocoding options
     * @return a CompletableFuture with the results
     */
    @NotNull
    public CompletableFuture<List<ReverseGeocodeResult>> reverseGeocodeBatchAsync(@NotNull List<Coordinate> coords, 
                                                                                    @Nullable ReverseGeocodeOptions options) {
        return CompletableFuture.supplyAsync(() -> reverseGeocodeBatch(coords, options));
    }
    
    /**
     * Get regions/municipalities intersecting a polygon (WKT).
     *
     * @param wkt   the WKT polygon
     * @param level the geographic level (ct, mun, subreg, reg)
     * @return list of intersecting regions
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<ReverseGeocodeResult> getIntersectingRegions(@NotNull String wkt, @NotNull String level) {
        Map<String, String> params = newParams("prox");
        params.put("cs", "4326");
        params.put("wkt", wkt);
        params.put("rqge", level);
        
        try {
            JsonNode response = request(params, "IntersectingRegions");
            return parseGelistResponse(response, level);
        } catch (CercaliaException e) {
            // Handle "No results found" gracefully
            if ("30006".equals(e.getErrorCode())) {
                return Collections.emptyList();
            }
            throw e;
        }
    }
    
    /**
     * Get regions/municipalities intersecting a polygon asynchronously.
     *
     * @param wkt   the WKT polygon
     * @param level the geographic level (ct, mun, subreg, reg)
     * @return a CompletableFuture with the results
     */
    @NotNull
    public CompletableFuture<List<ReverseGeocodeResult>> getIntersectingRegionsAsync(@NotNull String wkt, 
                                                                                       @NotNull String level) {
        return CompletableFuture.supplyAsync(() -> getIntersectingRegions(wkt, level));
    }
    
    /**
     * Get timezone information for a coordinate.
     *
     * @param coord the coordinate to get timezone for
     * @return timezone information, or null if not found
     * @throws CercaliaException if the request fails
     */
    @Nullable
    public TimezoneResult getTimezone(@NotNull Coordinate coord) {
        return getTimezone(coord, null);
    }
    
    /**
     * Get timezone information for a coordinate.
     *
     * @param coord   the coordinate to get timezone for
     * @param options optional datetime in ISO 8601 format
     * @return timezone information, or null if not found
     * @throws CercaliaException if the request fails
     */
    @Nullable
    public TimezoneResult getTimezone(@NotNull Coordinate coord, @Nullable TimezoneOptions options) {
        Map<String, String> params = newParams("prox");
        params.put("mocs", "gdd");
        params.put("mo", coord.getLat() + "," + coord.getLng());
        params.put("rqge", "timezone");
        
        if (options != null && options.getDateTime() != null) {
            params.put("datetime", options.getDateTime());
        }
        
        try {
            JsonNode response = request(params, "Timezone");
            
            JsonNode proximity = response.get("proximity");
            if (proximity == null || proximity.isNull()) {
                return null;
            }
            
            JsonNode gelist = proximity.get("gelist");
            if (gelist == null || gelist.isNull()) {
                return null;
            }
            
            JsonNode geArray = gelist.get("ge");
            if (geArray == null || geArray.isNull()) {
                return null;
            }
            
            int size = getArraySize(geArray);
            if (size == 0) {
                return null;
            }
            
            JsonNode geObj = getArrayElement(geArray, 0);
            if (geObj == null) {
                return null;
            }
            
            return TimezoneResult.builder()
                    .coord(coord)
                    .id(getCercaliaAttrOrEmpty(geObj, "id"))
                    .name(getCercaliaAttrOrEmpty(geObj, "name"))
                    .localDateTime(getCercaliaAttrOrEmpty(geObj, "localdatetime"))
                    .utcDateTime(getCercaliaAttrOrEmpty(geObj, "utcdatetime"))
                    .utcOffset(parseIntSafe(getCercaliaAttr(geObj, "utctimeoffset")))
                    .daylightSavingTime(parseIntSafe(getCercaliaAttr(geObj, "daylightsavingtime")))
                    .build();
        } catch (CercaliaException e) {
            // Handle "No results found" gracefully
            if ("30006".equals(e.getErrorCode())) {
                return null;
            }
            throw e;
        }
    }
    
    /**
     * Get timezone information for a coordinate asynchronously.
     *
     * @param coord   the coordinate to get timezone for
     * @param options optional datetime in ISO 8601 format
     * @return a CompletableFuture with the result
     */
    @NotNull
    public CompletableFuture<TimezoneResult> getTimezoneAsync(@NotNull Coordinate coord, 
                                                               @Nullable TimezoneOptions options) {
        return CompletableFuture.supplyAsync(() -> getTimezone(coord, options));
    }
    
    // ========== Private parsing methods ==========
    
    private List<ReverseGeocodeResult> parseProximityResponse(JsonNode response) {
        JsonNode proximity = response.get("proximity");
        if (proximity == null || proximity.isNull()) {
            return Collections.emptyList();
        }
        
        String type = getCercaliaAttr(proximity, "type");
        
        // POI type response
        if ("poi".equals(type)) {
            return parsePoiResponse(proximity);
        }
        
        // Timezone or administrative level response
        if ("timezone".equals(type) || "mun".equals(type) || "ct".equals(type) || 
            "subreg".equals(type) || "reg".equals(type) || "ctry".equals(type)) {
            return parseGelistResponse(response, type);
        }
        
        // Default 'adr', 'cadr', 'st', etc.
        JsonNode molist = proximity.get("molist");
        if (molist != null && !molist.isNull()) {
            JsonNode moArray = molist.get("mo");
            if (moArray != null && !moArray.isNull()) {
                List<ReverseGeocodeResult> results = new ArrayList<>();
                int size = getArraySize(moArray);
                for (int i = 0; i < size; i++) {
                    JsonNode mo = getArrayElement(moArray, i);
                    if (mo == null) continue;
                    
                    JsonNode ge = mo.get("ge");
                    if (ge == null || ge.isNull()) {
                        // Try getting ge from attribute
                        String geAttr = getCercaliaAttr(mo, "ge");
                        if (geAttr != null) {
                            continue; // Cannot parse from attribute
                        }
                        continue;
                    }
                    
                    try {
                        results.add(mapGeToResult(ge, type != null ? type : "adr"));
                    } catch (CercaliaException e) {
                        logger.warn("Skipping invalid ge element: %s", e.getMessage());
                    }
                }
                return results;
            }
        }
        
        // Fallback to gelist
        return parseGelistResponse(response, type != null ? type : "adr");
    }
    
    private List<ReverseGeocodeResult> parseGelistResponse(JsonNode response, String type) {
        JsonNode proximity = response.get("proximity");
        if (proximity == null || proximity.isNull()) {
            return Collections.emptyList();
        }
        
        JsonNode gelist = proximity.get("gelist");
        if (gelist == null || gelist.isNull()) {
            return Collections.emptyList();
        }
        
        JsonNode geArray = gelist.get("ge");
        if (geArray == null || geArray.isNull()) {
            return Collections.emptyList();
        }
        
        List<ReverseGeocodeResult> results = new ArrayList<>();
        int size = getArraySize(geArray);
        
        for (int i = 0; i < size; i++) {
            JsonNode ge = getArrayElement(geArray, i);
            if (ge == null) continue;
            
            try {
                results.add(mapGeToResult(ge, type));
            } catch (CercaliaException e) {
                logger.warn("Skipping invalid ge element: %s", e.getMessage());
            }
        }
        
        return results;
    }
    
    private List<ReverseGeocodeResult> parsePoiResponse(JsonNode proximity) {
        JsonNode poilist = proximity.get("poilist");
        if (poilist == null || poilist.isNull()) {
            return Collections.emptyList();
        }
        
        JsonNode poiArray = poilist.get("poi");
        if (poiArray == null || poiArray.isNull()) {
            return Collections.emptyList();
        }
        
        List<ReverseGeocodeResult> results = new ArrayList<>();
        int size = getArraySize(poiArray);
        
        for (int i = 0; i < size; i++) {
            JsonNode poi = getArrayElement(poiArray, i);
            if (poi == null) continue;
            
            try {
                results.add(mapPoiToResult(poi));
            } catch (CercaliaException e) {
                logger.warn("Skipping invalid poi element: %s", e.getMessage());
            }
        }
        
        return results;
    }
    
    private ReverseGeocodeResult mapGeToResult(JsonNode ge, String type) {
        // REGLA DE ORO: Coordenadas estrictas - validar existencia
        JsonNode coordNode = ge.get("coord");
        if (coordNode == null || coordNode.isNull()) {
            throw new CercaliaException("Invalid geographic element: missing coordinates");
        }
        
        String coordY = getCercaliaAttr(coordNode, "y");
        String coordX = getCercaliaAttr(coordNode, "x");
        
        if (coordY == null || coordX == null) {
            throw new CercaliaException("Invalid geographic element: missing coordinates");
        }
        
        double lat = parseCoordinate(coordY, "latitude");
        double lng = parseCoordinate(coordX, "longitude");
        
        // Build geocoding candidate
        GeocodingCandidate.Builder candidateBuilder = GeocodingCandidate.builder()
                .id(getCercaliaAttrOrDefault(ge, "id", "unknown"))
                .name(getNameFromGe(ge))
                .municipality(getCercaliaValue(ge.get("municipality")))
                .district(getCercaliaValue(ge.get("district")))
                .subregion(getCercaliaValue(ge.get("subregion")))
                .region(getCercaliaValue(ge.get("region")))
                .country(getCercaliaValue(ge.get("country")))
                .houseNumber(getCercaliaValue(ge.get("housenumber")))
                .coord(new Coordinate(lat, lng))
                .type(mapCandidateType(getCercaliaAttr(ge, "frc"), type))
                .level(mapLevel(getCercaliaAttr(ge, "type"), type));
        
        // REGLA DE ORO: Integridad de Identificadores - incluir todos los códigos
        // Mapear city → locality con localityCode
        JsonNode cityNode = ge.get("city");
        if (cityNode != null && !cityNode.isNull()) {
            candidateBuilder.locality(getCercaliaValue(cityNode));
            candidateBuilder.localityCode(getCercaliaAttr(cityNode, "id"));
        }
        
        // REGLA DE ORO: municipalityCode (con errata intencionada)
        JsonNode munNode = ge.get("municipality");
        if (munNode != null && !munNode.isNull()) {
            candidateBuilder.municipalityCode(getCercaliaAttr(munNode, "id"));
        }
        
        // Street con streetCode
        JsonNode streetNode = ge.get("street");
        if (streetNode != null && !streetNode.isNull()) {
            String streetName = getCercaliaValue(streetNode);
            if (streetName == null) {
                streetName = getCercaliaAttr(streetNode, "name");
            }
            candidateBuilder.street(streetName);
            candidateBuilder.streetCode(getCercaliaAttr(streetNode, "id"));
        }
        
        // Subregion con subregionCode
        JsonNode subregionNode = ge.get("subregion");
        if (subregionNode != null && !subregionNode.isNull()) {
            candidateBuilder.subregionCode(getCercaliaAttr(subregionNode, "id"));
        }
        
        // Region con regionCode
        JsonNode regionNode = ge.get("region");
        if (regionNode != null && !regionNode.isNull()) {
            candidateBuilder.regionCode(getCercaliaAttr(regionNode, "id"));
        }
        
        // Country con countryCode
        JsonNode countryNode = ge.get("country");
        if (countryNode != null && !countryNode.isNull()) {
            candidateBuilder.countryCode(getCercaliaAttr(countryNode, "id"));
        }
        
        // Postal code handling
        candidateBuilder.postalCode(getPostalCode(ge));
        
        // Build result
        ReverseGeocodeResult.Builder resultBuilder = ReverseGeocodeResult.builder()
                .ge(candidateBuilder.build());
        
        // Distance
        String dist = getCercaliaAttr(ge, "dist");
        if (dist != null) {
            resultBuilder.distance(parseDoubleSafe(dist));
        }
        
        // Speed
        String kmh = getCercaliaAttr(ge, "kmh");
        if (kmh != null) {
            resultBuilder.maxSpeed(parseDoubleSafe(kmh));
        }
        
        // Milestone
        String km = getCercaliaValue(ge.get("km"));
        if (km != null) {
            resultBuilder.km(km);
        }
        
        // Direction
        String direction = getCercaliaValue(ge.get("direction"));
        if (direction != null) {
            resultBuilder.direction(direction);
        }
        
        // Timezone specific
        if ("timezone".equals(type)) {
            resultBuilder.timezone(TimezoneInfo.builder()
                    .id(getCercaliaAttrOrEmpty(ge, "id"))
                    .name(getCercaliaAttrOrEmpty(ge, "name"))
                    .localDateTime(getCercaliaAttrOrEmpty(ge, "localdatetime"))
                    .utcDateTime(getCercaliaAttrOrEmpty(ge, "utcdatetime"))
                    .utcOffset(parseIntSafe(getCercaliaAttr(ge, "utctimeoffset")))
                    .daylightSavingTime(parseIntSafe(getCercaliaAttr(ge, "daylightsavingtime")))
                    .build());
        }
        
        return resultBuilder.build();
    }
    
    private ReverseGeocodeResult mapPoiToResult(JsonNode poi) {
        // REGLA DE ORO: Coordenadas estrictas
        JsonNode coordNode = poi.get("coord");
        if (coordNode == null || coordNode.isNull()) {
            throw new CercaliaException("Invalid POI: missing coordinates");
        }
        
        String coordY = getCercaliaAttr(coordNode, "y");
        String coordX = getCercaliaAttr(coordNode, "x");
        
        if (coordY == null || coordX == null) {
            throw new CercaliaException("Invalid POI: missing coordinates");
        }
        
        double lat = parseCoordinate(coordY, "latitude");
        double lng = parseCoordinate(coordX, "longitude");
        
        String category = getCercaliaAttr(poi, "category_id");
        JsonNode ge = poi.get("ge");
        
        GeocodingCandidate.Builder candidateBuilder = GeocodingCandidate.builder()
                .id(getCercaliaAttrOrDefault(poi, "id", ""))
                .name(getCercaliaValueOrDefault(poi.get("name"), ""))
                .coord(new Coordinate(lat, lng))
                .type(GeocodingCandidateType.POI);
        
        if (ge != null && !ge.isNull()) {
            candidateBuilder
                    .municipality(getCercaliaValue(ge.get("municipality")))
                    .subregion(getCercaliaValue(ge.get("subregion")))
                    .region(getCercaliaValue(ge.get("region")))
                    .country(getCercaliaValue(ge.get("country")));
            
            // REGLA DE ORO: Integridad de Identificadores
            // Mapear city → locality
            JsonNode cityNode = ge.get("city");
            if (cityNode != null && !cityNode.isNull()) {
                candidateBuilder.locality(getCercaliaValue(cityNode));
                candidateBuilder.localityCode(getCercaliaAttr(cityNode, "id"));
            }
            
            // REGLA DE ORO: municipalityCode
            JsonNode munNode = ge.get("municipality");
            if (munNode != null && !munNode.isNull()) {
                candidateBuilder.municipalityCode(getCercaliaAttr(munNode, "id"));
            }
            
            JsonNode subregionNode = ge.get("subregion");
            if (subregionNode != null && !subregionNode.isNull()) {
                candidateBuilder.subregionCode(getCercaliaAttr(subregionNode, "id"));
            }
            
            JsonNode regionNode = ge.get("region");
            if (regionNode != null && !regionNode.isNull()) {
                candidateBuilder.regionCode(getCercaliaAttr(regionNode, "id"));
            }
            
            JsonNode countryNode = ge.get("country");
            if (countryNode != null && !countryNode.isNull()) {
                candidateBuilder.countryCode(getCercaliaAttr(countryNode, "id"));
            }
        }
        
        ReverseGeocodeResult.Builder resultBuilder = ReverseGeocodeResult.builder()
                .ge(candidateBuilder.build());
        
        // Handle special categories
        if ("D00SECCEN".equals(category)) {
            String censusId = getCercaliaValue(poi.get("info"));
            if (censusId == null) {
                censusId = getCercaliaValue(poi.get("name"));
            }
            resultBuilder.censusId(censusId);
        } else if ("D00SIGPAC".equals(category)) {
            String info = getCercaliaValue(poi.get("info"));
            if (info == null) info = "";
            String[] parts = info.split("\\|");
            
            resultBuilder.sigpac(SigpacInfo.builder()
                    .id(getCercaliaValueOrDefault(poi.get("name"), ""))
                    .municipalityCode(parts.length > 0 ? parts[0] : "")
                    .usage(parts.length > 1 ? parts[1] : "")
                    .extensionHa(parts.length > 2 ? parseDoubleSafe(parts[2]) : 0.0)
                    .vulnerableType(parts.length > 3 ? parts[3] : null)
                    .vulnerableCode(parts.length > 4 ? parts[4] : null)
                    .build());
        }
        
        return resultBuilder.build();
    }
    
    private String getNameFromGe(JsonNode ge) {
        String name = getCercaliaAttr(ge, "name");
        if (name != null) {
            return name;
        }
        name = getCercaliaValue(ge.get("name"));
        if (name != null) {
            return name;
        }
        return "Unknown";
    }
    
    private String getPostalCode(JsonNode ge) {
        JsonNode pcNode = ge.get("postalcode");
        if (pcNode == null || pcNode.isNull()) {
            return null;
        }
        
        String pc = getCercaliaValue(pcNode);
        if (pc != null) {
            return pc;
        }
        
        return getCercaliaAttr(pcNode, "id");
    }
    
    private GeocodingCandidateType mapCandidateType(String frc, String type) {
        String t = frc != null ? frc.toLowerCase() : (type != null ? type.toLowerCase() : "");
        
        if (t.isEmpty()) {
            return GeocodingCandidateType.ADDRESS;
        }
        
        // Road types
        if ("ap".equals(t) || "av".equals(t) || "na1".equals(t) || "a2".equals(t) || 
            "pl".equals(t) || "ep".equals(t) || "cl".equals(t) || "pt".equals(t)) {
            return GeocodingCandidateType.ROAD;
        }
        
        // POI types
        if ("poi".equals(t) || "timezone".equals(t)) {
            return GeocodingCandidateType.POI;
        }
        
        // Municipality types
        if ("ct".equals(t) || "municipality".equals(t) || "mun".equals(t)) {
            return GeocodingCandidateType.MUNICIPALITY;
        }
        
        return GeocodingCandidateType.ADDRESS;
    }
    
    private GeocodingLevel mapLevel(String geType, String responseType) {
        String t = geType != null ? geType.toLowerCase() : (responseType != null ? responseType.toLowerCase() : "");
        
        if (t.isEmpty()) {
            return null;
        }
        
        switch (t) {
            case "adr":
            case "cadr":
                return GeocodingLevel.ADR;
            case "st":
                return GeocodingLevel.ST;
            case "ct":
                return GeocodingLevel.CT;
            case "pcode":
                return GeocodingLevel.PCODE;
            case "mun":
                return GeocodingLevel.MUN;
            case "subreg":
                return GeocodingLevel.SUBREG;
            case "reg":
                return GeocodingLevel.REG;
            case "ctry":
                return GeocodingLevel.CTRY;
            case "rd":
                return GeocodingLevel.RD;
            case "pk":
                return GeocodingLevel.PK;
            case "poi":
            case "timezone":
                return GeocodingLevel.POI;
            default:
                return null;
        }
    }
    
    private String getCercaliaAttrOrEmpty(JsonNode node, String attr) {
        String value = getCercaliaAttr(node, attr);
        return value != null ? value : "";
    }
    
    private String getCercaliaAttrOrDefault(JsonNode node, String attr, String defaultValue) {
        String value = getCercaliaAttr(node, attr);
        return value != null ? value : defaultValue;
    }
    
    private String getCercaliaValueOrDefault(JsonNode node, String defaultValue) {
        String value = getCercaliaValue(node);
        return value != null ? value : defaultValue;
    }
    
    private int parseIntSafe(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private double parseDoubleSafe(String value) {
        if (value == null || value.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
