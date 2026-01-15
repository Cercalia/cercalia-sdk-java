package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaClient;
import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.exception.CercaliaException;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.staticmaps.*;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.cercalia.sdk.util.CercaliaResponseParser.*;

/**
 * StaticMapsService - Generates static map images with markers, shapes, and labels.
 * <p>
 * This service allows you to create custom map images centered on coordinates or cities,
 * with various overlays like markers, circles, polylines, and rectangles.
 * <p>
 * Example usage:
 * <pre>{@code
 * StaticMapsService service = new StaticMapsService(config);
 * 
 * // Simple map centered on a city
 * StaticMapResult result = service.generateCityMap("Barcelona", "ESP", 800, 600);
 * String url = result.getImageUrl();
 * 
 * // Complex map with markers and shapes
 * StaticMapOptions options = StaticMapOptions.builder()
 *     .center(new Coordinate(41.38, 2.17))
 *     .width(1024)
 *     .height(768)
 *     .addMarker(StaticMapMarker.builder(new Coordinate(41.38, 2.17)).label("Center").build())
 *     .addShape(StaticMapCircle.builder(new Coordinate(41.38, 2.17), 500).fillColor("AA0000").build())
 *     .build();
 * 
 * StaticMapResult complexResult = service.generateMap(options);
 * byte[] imageData = service.downloadImage(complexResult.getImageUrl());
 * }</pre>
 *
 * @see <a href="https://docs.cercalia.com/docs/cercalia-webservices/static-maps/">Cercalia Static Maps API</a>
 */
public class StaticMapsService extends CercaliaClient {
    
    private static final int MAX_WIDTH = 1680;
    private static final int MAX_HEIGHT = 1280;
    
    private final OkHttpClient httpClient;
    
    /**
     * Creates a new StaticMapsService with the specified configuration.
     *
     * @param config the Cercalia configuration
     */
    public StaticMapsService(@NotNull CercaliaConfig config) {
        super(config);
        this.httpClient = new OkHttpClient();
    }
    
    /**
     * Generate a static map with optional markers and shapes.
     *
     * @param options the map generation options
     * @return the generated map result
     * @throws CercaliaException if the request fails
     */
    @NotNull
    public StaticMapResult generateMap(@NotNull StaticMapOptions options) {
        String coordSystem = options.getCoordinateSystem() != null ? options.getCoordinateSystem() : "gdd";
        
        Map<String, String> params = newParams("map");
        params.put("mocs", coordSystem);
        params.put("cs", coordSystem);
        
        // Image dimensions
        if (options.getWidth() != null) {
            params.put("width", String.valueOf(Math.min(options.getWidth(), MAX_WIDTH)));
        }
        if (options.getHeight() != null) {
            params.put("height", String.valueOf(Math.min(options.getHeight(), MAX_HEIGHT)));
        }
        
        // Location by city name
        if (options.getCityName() != null) {
            params.put("ctn", options.getCityName());
        }
        if (options.getCountryCode() != null) {
            params.put("ctryc", options.getCountryCode());
        }
        
        // Map extent
        if (options.getExtent() != null) {
            params.put("extent", options.getExtent().format());
        }
        
        // Center coordinate
        if (options.getCenter() != null) {
            params.put("mo", options.getCenter().getLat() + "," + options.getCenter().getLng());
        }
        
        // Label options
        if (options.getLabelOp() != null) {
            params.put("labelop", String.valueOf(options.getLabelOp()));
        }
        
        // Mode
        params.put("mode", String.valueOf(options.getMode()));
        
        // Priority filter
        params.put("priorityfilter", String.valueOf(options.isPriorityfilter()));
        
        // Markers
        if (!options.getMarkers().isEmpty()) {
            params.put("molist", formatMarkers(options.getMarkers()));
        }
        
        // Shapes
        if (!options.getShapes().isEmpty()) {
            params.put("shape", formatShapes(options.getShapes()));
        }
        
        JsonNode response = request(params, "StaticMaps");
        
        // Handle candidates (city disambiguation)
        if (response.has("candidates") && !response.has("map")) {
            return handleCandidates(response, options);
        }
        
        // Parse normal map response
        return parseMapResponse(response, options.isReturnImage());
    }
    
