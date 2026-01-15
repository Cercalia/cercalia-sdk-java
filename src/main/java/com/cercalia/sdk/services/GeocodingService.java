package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaClient;
import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.exception.CercaliaException;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.geocoding.*;
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
 * Service for geocoding addresses using the Cercalia API.
 * Converts addresses and place names into geographic coordinates.
 *
 * <pre>{@code
 * GeocodingService service = new GeocodingService(config);
 * 
 * // 1. Structured address search
 * List<GeocodingCandidate> results = service.geocode(GeocodingOptions.builder()
 *     .street("Carrer de la Provença")
 *     .locality("Barcelona")
 *     .countryCode("ESP")
 *     .build());
 *
 * // 2. Road kilometer mark search
 * List<GeocodingCandidate> roadResults = service.geocodeRoad("A-2", 582, null);
 *
 * // 3. Cities by postal code
 * List<PostalCodeCity> cities = service.geocodeCitiesByPostalCode("08013");
 * }</pre>
 * 
 * @see GeocodingOptions
 * @see GeocodingCandidate
 */
public class GeocodingService extends CercaliaClient {
    
    /**
     * Creates a new GeocodingService with the specified configuration.
     *
     * @param config the Cercalia configuration
     */
    public GeocodingService(@NotNull CercaliaConfig config) {
        super(config);
    }
    
    /**
     * Geocode an address using Cercalia API.
     *
     * @param options the geocoding options
     * @return list of geocoding candidates
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<GeocodingCandidate> geocode(@NotNull GeocodingOptions options) {
        Map<String, String> params = newParams("cand");
        params.put("detcand", "1");
        params.put("priorityfilter", "1");
        params.put("mode", "0"); // mode 0 for structured search
        params.put("cleanadr", "1");
        
        // Set country code (default to ESP)
        String countryCode = options.getCountryCode();
        params.put("ctryc", countryCode != null ? countryCode.toUpperCase() : "ESP");
        
        addIfPresent(params, "ctn", options.getLocality());
        addIfPresent(params, "munn", options.getMunicipality());
        addIfPresent(params, "adr", options.getStreet());
        addIfPresent(params, "pcode", options.getPostalCode());
        addIfPresent(params, "regn", options.getRegion());
        addIfPresent(params, "subregn", options.getSubregion());
        addIfPresent(params, "ctryn", options.getCountry());
        addIfPresent(params, "num", options.getLimit());
        addIfTrue(params, "fullsearch", options.getFullSearch(), "3");
        
        try {
            JsonNode response = request(params, "Cercalia Geocoding");
            return parseCandidates(response, options);
        } catch (CercaliaException e) {
            // Handle "No candidates found" gracefully
            if (e.isNoCandidatesFound()) {
                return Collections.emptyList();
            }
            throw e;
        }
    }
    
    /**
     * Geocode an address asynchronously.
     *
     * @param options the geocoding options
     * @return a CompletableFuture with the list of geocoding candidates
     */
    @NotNull
    public CompletableFuture<List<GeocodingCandidate>> geocodeAsync(@NotNull GeocodingOptions options) {
        return CompletableFuture.supplyAsync(() -> geocode(options));
    }
    
    /**
     * Geocode a road milestone (PK).
     *
     * @param rdn     the road name/identifier
     * @param km      the kilometer mark
     * @param options additional options (optional)
     * @return list of geocoding candidates
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<GeocodingCandidate> geocodeRoad(@NotNull String rdn, double km, @Nullable GeocodingOptions options) {
        Map<String, String> params = newParams("cand");
        params.put("detcand", "1");
        params.put("rdn", rdn);
        params.put("km", String.valueOf(km));
        
        String countryCode = options != null && options.getCountryCode() != null 
                ? options.getCountryCode().toUpperCase() 
                : "ESP";
        params.put("ctryc", countryCode);
        
        if (options != null) {
            addIfPresent(params, "subregn", options.getSubregion());
            addIfPresent(params, "munn", options.getMunicipality());
            addIfPresent(params, "pcode", options.getPostalCode());
        }
        
        try {
            JsonNode response = request(params, "GeocodingRoad");
            return parseRoadCandidates(response, rdn, km);
        } catch (CercaliaException e) {
            logger.error("[GeocodingRoad] Error: %s", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Geocode a road milestone asynchronously.
     *
     * @param rdn     the road name/identifier
     * @param km      the kilometer mark
     * @param options additional options (optional)
     * @return a CompletableFuture with the list of geocoding candidates
     */
    @NotNull
    public CompletableFuture<List<GeocodingCandidate>> geocodeRoadAsync(@NotNull String rdn, double km, 
                                                                         @Nullable GeocodingOptions options) {
        return CompletableFuture.supplyAsync(() -> geocodeRoad(rdn, km, options));
    }
    
