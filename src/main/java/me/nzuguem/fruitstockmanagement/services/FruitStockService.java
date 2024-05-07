package me.nzuguem.fruitstockmanagement.services;

import me.nzuguem.fruitstockmanagement.clients.FruitCodeProviderClient;
import me.nzuguem.fruitstockmanagement.exceptions.FruitQuantityNotFoundException;
import me.nzuguem.fruitstockmanagement.models.FruitQuantity;
import me.nzuguem.fruitstockmanagement.models.FruitQuantityMailTemplate;
import me.nzuguem.fruitstockmanagement.repositories.FruitStock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FruitStockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FruitStockService.class);

    private final FruitStock stock;

    private final FruitQuantityEventPublisher fruitQuantityEventPublisher;

    private final FruitCodeProviderClient fruitCodeProviderClient;

    private final FruitStockMailSender fruitStockMailSender;

    public FruitStockService(FruitStock stock,
                             FruitQuantityEventPublisher fruitQuantityEventPublisher,
                             FruitCodeProviderClient fruitCodeProviderClient,
                             FruitStockMailSender fruitStockMailSender) {
        this.stock = stock;
        this.fruitQuantityEventPublisher = fruitQuantityEventPublisher;
        this.fruitCodeProviderClient = fruitCodeProviderClient;
        this.fruitStockMailSender = fruitStockMailSender;
    }

    public FruitQuantity provide(FruitQuantity fruitQuantity) {

        LOGGER.info("Provision - {}", fruitQuantity);
        var existingFruitQuantity = this.stock.findByCode(fruitQuantity.code());

        if (Objects.nonNull(existingFruitQuantity)) {
            fruitQuantity = existingFruitQuantity.add(fruitQuantity);
        } else {
            var fruitCode = this.fruitCodeProviderClient.getFruitCode(fruitQuantity.name());
            fruitQuantity = fruitQuantity.withCode(fruitCode.code());
        }

        fruitQuantity = this.stock.save(fruitQuantity);

        var event = fruitQuantity.toEvent();
        LOGGER.info("Publish Provision - {}", event);
        this.fruitQuantityEventPublisher.publish(event);

        return fruitQuantity;
    }

    public FruitQuantity verify(String code) {

        var fruitQuantity = this.stock.findByCode(code);

        if (Objects.isNull(fruitQuantity)) {
            throw new FruitQuantityNotFoundException(STR."\{code} not found");
        }

        return fruitQuantity;
    }

    public void soldOut(FruitQuantity fruitQuantity) {

        LOGGER.info("Sold out - {}", fruitQuantity);
        var existingFruitQuantity = this.stock.findByCode(fruitQuantity.code());

        fruitQuantity = existingFruitQuantity.subtract(fruitQuantity);

        this.stock.save(fruitQuantity);

        if (fruitQuantity.outOfStock()) {
            LOGGER.warn("Completely sold out- {}", fruitQuantity);
            this.fruitStockMailSender.sendEmail("Épuisement de stock de fruits", FruitQuantityMailTemplate.SOLD_OUT_COMPLETELY);
        }
    }
}
