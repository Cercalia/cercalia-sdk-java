package com.cercalia.sdk.model.staticmaps;

/**
 * Types of shapes that can be drawn on a static map.
 */
public enum StaticMapShapeType {
    /** A circle defined by center and radius. */
    CIRCLE,
    /** A rectangle defined by two corners. */
    RECTANGLE,
    /** A sector (pie slice) of a circle. */
    SECTOR,
    /** A straight line between two points. */
    LINE,
    /** A sequence of connected line segments. */
    POLYLINE,
    /** A text label placed at a coordinate. */
    LABEL
}
