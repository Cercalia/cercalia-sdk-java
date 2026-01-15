package com.cercalia.sdk;

import com.cercalia.sdk.exception.CercaliaException;
import com.cercalia.sdk.util.Logger;
import com.cercalia.sdk.util.RetryUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.cercalia.sdk.util.CercaliaResponseParser.getCercaliaAttr;
import static com.cercalia.sdk.util.CercaliaResponseParser.getCercaliaValue;

/**
 * Abstract base class for all Cercalia API services.
 * <p>
 * This class provides the core HTTP request-response cycle infrastructure,
 * including connection pooling, request authentication, retry mechanisms,
 * and standard Cercalia XML/JSON response parsing.
 * <p>
 * Subclasses implement specific Cercalia services by defining parameters
 * and mapping the results to high-level POJOs.
 */
public abstract class CercaliaClient {
    
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    
    protected final CercaliaConfig config;
    protected final OkHttpClient httpClient;
    protected final ObjectMapper objectMapper;
    protected final Logger logger;
    
    /**
     * Creates a new CercaliaClient with the specified configuration.
     *
     * @param config the Cercalia configuration
     */
    protected CercaliaClient(@NotNull CercaliaConfig config) {
        this.config = config;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        this.logger = Logger.getInstance();
    }
    
    /**
     * Creates a new CercaliaClient with a custom OkHttpClient.
     *
     * @param config     the Cercalia configuration
     * @param httpClient the custom HTTP client
     */
    protected CercaliaClient(@NotNull CercaliaConfig config, @NotNull OkHttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
        this.logger = Logger.getInstance();
    }
    
    /**
     * Returns the configuration for this client.
     *
     * @return the configuration
     */
    @NotNull
    public CercaliaConfig getConfig() {
        return config;
    }
    
    /**
     * Makes a synchronous request to the Cercalia API.
     *
     * @param params        the query parameters
     * @param operationName the operation name for logging
     * @return the "cercalia" node from the response
     * @throws CercaliaException if the request fails or returns an error
     */
    protected JsonNode request(@NotNull Map<String, String> params, @NotNull String operationName) {
        return request(params, operationName, null);
    }
    
