package com.travel.repository;

import com.travel.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findByTourRouteId(Long tourRouteId);

    List<Registration> findByTouristId(Long touristId);

    List<Registration> findByTourRouteIdAndStatus(Long tourRouteId, Registration.RegistrationStatus status);

    List<Registration> findByTourRouteIdAndStatusIn(Long tourRouteId, List<Registration.RegistrationStatus> statuses);

    boolean existsByTourRouteIdAndTouristIdAndStatusIn(Long tourRouteId, Long touristId, List<Registration.RegistrationStatus> statuses);

    @Query("SELECT COALESCE(SUM(r.peopleCount), 0) FROM Registration r " +
           "WHERE r.tourRoute.id = :tourRouteId AND r.status IN ('PENDING', 'CONFIRMED')")
    Integer countConfirmedPeopleByTourRouteId(@Param("tourRouteId") Long tourRouteId);

    @Query("SELECT r FROM Registration r WHERE r.tourRoute.id = :tourRouteId AND r.status IN ('PENDING', 'CONFIRMED')")
    List<Registration> findActiveRegistrationsByTourRouteId(@Param("tourRouteId") Long tourRouteId);
}
