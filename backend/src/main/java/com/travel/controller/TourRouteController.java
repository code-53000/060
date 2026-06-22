package com.travel.controller;

import com.travel.dto.TourRouteDTO;
import com.travel.dto.TourRouteRequest;
import com.travel.entity.TourRoute;
import com.travel.service.TourRouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourRouteController {

    private final TourRouteService tourRouteService;

    @GetMapping
    public ResponseEntity<List<TourRouteDTO>> getAllTours(
            @RequestParam(required = false) TourRoute.TourStatus status) {
        if (status != null) {
            return ResponseEntity.ok(tourRouteService.getToursByStatus(status));
        }
        return ResponseEntity.ok(tourRouteService.getAllTours());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TourRouteDTO>> getUpcomingPendingTours() {
        return ResponseEntity.ok(tourRouteService.getUpcomingPendingTours());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourRouteDTO> getTourById(@PathVariable Long id) {
        return ResponseEntity.ok(tourRouteService.getTourById(id));
    }

    @PostMapping
    public ResponseEntity<TourRouteDTO> createTour(@Valid @RequestBody TourRouteRequest request) {
        return ResponseEntity.ok(tourRouteService.createTour(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourRouteDTO> updateTour(
            @PathVariable Long id,
            @Valid @RequestBody TourRouteRequest request) {
        return ResponseEntity.ok(tourRouteService.updateTour(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourRouteService.deleteTour(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<TourRouteDTO> cancelTour(@PathVariable Long id) {
        return ResponseEntity.ok(tourRouteService.cancelTour(id));
    }
}
