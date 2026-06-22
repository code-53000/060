package com.travel.service;

import com.travel.dto.GuideDTO;
import com.travel.dto.GuideRequest;
import com.travel.entity.Guide;
import com.travel.entity.TourRoute;
import com.travel.exception.BusinessException;
import com.travel.exception.ResourceNotFoundException;
import com.travel.repository.GuideRepository;
import com.travel.repository.TourRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideService {

    private final GuideRepository guideRepository;
    private final TourRouteRepository tourRouteRepository;

    public List<GuideDTO> getAllGuides() {
        return guideRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<GuideDTO> getActiveGuides() {
        return guideRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<GuideDTO> getAvailableGuides(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            return getActiveGuides();
        }
        return guideRepository.findAvailableGuidesBetweenDates(from, to).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<GuideDTO> getGuidesByLanguages(Set<String> languages) {
        return guideRepository.findByLanguagesIn(languages).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public GuideDTO getGuideById(Long id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guide", id));
        return toDTO(guide);
    }

    @Transactional
    public GuideDTO createGuide(GuideRequest request) {
        Guide guide = new Guide();
        guide.setName(request.getName());
        guide.setPhone(request.getPhone());
        guide.setEmail(request.getEmail());
        guide.setLanguages(request.getLanguages());
        guide.setLicenseNumber(request.getLicenseNumber());
        guide.setNotes(request.getNotes());
        guide.setActive(request.getActive() != null ? request.getActive() : true);

        Guide saved = guideRepository.save(guide);
        return toDTO(saved);
    }

    @Transactional
    public GuideDTO updateGuide(Long id, GuideRequest request) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guide", id));

        guide.setName(request.getName());
        guide.setPhone(request.getPhone());
        guide.setEmail(request.getEmail());
        guide.setLanguages(request.getLanguages());
        guide.setLicenseNumber(request.getLicenseNumber());
        guide.setNotes(request.getNotes());
        if (request.getActive() != null) {
            guide.setActive(request.getActive());
        }

        Guide saved = guideRepository.save(guide);
        return toDTO(saved);
    }

    @Transactional
    public void deleteGuide(Long id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guide", id));

        List<TourRoute> assignedTours = tourRouteRepository.findByAssignedGuideId(id);
        if (!assignedTours.isEmpty()) {
            throw new BusinessException("该导游有已分配的带团任务，不能删除");
        }

        guideRepository.delete(guide);
    }

    public boolean isGuideAvailable(Long guideId, LocalDate from, LocalDate to, Long excludeTourId) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new ResourceNotFoundException("Guide", guideId));

        if (!guide.getActive()) {
            return false;
        }

        List<TourRoute> assignedTours = tourRouteRepository.findByAssignedGuideId(guideId);
        for (TourRoute tour : assignedTours) {
            if (excludeTourId != null && tour.getId().equals(excludeTourId)) {
                continue;
            }
            if (!tour.getReturnDate().isBefore(from) && !tour.getDepartureDate().isAfter(to)) {
                return false;
            }
        }
        return true;
    }

    public List<TourRoute> getGuideAssignedTours(Long guideId) {
        return tourRouteRepository.findByAssignedGuideId(guideId);
    }

    private GuideDTO toDTO(Guide guide) {
        GuideDTO dto = new GuideDTO();
        dto.setId(guide.getId());
        dto.setName(guide.getName());
        dto.setPhone(guide.getPhone());
        dto.setEmail(guide.getEmail());
        dto.setLanguages(guide.getLanguages());
        dto.setLicenseNumber(guide.getLicenseNumber());
        dto.setNotes(guide.getNotes());
        dto.setActive(guide.getActive());
        dto.setCreatedAt(guide.getCreatedAt());
        dto.setUpdatedAt(guide.getUpdatedAt());
        return dto;
    }
}
