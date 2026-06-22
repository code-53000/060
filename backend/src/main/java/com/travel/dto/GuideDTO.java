package com.travel.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class GuideDTO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private Set<String> languages;
    private String licenseNumber;
    private String notes;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
