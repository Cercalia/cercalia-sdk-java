package com.cercalia.sdk.model.reversegeocoding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * SIGPAC (Agricultural parcel info - Spain only).
 */
public final class SigpacInfo {
    
    @NotNull
    private final String id;
    
    @NotNull
    private final String municipalityCode; // Note: keeping typo for 1:1 compatibility with TS SDK
    
    @NotNull
    private final String usage;
    
    private final double extensionHa;
    
    @Nullable
    private final String vulnerableType;
    
    @Nullable
    private final String vulnerableCode;
    
    private SigpacInfo(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id cannot be null");
        this.municipalityCode = Objects.requireNonNull(builder.municipalityCode, "municipalityCode cannot be null");
        this.usage = Objects.requireNonNull(builder.usage, "usage cannot be null");
        this.extensionHa = builder.extensionHa;
        this.vulnerableType = builder.vulnerableType;
        this.vulnerableCode = builder.vulnerableCode;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Returns the parcel ID.
     *
     * @return the parcel ID
     */
    @NotNull
    public String getId() {
        return id;
    }
    
    /**
     * Returns the municipality ID.
     * Note: Name preserved with typo for SDK compatibility.
     *
     * @return the municipality ID
     */
    @NotNull
    public String getMunicipalityCode() {
        return municipalityCode;
    }
    
    /**
     * Returns the land usage code.
     *
     * @return the usage code
     */
    @NotNull
    public String getUsage() {
        return usage;
    }
    
    /**
     * Returns the extension in hectares.
     *
     * @return the extension in Ha
     */
    public double getExtensionHa() {
        return extensionHa;
    }
    
    /**
     * Returns the vulnerable zone type.
     *
     * @return the vulnerable type
     */
    @Nullable
    public String getVulnerableType() {
        return vulnerableType;
    }
    
    /**
     * Returns the vulnerable zone code.
     *
     * @return the vulnerable code
     */
    @Nullable
    public String getVulnerableCode() {
        return vulnerableCode;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SigpacInfo that = (SigpacInfo) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "SigpacInfo{" +
                "id='" + id + '\'' +
                ", municipalityCode='" + municipalityCode + '\'' +
                ", usage='" + usage + '\'' +
                ", extensionHa=" + extensionHa +
                '}';
    }
    
    public static final class Builder {
        private String id = "";
        private String municipalityCode = "";
        private String usage = "";
        private double extensionHa;
        private String vulnerableType;
        private String vulnerableCode;
        
        private Builder() {}
        
        public Builder id(String id) { this.id = id; return this; }
        public Builder municipalityCode(String municipalityCode) { this.municipalityCode = municipalityCode; return this; }
        public Builder usage(String usage) { this.usage = usage; return this; }
        public Builder extensionHa(double extensionHa) { this.extensionHa = extensionHa; return this; }
        public Builder vulnerableType(String vulnerableType) { this.vulnerableType = vulnerableType; return this; }
        public Builder vulnerableCode(String vulnerableCode) { this.vulnerableCode = vulnerableCode; return this; }
        
        public SigpacInfo build() {
            return new SigpacInfo(this);
        }
    }
}
