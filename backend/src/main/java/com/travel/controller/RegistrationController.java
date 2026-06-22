package com.travel.controller;

import com.travel.dto.RegistrationDTO;
import com.travel.dto.RegistrationRequest;
import com.travel.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @GetMapping
    public ResponseEntity<List<RegistrationDTO>> getRegistrations(
            @RequestParam(required = false) Long tourRouteId,
            @RequestParam(required = false) Long touristId) {
        if (tourRouteId != null) {
            return ResponseEntity.ok(registrationService.getRegistrationsByTourRoute(tourRouteId));
        }
        if (touristId != null) {
            return ResponseEntity.ok(registrationService.getRegistrationsByTourist(touristId));
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/tour/{tourRouteId}/active")
    public ResponseEntity<List<RegistrationDTO>> getActiveRegistrations(@PathVariable Long tourRouteId) {
        return ResponseEntity.ok(registrationService.getActiveRegistrationsByTourRoute(tourRouteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationDTO> getRegistrationById(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.getRegistrationById(id));
    }

    @PostMapping
    public ResponseEntity<RegistrationDTO> createRegistration(@Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(registrationService.createRegistration(request));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<RegistrationDTO> confirmRegistration(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.confirmRegistration(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<RegistrationDTO> cancelRegistration(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.cancelRegistration(id));
    }
}