    /**
     * Get list of cities related to a postal code.
     *
     * @param postalCode  the postal code
     * @param countryCode the country code (default: ESP)
     * @return list of cities for the postal code
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<PostalCodeCity> geocodeCitiesByPostalCode(@NotNull String postalCode, @Nullable String countryCode) {
        Map<String, String> params = newParams("prox");
        params.put("rqge", "ctpcode");
        params.put("ctryc", countryCode != null ? countryCode.toUpperCase() : "ESP");
        params.put("pcode", postalCode);
        
        try {
            JsonNode response = request(params, "GeocodeCitiesByPostalCode");
            return parsePostalCodeCities(response);
        } catch (CercaliaException e) {
            logger.error("[GeocodeCitiesByPostalCode] Error: %s", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get list of cities related to a postal code (default country: ESP).
     *
     * @param postalCode the postal code
     * @return list of cities for the postal code
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<PostalCodeCity> geocodeCitiesByPostalCode(@NotNull String postalCode) {
        return geocodeCitiesByPostalCode(postalCode, "ESP");
    }
    
    /**
     * Get list of cities related to a postal code asynchronously.
     *
     * @param postalCode  the postal code
     * @param countryCode the country code (default: ESP)
     * @return a CompletableFuture with the list of cities
     */
    @NotNull
    public CompletableFuture<List<PostalCodeCity>> geocodeCitiesByPostalCodeAsync(@NotNull String postalCode, 
                                                                                    @Nullable String countryCode) {
        return CompletableFuture.supplyAsync(() -> geocodeCitiesByPostalCode(postalCode, countryCode));
    }
    
    // ========== Private parsing methods ==========
    
    private List<GeocodingCandidate> parseCandidates(JsonNode response, GeocodingOptions options) {
        JsonNode candidatesNode = response.get("candidates");
        if (candidatesNode == null || candidatesNode.isNull()) {
            return Collections.emptyList();
        }
        
        JsonNode candidateArray = candidatesNode.get("candidate");
        if (candidateArray == null || candidateArray.isNull()) {
            return Collections.emptyList();
        }
        
        List<GeocodingCandidate> results = new ArrayList<>();
        int size = getArraySize(candidateArray);
        
        for (int i = 0; i < size; i++) {
            JsonNode cand = getArrayElement(candidateArray, i);
            if (cand == null) continue;
            
            JsonNode ge = cand.get("ge");
            if (ge == null || ge.isNull()) continue;
            
            JsonNode coordNode = ge.get("coord");
            if (coordNode == null || coordNode.isNull()) continue;
            
            // Filter country-only results when searching for more specific locations
            String type = getCercaliaAttr(ge, "type");
            String id = getCercaliaAttr(ge, "id");
            String name = getCercaliaValue(ge.get("name"));
            
            if (isCountryResult(type, id, name) && hasSpecificSearch(options)) {
                // Allow if locality matches name or is very short
                if (options.getLocality() != null && name != null) {
                    if (options.getLocality().equalsIgnoreCase(name) || options.getLocality().length() <= 3) {
                        // Keep this result
                    } else {
                        continue; // Skip country-only result
                    }
                } else {
                    continue;
                }
            }
            
            GeocodingCandidate candidate = parseCandidate(cand, ge, coordNode);
            if (candidate != null) {
                results.add(candidate);
            }
        }
        
        return results;
    }
    
