package com.cercalia.sdk.model.suggest;

import org.jetbrains.annotations.Nullable;

/**
 * Administrative entity (municipality, subregion, region, country).
 */
public final class SuggestAdminEntity {
    
    /**
     * Internal Cercalia ID for the administrative entity.
     */
    @Nullable
    private final String code;
    
    /**
     * Name of the administrative entity.
     */
    @Nullable
    private final String name;
    
    private SuggestAdminEntity(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    /**
     * Creates a new administrative entity instance.
     * @param code Internal Cercalia ID.
     * @param name Name of the entity.
     * @return A new instance.
     */
    public static SuggestAdminEntity of(@Nullable String code, @Nullable String name) {
        return new SuggestAdminEntity(code, name);
    }
    
    /**
     * @return Internal Cercalia ID for the administrative entity.
     */
    @Nullable public String getCode() { return code; }

    /**
     * @return Name of the administrative entity.
     */
    @Nullable public String getName() { return name; }
}
