package com.travel.service;

import com.travel.dto.AssignGuideRequest;
import com.travel.dto.GuideDTO;
import com.travel.dto.TourRouteDTO;
import com.travel.entity.Guide;
import com.travel.entity.TourRoute;
import com.travel.exception.BusinessException;
import com.travel.exception.ResourceNotFoundException;
import com.travel.repository.GuideRepository;
import com.travel.repository.TourRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DispatchService {

    private final TourRouteRepository tourRouteRepository;
    private final GuideRepository guideRepository;
    private final GuideService guideService;
    private final TourRouteService tourRouteService;

    @Transactional
    public TourRouteDTO assignGuide(Long tourRouteId, AssignGuideRequest request) {
        TourRoute tourRoute = tourRouteRepository.findById(tourRouteId)
                .orElseThrow(() -> new ResourceNotFoundException("TourRoute", tourRouteId));

        if (tourRoute.getStatus() != TourRoute.TourStatus.PENDING &&
            tourRoute.getStatus() != TourRoute.TourStatus.CONFIRMED) {
            throw new BusinessException("只有待成团或已成团的线路才能派导游");
        }

        Guide guide = guideRepository.findById(request.getGuideId())
                .orElseThrow(() -> new ResourceNotFoundException("Guide", request.getGuideId()));

        if (!guide.getActive()) {
            throw new BusinessException("该导游已停用");
        }

        if (!guideService.isGuideAvailable(request.getGuideId(),
                tourRoute.getDepartureDate(),
                tourRoute.getReturnDate(),
                tourRouteId)) {
            throw new BusinessException("该导游在 " + tourRoute.getDepartureDate() + " 至 " + tourRoute.getReturnDate() + " 期间已有带团安排，档期冲突");
        }

        tourRoute.setAssignedGuide(guide);
        if (request.getMeetingPoint() != null) {
            tourRoute.setMeetingPoint(request.getMeetingPoint());
        }
        if (request.getMeetingTime() != null) {
            tourRoute.setMeetingTime(request.getMeetingTime());
        }

        TourRoute saved = tourRouteRepository.save(tourRoute);
        return tourRouteService.getTourById(saved.getId());
    }

    @Transactional
    public TourRouteDTO unassignGuide(Long tourRouteId) {
        TourRoute tourRoute = tourRouteRepository.findById(tourRouteId)
                .orElseThrow(() -> new ResourceNotFoundException("TourRoute", tourRouteId));

        if (tourRoute.getStatus() == TourRoute.TourStatus.COMPLETED) {
            throw new BusinessException("已完成的线路不能取消导游分配");
        }

        tourRoute.setAssignedGuide(null);
        TourRoute saved = tourRouteRepository.save(tourRoute);
        return tourRouteService.getTourById(saved.getId());
    }

    public List<GuideDTO> getAvailableGuidesForTour(Long tourRouteId) {
        TourRoute tourRoute = tourRouteRepository.findById(tourRouteId)
                .orElseThrow(() -> new ResourceNotFoundException("TourRoute", tourRouteId));

        return guideService.getAvailableGuides(tourRoute.getDepartureDate(), tourRoute.getReturnDate());
    }

    public List<TourRouteDTO> getToursNeedingGuide() {
        return tourRouteRepository.findByStatusIn(
                List.of(TourRoute.TourStatus.PENDING, TourRoute.TourStatus.CONFIRMED)
        ).stream()
                .filter(t -> t.getAssignedGuide() == null)
                .map(tourRouteService::getTourById)
                .collect(Collectors.toList());
    }
}