    private GeocodingCandidate parseCandidate(JsonNode cand, JsonNode ge, JsonNode coordNode) {
        String type = getCercaliaAttr(ge, "type");
        String id = getCercaliaAttr(ge, "id");
        String desc = getCercaliaAttr(cand, "desc");
        
        // Parse coordinates
        String xStr = getCercaliaAttr(coordNode, "x");
        String yStr = getCercaliaAttr(coordNode, "y");
        double lat = parseCoordinate(yStr, "latitude");
        double lng = parseCoordinate(xStr, "longitude");
        
        // Parse postal code with fallback
        String postalCode = getCercaliaValue(ge.get("postalcode"));
        if (postalCode == null) {
            JsonNode pcNode = ge.get("postalcode");
            if (pcNode != null) {
                postalCode = getCercaliaAttr(pcNode, "id");
            }
        }
        // Try extracting from desc if it looks like a postal code
        if (postalCode == null && desc != null && desc.matches("^\\d{5}$")) {
            postalCode = desc;
        }
        
        // Build candidate
        String candidateId = id;
        if (candidateId == null) {
            JsonNode countryNode = ge.get("country");
            if (countryNode != null) {
                candidateId = getCercaliaAttr(countryNode, "id");
            }
        }
        if (candidateId == null) {
            candidateId = "unknown";
        }
        
        String name = getCercaliaValue(ge.get("name"));
        if (name == null) {
            name = getCercaliaAttr(cand, "name");
        }
        if (name == null) {
            name = desc;
        }
        if (name == null) {
            name = "Unknown";
        }
        
        return GeocodingCandidate.builder()
                .id(candidateId)
                .name(name)
                .label(desc)
                .locality(getCercaliaValue(ge.get("city")))
                .localityCode(getCercaliaAttr(ge.get("city"), "id"))
                .municipality(getCercaliaValue(ge.get("municipality")))
                .municipalityCode(getCercaliaAttr(ge.get("municipality"), "id"))
                .district(getCercaliaValue(ge.get("district")))
                .districtCode(getCercaliaAttr(ge.get("district"), "id"))
                .subregion(getCercaliaValue(ge.get("subregion")))
                .subregionCode(getCercaliaAttr(ge.get("subregion"), "id"))
                .region(getCercaliaValue(ge.get("region")))
                .regionCode(getCercaliaAttr(ge.get("region"), "id"))
                .country(getCercaliaValue(ge.get("country")))
                .countryCode(getCercaliaAttr(ge.get("country"), "id"))
                .postalCode(postalCode)
                .houseNumber(getCercaliaValue(ge.get("housenumber")))
                .coord(new Coordinate(lat, lng))
                .type(GeocodingCandidateType.fromCercaliaType(type))
                .level(GeocodingLevel.fromValue(type))
                .build();
    }
    
    private List<GeocodingCandidate> parseRoadCandidates(JsonNode response, String rdn, double km) {
        JsonNode candidatesNode = response.get("candidates");
        if (candidatesNode == null || candidatesNode.isNull()) {
            return Collections.emptyList();
        }
        
        JsonNode candidateArray = candidatesNode.get("candidate");
        if (candidateArray == null || candidateArray.isNull()) {
            return Collections.emptyList();
        }
        
        List<GeocodingCandidate> results = new ArrayList<>();
        int size = getArraySize(candidateArray);
        
        for (int i = 0; i < size; i++) {
            JsonNode cand = getArrayElement(candidateArray, i);
            if (cand == null) continue;
            
            JsonNode ge = cand.get("ge");
            if (ge == null || ge.isNull()) continue;
            
            JsonNode coordNode = ge.get("coord");
            if (coordNode == null || coordNode.isNull()) continue;
            
            String type = getCercaliaAttr(ge, "type");
            String desc = getCercaliaAttr(cand, "desc");
            
            // Parse coordinates
            String xStr = getCercaliaAttr(coordNode, "x");
            String yStr = getCercaliaAttr(coordNode, "y");
            double lat = parseCoordinate(yStr, "latitude");
            double lng = parseCoordinate(xStr, "longitude");
            
            // Parse postal code
            String postalCode = getCercaliaAttr(ge.get("postalcode"), "id");
            if (postalCode == null) {
                postalCode = getCercaliaValue(ge.get("postalcode"));
            }
            
            String id = getCercaliaAttr(ge, "id");
            String name = getCercaliaValue(ge.get("name"));
            if (name == null) {
                name = String.format("%s KM %.0f", rdn, km);
            }
            
            GeocodingCandidate candidate = GeocodingCandidate.builder()
                    .id(id != null ? id : rdn)
                    .name(name)
                    .label(desc)
                    .locality(getCercaliaValue(ge.get("city")))
                    .localityCode(getCercaliaAttr(ge.get("city"), "id"))
                    .houseNumber(getCercaliaValue(ge.get("housenumber")))
                    .municipality(getCercaliaValue(ge.get("municipality")))
                    .municipalityCode(getCercaliaAttr(ge.get("municipality"), "id"))
                    .district(getCercaliaValue(ge.get("district")))
                    .districtCode(getCercaliaAttr(ge.get("district"), "id"))
                    .subregion(getCercaliaValue(ge.get("subregion")))
                    .subregionCode(getCercaliaAttr(ge.get("subregion"), "id"))
                    .region(getCercaliaValue(ge.get("region")))
                    .regionCode(getCercaliaAttr(ge.get("region"), "id"))
                    .country(getCercaliaValue(ge.get("country")))
                    .countryCode(getCercaliaAttr(ge.get("country"), "id"))
                    .postalCode(postalCode)
                    .coord(new Coordinate(lat, lng))
                    .type(GeocodingCandidateType.MILESTONE)
                    .level(GeocodingLevel.fromValue(type))
                    .build();
            
            results.add(candidate);
        }
        
        return results;
    }
    
