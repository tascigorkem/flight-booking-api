package com.tascigorkem.mailservice.service.kafka;

import com.tascigorkem.mailservice.dto.kafka.KafkaEmailMessageDto;
import com.tascigorkem.mailservice.service.EmailService;
import com.tascigorkem.mailservice.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaService {

    private final EmailService emailService;
    private final TemplateService templateService;

    @KafkaListener(topics = "${kafka-topics.kafka-object-topic.name}",
            properties = {
                    "spring.json.value.default.type=com.tascigorkem.mailservice.dto.kafka.KafkaEmailMessageDto",
                    "spring.json.use.type.headers=false"
            })
    public void getJsonObjectFromTopic(@Payload KafkaEmailMessageDto kafkaEmailMessageDto) {
        log.info("Received and will send email message object: {}", kafkaEmailMessageDto.toString());

        emailService.sendEmail(
                kafkaEmailMessageDto.getEmailAddress(),
                kafkaEmailMessageDto.getSubject(),
                templateService.generateProjectStatusChangeEmail(kafkaEmailMessageDto)
        );
    }
}
