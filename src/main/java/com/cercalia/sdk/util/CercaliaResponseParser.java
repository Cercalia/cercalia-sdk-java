package com.cercalia.sdk.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for parsing Cercalia API JSON responses.
 * <p>
 * Cercalia's JSON format has several idiosyncrasies due to its XML heritage:
 * <ul>
 *   <li>Attributes are prefixed with '@' (e.g., "@id").</li>
 *   <li>Values can be primitive strings or objects with keys like "value" or "$valor".</li>
 *   <li>Single-item lists are often returned as objects instead of arrays.</li>
 * </ul>
 * This class provides standard helpers to abstract away these inconsistencies.
 */
public final class CercaliaResponseParser {
    
    private CercaliaResponseParser() {
        // Private constructor
    }
    
    /**
     * Extract attribute value from Cercalia object.
     * Handles both @attr and attr formats.
     *
     * @param node the JSON node to extract from.
     * @param key  the attribute key (without @ prefix).
     * @return the attribute value, or {@code null} if not found.
     */
    @Nullable
    public static String getCercaliaAttr(@Nullable JsonNode node, String key) {
        if (node == null || node.isNull()) {
            return null;
        }
        
        // Try with @ prefix first
        JsonNode attrNode = node.get("@" + key);
        if (attrNode != null && !attrNode.isNull()) {
            return attrNode.asText();
        }
        
        // Try without prefix
        attrNode = node.get(key);
        if (attrNode != null && !attrNode.isNull() && attrNode.isValueNode()) {
            return attrNode.asText();
        }
        
        return null;
    }
    
    /**
     * Extract value from Cercalia value object.
     * Handles multiple formats: { "value": "..." }, { "$valor": "..." }, { "@value": "..." }, or plain string.
     *
     * @param node the JSON node to extract from.
     * @return the value, or {@code null} if not found.
     */
    @Nullable
    public static String getCercaliaValue(@Nullable JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        
        // If it's a plain string
        if (node.isValueNode()) {
            return node.asText();
        }
        
        // Try different value formats
        String[] valueKeys = {"$valor", "value", "@value"};
        for (String key : valueKeys) {
            JsonNode valueNode = node.get(key);
            if (valueNode != null && !valueNode.isNull()) {
                return valueNode.asText();
            }
        }
        
        return null;
    }
    
    /**
     * Parse a coordinate value from string to double.
     * Throws an exception if the value is null (strict coordinates rule).
     *
     * @param value the string value to parse.
     * @param name  the coordinate name (for error messages).
     * @return the parsed double value.
     * @throws IllegalArgumentException if value is null or invalid.
     */
    public static double parseCoordinate(@Nullable String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " coordinate cannot be null or empty");
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + name + " coordinate: " + value, e);
        }
    }
    
    /**
     * Safely parse a double value, returning null if parsing fails.
     *
     * @param value the string value to parse.
     * @return the parsed Double, or {@code null} if parsing fails.
     */
    @Nullable
    public static Double parseDoubleOrNull(@Nullable String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Safely parse an integer value, returning null if parsing fails.
     *
     * @param value the string value to parse.
     * @return the parsed Integer, or {@code null} if parsing fails.
     */
    @Nullable
    public static Integer parseIntOrNull(@Nullable String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Safely parse a long value, returning null if parsing fails.
     *
     * @param value the string value to parse.
     * @return the parsed Long, or {@code null} if parsing fails.
     */
    @Nullable
    public static Long parseLongOrNull(@Nullable String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Check if a JSON node represents an array or should be treated as one.
     *
     * @param node the JSON node to check.
     * @return {@code true} if the node is an array or a single object.
     */
    public static boolean isArrayOrSingle(@Nullable JsonNode node) {
        return node != null && !node.isNull();
    }
    
    /**
     * Get the size of an array node, treating single objects as arrays of size 1.
     *
     * @param node the JSON node.
     * @return the array size, or 0 if null.
     */
    public static int getArraySize(@Nullable JsonNode node) {
        if (node == null || node.isNull()) {
            return 0;
        }
        if (node.isArray()) {
            return node.size();
        }
        return 1; // Single object treated as array of 1
    }
    
    /**
     * Get an element from an array node at the specified index.
     * Handles both array nodes and single object nodes.
     *
     * @param node  the JSON node.
     * @param index the index.
     * @return the element at the index, or {@code null} if out of bounds.
     */
    @Nullable
    public static JsonNode getArrayElement(@Nullable JsonNode node, int index) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isArray()) {
            return index < node.size() ? node.get(index) : null;
        }
        return index == 0 ? node : null; // Single object treated as array of 1
    }
}
