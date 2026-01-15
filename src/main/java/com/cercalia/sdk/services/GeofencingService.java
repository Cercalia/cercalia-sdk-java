package com.cercalia.sdk.services;

import com.cercalia.sdk.CercaliaClient;
import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.exception.CercaliaException;
import com.cercalia.sdk.model.common.Coordinate;
import com.cercalia.sdk.model.geofencing.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.cercalia.sdk.util.CercaliaResponseParser.*;

/**
 * GeofencingService - Point-in-Polygon checks using Cercalia InsideGeoms API.
 * <p>
 * This service checks whether points are inside defined geographic zones (geofences).
 * Essential for delivery zone validation, fleet monitoring, and location-based alerts.
 * <p>
 * Example usage:
 * <pre>{@code
 * GeofencingService service = new GeofencingService(config);
 * 
 * // Define geofence zones
 * List<GeofenceShape> zones = Arrays.asList(
 *     GeofenceShape.circle("zone1", 2.17, 41.38, 1000), // 1km radius circle
 *     new GeofenceShape("zone2", "POLYGON((2.15 41.37, 2.19 41.37, 2.19 41.40, 2.15 41.40, 2.15 41.37))")
 * );
 * 
 * // Check points against zones
 * List<GeofencePoint> points = Arrays.asList(
 *     GeofencePoint.of("vehicle1", 41.385, 2.170),
 *     GeofencePoint.of("vehicle2", 41.400, 2.200)
 * );
 * 
 * GeofenceResult result = service.check(zones, points, GeofenceOptions.defaults());
 * }</pre>
 *
 * @see <a href="https://docs.cercalia.com/docs/cercalia-webservices/geofencing/">Cercalia Geofencing API</a>
 */
public class GeofencingService extends CercaliaClient {
    
    /**
     * Creates a new GeofencingService with the specified configuration.
     *
     * @param config the Cercalia configuration
     */
    public GeofencingService(@NotNull CercaliaConfig config) {
        super(config);
    }
    
    /**
     * Check which points are inside which geofence shapes.
     * <p>
     * Performs point-in-polygon checks for all combinations of shapes and points.
     * Returns only shapes that contain at least one point.
     *
     * @param shapes  array of geofence shapes (polygons or circles)
     * @param points  array of points to check
     * @param options optional coordinate system settings
     * @return result with matches (shapes containing points)
     * @throws CercaliaException if no shapes or points provided, or API error
     */
    @NotNull
    public GeofenceResult check(@NotNull List<GeofenceShape> shapes,
                                @NotNull List<GeofencePoint> points,
                                @NotNull GeofenceOptions options) {
        if (shapes == null || shapes.isEmpty()) {
            throw new CercaliaException("Geofencing requires at least one shape");
        }
        if (points == null || points.isEmpty()) {
            throw new CercaliaException("Geofencing requires at least one point");
        }
        
        Map<String, String> params = newParams("insidegeoms");
        
        // Build geoms parameter: [WKT|ID],[WKT|ID],...
        String geomsStr = buildGeomsString(shapes);
        params.put("geoms", geomsStr);
        
        // Build molist parameter: [lng,lat|ID],[lng,lat|ID],...
        String molistStr = buildMolistString(points);
        params.put("molist", molistStr);
        
        // Coordinate systems
        String srs = options.getShapeSrs() != null ? options.getShapeSrs() : "EPSG:4326";
        params.put("srs", srs);
        
        if (options.getPointSrs() != null) {
            params.put("mocs", options.getPointSrs());
        }
        
        JsonNode response = request(params, "Geofencing");
        return parseResponse(response, shapes.size(), points.size());
    }
    
    /**
     * Check which points are inside which geofence shapes asynchronously.
     *
     * @param shapes  array of geofence shapes
     * @param points  array of points to check
     * @param options optional coordinate system settings
     * @return CompletableFuture with the result
     */
    @NotNull
    public CompletableFuture<GeofenceResult> checkAsync(@NotNull List<GeofenceShape> shapes,
                                                         @NotNull List<GeofencePoint> points,
                                                         @NotNull GeofenceOptions options) {
        return CompletableFuture.supplyAsync(() -> check(shapes, points, options));
    }
    
