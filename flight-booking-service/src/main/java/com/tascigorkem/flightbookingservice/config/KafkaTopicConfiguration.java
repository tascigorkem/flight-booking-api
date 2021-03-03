package com.tascigorkem.flightbookingservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfiguration {

    @Value("${my-message-topic.name}")
    private String myMessageTopicName;
    @Value("${my-message-topic.num-partitions}")
    private int myMessageTopicNumPartitions;
    @Value("${my-message-topic.replication-factor}")
    private short myMessageTopicReplicationFactor;

    @Bean
    public NewTopic myMessageTopic() {
        return new NewTopic(myMessageTopicName, myMessageTopicNumPartitions, myMessageTopicReplicationFactor);
    }

    @Value("${my-object-topic.name}")
    private String myObjectTopicName;
    @Value("${my-object-topic.num-partitions}")
    private int myObjectTopicNumPartitions;
    @Value("${my-object-topic.replication-factor}")
    private short myObjectTopicReplicationFactor;

    @Bean
    public NewTopic myObjectTopic() {
        return new NewTopic(myObjectTopicName, myObjectTopicNumPartitions, myObjectTopicReplicationFactor);
    }

}
