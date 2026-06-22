package com.travel.strategy;

import com.travel.entity.TourRoute;

public interface GroupFormationStrategy {

    boolean shouldFormGroup(TourRoute tourRoute, int registeredPeople);

    String getStrategyName();
}
