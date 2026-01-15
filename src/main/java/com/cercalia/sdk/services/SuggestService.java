package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaClient;
import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.exception.CercaliaException;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.suggest.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service for address and POI autocomplete using Cercalia Suggest API.
 * <p>
 * This service provides real-time autocomplete suggestions for addresses,
 * streets, cities, and POIs. It's designed for typeahead search experiences.
 * <p>
 * <b>Key Features:</b>
 * <ul>
 *   <li>Street suggestions: Autocomplete street names with house number availability</li>
 *   <li>City suggestions: Find cities/localities by partial name</li>
 *   <li>POI suggestions: Search points of interest with category filtering</li>
 *   <li>Geocoding: Convert suggestions to precise coordinates</li>
 * </ul>
 * 
 * <pre>{@code
 * SuggestService service = new SuggestService(config);
 * 
 * // 1. Basic address autocomplete
 * List<SuggestResult> results = service.search(SuggestOptions.builder()
 *     .text("Provença 5")
 *     .build());
 * 
 * // 2. Filter by country and type
 * List<SuggestResult> streets = service.search(SuggestOptions.builder()
 *     .text("Gran Via")
 *     .countryCode("ESP")
 *     .geoType(SuggestGeoType.STREET)
 *     .build());
 * 
 * // 3. Geocode a specific result to get coordinates
 * if (!results.isEmpty()) {
 *     SuggestResult best = results.get(0);
 *     SuggestGeocodeResult coords = service.geocode(SuggestGeocodeOptions.builder()
 *         .cityCode(best.getCity().getCode())
 *         .streetCode(best.getStreet().getCode())
 *         .streetNumber("5")
 *         .build());
 * }
 * }</pre>
 * 
 * @see SuggestOptions
 * @see SuggestResult
 */
public class SuggestService extends CercaliaClient {
    
    /**
     * Base URL for Cercalia Suggest API (different from main services).
     */
    private static final String SUGGEST_BASE_URL = "https://lb.cercalia.com/suggest/SuggestServlet";
    
    /**
     * Creates a new SuggestService with the specified configuration.
     *
     * @param config the Cercalia configuration
     */
    public SuggestService(@NotNull CercaliaConfig config) {
        super(config);
    }
    
    // ========== Public API Methods ==========
    
    /**
     * Search for address/POI suggestions based on partial text input.
     * <p>
     * Use this method for autocomplete/typeahead functionality. Returns suggestions
     * ordered by relevance. Minimum 3 characters recommended for best results.
     *
     * @param options search configuration
     * @return list of suggestions ordered by relevance
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<SuggestResult> search(@NotNull SuggestOptions options) {
        if (options.getText() == null || options.getText().isEmpty()) {
            return Collections.emptyList();
        }
        
        Map<String, String> params = newParams();
        params.put("t", options.getText());
        
        // Geographic type filter
        if (options.getGeoType() != null) {
            params.put("getype", options.getGeoType().getValue());
        }
        
        // Geographic filters
        if (options.getCountryCode() != null) {
            params.put("ctryc", options.getCountryCode().toUpperCase());
        }
        addIfPresent(params, "regc", options.getRegionCode());
        addIfPresent(params, "subregc", options.getSubregionCode());
        addIfPresent(params, "munc", options.getMunicipalityCode());
        addIfPresent(params, "rsc", options.getStreetCode());
        addIfPresent(params, "rscp", options.getPostalCodePrefix());
        
        // Language
        addIfPresent(params, "lang", options.getLanguage());
        
        // Proximity search
        if (options.getCenter() != null) {
            params.put("pt", options.getCenter().getLat() + "," + options.getCenter().getLng());
            if (options.getRadius() != null) {
                params.put("d", String.valueOf(options.getRadius()));
            }
        }
        
        // POI categories
        if (options.getPoiCategories() != null && !options.getPoiCategories().isEmpty()) {
            params.put("poicat", String.join(",", options.getPoiCategories()));
        }
        
        try {
            JsonNode data = requestSuggest(params, "SuggestService");
            return parseSuggestResponse(data);
        } catch (Exception e) {
            logger.error("[SuggestService] Search error: %s", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Search for address/POI suggestions asynchronously.
     *
     * @param options search configuration
     * @return a CompletableFuture with the list of suggestions
     */
    @NotNull
    public CompletableFuture<List<SuggestResult>> searchAsync(@NotNull SuggestOptions options) {
        return CompletableFuture.supplyAsync(() -> search(options));
    }
    
