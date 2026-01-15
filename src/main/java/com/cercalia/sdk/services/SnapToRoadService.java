package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaClient;
import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.exception.CercaliaException;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.snaptoroad.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.cercalia.sdk.util.CercaliaResponseParser.*;

/**
 * SnapToRoadService - GPS Track Map Matching using Cercalia Geomtrack API.
 * <p>
 * This service matches raw GPS coordinates to the road network, providing
 * "snapped" geometries that follow actual roads. Essential for fleet management,
 * vehicle tracking, and trip analysis applications.
 * 
 * <pre>{@code
 * SnapToRoadService service = new SnapToRoadService(config);
 * 
 * // 1. Basic map matching
 * List<SnapToRoadPoint> track = Arrays.asList(
 *     SnapToRoadPoint.of(41.3851, 2.1734),
 *     SnapToRoadPoint.of(41.3870, 2.1700),
 *     SnapToRoadPoint.of(41.3890, 2.1680)
 * );
 * SnapToRoadResult result = service.match(track, SnapToRoadOptions.defaults());
 * 
 * // 2. With speeding detection
 * SnapToRoadOptions options = SnapToRoadOptions.builder()
 *     .speeding(true)
 *     .speedTolerance(10) // km/h
 *     .build();
 * SnapToRoadResult speedingResult = service.match(track, options);
 * }</pre>
 *
 * @see <a href="https://docs.cercalia.com/docs/cercalia-webservices/geomtrack/">Cercalia Geomtrack API</a>
 */
public class SnapToRoadService extends CercaliaClient {
    
    /**
     * Creates a new SnapToRoadService with the specified configuration.
     *
     * @param config the Cercalia configuration
     */
    public SnapToRoadService(@NotNull CercaliaConfig config) {
        super(config);
    }
    
    /**
     * Match GPS track points to the road network.
     * <p>
     * Takes an array of GPS points and returns road-matched geometries.
     * Points should be in temporal order for best matching results.
     *
     * @param points  array of GPS track points (minimum 2 required)
     * @param options map matching options
     * @return matched road segments with distances and optional speeding info
     * @throws CercaliaException if fewer than 2 points provided or API error
     */
    @NotNull
    public SnapToRoadResult match(@NotNull List<SnapToRoadPoint> points, @NotNull SnapToRoadOptions options) {
        if (points == null || points.size() < 2) {
            throw new CercaliaException("SnapToRoad requires at least 2 GPS points");
        }
        
        Map<String, String> params = newParams("geomtrack");
        params.put("srs", "EPSG:4326");
        
        // Build track parameter
        String trackStr = buildTrackString(points);
        params.put("track", trackStr);
        
        // Weight type (distance or time)
        if (options.getWeight() != null) {
            params.put("weight", options.getWeight().getValue());
        }
        
        // Country network
        addIfPresent(params, "net", options.getNet());
        
        // Geometry coordinate system
        addIfPresent(params, "geometrysrs", options.getGeometrySrs());
        
        // Geometry simplification
        addIfPresent(params, "geometrytolerance", options.getGeometryTolerance());
        
        // Return original GPS points displaced on road
        addIfTrue(params, "points", options.getPoints(), "true");
        
        // Speeding detection
        if (Boolean.TRUE.equals(options.getSpeeding())) {
            params.put("speeding", "true");
            addIfPresent(params, "speedtolerance", options.getSpeedTolerance());
        }
        
        // Low-level control parameters
        addIfTrue(params, "onlytrack", options.getOnlyTrack(), "true");
        addIfPresent(params, "maxdirectionsearchdistance", options.getMaxDirectionSearchDistance());
        addIfPresent(params, "maxsearchdistance", options.getMaxSearchDistance());
        addIfPresent(params, "factor", options.getFactor());
        
        JsonNode response = request(params, "SnapToRoad");
        return parseResponse(response);
    }
    
    /**
     * Match GPS track points to the road network asynchronously.
     *
     * @param points  array of GPS track points (minimum 2 required)
     * @param options map matching options
     * @return CompletableFuture with matched road segments
     */
    @NotNull
    public CompletableFuture<SnapToRoadResult> matchAsync(@NotNull List<SnapToRoadPoint> points, 
                                                          @NotNull SnapToRoadOptions options) {
        return CompletableFuture.supplyAsync(() -> match(points, options));
    }
    
