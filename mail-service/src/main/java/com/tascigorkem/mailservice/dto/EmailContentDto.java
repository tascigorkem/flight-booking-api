package com.tascigorkem.mailservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailContentDto {
    private String text;
    private String html;
}