    /**
     * Check if a single point is inside any of the given shapes.
     * <p>
     * Convenience method for single-point geofence checks.
     *
     * @param shapes array of geofence shapes
     * @param point  coordinate to check
     * @return list of shape IDs that contain the point
     */
    @NotNull
    public List<String> checkPoint(@NotNull List<GeofenceShape> shapes, @NotNull Coordinate point) {
        GeofenceResult result = check(
                shapes,
                Collections.singletonList(new GeofencePoint("point", point)),
                GeofenceOptions.defaults()
        );
        
        return result.getMatches().stream()
                .filter(m -> m.getPointsInside().stream().anyMatch(p -> "point".equals(p.getId())))
                .map(GeofenceMatch::getShapeId)
                .collect(Collectors.toList());
    }
    
    /**
     * Check if a single point is inside any of the given shapes asynchronously.
     *
     * @param shapes array of geofence shapes
     * @param point  coordinate to check
     * @return CompletableFuture with list of shape IDs that contain the point
     */
    @NotNull
    public CompletableFuture<List<String>> checkPointAsync(@NotNull List<GeofenceShape> shapes, 
                                                            @NotNull Coordinate point) {
        return CompletableFuture.supplyAsync(() -> checkPoint(shapes, point));
    }
    
    /**
     * Check if a point is inside a circular zone.
     * <p>
     * Convenience method for circular geofence checks.
     *
     * @param center       center of the circular zone
     * @param radiusMeters radius of the zone in meters
     * @param point        point to check
     * @return true if point is inside the circle
     */
    public boolean isInsideCircle(@NotNull Coordinate center, double radiusMeters, @NotNull Coordinate point) {
        String circleWkt = String.format("CIRCLE(%s %s, %s)", center.getLng(), center.getLat(), radiusMeters);
        List<String> zones = checkPoint(
                Collections.singletonList(new GeofenceShape("circle", circleWkt)),
                point
        );
        return zones.contains("circle");
    }
    
    /**
     * Check if a point is inside a circular zone asynchronously.
     *
     * @param center       center of the circular zone
     * @param radiusMeters radius of the zone in meters
     * @param point        point to check
     * @return CompletableFuture with true if point is inside the circle
     */
    @NotNull
    public CompletableFuture<Boolean> isInsideCircleAsync(@NotNull Coordinate center, 
                                                           double radiusMeters, 
                                                           @NotNull Coordinate point) {
        return CompletableFuture.supplyAsync(() -> isInsideCircle(center, radiusMeters, point));
    }
    
    /**
     * Check if a point is inside a polygon zone.
     * <p>
     * Convenience method for polygon geofence checks.
     *
     * @param polygonWkt polygon in WKT format
     * @param point      point to check
     * @return true if point is inside the polygon
     */
    public boolean isInsidePolygon(@NotNull String polygonWkt, @NotNull Coordinate point) {
        List<String> zones = checkPoint(
                Collections.singletonList(new GeofenceShape("polygon", polygonWkt)),
                point
        );
        return zones.contains("polygon");
    }
    
    /**
     * Check if a point is inside a polygon zone asynchronously.
     *
     * @param polygonWkt polygon in WKT format
     * @param point      point to check
     * @return CompletableFuture with true if point is inside the polygon
     */
    @NotNull
    public CompletableFuture<Boolean> isInsidePolygonAsync(@NotNull String polygonWkt, @NotNull Coordinate point) {
        return CompletableFuture.supplyAsync(() -> isInsidePolygon(polygonWkt, point));
    }
    
