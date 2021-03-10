package com.tascigorkem.flightbookingservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
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
