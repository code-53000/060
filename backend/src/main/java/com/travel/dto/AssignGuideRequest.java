package com.travel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignGuideRequest {
    @NotNull(message = "导游ID不能为空")
    private Long guideId;

    private String meetingPoint;
    private LocalDateTime meetingTime;
}
