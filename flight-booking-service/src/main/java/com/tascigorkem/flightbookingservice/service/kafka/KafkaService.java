package com.tascigorkem.flightbookingservice.service.kafka;

import com.tascigorkem.flightbookingservice.dto.kafka.KafkaEmailMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaService {

    @Value("${kafka-topics.kafka-message-topic.name}")
    private String myMessageTopicName;

    @Value("${kafka-topics.kafka-object-topic.name}")
    private String myObjectTopicName;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // producers
    public void sendMessageToTopic(String message) {
        this.kafkaTemplate.send(myMessageTopicName, message);
        log.info("Sent message string:" + message);
    }

    public void sendJsonObjectToTopic(KafkaEmailMessageDto kafkaEmailMessageDto) {
        this.kafkaTemplate.send(myObjectTopicName, kafkaEmailMessageDto);
        log.info("Sent message object:" + kafkaEmailMessageDto.toString());
    }

//    // consumers
//    @KafkaListener(topics = "${kafka-topics.kafka-message-topic.name}")
//    public void getMessageFromTopic(String message) {
//        log.info("Received message string:" + message);
//    }
//
//    @KafkaListener(topics = "${kafka-topics.kafka-object-topic.name}")
//    public void getJsonObjectFromTopic(@Payload KafkaEmailMessageDto kafkaEmailMessageDto) {
//        log.info("Received message object:" + kafkaEmailMessageDto.toString());
//    }
}
