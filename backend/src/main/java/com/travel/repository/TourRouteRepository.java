package com.travel.repository;

import com.travel.entity.TourRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TourRouteRepository extends JpaRepository<TourRoute, Long> {

    List<TourRoute> findByStatus(TourRoute.TourStatus status);

    List<TourRoute> findByStatusIn(List<TourRoute.TourStatus> statuses);

    @Query("SELECT t FROM TourRoute t WHERE t.status = 'PENDING' AND t.departureDate >= :date ORDER BY t.departureDate ASC")
    List<TourRoute> findUpcomingPendingTours(@Param("date") LocalDate date);

    @Query("SELECT t FROM TourRoute t WHERE t.departureDate >= :from AND t.departureDate <= :to ORDER BY t.departureDate ASC")
    List<TourRoute> findByDepartureDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT t FROM TourRoute t WHERE t.assignedGuide.id = :guideId AND t.status IN ('PENDING', 'CONFIRMED')")
    List<TourRoute> findByAssignedGuideId(@Param("guideId") Long guideId);
}