    /**
     * Match GPS track with automatic segment grouping by attribute.
     * <p>
     * Convenience method that automatically assigns attributes to points
     * for segment grouping. Useful for identifying distinct trip legs.
     *
     * @param coords    array of GPS coordinates
     * @param groupSize number of points per group (default: 10)
     * @param options   map matching options
     * @return matched segments grouped by attribute
     */
    @NotNull
    public SnapToRoadResult matchWithGroups(@NotNull List<Coordinate> coords, 
                                            int groupSize, 
                                            @NotNull SnapToRoadOptions options) {
        if (groupSize <= 0) {
            groupSize = 10;
        }
        
        List<SnapToRoadPoint> groupedPoints = new ArrayList<>(coords.size());
        for (int i = 0; i < coords.size(); i++) {
            char attr = (char) ('A' + (i / groupSize));
            groupedPoints.add(SnapToRoadPoint.builder()
                    .coord(coords.get(i))
                    .attribute(String.valueOf(attr))
                    .build());
        }
        
        return match(groupedPoints, options);
    }
    
    /**
     * Match GPS track with automatic segment grouping by attribute asynchronously.
     *
     * @param coords    array of GPS coordinates
     * @param groupSize number of points per group
     * @param options   map matching options
     * @return CompletableFuture with matched segments grouped by attribute
     */
    @NotNull
    public CompletableFuture<SnapToRoadResult> matchWithGroupsAsync(@NotNull List<Coordinate> coords, 
                                                                     int groupSize, 
                                                                     @NotNull SnapToRoadOptions options) {
        return CompletableFuture.supplyAsync(() -> matchWithGroups(coords, groupSize, options));
    }
    
    /**
     * Match GPS track with speed data for violation detection.
     * <p>
     * Convenience method that enables speeding detection with sensible defaults.
     *
     * @param points       array of GPS points with speed data
     * @param toleranceKmh speed tolerance in km/h (default: 10)
     * @return matched segments with speeding flags
     */
    @NotNull
    public SnapToRoadResult matchWithSpeedingDetection(@NotNull List<SnapToRoadPoint> points, 
                                                        int toleranceKmh) {
        SnapToRoadOptions options = SnapToRoadOptions.builder()
                .speeding(true)
                .speedTolerance(toleranceKmh)
                .build();
        return match(points, options);
    }
    
    /**
     * Match GPS track with speed data for violation detection asynchronously.
     *
     * @param points       array of GPS points with speed data
     * @param toleranceKmh speed tolerance in km/h
     * @return CompletableFuture with matched segments with speeding flags
     */
    @NotNull
    public CompletableFuture<SnapToRoadResult> matchWithSpeedingDetectionAsync(
            @NotNull List<SnapToRoadPoint> points, int toleranceKmh) {
        return CompletableFuture.supplyAsync(() -> matchWithSpeedingDetection(points, toleranceKmh));
    }
    
    /**
     * Get a simplified/generalized track matching.
     * <p>
     * Convenience method for getting a simplified geometry suitable for display.
     * Higher tolerance values produce simpler geometries with fewer points.
     *
     * @param points    array of GPS points
     * @param tolerance simplification tolerance in meters (default: 50)
     * @return matched segments with simplified geometries
     */
    @NotNull
    public SnapToRoadResult matchSimplified(@NotNull List<SnapToRoadPoint> points, int tolerance) {
        SnapToRoadOptions options = SnapToRoadOptions.builder()
                .geometryTolerance(tolerance)
                .build();
        return match(points, options);
    }
    
    /**
     * Get a simplified/generalized track matching asynchronously.
     *
     * @param points    array of GPS points
     * @param tolerance simplification tolerance in meters
     * @return CompletableFuture with matched segments with simplified geometries
     */
    @NotNull
    public CompletableFuture<SnapToRoadResult> matchSimplifiedAsync(@NotNull List<SnapToRoadPoint> points, 
                                                                     int tolerance) {
        return CompletableFuture.supplyAsync(() -> matchSimplified(points, tolerance));
    }
    
    // ============================================
    // PRIVATE HELPERS
    // ============================================
    