    /**
     * Makes a synchronous request to the Cercalia API with a custom base URL.
     *
     * @param params        the query parameters
     * @param operationName the operation name for logging
     * @param baseUrl       the base URL (optional, uses config.baseUrl if null)
     * @return the "cercalia" node from the response
     * @throws CercaliaException if the request fails or returns an error
     */
    protected JsonNode request(@NotNull Map<String, String> params, @NotNull String operationName, String baseUrl) {
        try {
            return RetryUtil.withRetry(() -> executeRequest(params, operationName, baseUrl),
                    RetryUtil.options()
                            .operationName(operationName)
                            .maxAttempts(MAX_RETRY_ATTEMPTS)
                            .logRetries(true));
        } catch (CercaliaException e) {
            throw e;
        } catch (Exception e) {
            logger.error("[%s] Failed: %s", operationName, e.getMessage());
            throw new CercaliaException("Request failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Makes an asynchronous request to the Cercalia API.
     *
     * @param params        the query parameters
     * @param operationName the operation name for logging
     * @return a CompletableFuture with the "cercalia" node from the response
     */
    protected CompletableFuture<JsonNode> requestAsync(@NotNull Map<String, String> params, @NotNull String operationName) {
        return requestAsync(params, operationName, null);
    }
    
    /**
     * Makes an asynchronous request to the Cercalia API with a custom base URL.
     *
     * @param params        the query parameters
     * @param operationName the operation name for logging
     * @param baseUrl       the base URL (optional, uses config.baseUrl if null)
     * @return a CompletableFuture with the "cercalia" node from the response
     */
    protected CompletableFuture<JsonNode> requestAsync(@NotNull Map<String, String> params, 
                                                        @NotNull String operationName, 
                                                        String baseUrl) {
        return CompletableFuture.supplyAsync(() -> request(params, operationName, baseUrl));
    }
    
    /**
     * Executes a single HTTP request to the Cercalia API.
     */
    private JsonNode executeRequest(Map<String, String> params, String operationName, String baseUrl) 
            throws IOException {
        // Build URL with parameters
        String effectiveBaseUrl = baseUrl != null ? baseUrl : config.getBaseUrl();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(effectiveBaseUrl).newBuilder();
        
        // Add API key
        urlBuilder.addQueryParameter("key", config.getApiKey());
        
        // Add all other parameters
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        
        HttpUrl url = urlBuilder.build();
        logger.debug("[%s] Request URL: %s", operationName, url.toString());
        
        // Build and execute request
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                ResponseBody body = response.body();
                String errorText = body != null ? body.string() : "No response body";
                logger.error("[%s] HTTP Error %d: %s", operationName, response.code(), errorText);
                throw new CercaliaException("Cercalia API error: " + response.code() + " " + response.message());
            }
            
            ResponseBody body = response.body();
            if (body == null) {
                throw new CercaliaException("Empty response from Cercalia API");
            }
            
            String rawData = body.string();
            logger.debug("[%s] Response: %s...", operationName, 
                    rawData.length() > 500 ? rawData.substring(0, 500) : rawData);
            
            // Parse JSON
            JsonNode rootNode;
            try {
                rootNode = objectMapper.readTree(rawData);
            } catch (Exception e) {
                logger.error("[%s] Invalid JSON response: %s", operationName, rawData);
                throw new CercaliaException("Invalid JSON response from Cercalia API", e);
            }
            
            // Extract cercalia node
            JsonNode cercaliaNode = rootNode.get("cercalia");
            if (cercaliaNode == null || cercaliaNode.isNull()) {
                throw new CercaliaException("Invalid response format: missing 'cercalia' root property");
            }
            
            // Check for errors
            JsonNode errorNode = cercaliaNode.get("error");
            if (errorNode != null && !errorNode.isNull()) {
                String errorCode = getCercaliaAttr(errorNode, "id");
                String errorMsg = getCercaliaValue(errorNode);
                throw new CercaliaException(
                        String.format("Cercalia error [%s]: %s", errorCode, errorMsg),
                        errorCode);
            }
            
            return cercaliaNode;
        }
    }
    
    /**
     * Creates a new parameter map.
     *
     * @return a new HashMap for parameters
     */
    protected Map<String, String> newParams() {
        return new HashMap<>();
    }
    
    /**
     * Creates a new parameter map with the specified command.
     *
     * @param cmd the command
     * @return a new HashMap with the command set
     */
    protected Map<String, String> newParams(String cmd) {
        Map<String, String> params = new HashMap<>();
        params.put("cmd", cmd);
        return params;
    }
    
    /**
     * Adds a parameter to the map if the value is not null or empty.
     *
     * @param params the parameter map
     * @param key    the parameter key
     * @param value  the parameter value
     */
    protected void addIfPresent(Map<String, String> params, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            params.put(key, value);
        }
    }
    
    /**
     * Adds a parameter to the map if the value is not null.
     *
     * @param params the parameter map
     * @param key    the parameter key
     * @param value  the parameter value
     */
    protected void addIfPresent(Map<String, String> params, String key, Integer value) {
        if (value != null) {
            params.put(key, String.valueOf(value));
        }
    }
    
    /**
     * Adds a parameter to the map if the value is not null.
     *
     * @param params the parameter map
     * @param key    the parameter key
     * @param value  the parameter value
     */
    protected void addIfPresent(Map<String, String> params, String key, Double value) {
        if (value != null) {
            params.put(key, String.valueOf(value));
        }
    }
    
    /**
     * Adds a boolean parameter to the map if the value is true.
     *
     * @param params     the parameter map
     * @param key        the parameter key
     * @param value      the parameter value
     * @param trueValue  the string value to use when true
     */
    protected void addIfTrue(Map<String, String> params, String key, Boolean value, String trueValue) {
        if (Boolean.TRUE.equals(value)) {
            params.put(key, trueValue);
        }
    }
}
