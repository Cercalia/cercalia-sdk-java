package com.cercalia.sdk.model.routing;

/**
 * Vehicle type for routing calculations.
 * <p>
 * Different vehicle types use different road networks and have
 * different routing constraints (speed limits, road access, etc.).
 */
public enum VehicleType {
    /**
     * Standard automobile routing.
     * <p>
     * Uses road network suitable for cars, respecting speed limits
     * and vehicle restrictions.
     */
    CAR("car"),

    /**
     * Truck or heavy goods vehicle routing.
     * <p>
     * Uses road network suitable for trucks, taking into account:
     * <ul>
     *   <li>Maximum weight limits</li>
     *   <li>Maximum height/width restrictions</li>
     *   <li>Hazardous material restrictions</li>
     * </ul>
     */
    TRUCK("truck"),

    /**
     * Pedestrian walking routing.
     * <p>
     * Uses pedestrian pathways and sidewalks where available.
     * May use hiking trails for longer distances.
     */
    WALKING("walking");

    private final String value;

    VehicleType(String value) {
        this.value = value;
    }

    /**
     * Returns the Cercalia API value for this vehicle type.
     *
     * @return API value
     */
    public String getValue() {
        return value;
    }
}
