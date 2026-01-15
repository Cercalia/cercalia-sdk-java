package com.cercalia.sdk.model.staticmaps;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a marker to be rendered on a static map.
 */
public final class StaticMapMarker {
    
    /**
     * Coordinate where the marker will be placed.
     */
    @NotNull
    private final Coordinate coord;
    
    /**
     * ID of the icon to be used for the marker.
     */
    @Nullable
    private final Integer icon;
    
    private StaticMapMarker(@NotNull Coordinate coord, @Nullable Integer icon) {
        this.coord = coord;
        this.icon = icon;
    }
    
    /**
     * @return Coordinate of the marker.
     */
    @NotNull
    public Coordinate getCoord() {
        return coord;
    }
    
    /**
     * @return Icon ID.
     */
    @Nullable
    public Integer getIcon() {
        return icon;
    }
    
    /**
     * Creates a marker at the specified coordinate.
     * @param coord Marker location.
     * @return A new marker.
     */
    @NotNull
    public static StaticMapMarker at(@NotNull Coordinate coord) {
        return new StaticMapMarker(coord, null);
    }
    
    /**
     * Creates a marker at the specified coordinate with an icon.
     * @param coord Marker location.
     * @param icon Icon ID.
     * @return A new marker.
     */
    @NotNull
    public static StaticMapMarker at(@NotNull Coordinate coord, int icon) {
        return new StaticMapMarker(coord, icon);
    }
    
    /**
     * Formats the marker for the Cercalia API {@code molist} parameter.
     * @return Formatted string.
     */
    @NotNull
    public String format() {
        String coordStr = coord.getLat() + "," + coord.getLng();
        if (icon != null) {
            return "[" + coordStr + "|" + icon + "]";
        }
        return "[" + coordStr + "]";
    }
}
