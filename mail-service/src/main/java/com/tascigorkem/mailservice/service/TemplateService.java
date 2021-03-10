package com.tascigorkem.mailservice.service;

import com.tascigorkem.mailservice.dto.EmailContentDto;
import com.tascigorkem.mailservice.dto.kafka.KafkaEmailMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class TemplateService {
    private final SpringTemplateEngine templateEngine;

    public EmailContentDto generateProjectStatusChangeEmail(KafkaEmailMessageDto kafkaEmailMessageDto) {
        Context context = new Context();
        context.setVariable("fullName", kafkaEmailMessageDto.getFullName());
        context.setVariable("bookingId", kafkaEmailMessageDto.getBookingId());

        return EmailContentDto
                .builder()
                .text(templateEngine.process("mail-content.txt", context))
                .html(templateEngine.process("mail-content.html", context))
                .build();
    }
}