    /**
     * Filter points to only those inside a shape.
     * <p>
     * Useful for filtering a list of locations to only those within a service area.
     *
     * @param shape  single geofence shape
     * @param points array of points to filter
     * @return only points that are inside the shape
     */
    @NotNull
    public List<GeofencePoint> filterPointsInShape(@NotNull GeofenceShape shape, 
                                                    @NotNull List<GeofencePoint> points) {
        if (points.isEmpty()) {
            return Collections.emptyList();
        }
        
        GeofenceResult result = check(Collections.singletonList(shape), points, GeofenceOptions.defaults());
        
        Optional<GeofenceMatch> match = result.getMatches().stream()
                .filter(m -> m.getShapeId().equals(shape.getId()))
                .findFirst();
        
        if (!match.isPresent()) {
            return Collections.emptyList();
        }
        
        Set<String> insideIds = match.get().getPointsInside().stream()
                .map(GeofenceMatch.MatchedPoint::getId)
                .collect(Collectors.toSet());
        
        return points.stream()
                .filter(p -> insideIds.contains(p.getId()))
                .collect(Collectors.toList());
    }
    
    /**
     * Filter points to only those inside a shape asynchronously.
     *
     * @param shape  single geofence shape
     * @param points array of points to filter
     * @return CompletableFuture with only points that are inside the shape
     */
    @NotNull
    public CompletableFuture<List<GeofencePoint>> filterPointsInShapeAsync(@NotNull GeofenceShape shape, 
                                                                            @NotNull List<GeofencePoint> points) {
        return CompletableFuture.supplyAsync(() -> filterPointsInShape(shape, points));
    }
    
    /**
     * Create a circular geofence shape helper.
     *
     * @param id           unique identifier for the geofence
     * @param center       center coordinate
     * @param radiusMeters radius in meters
     * @return GeofenceShape object
     */
    @NotNull
    public GeofenceShape createCircle(@NotNull String id, @NotNull Coordinate center, double radiusMeters) {
        return GeofenceShape.circle(id, center.getLng(), center.getLat(), radiusMeters);
    }
    
    /**
     * Create a rectangular geofence shape helper.
     *
     * @param id        unique identifier for the geofence
     * @param southwest southwest corner coordinate
     * @param northeast northeast corner coordinate
     * @return GeofenceShape object with polygon WKT
     */
    @NotNull
    public GeofenceShape createRectangle(@NotNull String id, 
                                          @NotNull Coordinate southwest, 
                                          @NotNull Coordinate northeast) {
        return GeofenceShape.rectangle(id, 
                southwest.getLng(), southwest.getLat(), 
                northeast.getLng(), northeast.getLat());
    }
    
    // ============================================
    // PRIVATE HELPERS
    // ============================================
    
    /**
     * Build the geoms parameter string from shapes.
     * Format: [WKT|ID],[WKT|ID],...
     */
    private String buildGeomsString(List<GeofenceShape> shapes) {
        return shapes.stream()
                .map(s -> "[" + s.getWkt() + "|" + s.getId() + "]")
                .collect(Collectors.joining(","));
    }
    
    /**
     * Build the molist parameter string from points.
     * Format: [lng,lat|ID],[lng,lat|ID],...
     */
    private String buildMolistString(List<GeofencePoint> points) {
        return points.stream()
                .map(p -> "[" + p.getCoord().getLng() + "," + p.getCoord().getLat() + "|" + p.getId() + "]")
                .collect(Collectors.joining(","));
    }
    
