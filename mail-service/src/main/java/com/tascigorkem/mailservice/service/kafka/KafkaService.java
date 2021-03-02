package com.tascigorkem.mailservice.service.kafka;

import com.tascigorkem.mailservice.dto.kafka.KafkaMessageDto;
import com.tascigorkem.mailservice.service.EmailService;
import com.tascigorkem.mailservice.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaService {

    private final EmailService emailService;
    private final TemplateService templateService;

    @KafkaListener(topics = "${my-object-topic.name}",
                    properties = {
                            "spring.json.value.default.type=com.tascigorkem.mailservice.dto.kafka.KafkaMessageDto",
                            "spring.json.use.type.headers=false"
                    })
    public void getJsonObjectFromTopic(@Payload KafkaMessageDto kafkaMessageDto) {
        log.info("Received and will send email message object: {}", kafkaMessageDto.toString());

        try {
            emailService.sendEmail(
                    "gorkem_tasci@hotmail.com",
                    "Hello Gorkem Subject Test ",
                    templateService.generateProjectStatusChangeEmail(kafkaMessageDto.getMessage())
            );
        } catch (MailException e) {
            log.error("Could not send e-mail", e);
        }
    }
}
