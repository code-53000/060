package com.travel.controller;

import com.travel.dto.AssignGuideRequest;
import com.travel.dto.GuideDTO;
import com.travel.dto.TourRouteDTO;
import com.travel.service.DispatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispatch")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService dispatchService;

    @GetMapping("/tours-needing-guide")
    public ResponseEntity<List<TourRouteDTO>> getToursNeedingGuide() {
        return ResponseEntity.ok(dispatchService.getToursNeedingGuide());
    }

    @GetMapping("/tours/{tourRouteId}/available-guides")
    public ResponseEntity<List<GuideDTO>> getAvailableGuidesForTour(@PathVariable Long tourRouteId) {
        return ResponseEntity.ok(dispatchService.getAvailableGuidesForTour(tourRouteId));
    }

    @PostMapping("/tours/{tourRouteId}/assign-guide")
    public ResponseEntity<TourRouteDTO> assignGuide(
            @PathVariable Long tourRouteId,
            @Valid @RequestBody AssignGuideRequest request) {
        return ResponseEntity.ok(dispatchService.assignGuide(tourRouteId, request));
    }

    @PostMapping("/tours/{tourRouteId}/unassign-guide")
    public ResponseEntity<TourRouteDTO> unassignGuide(@PathVariable Long tourRouteId) {
        return ResponseEntity.ok(dispatchService.unassignGuide(tourRouteId));
    }
}