    /**
     * Generate a static map asynchronously.
     *
     * @param options the map generation options
     * @return CompletableFuture with the result
     */
    @NotNull
    public CompletableFuture<StaticMapResult> generateMapAsync(@NotNull StaticMapOptions options) {
        return CompletableFuture.supplyAsync(() -> generateMap(options));
    }
    
    /**
     * Generate a map centered on a city with a label.
     *
     * @param cityName    the city name
     * @param countryCode the country code (e.g., "ESP")
     * @param width       optional width
     * @param height      optional height
     * @return the generated map result
     */
    @NotNull
    public StaticMapResult generateCityMap(@NotNull String cityName, @NotNull String countryCode,
                                            @Nullable Integer width, @Nullable Integer height) {
        return generateMap(StaticMapOptions.builder()
                .cityName(cityName)
                .countryCode(countryCode)
                .width(width)
                .height(height)
                .build());
    }
    
    /**
     * Generate a map centered on a city with default dimensions.
     */
    @NotNull
    public StaticMapResult generateCityMap(@NotNull String cityName, @NotNull String countryCode) {
        return generateCityMap(cityName, countryCode, null, null);
    }
    
    /**
     * Generate a map with a circle shape.
     *
     * @param center  the center coordinate
     * @param radius  the radius in meters
     * @param circle  optional circle style (uses defaults if null)
     * @param width   optional width
     * @param height  optional height
     * @return the generated map result
     */
    @NotNull
    public StaticMapResult generateMapWithCircle(@NotNull Coordinate center, int radius,
                                                   @Nullable StaticMapCircle circle,
                                                   @Nullable Integer width, @Nullable Integer height) {
        StaticMapCircle actualCircle = circle != null ? circle :
                StaticMapCircle.builder(center, radius).build();
        
        return generateMap(StaticMapOptions.builder()
                .center(center)
                .addShape(actualCircle)
                .width(width)
                .height(height)
                .coordinateSystem("gdd")
                .build());
    }
    
    /**
     * Generate a map with a circle shape using default style.
     */
    @NotNull
    public StaticMapResult generateMapWithCircle(@NotNull Coordinate center, int radius) {
        return generateMapWithCircle(center, radius, null, null, null);
    }
    
    /**
     * Generate a map with a rectangle shape.
     *
     * @param upperLeft  the upper left corner
     * @param lowerRight the lower right corner
     * @param rectangle  optional rectangle style
     * @param cityName   optional city name for centering
     * @param width      optional width
     * @param height     optional height
     * @return the generated map result
     */
    @NotNull
    public StaticMapResult generateMapWithRectangle(@NotNull Coordinate upperLeft, 
                                                      @NotNull Coordinate lowerRight,
                                                      @Nullable StaticMapRectangle rectangle,
                                                      @Nullable String cityName,
                                                      @Nullable Integer width, 
                                                      @Nullable Integer height) {
        StaticMapRectangle actualRect = rectangle != null ? rectangle :
                StaticMapRectangle.builder(upperLeft, lowerRight).build();
        
        return generateMap(StaticMapOptions.builder()
                .cityName(cityName)
                .addShape(actualRect)
                .width(width)
                .height(height)
                .build());
    }
    
    /**
     * Generate a map with a polyline.
     *
     * @param coordinates the polyline coordinates
     * @param polyline    optional polyline style
     * @param width       optional width
     * @param height      optional height
     * @return the generated map result
     */
    @NotNull
    public StaticMapResult generateMapWithPolyline(@NotNull List<Coordinate> coordinates,
                                                     @Nullable StaticMapPolyline polyline,
                                                     @Nullable Integer width,
                                                     @Nullable Integer height) {
        StaticMapPolyline actualPolyline = polyline != null ? polyline :
                StaticMapPolyline.builder(coordinates).build();
        
        // Calculate extent from coordinates
        double minLat = coordinates.stream().mapToDouble(Coordinate::getLat).min().orElse(0);
        double maxLat = coordinates.stream().mapToDouble(Coordinate::getLat).max().orElse(0);
        double minLng = coordinates.stream().mapToDouble(Coordinate::getLng).min().orElse(0);
        double maxLng = coordinates.stream().mapToDouble(Coordinate::getLng).max().orElse(0);
        double padding = 0.01;
        
        StaticMapExtent extent = StaticMapExtent.of(
                new Coordinate(maxLat + padding, minLng - padding),
                new Coordinate(minLat - padding, maxLng + padding)
        );
        
        return generateMap(StaticMapOptions.builder()
                .extent(extent)
                .addShape(actualPolyline)
                .width(width)
                .height(height)
                .coordinateSystem("gdd")
                .labelOp(0)
                .build());
    }
    
