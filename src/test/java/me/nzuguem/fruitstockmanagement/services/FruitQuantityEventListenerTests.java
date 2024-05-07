package me.nzuguem.fruitstockmanagement.services;

import me.nzuguem.fruitstockmanagement.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.shaded.org.awaitility.core.ConditionTimeoutException;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class FruitQuantityEventListenerTests extends BaseIntegrationTest {

    @Autowired
    FruitStockService service;

    @Test
    void Should_consumeEvent_When_fruitIsSoldOut() {

        try {
            Awaitility.await().atMost(4, TimeUnit.SECONDS)
                    .pollDelay(400, TimeUnit.MILLISECONDS)
                    .pollInterval(400, TimeUnit.MILLISECONDS)
                    .until(() -> {
                        var fruitQuantity = this.service.verify("KW");

                        // 250 is the basic provisioned quantity, and we must have a different quantity after a Kafka consumption.
                        if (Objects.equals(fruitQuantity.quantity(), 250)) {
                            return false;
                        }

                        assertThat(fruitQuantity.quantity()).isLessThan(250);
                        assertThat(fruitQuantity.code()).isEqualTo("KW");
                        return true;
                    });
        } catch (ConditionTimeoutException timeoutException) {
            fail("The expected FruitQuantityEvent was not consumed in expected delay");
        }
    }
}
