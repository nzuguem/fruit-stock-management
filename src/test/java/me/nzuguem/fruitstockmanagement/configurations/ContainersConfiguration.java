package me.nzuguem.fruitstockmanagement.configurations;

import io.github.microcks.testcontainers.MicrocksContainersEnsemble;
import io.github.microcks.testcontainers.connection.KafkaConnection;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.*;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfiguration {

    private static final Network NETWORK = Network.newNetwork();

    private KafkaContainer kafkaContainer;

    @Bean
    ContainerLogsBeanPostProcessor containerLogsBeanPostProcessor() {
        return new ContainerLogsBeanPostProcessor();
    }

    @Bean
    @ServiceConnection("postgres")
    PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>("postgres:15.6-alpine")
                .withDatabaseName("stock")
                .withUsername("stock")
                .withPassword("stock")
                .withNetwork(NETWORK);
                // Using Flyway instead of mount volumes
                //.withCopyFileToContainer(
                //        MountableFile.forHostPath("./db/init"),
                //        "/docker-entrypoint-initdb.d"
                //)
    }

    @Bean
    @ServiceConnection("kafka")
    KafkaContainer kafkaContainer() {
        kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"))
                .withNetwork(NETWORK)
                .withNetworkAliases("kafka")
                .withListener(() -> "kafka:19092")
                .withExposedPorts(9093);

        return kafkaContainer;
    }

    /*
    // Service simulated with Microcks, using OpenAPI specs & Postman collection
    @Bean
    GenericContainer<?> codeProviderApiContainer(DynamicPropertyRegistry propertyRegistry) {

        var wireMockContainer = new GenericContainer<>("wiremock/wiremock")
                .withNetwork(NETWORK)
                .withNetworkAliases("code-provider-api")
                .withExposedPorts(8080)
                .withCopyFileToContainer(
                        MountableFile.forHostPath("./dependencies/wiremock/mappings"),
                        "/home/wiremock/mappings"
                )
                .withCopyFileToContainer(
                        MountableFile.forHostPath("./dependencies/wiremock/stubs"),
                        "/home/wiremock/__files"
                );

        propertyRegistry.add("application.clients.code-provider.url",
                () -> STR."http://localhost:\{wireMockContainer.getMappedPort(8080)}");
        return wireMockContainer;
    }
     */

    @Bean
    GenericContainer<?> mailpitContainer(DynamicPropertyRegistry propertyRegistry) {

        var mailpitContainer = new GenericContainer<>("axllent/mailpit")
                .withNetwork(NETWORK)
                .withNetworkAliases("mailpit")
                .withExposedPorts(1025, 8025)
                .withEnv("MP_MAX_MESSAGES", "5000")
                .withEnv("MP_SMTP_AUTH_ACCEPT_ANY", "1")
                .withEnv("MP_SMTP_AUTH_ALLOW_INSECURE", "1");

        propertyRegistry.add("spring.mail.host", () -> "localhost");
        propertyRegistry.add("spring.mail.port", () -> mailpitContainer.getMappedPort(1025));

        return mailpitContainer;
    }

    @Bean
    MicrocksContainersEnsemble microcksEnsemble(DynamicPropertyRegistry registry) {


        var ensemble = new MicrocksContainersEnsemble(NETWORK, "quay.io/microcks/microcks-uber:1.9.0-native")
                .withPostman()             // We need this to do contract-testing with Postman collection
                .withAsyncFeature()        // We need this for async mocking and contract-testing
                .withAccessToHost(true)   // We need this to access our webapp while it runs
                .withKafkaConnection(new KafkaConnection("kafka:19092"))   // We need this to connect to Kafka
                .withMainArtifacts(
                        "specifications/fruit-stock-management.openapi.yml",
                        "specifications/fruit-stock-management.asyncapi.yml",
                        "third-parties/code-provider.openapi.yml")
                //.withSecondaryArtifacts("order-service-postman-collection.json", "third-parties/apipastries-postman-collection.json")
                .withAsyncDependsOn(this.kafkaContainer);   // We need this to be sure Kafka will be up before Microcks async minion

        ensemble.getMicrocksContainer()
                .withCopyFileToContainer(
                        MountableFile.forHostPath("./microcks/config"),
                        "/deployments/config"  
                );
        
        // We need to replace the default endpoints with those provided by Microcks.
        registry.add("application.clients.code-provider.url",
                () -> ensemble.getMicrocksContainer().getRestMockEndpoint("OpenAPI Code Provider Service", "1.0.0"));
        registry.add("application.kafka.consumer.topic-name",
                () -> ensemble.getAsyncMinionContainer().getKafkaMockTopic("AsyncAPI Fruit Stock Management", "1.0.0", "receiveFruitQuantityEvent"));

        return ensemble;
    }

}
