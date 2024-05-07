package me.nzuguem.fruitstockmanagement.services;

import me.nzuguem.fruitstockmanagement.models.FruitQuantityEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FruitQuantityEventPublisher {

    @Value(value = "${application.kafka.producer.topic-name}")
    private String topicName;

    private final KafkaTemplate<String, FruitQuantityEvent> kafkaTemplate;

    public FruitQuantityEventPublisher(KafkaTemplate<String, FruitQuantityEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(FruitQuantityEvent event) {
        this.kafkaTemplate.send(this.topicName, event);
    }
}
