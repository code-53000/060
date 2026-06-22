package com.travel.controller;

import com.travel.dto.GuideDTO;
import com.travel.dto.GuideRequest;
import com.travel.entity.TourRoute;
import com.travel.service.GuideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/guides")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

    @GetMapping
    public ResponseEntity<List<GuideDTO>> getAllGuides(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (from != null && to != null) {
            return ResponseEntity.ok(guideService.getAvailableGuides(from, to));
        }
        if (active != null && active) {
            return ResponseEntity.ok(guideService.getActiveGuides());
        }
        return ResponseEntity.ok(guideService.getAllGuides());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuideDTO> getGuideById(@PathVariable Long id) {
        return ResponseEntity.ok(guideService.getGuideById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<GuideDTO>> searchGuidesByLanguages(
            @RequestParam Set<String> languages) {
        return ResponseEntity.ok(guideService.getGuidesByLanguages(languages));
    }

    @GetMapping("/{id}/tours")
    public ResponseEntity<List<TourRoute>> getGuideAssignedTours(@PathVariable Long id) {
        return ResponseEntity.ok(guideService.getGuideAssignedTours(id));
    }

    @PostMapping
    public ResponseEntity<GuideDTO> createGuide(@Valid @RequestBody GuideRequest request) {
        return ResponseEntity.ok(guideService.createGuide(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuideDTO> updateGuide(
            @PathVariable Long id,
            @Valid @RequestBody GuideRequest request) {
        return ResponseEntity.ok(guideService.updateGuide(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuide(@PathVariable Long id) {
        guideService.deleteGuide(id);
        return ResponseEntity.noContent().build();
    }
}
