package com.travel.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TouristDTO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String idCardNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
