package com.travel.dto;

import com.travel.entity.Registration;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RegistrationDTO {
    private Long id;
    private Long tourRouteId;
    private String tourRouteName;
    private Long touristId;
    private String touristName;
    private String touristPhone;
    private Integer peopleCount;
    private BigDecimal totalPrice;
    private Registration.RegistrationStatus status;
    private String paymentProofUrl;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