    /**
     * Search for street suggestions only.
     * <p>
     * Convenience method for street-only autocomplete.
     *
     * @param text        search text
     * @param countryCode optional country code filter
     * @return list of street suggestions
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<SuggestResult> searchStreets(@NotNull String text, @Nullable String countryCode) {
        return search(SuggestOptions.builder()
                .text(text)
                .geoType(SuggestGeoType.ST)
                .countryCode(countryCode)
                .build());
    }
    
    /**
     * Search for street suggestions asynchronously.
     *
     * @param text        search text
     * @param countryCode optional country code filter
     * @return a CompletableFuture with the list of street suggestions
     */
    @NotNull
    public CompletableFuture<List<SuggestResult>> searchStreetsAsync(@NotNull String text, @Nullable String countryCode) {
        return CompletableFuture.supplyAsync(() -> searchStreets(text, countryCode));
    }
    
    /**
     * Search for city/locality suggestions only.
     * <p>
     * Convenience method for city-only autocomplete.
     *
     * @param text        search text
     * @param countryCode optional country code filter
     * @return list of city suggestions
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<SuggestResult> searchCities(@NotNull String text, @Nullable String countryCode) {
        return search(SuggestOptions.builder()
                .text(text)
                .geoType(SuggestGeoType.CT)
                .countryCode(countryCode)
                .build());
    }
    
    /**
     * Search for city/locality suggestions asynchronously.
     *
     * @param text        search text
     * @param countryCode optional country code filter
     * @return a CompletableFuture with the list of city suggestions
     */
    @NotNull
    public CompletableFuture<List<SuggestResult>> searchCitiesAsync(@NotNull String text, @Nullable String countryCode) {
        return CompletableFuture.supplyAsync(() -> searchCities(text, countryCode));
    }
    
    /**
     * Search for POI suggestions only.
     * <p>
     * Convenience method for POI-only autocomplete.
     *
     * @param text        search text
     * @param countryCode optional country code filter
     * @param center      optional center point for proximity search
     * @param radius      optional radius in meters
     * @param poiCategories optional POI category codes
     * @return list of POI suggestions
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public List<SuggestResult> searchPois(@NotNull String text, 
                                          @Nullable String countryCode,
                                          @Nullable Coordinate center,
                                          @Nullable Integer radius,
                                          @Nullable List<String> poiCategories) {
        SuggestOptions.Builder builder = SuggestOptions.builder()
                .text(text)
                .geoType(SuggestGeoType.POI)
                .countryCode(countryCode);
        
        if (center != null) {
            builder.center(center);
        }
        if (radius != null) {
            builder.radius(radius);
        }
        if (poiCategories != null) {
            builder.poiCategories(poiCategories);
        }
        
        return search(builder.build());
    }
    
    /**
     * Search for POI suggestions asynchronously.
     *
     * @param text        search text
     * @param countryCode optional country code filter
     * @param center      optional center point for proximity search
     * @param radius      optional radius in meters
     * @param poiCategories optional POI category codes
     * @return a CompletableFuture with the list of POI suggestions
     */
    @NotNull
    public CompletableFuture<List<SuggestResult>> searchPoisAsync(@NotNull String text,
                                                                   @Nullable String countryCode,
                                                                   @Nullable Coordinate center,
                                                                   @Nullable Integer radius,
                                                                   @Nullable List<String> poiCategories) {
        return CompletableFuture.supplyAsync(() -> searchPois(text, countryCode, center, radius, poiCategories));
    }
    
