package com.travel.service;

import com.travel.dto.TourRouteDTO;
import com.travel.dto.TourRouteRequest;
import com.travel.entity.TourRoute;
import com.travel.exception.BusinessException;
import com.travel.exception.ResourceNotFoundException;
import com.travel.repository.RegistrationRepository;
import com.travel.repository.TourRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourRouteService {

    private final TourRouteRepository tourRouteRepository;
    private final RegistrationRepository registrationRepository;

    @Transactional(readOnly = true)
    public List<TourRouteDTO> getAllTours() {
        return tourRouteRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TourRouteDTO> getToursByStatus(TourRoute.TourStatus status) {
        return tourRouteRepository.findByStatus(status).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TourRouteDTO> getUpcomingPendingTours() {
        return tourRouteRepository.findUpcomingPendingTours(LocalDate.now()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TourRouteDTO getTourById(Long id) {
        TourRoute tour = tourRouteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TourRoute", id));
        return toDTO(tour);
    }

    @Transactional
    public TourRouteDTO createTour(TourRouteRequest request) {
        if (request.getReturnDate().isBefore(request.getDepartureDate())) {
            throw new BusinessException("返回日期不能早于出发日期");
        }
        if (request.getMaxPeople() < request.getMinPeople()) {
            throw new BusinessException("最大人数不能小于最低成团人数");
        }

        TourRoute tour = new TourRoute();
        tour.setName(request.getName());
        tour.setDescription(request.getDescription());
        tour.setItinerary(request.getItinerary());
        tour.setDepartureDate(request.getDepartureDate());
        tour.setReturnDate(request.getReturnDate());
        tour.setPrice(request.getPrice());
        tour.setMinPeople(request.getMinPeople());
        tour.setMaxPeople(request.getMaxPeople());
        tour.setDepartureCity(request.getDepartureCity());
        tour.setDestination(request.getDestination());
        tour.setMeetingPoint(request.getMeetingPoint());
        tour.setStatus(TourRoute.TourStatus.PENDING);

        TourRoute saved = tourRouteRepository.save(tour);
        return toDTO(saved);
    }

    @Transactional
    public TourRouteDTO updateTour(Long id, TourRouteRequest request) {
        TourRoute tour = tourRouteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TourRoute", id));

        if (request.getReturnDate().isBefore(request.getDepartureDate())) {
            throw new BusinessException("返回日期不能早于出发日期");
        }
        if (request.getMaxPeople() < request.getMinPeople()) {
            throw new BusinessException("最大人数不能小于最低成团人数");
        }

        tour.setName(request.getName());
        tour.setDescription(request.getDescription());
        tour.setItinerary(request.getItinerary());
        tour.setDepartureDate(request.getDepartureDate());
        tour.setReturnDate(request.getReturnDate());
        tour.setPrice(request.getPrice());
        tour.setMinPeople(request.getMinPeople());
        tour.setMaxPeople(request.getMaxPeople());
        tour.setDepartureCity(request.getDepartureCity());
        tour.setDestination(request.getDestination());
        tour.setMeetingPoint(request.getMeetingPoint());

        TourRoute saved = tourRouteRepository.save(tour);
        return toDTO(saved);
    }

    @Transactional
    public void deleteTour(Long id) {
        TourRoute tour = tourRouteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TourRoute", id));

        if (tour.getStatus() == TourRoute.TourStatus.CONFIRMED) {
            throw new BusinessException("已成团的线路不能删除");
        }

        tourRouteRepository.delete(tour);
    }

    @Transactional
    public TourRouteDTO cancelTour(Long id) {
        TourRoute tour = tourRouteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TourRoute", id));

        if (tour.getStatus() == TourRoute.TourStatus.COMPLETED) {
            throw new BusinessException("已完成的线路不能取消");
        }

        tour.setStatus(TourRoute.TourStatus.CANCELLED);
        TourRoute saved = tourRouteRepository.save(tour);
        return toDTO(saved);
    }

    public int getRegisteredPeopleCount(Long tourRouteId) {
        return registrationRepository.countConfirmedPeopleByTourRouteId(tourRouteId);
    }

    private TourRouteDTO toDTO(TourRoute tour) {
        TourRouteDTO dto = new TourRouteDTO();
        dto.setId(tour.getId());
        dto.setName(tour.getName());
        dto.setDescription(tour.getDescription());
        dto.setItinerary(tour.getItinerary());
        dto.setDepartureDate(tour.getDepartureDate());
        dto.setReturnDate(tour.getReturnDate());
        dto.setPrice(tour.getPrice());
        dto.setMinPeople(tour.getMinPeople());
        dto.setMaxPeople(tour.getMaxPeople());
        dto.setDepartureCity(tour.getDepartureCity());
        dto.setDestination(tour.getDestination());
        dto.setStatus(tour.getStatus());
        dto.setMeetingPoint(tour.getMeetingPoint());
        dto.setMeetingTime(tour.getMeetingTime());
        dto.setCreatedAt(tour.getCreatedAt());
        dto.setUpdatedAt(tour.getUpdatedAt());

        if (tour.getAssignedGuide() != null) {
            dto.setGuideId(tour.getAssignedGuide().getId());
            dto.setGuideName(tour.getAssignedGuide().getName());
        }

        dto.setRegisteredPeople(getRegisteredPeopleCount(tour.getId()));

        return dto;
    }
}
