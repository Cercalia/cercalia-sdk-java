package com.cercalia.sdk.exception;

import org.jetbrains.annotations.Nullable;

/**
 * Base exception class for all Cercalia SDK errors.
 * <p>
 * This exception is thrown when an API request fails, either due to network
 * issues, invalid parameters, or specific error codes returned by the
 * Cercalia Web Services (e.g., "30006" for no candidates found).
 */
public class CercaliaException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    @Nullable
    private final String errorCode;
    
    /**
     * Creates a new {@link CercaliaException} with a message.
     *
     * @param message the error message.
     */
    public CercaliaException(String message) {
        super(message);
        this.errorCode = null;
    }
    
    /**
     * Creates a new {@link CercaliaException} with a message and error code.
     *
     * @param message   the error message.
     * @param errorCode the Cercalia error code.
     */
    public CercaliaException(String message, @Nullable String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Creates a new {@link CercaliaException} with a message and cause.
     *
     * @param message the error message.
     * @param cause   the underlying cause.
     */
    public CercaliaException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }
    
    /**
     * Creates a new {@link CercaliaException} with a message, error code, and cause.
     *
     * @param message   the error message.
     * @param errorCode the Cercalia error code.
     * @param cause     the underlying cause.
     */
    public CercaliaException(String message, @Nullable String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * Returns the Cercalia error code, if available.
     *
     * @return the error code, or {@code null} if not available.
     */
    @Nullable
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Checks if this exception represents a "no candidates found" error.
     * This is not a system error but indicates an empty result set.
     *
     * @return {@code true} if this is a "no candidates found" error.
     */
    public boolean isNoCandidatesFound() {
        return "30006".equals(errorCode);
    }
    
    /**
     * Checks if this exception represents a "no results found" error.
     * This is an alias for {@link #isNoCandidatesFound()} for consistency across services.
     *
     * @return {@code true} if this is a "no results found" error.
     */
    public boolean isNoResultsFound() {
        return "30006".equals(errorCode);
    }
}
