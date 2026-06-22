package com.travel.service;

import com.travel.dto.RegistrationDTO;
import com.travel.dto.RegistrationRequest;
import com.travel.entity.Registration;
import com.travel.entity.TourRoute;
import com.travel.entity.Tourist;
import com.travel.exception.BusinessException;
import com.travel.exception.ResourceNotFoundException;
import com.travel.repository.RegistrationRepository;
import com.travel.repository.TourRouteRepository;
import com.travel.repository.TouristRepository;
import com.travel.strategy.GroupFormationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final TourRouteRepository tourRouteRepository;
    private final TouristRepository touristRepository;

    @Qualifier("minPeopleGroupFormationStrategy")
    private final GroupFormationStrategy groupFormationStrategy;

    public List<RegistrationDTO> getRegistrationsByTourRoute(Long tourRouteId) {
        return registrationRepository.findByTourRouteId(tourRouteId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<RegistrationDTO> getActiveRegistrationsByTourRoute(Long tourRouteId) {
        return registrationRepository.findActiveRegistrationsByTourRouteId(tourRouteId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<RegistrationDTO> getRegistrationsByTourist(Long touristId) {
        return registrationRepository.findByTouristId(touristId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RegistrationDTO getRegistrationById(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", id));
        return toDTO(registration);
    }

    @Transactional
    public RegistrationDTO createRegistration(RegistrationRequest request) {
        TourRoute tourRoute = tourRouteRepository.findById(request.getTourRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("TourRoute", request.getTourRouteId()));

        if (tourRoute.getStatus() == TourRoute.TourStatus.CANCELLED) {
            throw new BusinessException("该线路已取消，不能报名");
        }
        if (tourRoute.getStatus() == TourRoute.TourStatus.COMPLETED) {
            throw new BusinessException("该线路已完成，不能报名");
        }

        int currentRegistered = registrationRepository.countConfirmedPeopleByTourRouteId(request.getTourRouteId());
        if (currentRegistered + request.getPeopleCount() > tourRoute.getMaxPeople()) {
            throw new BusinessException("报名人数超过最大限制，当前已报名: " + currentRegistered + ", 还可报名: " + (tourRoute.getMaxPeople() - currentRegistered));
        }

        Tourist tourist = findOrCreateTourist(request);

        Registration registration = new Registration();
        registration.setTourRoute(tourRoute);
        registration.setTourist(tourist);
        registration.setPeopleCount(request.getPeopleCount());
        registration.setTotalPrice(tourRoute.getPrice().multiply(BigDecimal.valueOf(request.getPeopleCount())));
        registration.setStatus(Registration.RegistrationStatus.PENDING);
        registration.setPaymentProofUrl(request.getPaymentProofUrl());
        registration.setRemarks(request.getRemarks());

        Registration saved = registrationRepository.save(registration);

        checkAndFormGroup(tourRoute);

        return toDTO(saved);
    }

    @Transactional
    public RegistrationDTO confirmRegistration(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", id));

        if (registration.getStatus() != Registration.RegistrationStatus.PENDING) {
            throw new BusinessException("只有待确认的报名才能确认");
        }

        registration.setStatus(Registration.RegistrationStatus.CONFIRMED);
        Registration saved = registrationRepository.save(registration);

        checkAndFormGroup(registration.getTourRoute());

        return toDTO(saved);
    }

    @Transactional
    public RegistrationDTO cancelRegistration(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", id));

        if (registration.getStatus() == Registration.RegistrationStatus.CANCELLED ||
            registration.getStatus() == Registration.RegistrationStatus.REFUNDED) {
            throw new BusinessException("该报名已取消或退款");
        }

        registration.setStatus(Registration.RegistrationStatus.CANCELLED);
        Registration saved = registrationRepository.save(registration);
        return toDTO(saved);
    }

    public void checkAndFormGroup(TourRoute tourRoute) {
        if (tourRoute.getStatus() != TourRoute.TourStatus.PENDING) {
            return;
        }

        int registeredPeople = registrationRepository.countConfirmedPeopleByTourRouteId(tourRoute.getId());

        if (groupFormationStrategy.shouldFormGroup(tourRoute, registeredPeople)) {
            tourRoute.setStatus(TourRoute.TourStatus.CONFIRMED);
            tourRouteRepository.save(tourRoute);

            List<Registration> registrations = registrationRepository.findActiveRegistrationsByTourRouteId(tourRoute.getId());
            for (Registration reg : registrations) {
                if (reg.getStatus() == Registration.RegistrationStatus.PENDING) {
                    reg.setStatus(Registration.RegistrationStatus.CONFIRMED);
                    registrationRepository.save(reg);
                }
            }
        }
    }

    private Tourist findOrCreateTourist(RegistrationRequest request) {
        if (request.getTouristPhone() != null && !request.getTouristPhone().isEmpty()) {
            return touristRepository.findByPhone(request.getTouristPhone()).orElseGet(() -> {
                Tourist newTourist = new Tourist();
                newTourist.setName(request.getTouristName());
                newTourist.setPhone(request.getTouristPhone());
                newTourist.setEmail(request.getTouristEmail());
                newTourist.setIdCardNumber(request.getTouristIdCardNumber());
                return touristRepository.save(newTourist);
            });
        }

        Tourist newTourist = new Tourist();
        newTourist.setName(request.getTouristName());
        newTourist.setPhone(request.getTouristPhone());
        newTourist.setEmail(request.getTouristEmail());
        newTourist.setIdCardNumber(request.getTouristIdCardNumber());
        return touristRepository.save(newTourist);
    }

    private RegistrationDTO toDTO(Registration registration) {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setId(registration.getId());
        dto.setTourRouteId(registration.getTourRoute().getId());
        dto.setTourRouteName(registration.getTourRoute().getName());
        dto.setTouristId(registration.getTourist().getId());
        dto.setTouristName(registration.getTourist().getName());
        dto.setTouristPhone(registration.getTourist().getPhone());
        dto.setPeopleCount(registration.getPeopleCount());
        dto.setTotalPrice(registration.getTotalPrice());
        dto.setStatus(registration.getStatus());
        dto.setPaymentProofUrl(registration.getPaymentProofUrl());
        dto.setRemarks(registration.getRemarks());
        dto.setCreatedAt(registration.getCreatedAt());
        dto.setUpdatedAt(registration.getUpdatedAt());
        return dto;
    }
}
