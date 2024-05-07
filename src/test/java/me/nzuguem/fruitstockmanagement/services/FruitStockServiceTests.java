package me.nzuguem.fruitstockmanagement.services;

import io.github.microcks.testcontainers.model.TestRequest;
import io.github.microcks.testcontainers.model.TestRunnerType;
import me.nzuguem.fruitstockmanagement.BaseIntegrationTest;
import me.nzuguem.fruitstockmanagement.models.FruitQuantity;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.containers.KafkaContainer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


class FruitStockServiceTests extends BaseIntegrationTest {

    @Autowired
    KafkaContainer kafkaContainer;

    @Autowired
    FruitStockService service;

    @Test
    void Should_publishEvent_When_fruitIsProvisioning() {

        // Arrange
        //this.ensureTopicExists();

        // Prepare a Microcks test.
        var kafkaTest = new TestRequest.Builder()
                .serviceId("AsyncAPI Fruit Stock Management:1.0.0")
                .filteredOperations(List.of("sendFruitQuantityEvent"))
                .runnerType(TestRunnerType.ASYNC_API_SCHEMA.name())
                .testEndpoint("kafka://kafka:19092/fruits-provisioning")
                .timeout(Duration.ofSeconds(2))
                .build();

        var fruitQuantity = new FruitQuantity(null,null, "Apple", 100);

        try {
            // Launch the Microcks test and wait a bit to be sure it actually connects to Kafka.
            var testRequestFuture = microcksEnsemble.getMicrocksContainer().testEndpointAsync(kafkaTest);

            TimeUnit.MILLISECONDS.sleep(750L);

            // Act
            service.provide(fruitQuantity);

            // Get the Microcks test result.
            var testResult = testRequestFuture.get();

            // Check success and that we read 1 valid message on the topic.
            System.err.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(testResult));
            System.err.println(microcksEnsemble.getAsyncMinionContainer().getLogs());

            assertThat(testResult.isSuccess()).isTrue();
            //assertThat(testResult.getTestCaseResults()).isNotEmpty();
            //assertThat(testResult.getTestCaseResults().getFirst().getTestStepResults()).isNotEmpty();

        } catch (Exception e) {
            fail("No exception should be thrown when testing Kafka publication", e);
        }

    }

    private void ensureTopicExists() {
        var properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaContainer.getBootstrapServers());
        var adminClient = AdminClient.create(properties);
        adminClient.createTopics(List.of(new NewTopic("fruits-provisioning", 1, Short.parseShort("1"))));
    }
}
