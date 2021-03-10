package com.tascigorkem.flightbookingservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfiguration {

    @Value("${kafka-topics.kafka-message-topic.name}")
    private String myMessageTopicName;
    @Value("${kafka-topics.kafka-message-topic.num-partitions}")
    private int myMessageTopicNumPartitions;
    @Value("${kafka-topics.kafka-message-topic.replication-factor}")
    private short myMessageTopicReplicationFactor;

    @Bean
    public NewTopic myMessageTopic() {
        return new NewTopic(myMessageTopicName, myMessageTopicNumPartitions, myMessageTopicReplicationFactor);
    }

    @Value("${kafka-topics.kafka-object-topic.name}")
    private String myObjectTopicName;
    @Value("${kafka-topics.kafka-object-topic.num-partitions}")
    private int myObjectTopicNumPartitions;
    @Value("${kafka-topics.kafka-object-topic.replication-factor}")
    private short myObjectTopicReplicationFactor;

    @Bean
    public NewTopic myObjectTopic() {
        return new NewTopic(myObjectTopicName, myObjectTopicNumPartitions, myObjectTopicReplicationFactor);
    }

}