    /**
     * Generate a map with a line between two points.
     *
     * @param start  the start coordinate
     * @param end    the end coordinate
     * @param line   optional line style
     * @param width  optional width
     * @param height optional height
     * @return the generated map result
     */
    @NotNull
    public StaticMapResult generateMapWithLine(@NotNull Coordinate start, @NotNull Coordinate end,
                                                 @Nullable StaticMapLine line,
                                                 @Nullable Integer width,
                                                 @Nullable Integer height) {
        StaticMapLine actualLine = line != null ? line :
                StaticMapLine.builder(start, end).build();
        
        double padding = 0.01;
        double minLat = Math.min(start.getLat(), end.getLat());
        double maxLat = Math.max(start.getLat(), end.getLat());
        double minLng = Math.min(start.getLng(), end.getLng());
        double maxLng = Math.max(start.getLng(), end.getLng());
        
        StaticMapExtent extent = StaticMapExtent.of(
                new Coordinate(maxLat + padding, minLng - padding),
                new Coordinate(minLat - padding, maxLng + padding)
        );
        
        return generateMap(StaticMapOptions.builder()
                .extent(extent)
                .addShape(actualLine)
                .width(width)
                .height(height)
                .coordinateSystem("gdd")
                .build());
    }
    
    /**
     * Generate a map with markers.
     *
     * @param markers the markers to place
     * @param width   optional width
     * @param height  optional height
     * @return the generated map result
     */
    @NotNull
    public StaticMapResult generateMapWithMarkers(@NotNull List<StaticMapMarker> markers,
                                                    @Nullable Integer width,
                                                    @Nullable Integer height) {
        // Calculate extent from markers
        double minLat = markers.stream().mapToDouble(m -> m.getCoord().getLat()).min().orElse(0);
        double maxLat = markers.stream().mapToDouble(m -> m.getCoord().getLat()).max().orElse(0);
        double minLng = markers.stream().mapToDouble(m -> m.getCoord().getLng()).min().orElse(0);
        double maxLng = markers.stream().mapToDouble(m -> m.getCoord().getLng()).max().orElse(0);
        double padding = 0.01;
        
        StaticMapExtent extent = StaticMapExtent.of(
                new Coordinate(maxLat + padding, minLng - padding),
                new Coordinate(minLat - padding, maxLng + padding)
        );
        
        return generateMap(StaticMapOptions.builder()
                .extent(extent)
                .markers(markers)
                .width(width)
                .height(height)
                .coordinateSystem("gdd")
                .build());
    }
    
    /**
     * Generate a map with a label at a specific position.
     *
     * @param center the label position
     * @param text   the label text
     * @param width  optional width
     * @param height optional height
     * @return the generated map result
     */
    @NotNull
    public StaticMapResult generateMapWithLabel(@NotNull Coordinate center, @NotNull String text,
                                                  @Nullable Integer width, @Nullable Integer height) {
        StaticMapLabel label = StaticMapLabel.builder(center, text).build();
        
        return generateMap(StaticMapOptions.builder()
                .center(center)
                .addShape(label)
                .width(width)
                .height(height)
                .coordinateSystem("gdd")
                .build());
    }
    