    /**
     * Geocode a suggestion to get precise coordinates.
     * <p>
     * After selecting a suggestion from {@link #search(SuggestOptions)}, use this method to get
     * the exact coordinates for the address. For streets, you can specify
     * a house number to get the precise location.
     *
     * @param options geocode options with codes from suggestion
     * @return geocoded result with coordinates and full address
     * @throws CercaliaException if geocoding fails or no results found
     */
    @NotNull
    public SuggestGeocodeResult geocode(@NotNull SuggestGeocodeOptions options) {
        Map<String, String> params = newParams();
        
        addIfPresent(params, "ctc", options.getCityCode());
        addIfPresent(params, "pcode", options.getPostalCode());
        addIfPresent(params, "stc", options.getStreetCode());
        addIfPresent(params, "stnum", options.getStreetNumber());
        
        if (options.getCountryCode() != null) {
            params.put("ctryc", options.getCountryCode().toUpperCase());
        }
        
        try {
            JsonNode data = requestSuggest(params, "SuggestGeocode");
            return parseGeocodeResponse(data);
        } catch (Exception e) {
            logger.error("[SuggestService] Geocode error: %s", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Geocode a suggestion asynchronously.
     *
     * @param options geocode options with codes from suggestion
     * @return a CompletableFuture with the geocoded result
     */
    @NotNull
    public CompletableFuture<SuggestGeocodeResult> geocodeAsync(@NotNull SuggestGeocodeOptions options) {
        return CompletableFuture.supplyAsync(() -> geocode(options));
    }
    
    /**
     * Combined search and geocode - finds and geocodes the best match.
     * <p>
     * This is a convenience method that combines search and geocode in one call.
     * Useful when you need coordinates directly from a text query.
     *
     * @param text         address text to search
     * @param countryCode  optional country code filter
     * @param streetNumber optional street number for geocoding
     * @return geocoded result of the best match, or null if no results
     * @throws CercaliaException if the request fails
     */
    @Nullable
    public SuggestGeocodeResult findAndGeocode(@NotNull String text, 
                                                @Nullable String countryCode,
                                                @Nullable String streetNumber) {
        List<SuggestResult> suggestions = search(SuggestOptions.builder()
                .text(text)
                .countryCode(countryCode)
                .build());
        
        if (suggestions.isEmpty()) {
            return null;
        }
        
        SuggestResult best = suggestions.get(0);
        
        // If suggestion already has coordinates, return them
        if (best.getCoord() != null) {
            return SuggestGeocodeResult.builder()
                    .coord(best.getCoord())
                    .formattedAddress(best.getDisplayText())
                    .name(best.getDisplayText())
                    .streetCode(best.getStreet() != null ? best.getStreet().getCode() : null)
                    .streetName(best.getStreet() != null ? best.getStreet().getName() : null)
                    .cityCode(best.getCity() != null ? best.getCity().getCode() : null)
                    .cityName(best.getCity() != null ? best.getCity().getName() : null)
                    .municipalityCode(best.getMunicipality() != null ? best.getMunicipality().getCode() : null)
                    .municipalityName(best.getMunicipality() != null ? best.getMunicipality().getName() : null)
                    .subregionCode(best.getSubregion() != null ? best.getSubregion().getCode() : null)
                    .subregionName(best.getSubregion() != null ? best.getSubregion().getName() : null)
                    .regionCode(best.getRegion() != null ? best.getRegion().getCode() : null)
                    .regionName(best.getRegion() != null ? best.getRegion().getName() : null)
                    .countryCode(best.getCountry() != null ? best.getCountry().getCode() : null)
                    .countryName(best.getCountry() != null ? best.getCountry().getName() : null)
                    .postalCode(best.getPostalCode())
                    .build();
        }
        
        // Otherwise geocode the suggestion
        return geocode(SuggestGeocodeOptions.builder()
                .streetCode(best.getStreet() != null ? best.getStreet().getCode() : null)
                .cityCode(best.getCity() != null ? best.getCity().getCode() : null)
                .streetNumber(streetNumber)
                .countryCode(best.getCountry() != null ? best.getCountry().getCode() : countryCode)
                .build());
    }
    
    /**
     * Combined search and geocode asynchronously.
     *
     * @param text         address text to search
     * @param countryCode  optional country code filter
     * @param streetNumber optional street number for geocoding
     * @return a CompletableFuture with the geocoded result, or null if no results
     */
    @NotNull
    public CompletableFuture<SuggestGeocodeResult> findAndGeocodeAsync(@NotNull String text,
                                                                        @Nullable String countryCode,
                                                                        @Nullable String streetNumber) {
        return CompletableFuture.supplyAsync(() -> findAndGeocode(text, countryCode, streetNumber));
    }
    
    // ========== Private Request Methods ==========
    
    /**
     * Custom request method for Suggest API (uses Solr JSON format, not standard Cercalia XML).
     */
    private JsonNode requestSuggest(Map<String, String> params, String operationName) {
        // Add API key
        params.put("key", config.getApiKey());
        
        // Build URL manually since we're using a different base URL
        StringBuilder urlBuilder = new StringBuilder(SUGGEST_BASE_URL);
        urlBuilder.append("?");
        
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                urlBuilder.append("&");
            }
            first = false;
            urlBuilder.append(entry.getKey()).append("=").append(encodeUrl(entry.getValue()));
        }
        
        String url = urlBuilder.toString();
        logger.debug("[%s] Request URL: %s", operationName, url);
        
        try {
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .get()
                    .build();
            
            try (okhttp3.Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    okhttp3.ResponseBody body = response.body();
                    String errorText = body != null ? body.string() : "No response body";
                    logger.error("[%s] HTTP Error %d: %s", operationName, response.code(), errorText);
                    throw new CercaliaException("Cercalia Suggest API error: " + response.code() + " " + response.message());
                }
                
                okhttp3.ResponseBody body = response.body();
                if (body == null) {
                    throw new CercaliaException("Empty response from Cercalia Suggest API");
                }
                
                String rawData = body.string();
                logger.debug("[%s] Response: %s...", operationName,
                        rawData.length() > 500 ? rawData.substring(0, 500) : rawData);
                
                // Parse JSON
                JsonNode rootNode;
                try {
                    rootNode = objectMapper.readTree(rawData);
                } catch (Exception e) {
                    logger.error("[%s] Invalid JSON response: %s", operationName, rawData);
                    throw new CercaliaException("Invalid JSON response from Suggest API", e);
                }
                
                // Validate Solr response format
                JsonNode responseNode = rootNode.get("response");
                if (responseNode == null || responseNode.isNull()) {
                    throw new CercaliaException("Invalid Solr response format: missing response object");
                }
                
                return rootNode;
            }
        } catch (CercaliaException e) {
            throw e;
        } catch (Exception e) {
            logger.error("[%s] Request failed: %s", operationName, e.getMessage());
            throw new CercaliaException("Request failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * URL encode a string value.
     */
    private String encodeUrl(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return value;
        }
    }
    
    // ========== Private Parsing Methods ==========
    
    /**
     * Parse the Suggest API response into normalized SuggestResult array.
     */
    private List<SuggestResult> parseSuggestResponse(JsonNode data) {
        // Check for API error status
        JsonNode headerNode = data.get("responseHeader");
        if (headerNode != null) {
            JsonNode statusNode = headerNode.get("status");
            if (statusNode != null && statusNode.asInt() != 0) {
                throw new CercaliaException("Cercalia Suggest API error: status " + statusNode.asInt());
            }
        }
        
        JsonNode responseNode = data.get("response");
        if (responseNode == null || responseNode.isNull()) {
            return Collections.emptyList();
        }
        
        JsonNode docsNode = responseNode.get("docs");
        if (docsNode == null || docsNode.isNull() || !docsNode.isArray() || docsNode.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<SuggestResult> results = new ArrayList<>();
        for (JsonNode doc : docsNode) {
            SuggestResult result = parseSuggestion(doc);
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * Parse a single suggestion document from Solr response.
     * <p>
     * Field mapping from API response (following GOLDEN RULES - direct 1:1 mapping):
     * <ul>
     *   <li>calle_id -> street.code</li>
     *   <li>calle_nombre -> street.name</li>
     *   <li>calle_descripcion -> street.description</li>
     *   <li>calle_tipo -> street.type</li>
     *   <li>calle_articulo -> street.article</li>
     *   <li>localidad_id -> city.code</li>
     *   <li>localidad_nombre -> city.name</li>
     *   <li>distrito_nombre -> city.bracketLocality</li>
     *   <li>municipio_id -> municipality.code</li>
     *   <li>municipio_nombre -> municipality.name</li>
     *   <li>provincia_id -> subregion.code</li>
     *   <li>provincia_nombre -> subregion.name</li>
     *   <li>comunidad_id -> region.code</li>
     *   <li>comunidad_nombre -> region.name</li>
     *   <li>pais_id -> country.code</li>
     *   <li>pais_nombre -> country.name</li>
     *   <li>codigo_postal -> postalCode</li>
     *   <li>coord -> coordinates (format: "lat,lng")</li>
     *   <li>portal_min, portal_max, portal, portal_disponible, portal_en -> houseNumbers</li>
     *   <li>oficial -> isOfficial</li>
     *   <li>score -> score</li>
     * </ul>
     */
    private SuggestResult parseSuggestion(JsonNode s) {
        String id = getStringValue(s, "id", "");
        
        // Build display text from available fields
        String displayText = buildDisplayText(s);
        
        // Determine type based on available fields
        SuggestResultType type = determineType(s);
        
        SuggestResult.Builder builder = SuggestResult.builder()
                .id(id)
                .displayText(displayText)
                .type(type);
        
        // Street information - DIRECT MAPPING from API fields
        String calleId = getStringValue(s, "calle_id");
        String calleNombre = getStringValue(s, "calle_nombre");
        String calleDescripcion = getStringValue(s, "calle_descripcion");
        
        if (calleId != null || calleNombre != null || calleDescripcion != null) {
            builder.street(SuggestStreet.builder()
                    .code(calleId)
                    .name(calleNombre)
                    .description(calleDescripcion)
                    .type(getStringValue(s, "calle_tipo"))
                    .article(getStringValue(s, "calle_articulo"))
                    .build());
        }
        
        // City/locality information - DIRECT MAPPING
        String localidadId = getStringValue(s, "localidad_id");
        String localidadNombre = getStringValue(s, "localidad_nombre");
        
        if (localidadId != null || localidadNombre != null) {
            builder.city(SuggestCity.builder()
                    .code(localidadId)
                    .name(localidadNombre)
                    .bracketLocality(getStringValue(s, "distrito_nombre"))
                    .build());
        }
        
        // Postal code - DIRECT MAPPING
        builder.postalCode(getStringValue(s, "codigo_postal"));
        
        // Municipality - DIRECT MAPPING
        String municipioId = getStringValue(s, "municipio_id");
        String municipioNombre = getStringValue(s, "municipio_nombre");
        
        if (municipioId != null || municipioNombre != null) {
            builder.municipality(SuggestAdminEntity.of(municipioId, municipioNombre));
        }
        
        // Subregion/Province - DIRECT MAPPING
        String provinciaId = getStringValue(s, "provincia_id");
        String provinciaNombre = getStringValue(s, "provincia_nombre");
        
        if (provinciaId != null || provinciaNombre != null) {
            builder.subregion(SuggestAdminEntity.of(provinciaId, provinciaNombre));
        }
        
        // Region (comunidad autónoma in Spain) - DIRECT MAPPING
        String comunidadId = getStringValue(s, "comunidad_id");
        String comunidadNombre = getStringValue(s, "comunidad_nombre");
        
        if (comunidadId != null || comunidadNombre != null) {
            builder.region(SuggestAdminEntity.of(comunidadId, comunidadNombre));
        }
        
        // Country - DIRECT MAPPING
        String paisId = getStringValue(s, "pais_id");
        String paisNombre = getStringValue(s, "pais_nombre");
        
        if (paisId != null || paisNombre != null) {
            builder.country(SuggestAdminEntity.of(paisId, paisNombre));
        }
        
        // Coordinates (format: "lat,lng" as string)
        String coordStr = getStringValue(s, "coord");
        if (coordStr != null && coordStr.contains(",")) {
            String[] parts = coordStr.split(",");
            if (parts.length == 2) {
                try {
                    double lat = Double.parseDouble(parts[0].trim());
                    double lng = Double.parseDouble(parts[1].trim());
                    builder.coord(new Coordinate(lat, lng));
                } catch (NumberFormatException ignored) {
                    // Skip invalid coordinates
                }
            }
        }
        
        // House numbers availability - DIRECT MAPPING of all portal fields
        Integer portalMin = getIntValue(s, "portal_min");
        Integer portalMax = getIntValue(s, "portal_max");
        Integer portal = getIntValue(s, "portal");
        Integer portalDisponible = getIntValue(s, "portal_disponible");
        Boolean portalEn = getBoolValue(s, "portal_en");
        
        if (portalMin != null || portalMax != null || portal != null || portalDisponible != null) {
            SuggestHouseNumbers.Builder hnBuilder = SuggestHouseNumbers.builder()
                    .available(portalMin != null || portalMax != null)
                    .min(portalMin)
                    .max(portalMax)
                    .current(portal)
                    .adjusted(portalDisponible)
                    .isEnglishFormat(portalEn);
            
            // Build hint from portal range
            if (portalMin != null && portalMax != null) {
                hnBuilder.hint(portalMin + "-" + portalMax);
            }
            
            builder.houseNumbers(hnBuilder.build());
        }
        
        // Official name flag
        String oficial = getStringValue(s, "oficial");
        if ("Y".equals(oficial)) {
            builder.isOfficial(true);
        }
        
        // Relevance score
        JsonNode scoreNode = s.get("score");
        if (scoreNode != null && scoreNode.isNumber()) {
            builder.score(scoreNode.asDouble());
        }
        
        // POI specific data
        if (type == SuggestResultType.POI) {
            String poiId = getStringValue(s, "poi_id");
            String poiName = getStringValue(s, "poi_name");
            String poiCat = getStringValue(s, "poi_cat");
            String categoryId = getStringValue(s, "category_id");
            String nombre = getStringValue(s, "nombre");
            
            if (poiId != null || poiName != null || poiCat != null || categoryId != null) {
                builder.poi(SuggestPoi.builder()
                        .code(poiId != null ? poiId : id)
                        .name(poiName != null ? poiName : (nombre != null ? nombre : displayText))
                        .categoryCode(poiCat != null ? poiCat : categoryId)
                        .build());
            }
        }
        
        return builder.build();
    }
    
    /**
     * Build a human-readable display text from suggestion fields.
     */
    private String buildDisplayText(JsonNode s) {
        List<String> parts = new ArrayList<>();
        
        // Street (use descripcion which includes type like "Carrer de Provença")
        String street = getStringValue(s, "calle_descripcion");
        if (street == null) {
            street = getStringValue(s, "calle_nombre");
        }
        if (street != null) {
            Integer number = getIntValue(s, "portal");
            if (number != null) {
                parts.add(street + ", " + number);
            } else {
                parts.add(street);
            }
        }
        
        // City/locality (with optional district)
        String city = getStringValue(s, "localidad_nombre");
        if (city != null) {
            String district = getStringValue(s, "distrito_nombre");
            if (district != null) {
                parts.add(city + " (" + district + ")");
            } else {
                parts.add(city);
            }
        }
        
        // Municipality (if different from city)
        String mun = getStringValue(s, "municipio_nombre");
        if (mun != null && !mun.equals(city)) {
            parts.add(mun);
        }
        
        // Province
        String prov = getStringValue(s, "provincia_nombre");
        if (prov != null) {
            parts.add(prov);
        }
        
        // Country
        String country = getStringValue(s, "pais_nombre");
        if (country != null) {
            parts.add(country);
        }
        
        // Fallback to name field or id
        if (parts.isEmpty()) {
            String nombre = getStringValue(s, "nombre");
            if (nombre != null) {
                return nombre;
            }
            String id = getStringValue(s, "id");
            return id != null ? id : "";
        }
        
        return String.join(", ", parts);
    }
    
    /**
     * Determine the type of suggestion based on available fields.
     */
    private SuggestResultType determineType(JsonNode s) {
        // POI type
        if (s.has("poi_id") || s.has("poi_cat") || s.has("category_id")) {
            return SuggestResultType.POI;
        }
        
        // Has street ID = street suggestion (main use case from Suggest API)
        if (s.has("calle_id") && !s.get("calle_id").isNull()) {
            // If has portal number, it's an address
            if (s.has("portal") && !s.get("portal").isNull()) {
                return SuggestResultType.ADDRESS;
            }
            return SuggestResultType.STREET;
        }
        
        // Has city/locality but no street = city suggestion
        boolean hasLocalidad = s.has("localidad_id") && !s.get("localidad_id").isNull();
        boolean hasCalle = (s.has("calle_id") && !s.get("calle_id").isNull()) ||
                          (s.has("calle_nombre") && !s.get("calle_nombre").isNull());
        
        if (hasLocalidad && !hasCalle) {
            return SuggestResultType.CITY;
        }
        
        // Default to street for results from Suggest API (most common case)
        if (s.has("calle_nombre") || s.has("calle_descripcion")) {
            return SuggestResultType.STREET;
        }
        
        // Default fallback
        return SuggestResultType.ADDRESS;
    }
    
    /**
     * Parse geocode response into normalized result.
     * <p>
     * GOLDEN RULES COMPLIANCE:
     * <ol>
     *   <li>Direct Mapping (No Administrative Fallbacks) - Maps fields 1:1 from API</li>
     *   <li>Code Integrity - Every administrative name field has its corresponding code</li>
     *   <li>Strict Coordinates - Coordinates are required for geocode results</li>
     * </ol>
     */
    private SuggestGeocodeResult parseGeocodeResponse(JsonNode data) {
        // Check for API error status
        JsonNode headerNode = data.get("responseHeader");
        if (headerNode != null) {
            JsonNode statusNode = headerNode.get("status");
            if (statusNode != null && statusNode.asInt() != 0) {
                throw new CercaliaException("Cercalia Suggest Geocode API error: status " + statusNode.asInt());
            }
        }
        
        JsonNode resp = data.get("response");
        if (resp == null || resp.isNull()) {
            throw new CercaliaException("Cercalia Suggest Geocode: No response in response");
        }
        
        // GOLDEN RULE #3: Extract coordinate (required - throw if missing)
        Coordinate coord = null;
        
        JsonNode coordNode = resp.get("coord");
        if (coordNode != null && !coordNode.isNull()) {
            if (coordNode.isTextual()) {
                // Format: "lat,lng" string
                String coordStr = coordNode.asText();
                if (coordStr.contains(",")) {
                    String[] parts = coordStr.split(",");
                    if (parts.length == 2) {
                        try {
                            double lat = Double.parseDouble(parts[0].trim());
                            double lng = Double.parseDouble(parts[1].trim());
                            coord = new Coordinate(lat, lng);
                        } catch (NumberFormatException ignored) {
                            // Continue to try other formats
                        }
                    }
                }
            } else if (coordNode.isObject()) {
                // Format: { "x": lng, "y": lat }
                JsonNode xNode = coordNode.get("x");
                JsonNode yNode = coordNode.get("y");
                if (xNode != null && yNode != null) {
                    try {
                        double lat = yNode.asDouble();
                        double lng = xNode.asDouble();
                        coord = new Coordinate(lat, lng);
                    } catch (Exception ignored) {
                        // Skip invalid format
                    }
                }
            }
        }
        
        if (coord == null) {
            throw new CercaliaException("Cercalia Suggest Geocode: No coordinates in response");
        }
        
        // Extract all fields from response - DIRECT MAPPING
        String desc = getStringValue(resp, "desc");
        String name = getStringValue(resp, "name");
        String housenumber = getStringValue(resp, "housenumber");
        String postalcode = getStringValue(resp, "postalcode");
        
        return SuggestGeocodeResult.builder()
                .coord(coord)
                .formattedAddress(desc != null ? desc : (name != null ? name : "Unknown address"))
                .name(name)
                .houseNumber(housenumber)
                .postalCode(postalcode)
                // Note: The basic geocode response doesn't include administrative codes
                // These would come from the suggestion that was geocoded
                .streetCode(null)
                .streetName(null)
                .cityCode(null)
                .cityName(null)
                .municipalityCode(null)
                .municipalityName(null)
                .subregionCode(null)
                .subregionName(null)
                .regionCode(null)
                .regionName(null)
                .countryCode(null)
                .countryName(null)
                .build();
    }
    
    // ========== Helper Methods ==========
    
    @Nullable
    private String getStringValue(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull()) {
            return null;
        }
        return fieldNode.asText();
    }
    
    @NotNull
    private String getStringValue(JsonNode node, String field, String defaultValue) {
        String value = getStringValue(node, field);
        return value != null ? value : defaultValue;
    }
    
    @Nullable
    private Integer getIntValue(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull()) {
            return null;
        }
        if (fieldNode.isNumber()) {
            return fieldNode.asInt();
        }
        if (fieldNode.isTextual()) {
            try {
                return Integer.parseInt(fieldNode.asText());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    @Nullable
    private Boolean getBoolValue(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull()) {
            return null;
        }
        if (fieldNode.isBoolean()) {
            return fieldNode.asBoolean();
        }
        return null;
    }
}
