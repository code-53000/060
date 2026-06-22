package com.travel.strategy;

import com.travel.entity.TourRoute;
import org.springframework.stereotype.Component;

@Component
public class MinPeopleGroupFormationStrategy implements GroupFormationStrategy {

    @Override
    public boolean shouldFormGroup(TourRoute tourRoute, int registeredPeople) {
        if (tourRoute == null || tourRoute.getMinPeople() == null) {
            return false;
        }
        return registeredPeople >= tourRoute.getMinPeople();
    }

    @Override
    public String getStrategyName() {
        return "MIN_PEOPLE";
    }
}
