package me.nzuguem.fruitstockmanagement.clients;

import me.nzuguem.fruitstockmanagement.BaseIntegrationTest;
import me.nzuguem.fruitstockmanagement.models.FruitCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class FruitCodeProviderClientTests extends BaseIntegrationTest {

    @Autowired
    FruitCodeProviderClient client;

    @Test
    void Should_Return_FruitCode() {

        // Act
        var code = client.getFruitCode("Apple");

        // Assert
        assertThat(code).isEqualTo(new FruitCode("APLE", "Apple"));
    }
}
