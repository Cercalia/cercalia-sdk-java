package com.cercalia.sdk.model.staticmaps;

import org.jetbrains.annotations.NotNull;

/**
 * Base interface for static map shapes.
 */
public interface StaticMapShape {
    
    @NotNull
    StaticMapShapeType getType();
    
    @NotNull
    RGBAColor getOutlineColor();
    
    int getOutlineSize();
    
    @NotNull
    RGBAColor getFillColor();
    
    /**
     * Formats the shape for Cercalia API shape parameter.
     */
    @NotNull
    String format();
}