    private List<PostalCodeCity> parsePostalCodeCities(JsonNode response) {
        JsonNode proximityNode = response.get("proximity");
        if (proximityNode == null || proximityNode.isNull()) {
            return Collections.emptyList();
        }
        
        JsonNode gelistNode = proximityNode.get("gelist");
        if (gelistNode == null || gelistNode.isNull()) {
            return Collections.emptyList();
        }
        
        JsonNode geArray = gelistNode.get("ge");
        if (geArray == null || geArray.isNull()) {
            return Collections.emptyList();
        }
        
        List<PostalCodeCity> results = new ArrayList<>();
        int size = getArraySize(geArray);
        
        for (int i = 0; i < size; i++) {
            JsonNode ge = getArrayElement(geArray, i);
            if (ge == null) continue;
            
            JsonNode coordNode = ge.get("coord");
            if (coordNode == null || coordNode.isNull()) continue;
            
            // Parse coordinates
            String xStr = getCercaliaAttr(coordNode, "x");
            String yStr = getCercaliaAttr(coordNode, "y");
            double lat = parseCoordinate(yStr, "latitude");
            double lng = parseCoordinate(xStr, "longitude");
            
            String id = getCercaliaAttr(ge, "id");
            String name = getCercaliaAttr(ge, "name");
            
            PostalCodeCity city = PostalCodeCity.builder()
                    .id(id != null ? id : "unknown")
                    .name(name != null ? name : "Unknown")
                    .municipality(getCercaliaValue(ge.get("municipality")))
                    .municipalityCode(getCercaliaAttr(ge.get("municipality"), "id"))
                    .subregion(getCercaliaValue(ge.get("subregion")))
                    .subregionCode(getCercaliaAttr(ge.get("subregion"), "id"))
                    .region(getCercaliaValue(ge.get("region")))
                    .regionCode(getCercaliaAttr(ge.get("region"), "id"))
                    .country(getCercaliaValue(ge.get("country")))
                    .countryCode(getCercaliaAttr(ge.get("country"), "id"))
                    .coord(new Coordinate(lat, lng))
                    .build();
            
            results.add(city);
        }
        
        return results;
    }
    
    private boolean isCountryResult(String type, String id, String name) {
        if ("ctry".equals(type) || "country".equals(type)) {
            return true;
        }
        if (id != null && id.length() == 3) {
            return true;
        }
        if (name != null) {
            String lowerName = name.toLowerCase();
            return "españa".equals(lowerName) || "spain".equals(lowerName);
        }
        return false;
    }
    
    private boolean hasSpecificSearch(GeocodingOptions options) {
        return options.getLocality() != null || 
               options.getStreet() != null || 
               options.getPostalCode() != null;
    }
}
