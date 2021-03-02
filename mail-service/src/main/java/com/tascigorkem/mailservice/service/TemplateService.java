package com.tascigorkem.mailservice.service;

import com.tascigorkem.mailservice.dto.EmailContentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class TemplateService {
    private final SpringTemplateEngine templateEngine;

    public EmailContentDto generateProjectStatusChangeEmail(String message) {
        Context context = new Context();
        context.setVariable("fullName", "Görkem Taşçı");
        context.setVariable("productName", message);

        return EmailContentDto
                .builder()
                .text(templateEngine.process("mail-content.txt", context))
                .html(templateEngine.process("mail-content.html", context))
                .build();
    }
}