    /**
     * Parse the Cercalia insidegeoms API response.
     * <p>
     * Response structure:
     * <pre>
     * {
     *   "insidegeoms": {
     *     "geometry": [{
     *       "@id": "zone1",
     *       "wkt": { "value": "CIRCLE(...)" },
     *       "molist": {
     *         "mo": [{ "@id": "P1", "coord": { "@x": "2.17", "@y": "41.38" } }]
     *       }
     *     }]
     *   }
     * }
     * </pre>
     */
    private GeofenceResult parseResponse(JsonNode response, int totalShapes, int totalPoints) {
        JsonNode insidegeoms = response.get("insidegeoms");
        if (insidegeoms == null || insidegeoms.isNull()) {
            // No matches - no shapes contain any points
            return GeofenceResult.empty(totalPoints, totalShapes);
        }
        
        JsonNode geometries = insidegeoms.get("geometry");
        if (geometries == null || geometries.isNull()) {
            return GeofenceResult.empty(totalPoints, totalShapes);
        }
        
        // Normalize to list
        List<JsonNode> geomList = new ArrayList<>();
        if (geometries.isArray()) {
            geometries.forEach(geomList::add);
        } else {
            geomList.add(geometries);
        }
        
        List<GeofenceMatch> matches = geomList.stream()
                .map(this::parseGeometry)
                .filter(GeofenceMatch::hasPointsInside)
                .collect(Collectors.toList());
        
        return GeofenceResult.builder()
                .matches(matches)
                .totalPointsChecked(totalPoints)
                .totalShapesChecked(totalShapes)
                .build();
    }
    
    /**
     * Parse a single geometry from the Cercalia response to a GeofenceMatch.
     */
    private GeofenceMatch parseGeometry(JsonNode g) {
        // Extract shape ID using standard helper
        String shapeId = getCercaliaAttr(g, "id");
        if (shapeId == null) shapeId = "";
        
        // Extract WKT - can be {value: "..."} or direct string
        String shapeWkt = "";
        JsonNode wktNode = g.get("wkt");
        if (wktNode != null) {
            String wkt = getCercaliaValue(wktNode);
            if (wkt != null) {
                shapeWkt = wkt;
            } else if (wktNode.isTextual()) {
                shapeWkt = wktNode.asText();
            }
        }
        
        List<GeofenceMatch.MatchedPoint> pointsInside = new ArrayList<>();
        
        // Parse molist (matched objects/points inside this geometry)
        JsonNode molist = g.get("molist");
        if (molist != null && !molist.isNull()) {
            JsonNode moNode = molist.get("mo");
            if (moNode != null && !moNode.isNull()) {
                List<JsonNode> moList = new ArrayList<>();
                if (moNode.isArray()) {
                    moNode.forEach(moList::add);
                } else {
                    moList.add(moNode);
                }
                
                for (JsonNode mo : moList) {
                    try {
                        pointsInside.add(parsePoint(mo));
                    } catch (CercaliaException e) {
                        logger.error("[GeofencingService] Failed to parse point: %s", e.getMessage());
                    }
                }
            }
        }
        
        return GeofenceMatch.builder()
                .shapeId(shapeId)
                .shapeWkt(shapeWkt)
                .pointsInside(pointsInside)
                .build();
    }
    
    /**
     * Parse a single point (mo) from the Cercalia response.
     * <p>
     * Following "Reglas de Oro":
     * - Strict coordinates: validate existence before parsing
     * - Use assertion (!) after validation rather than default values
     */
    private GeofenceMatch.MatchedPoint parsePoint(JsonNode mo) {
        String pointId = getCercaliaAttr(mo, "id");
        if (pointId == null) pointId = "";
        
        JsonNode coordNode = mo.get("coord");
        
        // Strict coordinate extraction - no default values (Golden Rule)
        if (coordNode == null || coordNode.isNull()) {
            throw new CercaliaException("Missing coordinate for point " + pointId);
        }
        
        String x = getCercaliaAttr(coordNode, "x");
        String y = getCercaliaAttr(coordNode, "y");
        
        if (x == null || y == null) {
            throw new CercaliaException("Invalid coordinates for point " + pointId + ": x=" + x + ", y=" + y);
        }
        
        double lng, lat;
        try {
            lng = Double.parseDouble(x);
            lat = Double.parseDouble(y);
        } catch (NumberFormatException e) {
            throw new CercaliaException("Cannot parse coordinates for point " + pointId + ": x=" + x + ", y=" + y);
        }
        
        return new GeofenceMatch.MatchedPoint(pointId, new Coordinate(lat, lng));
    }
}
