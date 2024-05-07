package me.nzuguem.fruitstockmanagement.services;

import me.nzuguem.fruitstockmanagement.models.FruitQuantityEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FruitQuantityEventListener {

    private final FruitStockService fruitStockService;

    public FruitQuantityEventListener(FruitStockService fruitStockService) {
        this.fruitStockService = fruitStockService;
    }

    @KafkaListener(topics = "${application.kafka.consumer.topic-name}")
    public void publish(FruitQuantityEvent event) {
        this.fruitStockService.soldOut(event.fruitQuantity());
    }
}
