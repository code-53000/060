package com.travel.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TourRouteRequest {
    @NotBlank(message = "线路名称不能为空")
    @Size(max = 200, message = "线路名称不能超过200个字符")
    private String name;

    private String description;

    private String itinerary;

    @NotNull(message = "出发日期不能为空")
    private LocalDate departureDate;

    @NotNull(message = "返回日期不能为空")
    private LocalDate returnDate;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "最低成团人数不能为空")
    @Min(value = 1, message = "最低成团人数至少为1")
    private Integer minPeople;

    @NotNull(message = "最大人数不能为空")
    @Min(value = 1, message = "最大人数至少为1")
    private Integer maxPeople;

    private String departureCity;
    private String destination;
    private String meetingPoint;
}
