package com.cercalia.sdk.util;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Retry utility for Cercalia API calls.
 * Implements silent retry logic with exponential backoff.
 */
public final class RetryUtil {
    
    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final long DEFAULT_DELAY_MS = 500;
    private static final double BACKOFF_MULTIPLIER = 1.5;
    
    private static final Logger logger = Logger.getInstance();
    
    private RetryUtil() {
        // Private constructor
    }
    
    /**
     * Options for retry behavior.
     */
    public static class RetryOptions {
        private int maxAttempts = DEFAULT_MAX_ATTEMPTS;
        private long delayMs = DEFAULT_DELAY_MS;
        private boolean logRetries = true;
        private String operationName = "operation";
        
        /**
         * @param maxAttempts maximum number of attempts.
         * @return this options instance.
         */
        public RetryOptions maxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }
        
        /**
         * @param delayMs delay between retries in milliseconds.
         * @return this options instance.
         */
        public RetryOptions delayMs(long delayMs) {
            this.delayMs = delayMs;
            return this;
        }
        
        /**
         * @param logRetries whether to log retry attempts.
         * @return this options instance.
         */
        public RetryOptions logRetries(boolean logRetries) {
            this.logRetries = logRetries;
            return this;
        }
        
        /**
         * @param operationName name of the operation for logging.
         * @return this options instance.
         */
        public RetryOptions operationName(String operationName) {
            this.operationName = operationName;
            return this;
        }
        
        /**
         * @return maximum number of attempts.
         */
        public int getMaxAttempts() { return maxAttempts; }

        /**
         * @return delay between retries in milliseconds.
         */
        public long getDelayMs() { return delayMs; }

        /**
         * @return whether to log retry attempts.
         */
        public boolean isLogRetries() { return logRetries; }

        /**
         * @return name of the operation for logging.
         */
        public String getOperationName() { return operationName; }
    }
    
    /**
     * Creates default retry options.
     *
     * @return a new {@link RetryOptions} instance.
     */
    public static RetryOptions options() {
        return new RetryOptions();
    }
    
    /**
     * Execute a callable with automatic retries.
     *
     * @param callable the callable to execute.
     * @param options  retry configuration options.
     * @param <T>      the return type.
     * @return the result of the callable if successful.
     * @throws Exception the last exception if all attempts fail.
     */
    public static <T> T withRetry(Callable<T> callable, RetryOptions options) throws Exception {
        Exception lastError = null;
        
        for (int attempt = 1; attempt <= options.getMaxAttempts(); attempt++) {
            try {
                return callable.call();
            } catch (Exception e) {
                lastError = e;
                
                if (options.isLogRetries()) {
                    logger.info("[Retry] %s attempt %d/%d failed: %s",
                            options.getOperationName(), attempt, options.getMaxAttempts(), e.getMessage());
                }
                
                if (attempt < options.getMaxAttempts()) {
                    long waitTime = (long) (options.getDelayMs() * Math.pow(BACKOFF_MULTIPLIER, attempt - 1));
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            }
        }
        
        throw lastError;
    }
    
    /**
     * Execute a callable with automatic retries using default options.
     *
     * @param callable      the callable to execute.
     * @param operationName the operation name for logging.
     * @param <T>           the return type.
     * @return the result of the callable if successful.
     * @throws Exception the last exception if all attempts fail.
     */
    public static <T> T withRetry(Callable<T> callable, String operationName) throws Exception {
        return withRetry(callable, options().operationName(operationName));
    }
    
    /**
     * Execute a supplier with automatic retries (RuntimeException only).
     *
     * @param supplier the supplier to execute.
     * @param options  retry configuration options.
     * @param <T>      the return type.
     * @return the result of the supplier if successful.
     * @throws RuntimeException the last exception if all attempts fail.
     */
    public static <T> T withRetryUnchecked(Supplier<T> supplier, RetryOptions options) {
        RuntimeException lastError = null;
        
        for (int attempt = 1; attempt <= options.getMaxAttempts(); attempt++) {
            try {
                return supplier.get();
            } catch (RuntimeException e) {
                lastError = e;
                
                if (options.isLogRetries()) {
                    logger.info("[Retry] %s attempt %d/%d failed: %s",
                            options.getOperationName(), attempt, options.getMaxAttempts(), e.getMessage());
                }
                
                if (attempt < options.getMaxAttempts()) {
                    long waitTime = (long) (options.getDelayMs() * Math.pow(BACKOFF_MULTIPLIER, attempt - 1));
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            }
        }
        
        throw lastError;
    }
    
    /**
     * Execute multiple alternative suppliers until one succeeds.
     *
     * @param alternatives list of suppliers to try in order.
     * @param options      retry configuration options.
     * @param <T>          the return type.
     * @return the result of the first successful supplier, or {@code null} if all fail.
     */
    public static <T> T withRetryAlternatives(List<Supplier<T>> alternatives, RetryOptions options) {
        RuntimeException lastError = null;
        
        for (int i = 0; i < alternatives.size(); i++) {
            try {
                T result = alternatives.get(i).get();
                if (result != null) {
                    return result;
                }
                if (options.isLogRetries() && i < alternatives.size() - 1) {
                    logger.info("[Retry] %s attempt %d/%d returned null, trying alternative",
                            options.getOperationName(), i + 1, alternatives.size());
                }
            } catch (RuntimeException e) {
                lastError = e;
                if (options.isLogRetries()) {
                    logger.info("[Retry] %s attempt %d/%d failed: %s",
                            options.getOperationName(), i + 1, alternatives.size(), e.getMessage());
                }
            }
        }
        
        if (lastError != null) {
            throw lastError;
        }
        return null;
    }
}
