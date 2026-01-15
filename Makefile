# Cercalia SDK for Java - Makefile
# ================================
# 
# Common Maven tasks for development and testing.
# 
# Usage:
#   make help          - Show available commands
#   make build         - Compile the SDK
#   make test          - Run all tests
#   make test-single   - Run a single test (TEST=ServiceName)
#   make examples      - Run all examples
#   make example-XXX   - Run specific example (e.g., example-routing)

# Java settings - use sdkman's current Java if available
JAVA_HOME ?= $(shell echo $$HOME/.sdkman/candidates/java/current)
MVN = JAVA_HOME=$(JAVA_HOME) mvn

# Colors for output
GREEN  := \033[0;32m
YELLOW := \033[1;33m
CYAN   := \033[0;36m
NC     := \033[0m # No Color

.PHONY: help build compile test test-single clean package install docs examples \
        example-geocoding example-reversegeocoding example-suggest example-routing \
        example-poi example-isochrone example-proximity example-geoment \
        example-staticmaps example-snaptoroad example-geofencing example-all

## General Commands

help: ## Show this help
	@echo ""
	@echo "$(CYAN)Cercalia SDK for Java - Available Commands$(NC)"
	@echo "============================================"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "$(GREEN)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "$(YELLOW)Example usage:$(NC)"
	@echo "  make build              # Compile the SDK"
	@echo "  make test               # Run all tests"
	@echo "  make test-single TEST=RoutingServiceTest"
	@echo "  make example-routing    # Run routing example"
	@echo ""

## Build Commands

build: ## Compile the SDK (skip tests)
	@echo "$(CYAN)Building Cercalia SDK...$(NC)"
	$(MVN) compile -DskipTests

compile: build ## Alias for build

package: ## Create JAR package
	@echo "$(CYAN)Creating JAR package...$(NC)"
	$(MVN) package -DskipTests

install: ## Install to local Maven repository
	@echo "$(CYAN)Installing to local Maven repository...$(NC)"
	$(MVN) install -DskipTests

clean: ## Clean build artifacts
	@echo "$(CYAN)Cleaning build artifacts...$(NC)"
	$(MVN) clean

## Testing Commands

test: ## Run all tests
	@echo "$(CYAN)Running all tests...$(NC)"
	$(MVN) test

test-verbose: ## Run all tests with verbose output
	@echo "$(CYAN)Running all tests (verbose)...$(NC)"
	$(MVN) test -X

test-single: ## Run a single test class (usage: make test-single TEST=RoutingServiceTest)
ifndef TEST
	@echo "$(YELLOW)Usage: make test-single TEST=ServiceNameTest$(NC)"
	@echo "Available tests:"
	@ls -1 src/test/java/com/cercalia/sdk/services/*.java 2>/dev/null | xargs -I{} basename {} .java | sed 's/^/  - /'
else
	@echo "$(CYAN)Running test: $(TEST)...$(NC)"
	$(MVN) test -Dtest=$(TEST)
endif

## Documentation

docs: ## Generate Javadoc
	@echo "$(CYAN)Generating Javadoc...$(NC)"
	$(MVN) javadoc:javadoc

## Examples Commands

examples: example-all ## Alias for example-all

example-all: ## Run all examples
	@echo "$(CYAN)Running all examples...$(NC)"
	cd examples && $(MVN) compile exec:java -Dexec.mainClass="com.cercalia.examples.AllExamplesRunner"

example-geocoding: ## Run geocoding example
	@echo "$(CYAN)Running Geocoding example...$(NC)"
	cd examples && $(MVN) compile exec:java -Dexec.mainClass="com.cercalia.examples.GeocodingExample"

example-reversegeocoding: ## Run reverse geocoding example
	@echo "$(CYAN)Running Reverse Geocoding example...$(NC)"
	cd examples && $(MVN) compile exec:java -Dexec.mainClass="com.cercalia.examples.ReverseGeocodingExample"

example-suggest: ## Run suggest example
	@echo "$(CYAN)Running Suggest example...$(NC)"
	cd examples && $(MVN) compile exec:java -Dexec.mainClass="com.cercalia.examples.SuggestExample"

example-routing: ## Run routing example
	@echo "$(CYAN)Running Routing example...$(NC)"
	cd examples && $(MVN) compile exec:java -Dexec.mainClass="com.cercalia.examples.RoutingExample"

example-poi: ## Run POI example
	@echo "$(CYAN)Running POI example...$(NC)"
	cd examples && $(MVN) compile exec:java -Dexec.mainClass="com.cercalia.examples.PoiExample"

example-isochrone: ## Run isochrone example
	@echo "$(CYAN)Running Isochrone example...$(NC)"
	cd examples && $(MVN) compile exec:java -Dexec.mainClass="com.cercalia.examples.IsochroneExample"

example-proximity: ## Run proximity example
	@echo "$(CYAN)Running Proximity example...$(NC)"
	cd examples && $(MVN) compile exec:java -Dexec.mainClass="com.cercalia.examples.ProximityExample"

example-geoment: ## Run geoment example
	@echo "$(CYAN)Running Geoment example...$(NC)"
	cd examples && $(MVN) compile exec:java -Dexec.mainClass="com.cercalia.examples.GeomentExample"

example-staticmaps: ## Run static maps example
	@echo "$(CYAN)Running Static Maps example...$(NC)"
	cd examples && $(MVN) compile exec:java -Dexec.mainClass="com.cercalia.examples.StaticMapsExample"

example-snaptoroad: ## Run snap to road example
	@echo "$(CYAN)Running Snap to Road example...$(NC)"
	cd examples && $(MVN) compile exec:java -Dexec.mainClass="com.cercalia.examples.SnapToRoadExample"

example-geofencing: ## Run geofencing example
	@echo "$(CYAN)Running Geofencing example...$(NC)"
	cd examples && $(MVN) compile exec:java -Dexec.mainClass="com.cercalia.examples.GeofencingExample"

## Development Commands

check: ## Run linting and static analysis
	@echo "$(CYAN)Running checks...$(NC)"
	$(MVN) verify -DskipTests

format: ## Format code (if formatter plugin is configured)
	@echo "$(CYAN)Formatting code...$(NC)"
	$(MVN) formatter:format || echo "Formatter plugin not configured"

deps: ## Display dependency tree
	@echo "$(CYAN)Dependency tree:$(NC)"
	$(MVN) dependency:tree

deps-updates: ## Check for dependency updates
	@echo "$(CYAN)Checking for dependency updates...$(NC)"
	$(MVN) versions:display-dependency-updates

## Quick Commands

quick-test: build test ## Build and test

release: clean package ## Clean build and create package

all: clean build test package ## Full build cycle
