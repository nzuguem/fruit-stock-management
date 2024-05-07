package me.nzuguem.fruitstockmanagement.controllers;

import io.github.microcks.testcontainers.model.TestRequest;
import io.github.microcks.testcontainers.model.TestRunnerType;
import me.nzuguem.fruitstockmanagement.BaseIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static java.lang.StringTemplate.STR;
import static org.assertj.core.api.Assertions.assertThat;

class FruitStockControllerTests extends BaseIntegrationTest {

    @Test
    void Should_complyWith_OpenAPIContract() throws Exception {
        // Arrange
        // Ask for an Open API conformance to be launched.
        var testRequest = new TestRequest.Builder()
                .serviceId("OpenAPI Fruit Stock Management:1.0.0")
                .runnerType(TestRunnerType.OPEN_API_SCHEMA.name())
                // Access to this application on the host machine 
                // https://java.testcontainers.org/features/networking/#exposing-host-ports-to-the-container
                .testEndpoint(STR."http://host.testcontainers.internal:\{this.port}")
                .build();

        var testResult = microcksEnsemble.getMicrocksContainer().testEndpoint(testRequest);

        // You may inspect complete response object with following:
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(testResult));
        // You may inspect complete response object with following:
        System.err.println(microcksEnsemble.getMicrocksContainer().getLogs());

        // Assert
        assertThat(testResult.isSuccess()).isTrue();
        assertThat(testResult.getTestCaseResults()).isNotEmpty();
    }

    @Test
    @Disabled("No Postman collections and tests for the moment")
    void Should_complyWith_PostmanTests() throws Exception {
        // Arrange
        // Ask for an Open API conformance to be launched.
        var testRequest = new TestRequest.Builder()
                .serviceId("Fruit Stock Management:1.0.0")
                .runnerType(TestRunnerType.POSTMAN.name())
                // Access to this application on the host machine 
                // https://java.testcontainers.org/features/networking/#exposing-host-ports-to-the-container
                .testEndpoint(STR."http://host.testcontainers.internal:\{this.port}")
                .build();

        var testResult = microcksEnsemble.getMicrocksContainer().testEndpoint(testRequest);

        // You may inspect complete response object with following:
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(testResult));
        // You may inspect complete response object with following:
        System.err.println(microcksEnsemble.getMicrocksContainer().getLogs());
        System.err.println(microcksEnsemble.getPostmanContainer().getLogs());

        // Assert
        assertThat(testResult.isSuccess()).isTrue();
        assertThat(testResult.getTestCaseResults()).isNotEmpty();
    }
}
