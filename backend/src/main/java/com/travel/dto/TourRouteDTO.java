package com.travel.dto;

import com.travel.entity.TourRoute;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TourRouteDTO {
    private Long id;
    private String name;
    private String description;
    private String itinerary;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private BigDecimal price;
    private Integer minPeople;
    private Integer maxPeople;
    private String departureCity;
    private String destination;
    private TourRoute.TourStatus status;
    private Long guideId;
    private String guideName;
    private String meetingPoint;
    private LocalDateTime meetingTime;
    private Integer registeredPeople;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
