package me.nzuguem.fruitstockmanagement.configurations;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

  @Value(value = "${application.kafka.producer.topic-name}")
  private String topicName;

  @Bean
  public NewTopic taskTopic() {
    return TopicBuilder.name(this.topicName)
        .partitions(1)
        .replicas(1)
        .build();
  }
}