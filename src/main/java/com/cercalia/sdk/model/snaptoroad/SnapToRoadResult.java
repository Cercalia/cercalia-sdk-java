package com.cercalia.sdk.model.snaptoroad;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Result from snap-to-road map matching.
 */
public final class SnapToRoadResult {
    
    /** Matched road segments. */
    private final List<SnapToRoadSegment> segments;
    /** Total distance of all segments in kilometers. */
    private final double totalDistance;
    
    private SnapToRoadResult(Builder builder) {
        this.segments = builder.segments != null 
                ? Collections.unmodifiableList(builder.segments) 
                : Collections.emptyList();
        this.totalDistance = builder.totalDistance;
    }
    
    /** @return Unmodifiable list of segments. */
    @NotNull
    public List<SnapToRoadSegment> getSegments() {
        return segments;
    }
    
    /** @return Total distance in km. */
    public double getTotalDistance() {
        return totalDistance;
    }
    
    /** @return True if segments exist. */
    public boolean hasSegments() {
        return !segments.isEmpty();
    }
    
    /** @return Segment count. */
    public int getSegmentCount() {
        return segments.size();
    }
    
    /** @return A new builder for {@link SnapToRoadResult}. */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }
    
    /** @return Empty result. */
    @NotNull
    public static SnapToRoadResult empty() {
        return builder().totalDistance(0).build();
    }
    
    @Override
    public String toString() {
        return "SnapToRoadResult{" +
                "segmentCount=" + segments.size() +
                ", totalDistance=" + totalDistance +
                '}';
    }
    
    /**
     * Builder for {@link SnapToRoadResult}.
     */
    public static final class Builder {
        private List<SnapToRoadSegment> segments;
        private double totalDistance;
        
        private Builder() {
        }
        
        /** @param segments Matched segments. @return This builder. */
        @NotNull
        public Builder segments(@NotNull List<SnapToRoadSegment> segments) {
            this.segments = segments;
            return this;
        }
        
        /** @param totalDistance Total distance in km. @return This builder. */
        @NotNull
        public Builder totalDistance(double totalDistance) {
            this.totalDistance = totalDistance;
            return this;
        }
        
        /** @return A new {@link SnapToRoadResult} instance. */
        @NotNull
        public SnapToRoadResult build() {
            return new SnapToRoadResult(this);
        }
    }
}
