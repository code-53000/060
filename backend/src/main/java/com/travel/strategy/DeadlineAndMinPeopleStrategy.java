package com.travel.strategy;

import com.travel.entity.TourRoute;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class DeadlineAndMinPeopleStrategy implements GroupFormationStrategy {

    private static final int DAYS_BEFORE_DEADLINE = 3;

    @Override
    public boolean shouldFormGroup(TourRoute tourRoute, int registeredPeople) {
        if (tourRoute == null || tourRoute.getMinPeople() == null) {
            return false;
        }

        boolean meetsMinPeople = registeredPeople >= tourRoute.getMinPeople();
        if (!meetsMinPeople) {
            return false;
        }

        LocalDate today = LocalDate.now();
        long daysUntilDeparture = ChronoUnit.DAYS.between(today, tourRoute.getDepartureDate());
        return daysUntilDeparture <= DAYS_BEFORE_DEADLINE || registeredPeople >= tourRoute.getMinPeople() * 2;
    }

    @Override
    public String getStrategyName() {
        return "DEADLINE_AND_MIN_PEOPLE";
    }
}
