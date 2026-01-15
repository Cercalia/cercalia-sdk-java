package com.cercalia.sdk.model.staticmaps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an RGBA color for static map shapes.
 */
public final class RGBAColor {
    
    /** Red component (0-255). */
    private final int r;
    /** Green component (0-255). */
    private final int g;
    /** Blue component (0-255). */
    private final int b;
    /** Alpha component (0-255), optional. */
    @Nullable
    private final Integer a;
    
    private RGBAColor(int r, int g, int b, @Nullable Integer a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
    
    /** @return Red component. */
    public int getR() {
        return r;
    }
    
    /** @return Green component. */
    public int getG() {
        return g;
    }
    
    /** @return Blue component. */
    public int getB() {
        return b;
    }
    
    /** @return Alpha component. */
    @Nullable
    public Integer getA() {
        return a;
    }
    
    /**
     * Creates an RGB color without alpha.
     * @param r Red (0-255).
     * @param g Green (0-255).
     * @param b Blue (0-255).
     * @return A new color.
     */
    @NotNull
    public static RGBAColor rgb(int r, int g, int b) {
        return new RGBAColor(r, g, b, null);
    }
    
    /**
     * Creates an RGBA color with alpha.
     * @param r Red (0-255).
     * @param g Green (0-255).
     * @param b Blue (0-255).
     * @param a Alpha (0-255).
     * @return A new color.
     */
    @NotNull
    public static RGBAColor rgba(int r, int g, int b, int a) {
        return new RGBAColor(r, g, b, a);
    }
    
    /**
     * Creates a red color.
     * @return Red color.
     */
    @NotNull
    public static RGBAColor red() {
        return rgb(255, 0, 0);
    }
    
    /**
     * Creates a green color.
     * @return Green color.
     */
    @NotNull
    public static RGBAColor green() {
        return rgb(0, 255, 0);
    }
    
    /**
     * Creates a blue color.
     * @return Blue color.
     */
    @NotNull
    public static RGBAColor blue() {
        return rgb(0, 0, 255);
    }
    
    /**
     * Creates a red color with alpha.
     * @param alpha Alpha value (0-255).
     * @return Red color with alpha.
     */
    @NotNull
    public static RGBAColor red(int alpha) {
        return rgba(255, 0, 0, alpha);
    }
    
    /**
     * Creates a green color with alpha.
     * @param alpha Alpha value (0-255).
     * @return Green color with alpha.
     */
    @NotNull
    public static RGBAColor green(int alpha) {
        return rgba(0, 255, 0, alpha);
    }
    
    /**
     * Formats the color for Cercalia API: {@code "R,G,B,A"} or {@code "R,G,B"}.
     * @return Formatted string.
     */
    @NotNull
    public String format() {
        if (a != null) {
            return r + "," + g + "," + b + "," + a;
        }
        return r + "," + g + "," + b;
    }
    
    @Override
    public String toString() {
        return format();
    }
}
