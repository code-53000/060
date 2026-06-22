package com.travel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class GuideRequest {
    @NotBlank(message = "导游姓名不能为空")
    @Size(max = 100, message = "导游姓名不能超过100个字符")
    private String name;

    private String phone;
    private String email;
    private Set<String> languages;
    private String licenseNumber;
    private String notes;
    private Boolean active = true;
}
