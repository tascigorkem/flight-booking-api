package com.tascigorkem.flightbookingservice.controller.kafka;

import com.tascigorkem.flightbookingservice.dto.kafka.KafkaEmailMessageDto;
import com.tascigorkem.flightbookingservice.service.kafka.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaService kafkaService;

    @GetMapping("/publish")
    public void sendMessage(@RequestParam("message") String message) {
        kafkaService.sendMessageToTopic(message);
    }

    @PostMapping("/publish/object")
    public void sendJsonObject(@RequestBody KafkaEmailMessageDto kafkaEmailMessageDto) {
        kafkaService.sendJsonObjectToTopic(kafkaEmailMessageDto);
    }

}