    /**
     * Generate a map with a sector shape.
     *
     * @param center      the center coordinate
     * @param innerRadius the inner radius
     * @param outerRadius the outer radius
     * @param startAngle  the start angle in degrees
     * @param endAngle    the end angle in degrees
     * @param width       optional width
     * @param height      optional height
     * @return the generated map result
     */
    @NotNull
    public StaticMapResult generateMapWithSector(@NotNull Coordinate center,
                                                   int innerRadius, int outerRadius,
                                                   int startAngle, int endAngle,
                                                   @Nullable Integer width, @Nullable Integer height) {
        StaticMapSector sector = StaticMapSector.builder(center)
                .innerRadius(innerRadius)
                .outerRadius(outerRadius)
                .startAngle(startAngle)
                .endAngle(endAngle)
                .build();
        
        return generateMap(StaticMapOptions.builder()
                .center(center)
                .addShape(sector)
                .width(width)
                .height(height)
                .coordinateSystem("gdd")
                .build());
    }
    
    /**
     * Download the static map image as a byte array.
     *
     * @param imageUrl the image URL
     * @return the image bytes
     * @throws CercaliaException if download fails
     */
    @NotNull
    public byte[] downloadImage(@NotNull String imageUrl) {
        Request request = new Request.Builder()
                .url(imageUrl)
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new CercaliaException("Failed to download image: " + response.code() + " " + response.message());
            }
            if (response.body() == null) {
                throw new CercaliaException("Empty response body");
            }
            return response.body().bytes();
        } catch (IOException e) {
            throw new CercaliaException("Failed to download image: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate map and return image data directly.
     *
     * @param options the map generation options (returnImage is ignored)
     * @return the result with imageData populated
     */
    @NotNull
    public StaticMapResult generateMapAsImage(@NotNull StaticMapOptions options) {
        StaticMapResult result = generateMap(options);
        if (result.getImageUrl() != null) {
            byte[] imageData = downloadImage(result.getImageUrl());
            return StaticMapResult.builder()
                    .imageUrl(result.getImageUrl())
                    .imagePath(result.getImagePath())
                    .width(result.getWidth())
                    .height(result.getHeight())
                    .format(result.getFormat())
                    .scale(result.getScale())
                    .center(result.getCenter())
                    .extent(result.getExtent())
                    .label(result.getLabel())
                    .imageData(imageData)
                    .build();
        }
        return result;
    }
    
    // ========== Private helper methods ==========
    
    private StaticMapResult handleCandidates(JsonNode response, StaticMapOptions options) {
        JsonNode candidates = response.get("candidates");
        JsonNode candidateArray = candidates.get("candidate");
        
        if (candidateArray == null || candidateArray.isEmpty()) {
            throw new CercaliaException("No valid candidates found for city");
        }
        
        // Get first candidate
        JsonNode firstCandidate = getArrayElement(candidateArray, 0);
        if (firstCandidate == null) {
            throw new CercaliaException("Empty candidate in response");
        }
        
        // Find ctc parameter
        JsonNode urlparams = firstCandidate.get("urlparams");
        if (urlparams == null) {
            throw new CercaliaException("No urlparams in candidate");
        }
        
        JsonNode paramArray = urlparams.get("param");
        String ctc = null;
        int size = getArraySize(paramArray);
        for (int i = 0; i < size; i++) {
            JsonNode param = getArrayElement(paramArray, i);
            if (param != null && "ctc".equals(getCercaliaAttr(param, "name"))) {
                ctc = getCercaliaAttr(param, "value");
                break;
            }
        }
        
        if (ctc == null) {
            throw new CercaliaException("No ctc parameter found in candidate");
        }
        
        logger.info("[StaticMaps] Using candidate with ctc=%s", ctc);
        
        // Make new request with ctc parameter
        String coordSystem = options.getCoordinateSystem() != null ? options.getCoordinateSystem() : "gdd";
        Map<String, String> params = newParams("map");
        params.put("ctc", ctc);
        
        if (options.getCoordinateSystem() != null) {
            params.put("mocs", coordSystem);
            params.put("cs", coordSystem);
        }
        if (options.getWidth() != null) {
            params.put("width", String.valueOf(Math.min(options.getWidth(), MAX_WIDTH)));
        }
        if (options.getHeight() != null) {
            params.put("height", String.valueOf(Math.min(options.getHeight(), MAX_HEIGHT)));
        }
        if (!options.getShapes().isEmpty()) {
            params.put("shape", formatShapes(options.getShapes()));
        }
        if (!options.getMarkers().isEmpty()) {
            params.put("molist", formatMarkers(options.getMarkers()));
        }
        
        JsonNode retryResponse = request(params, "StaticMaps (Retry)");
        return parseMapResponse(retryResponse, options.isReturnImage());
    }
    
    private StaticMapResult parseMapResponse(JsonNode response, boolean downloadImage) {
        JsonNode map = response.get("map");
        if (map == null || map.isNull()) {
            throw new CercaliaException("No map data in response");
        }
        
        JsonNode img = map.get("img");
        if (img == null || img.isNull()) {
            throw new CercaliaException("No image data in response");
        }
        
        String href = getCercaliaAttr(img, "href");
        if (href == null) {
            throw new CercaliaException("No image href in response");
        }
        
        // Build full URL
        String baseUrl;
        try {
            baseUrl = new URL(config.getBaseUrl()).getProtocol() + "://" + new URL(config.getBaseUrl()).getHost();
        } catch (Exception e) {
            baseUrl = "https://lb.cercalia.com";
        }
        String imageUrl = baseUrl + href;
        
        // Parse dimensions
        Integer width = parseIntOrNull(getCercaliaAttr(img, "width"));
        Integer height = parseIntOrNull(getCercaliaAttr(img, "height"));
        String format = getCercaliaAttr(img, "format");
        Integer scale = parseIntOrNull(getCercaliaAttr(img, "scale"));
        
        // Parse center
        Coordinate center = null;
        String centerStr = getCercaliaAttr(img, "center");
        if (centerStr != null && centerStr.contains(",")) {
            String[] parts = centerStr.split(",");
            if (parts.length == 2) {
                try {
                    double lng = Double.parseDouble(parts[0]);
                    double lat = Double.parseDouble(parts[1]);
                    center = new Coordinate(lat, lng);
                } catch (NumberFormatException ignored) {}
            }
        }
        
        // Parse extent
        StaticMapExtent extent = null;
        JsonNode extentNode = img.get("extent");
        if (extentNode != null && extentNode.has("coord")) {
            JsonNode coordArray = extentNode.get("coord");
            if (coordArray != null && getArraySize(coordArray) >= 2) {
                JsonNode coord0 = getArrayElement(coordArray, 0);
                JsonNode coord1 = getArrayElement(coordArray, 1);
                if (coord0 != null && coord1 != null) {
                    Double x0 = parseDoubleOrNull(getCercaliaAttr(coord0, "x"));
                    Double y0 = parseDoubleOrNull(getCercaliaAttr(coord0, "y"));
                    Double x1 = parseDoubleOrNull(getCercaliaAttr(coord1, "x"));
                    Double y1 = parseDoubleOrNull(getCercaliaAttr(coord1, "y"));
                    if (x0 != null && y0 != null && x1 != null && y1 != null) {
                        extent = StaticMapExtent.of(
                                new Coordinate(y0, x0),
                                new Coordinate(y1, x1)
                        );
                    }
                }
            }
        }
        
        // Parse label
        String label = null;
        JsonNode labelNode = map.get("label");
        if (labelNode != null) {
            label = getCercaliaValue(labelNode);
        }
        
        StaticMapResult.Builder builder = StaticMapResult.builder()
                .imageUrl(imageUrl)
                .imagePath(href)
                .width(width)
                .height(height)
                .format(format)
                .scale(scale)
                .center(center)
                .extent(extent)
                .label(label);
        
        // Download image if requested
        if (downloadImage) {
            byte[] imageData = downloadImage(imageUrl);
            builder.imageData(imageData);
        }
        
        return builder.build();
    }
    
    private String formatMarkers(List<StaticMapMarker> markers) {
        return markers.stream()
                .map(StaticMapMarker::format)
                .collect(Collectors.joining(","));
    }
    
    private String formatShapes(List<StaticMapShape> shapes) {
        return shapes.stream()
                .map(StaticMapShape::format)
                .collect(Collectors.joining(","));
    }
    
    @Nullable
    private Double parseDoubleOrNull(@Nullable String value) {
        if (value == null) return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
