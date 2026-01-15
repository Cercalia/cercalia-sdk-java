package com.cercalia.sdk.model.proximity;

import com.cercalia.sdk.model.common.Coordinate;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Result of a proximity search.
 */
public final class ProximityResult {
    
    @NotNull
    private final List<ProximityItem> items;
    
    @NotNull
    private final Coordinate center;
    
    private final int totalFound;
    
    public ProximityResult(@NotNull List<ProximityItem> items, @NotNull Coordinate center, int totalFound) {
        this.items = Objects.requireNonNull(items, "items cannot be null");
        this.center = Objects.requireNonNull(center, "center cannot be null");
        this.totalFound = totalFound;
    }
    
    /**
     * @return The list of items found in proximity.
     */
    @NotNull
    public List<ProximityItem> getItems() {
        return items;
    }
    
    /**
     * @return The center coordinate used for the search.
     */
    @NotNull
    public Coordinate getCenter() {
        return center;
    }
    
    /**
     * @return The total number of items found in the API.
     */
    public int getTotalFound() {
        return totalFound;
    }
    
    @Override
    public String toString() {
        return "ProximityResult{" +
                "items=" + items.size() +
                ", center=" + center +
                ", totalFound=" + totalFound +
                '}';
    }
}
