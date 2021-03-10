package com.tascigorkem.mailservice.dto.kafka;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KafkaEmailMessageDto {
    private String emailAddress;
    private String fullName;
    private String subject;
    private String bookingId;
    private String id = UUID.randomUUID().toString();
    private LocalDate messageDate = LocalDate.now();
}
