# Cercalia SDK for Java

The official, type-safe Java SDK for [Cercalia](https://www.cercalia.com/) web services. Built by [Nexus Geographics](https://www.nexusgeographics.com/), this SDK provides a robust, enterprise-grade interface for integrating Cercalia's geospatial capabilities into your Java applications.

[![Cercalia](https://img.shields.io/badge/Powered%20by-Cercalia-blue)](https://www.cercalia.com)
[![Nexus Geographics](https://img.shields.io/badge/Product%20of-Nexus%20Geographics-green)](https://www.nexusgeographics.com)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.cercalia/cercalia-sdk.svg)](https://search.maven.org/artifact/com.cercalia/cercalia-sdk)

## 🌟 About Cercalia

[**Cercalia**](https://www.cercalia.com/) is a comprehensive SaaS geospatial platform developed by [**Nexus Geographics**](https://www.nexusgeographics.com/), a leading company in geospatial technology and innovation. Cercalia provides enterprise-grade mapping, geocoding, routing, and location intelligence services, with exceptional coverage of European and American markets and advanced spatial analysis capabilities.
Trusted by leading enterprises across logistics, emergency services, fleet management, and smart city solutions, Cercalia delivers the precision and reliability that mission-critical applications demand.

### Why Choose Cercalia?

- **Global Coverage**: Based on TomTom content, enriched with OpenStreetMap data
- **European Leadership**: Unmatched data quality and coverage across all of Europe, with particular strength in Western, Central, and Southern regions—ideal for pan-European applications and businesses seeking reliable, up-to-date geospatial information
- **Live & Historical Traffic Data**: Global coverage of road incidents, congestion, closures, traffic-based ETAs, and routing with live or expected traffic
- **Enterprise-Ready**: Built for scale with high availability, performance SLAs, and dedicated support
- **Comprehensive Platform**: 12+ geospatial services accessible through modern, type-safe SDKs
- **Innovation Leader**: Powered by Nexus Geographics' 25+ years of GIS expertise

**Learn More:**
- 🌐 Official Website: [www.cercalia.com](https://www.cercalia.com)
- 📝 Sign Up: [clients.cercalia.com/register](https://clients.cercalia.com/register)
- 🏢 Nexus Geographics: [www.nexusgeographics.com](https://www.nexusgeographics.com)
- 🐦 Twitter: [@nexusgeographics](https://x.com/nexusgeographic)
- 💼 LinkedIn: [Nexus Geographics](https://www.linkedin.com/company/nexus-geographics/)

## ✨ Features

- **🎯 Type-Safe**: Strongly typed models with null-safety annotations for reliable code
- **☕ Java 8+ Compatible**: Supports Java 8, 11, 17, and 21 (LTS versions)
- **⚡ Async Support**: Built-in `CompletableFuture` support for non-blocking I/O
- **🔄 Comprehensive Services**: Access 11+ geospatial services
- **🛡️ Resilient**: Built-in retry logic and robust error handling
- **🧪 Well-Tested**: 186 tests across all services with high coverage

## 🚀 Installation

### Maven

```xml
<dependency>
    <groupId>com.cercalia</groupId>
    <artifactId>cercalia-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.cercalia:cercalia-sdk:1.0.0'
```

### Build from Source

```bash
git clone https://github.com/cercalia/cercalia-sdk-java.git
cd cercalia-sdk-java
make build
make install  # Install to local Maven repository
```

## 🔑 Getting Started

### 1. Get Your API Key

Register for a free Cercalia account and obtain your API key:

👉 **[Register here](https://clients.cercalia.com/register)**

### 2. Quick Example

```java
import com.cercalia.sdk.CercaliaConfig;
import com.cercalia.sdk.services.GeocodingService;
import com.cercalia.sdk.services.RoutingService;
import com.cercalia.sdk.model.geocoding.*;
import com.cercalia.sdk.model.routing.*;
import com.cercalia.sdk.model.common.Coordinate;
import java.util.List;

public class QuickStart {
    public static void main(String[] args) {
        // Configure the SDK
        CercaliaConfig config = new CercaliaConfig("YOUR_API_KEY_HERE");
        
        // Geocode an address
        GeocodingService geocoding = new GeocodingService(config);
        List<GeocodingCandidate> results = geocoding.geocode(
            GeocodingOptions.builder()
                .street("Paseo de la Castellana, 1")
                .locality("Madrid")
                .countryCode("ESP")
                .build()
        );
        
        GeocodingCandidate location = results.get(0);
        System.out.println("Found: " + location.getName());
        System.out.println("Coordinates: " + location.getCoord().getLat() + 
                          ", " + location.getCoord().getLng());
        
        // Calculate a route
        RoutingService routing = new RoutingService(config);
        RouteResult route = routing.calculateRoute(
            location.getCoord(),
            new Coordinate(41.387015, 2.170047)  // Barcelona
        );
        
        System.out.println("Distance: " + String.format("%.2f", route.getDistance() / 1000.0) + " km");
        System.out.println("Duration: " + (route.getDuration() / 60) + " minutes");
    }
}
```

## 🛠️ Available Services

| Service | Description | Class |
|---------|-------------|-------|
| **Geocoding** | Convert addresses to geographic coordinates | `GeocodingService` |
| **Reverse Geocoding** | Get addresses from coordinates | `ReverseGeocodingService` |
| **Routing** | Calculate optimal routes with turn-by-turn directions | `RoutingService` |
| **Suggest** | Autocomplete and place search suggestions | `SuggestService` |
| **POI Search** | Find Points of Interest near locations | `PoiService` |
| **Isochrones** | Calculate reachability areas (drive time/distance) | `IsochroneService` |
| **Proximity** | Distance calculations and nearest neighbor search | `ProximityService` |
| **Geofencing** | Point-in-polygon and spatial boundary operations | `GeofencingService` |
| **Static Maps** | Generate static map images | `StaticMapsService` |
| **Snap to Road** | Match GPS traces to road network | `SnapToRoadService` |
| **Geoment** | Geographic element queries and geometries | `GeomentService` |

## 📚 Documentation

- **📖 SDK API Reference (JavaDoc)**: [docs.cercalia.com/sdk/docs/java/](https://docs.cercalia.com/sdk/docs/java/)
- **📘 Official Cercalia API Docs**: [docs.cercalia.com/docs/](https://docs.cercalia.com/docs/)
- **💡 Examples**: Browse the [`examples/`](./examples) directory for runnable code samples

## 🧪 Development

The SDK includes a comprehensive Makefile for common development tasks:

```bash
# Build the SDK
make build

# Run all tests (186 tests)
make test

# Run a specific test
make test-single TEST=GeocodingServiceTest

# Run all examples
make examples

# Run a specific example
make example-routing

# Create JAR package
make package

# Install to local Maven repository
make install

# Generate Javadoc
make docs

# Clean build artifacts
make clean
```

## 🤝 Support & Community

Need help or have questions?

- **Documentation**: [docs.cercalia.com](https://docs.cercalia.com)
- **Support Portal**: Available through your Cercalia dashboard
- **Issues**: [GitHub Issues](https://github.com/cercalia/cercalia-sdk-java/issues)

## 📄 License

This SDK is provided for use with Cercalia web services. Please refer to your Cercalia service agreement for terms of use.

---

<p align="center">
  <strong>Built with ❤️ by <a href="https://www.nexusgeographics.com">Nexus Geographics</a></strong><br>
  <a href="https://www.cercalia.com">www.cercalia.com</a> • 
  <a href="https://x.com/nexusgeographic">Twitter</a> • 
  <a href="https://www.linkedin.com/company/nexus-geographics/">LinkedIn</a>
</p>
