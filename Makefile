default: help

help: ## Display this help.
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make \033[36m<target>\033[0m\n"} /^[a-zA-Z_0-9-]+:.*?##/ { printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2 } /^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

run-with-compose-devservices: ## Launch application in Compose DevServices mode
	@./mvnw spring-boot:run

run-with-testcontainers-devservices: ## Launch application in TestContainers DevServices mode
	@./mvnw spring-boot:test-run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false

tests: ## Run All tests
	@./mvnw test
provide-fruit-quantity: ## Provision of fruit
	@curl -X POST http://localhost:8080/fruits -H "Content-Type: application/json" -d @./payloads/$(PAYLOAD)

sold-out-fruit-quantity: ## Sold out a certain quantity of fruit. KAFKA_PORT=<9092(compose) | 9093(testcontainers)>
	@kcat -P -b localhost:`tools/get-kafka-exposed-port.sh $(KAFKA_PORT)` -t fruits-sold-out ./payloads/$(PAYLOAD)

consume-provision-fruit-quantity: ## Consume Provision. KAFKA_PORT=<9092(compose) | 9093(testcontainers)>
	@kcat -C -b localhost:`tools/get-kafka-exposed-port.sh $(KAFKA_PORT)` -t fruits-provisioning

start-dependency-track: ## Start Dependency Track
	@docker compose -f compose.dtrack.yml up --wait

stop-dependency-track: ## Start Dependency Track
	@docker compose -f compose.dtrack.yml stop

upload-sbom-on-dependency-track: ## Upload SBOM on Dependency Track
	@curl -X POST http://localhost:8181/api/v1/bom \
  		-H "Content-Type: multipart/form-data" \
  		-H "X-API-Key: $(DTRACK_API_KEY)" \
  		-F "project=Test" \
  		-F "bom=@target/classes/META-INF/sbom/application.cdx.json"