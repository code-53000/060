package com.travel.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationRequest {
    @NotNull(message = "线路ID不能为空")
    private Long tourRouteId;

    @NotBlank(message = "游客姓名不能为空")
    @Size(max = 100, message = "游客姓名不能超过100个字符")
    private String touristName;

    private String touristPhone;
    private String touristEmail;
    private String touristIdCardNumber;

    @NotNull(message = "报名人数不能为空")
    @Min(value = 1, message = "报名人数至少为1")
    private Integer peopleCount;

    private String paymentProofUrl;
    private String remarks;
}