    /**
     * Build the track parameter string from GPS points.
     * <p>
     * Cercalia format: [lng,lat@compass,angle@@speed@@@attribute]
     * <p>
     * Each point is enclosed in brackets and contains:
     * <ul>
     *   <li>lng,lat (required)</li>
     *   <li>@compass,angle (optional - heading direction)</li>
     *   <li>@@speed (optional - speed in km/h)</li>
     *   <li>@@@attribute (optional - grouping identifier)</li>
     * </ul>
     * <p>
     * Example: [2.825850,41.969279@0,45@@70@@@A]
     */
    private String buildTrackString(List<SnapToRoadPoint> points) {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < points.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            
            SnapToRoadPoint p = points.get(i);
            sb.append("[");
            
            // Start with coordinates (lng,lat)
            sb.append(p.getCoord().getLng()).append(",").append(p.getCoord().getLat());
            
            // Add compass and angle if provided
            if (p.getCompass() != null && p.getAngle() != null) {
                sb.append("@").append(p.getCompass()).append(",").append(p.getAngle());
            } else if (p.getCompass() != null) {
                // If only compass is provided, use 0 for angle
                sb.append("@").append(p.getCompass()).append(",0");
            }
            
            // Add speed if provided
            if (p.getSpeed() != null) {
                sb.append("@@").append(p.getSpeed());
            }
            
            // Add attribute if provided
            if (p.getAttribute() != null) {
                sb.append("@@@").append(p.getAttribute());
            }
            
            sb.append("]");
        }
        
        return sb.toString();
    }
    
    /**
     * Parse the Cercalia geomtrack API response.
     * <p>
     * Response structure:
     * <pre>
     * {
     *   "cercalia": {
     *     "track": {
     *       "geometry": [{
     *         "@attribute": "A",
     *         "@distance": "0.97",
     *         "@speeding": "true",
     *         "@speedinglevel": "2",
     *         "wkt": { "value": "LINESTRING(...)" }
     *       }]
     *     }
     *   }
     * }
     * </pre>
     */
    private SnapToRoadResult parseResponse(JsonNode response) {
        JsonNode track = response.get("track");
        if (track == null || track.isNull()) {
            throw new CercaliaException("Cercalia SnapToRoad: No track data in response");
        }
        
        JsonNode geometries = track.get("geometry");
        if (geometries == null || geometries.isNull()) {
            return SnapToRoadResult.empty();
        }
        
        // Normalize to array
        List<JsonNode> geomList = new ArrayList<>();
        if (geometries.isArray()) {
            geometries.forEach(geomList::add);
        } else {
            geomList.add(geometries);
        }
        
        List<SnapToRoadSegment> segments = new ArrayList<>(geomList.size());
        double totalDistance = 0;
        
        for (JsonNode g : geomList) {
            SnapToRoadSegment segment = parseSegment(g);
            segments.add(segment);
            totalDistance += segment.getDistance();
        }
        
        return SnapToRoadResult.builder()
                .segments(segments)
                .totalDistance(totalDistance)
                .build();
    }
    
    /**
     * Parse a single geometry segment from the response.
     */
    private SnapToRoadSegment parseSegment(JsonNode g) {
        // Extract WKT - handle different response formats
        String wkt = extractWkt(g);
        if (wkt == null || wkt.isEmpty()) {
            throw new CercaliaException("Cercalia SnapToRoad: Missing WKT in segment");
        }
        
        // Parse distance
        String distanceStr = getCercaliaAttr(g, "distance");
        double distance = distanceStr != null ? parseDouble(distanceStr, 0) : 0;
        
        SnapToRoadSegment.Builder builder = SnapToRoadSegment.builder()
                .wkt(wkt)
                .distance(distance);
        
        // Attribute
        String attr = getCercaliaAttr(g, "attribute");
        if (attr != null) {
            builder.attribute(attr);
        }
        
        // Speeding flags
        String speeding = getCercaliaAttr(g, "speeding");
        if ("true".equals(speeding) || "1".equals(speeding)) {
            builder.speeding(true);
            
            String level = getCercaliaAttr(g, "speedinglevel");
            if (level != null) {
                builder.speedingLevel(parseInt(level, 0));
            }
        } else if (speeding != null) {
            builder.speeding(false);
        }
        
        return builder.build();
    }
    
    /**
     * Extract WKT from various possible locations in the response.
     */
    private String extractWkt(JsonNode g) {
        // Try wkt node
        JsonNode wktNode = g.get("wkt");
        if (wktNode != null) {
            String wkt = getCercaliaValue(wktNode);
            if (wkt != null && !wkt.isEmpty()) {
                return wkt;
            }
            // Try direct value
            if (wktNode.isTextual()) {
                return wktNode.asText();
            }
        }
        
        // Try geometry.wkt path
        JsonNode geometry = g.get("geometry");
        if (geometry != null && !geometry.isNull()) {
            JsonNode innerWkt = geometry.get("wkt");
            if (innerWkt != null) {
                String wkt = getCercaliaValue(innerWkt);
                if (wkt != null) return wkt;
            }
        }
        
        return null;
    }
    
    /**
     * Safely parse a double with a default value.
     */
    private double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Safely parse an integer with a default value.
     */
    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
