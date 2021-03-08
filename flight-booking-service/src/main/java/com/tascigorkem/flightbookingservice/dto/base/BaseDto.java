package com.tascigorkem.flightbookingservice.dto.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@SuperBuilder
public abstract class BaseDto {
    private UUID id;
    protected LocalDateTime creationTime;
    protected LocalDateTime updateTime;
}
