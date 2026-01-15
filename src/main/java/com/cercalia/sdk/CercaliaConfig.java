package com.cercalia.sdk;

import org.jetbrains.annotations.NotNull;

/**
 * Configuration for the Cercalia SDK.
 * <p>
 * Holds the necessary credentials (API key) and connection settings
 * (base URL) required to interact with Cercalia Web Services.
 * <p>
 * Typical instantiation:
 * <pre>{@code
 * CercaliaConfig config = new CercaliaConfig("YOUR_API_KEY");
 * // Or from environment variables CERCALIA_API_KEY
 * CercaliaConfig envConfig = CercaliaConfig.fromEnvironment();
 * }</pre>
 */
public final class CercaliaConfig {
    
    private static final String DEFAULT_BASE_URL = "https://lb.cercalia.com/services/v2/json";
    
    @NotNull
    private final String apiKey;
    
    @NotNull
    private final String baseUrl;
    
    /**
     * Creates a new CercaliaConfig with the specified API key and default base URL.
     *
     * @param apiKey the Cercalia API key
     */
    public CercaliaConfig(@NotNull String apiKey) {
        this(apiKey, DEFAULT_BASE_URL);
    }
    
    /**
     * Creates a new CercaliaConfig with the specified API key and base URL.
     *
     * @param apiKey  the Cercalia API key
     * @param baseUrl the base URL for API requests
     */
    public CercaliaConfig(@NotNull String apiKey, @NotNull String baseUrl) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Base URL cannot be null or empty");
        }
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }
    
    /**
     * Creates a CercaliaConfig from environment variables.
     * Uses CERCALIA_API_KEY and CERCALIA_BASE_URL (optional).
     *
     * @return a new CercaliaConfig instance
     * @throws IllegalStateException if CERCALIA_API_KEY is not set
     */
    public static CercaliaConfig fromEnvironment() {
        String apiKey = System.getenv("CERCALIA_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("CERCALIA_API_KEY environment variable is not set");
        }
        
        String baseUrl = System.getenv("CERCALIA_BASE_URL");
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            baseUrl = DEFAULT_BASE_URL;
        }
        
        return new CercaliaConfig(apiKey, baseUrl);
    }
    
    /**
     * Returns the API key.
     *
     * @return the API key
     */
    @NotNull
    public String getApiKey() {
        return apiKey;
    }
    
    /**
     * Returns the base URL.
     *
     * @return the base URL
     */
    @NotNull
    public String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * Returns the default base URL for Cercalia API.
     *
     * @return the default base URL
     */
    public static String getDefaultBaseUrl() {
        return DEFAULT_BASE_URL;
    }
    
    @Override
    public String toString() {
        return "CercaliaConfig{" +
                "apiKey='***'" +
                ", baseUrl='" + baseUrl + '\'' +
                '}';
    }
}
