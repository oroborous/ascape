package edu.brook.aa.log;

import edu.brook.aa.HouseholdAggregate;
import edu.brook.aa.Location;

public class HouseholdEvent {
    private int period;
    private EventType eventType;
    private HouseholdAggregate household;
    private Location location;
    private int distanceToWater;
    private boolean decision;

    public HouseholdEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public HouseholdEvent(int period,
                          EventType eventType,
                          boolean decision,
                          HouseholdAggregate household) {
        this.period = period;
        this.eventType = eventType;
        this.decision = decision;
        this.household = household;
    }

    public HouseholdEvent(int period,
                          EventType eventType,
                          boolean decision,
                          HouseholdAggregate household,
                          Location location,
                          int distanceToWater) {
        this.period = period;
        this.eventType = eventType;
        this.decision = decision;
        this.household = household;
        this.location = location;
        this.distanceToWater = distanceToWater;
    }


    public String toString() {
        if (household.getNumAdults() > 1) {
            System.out.println("Wow");
        }
        // period, hhID, eventType, decision,
        // age, hasSettlement, hasFarm,
        // nutritionAvail, totalCorn, nextYearCorn,
        // locYield, waterDist, isAvailable
        if (location == null) {
            return String.format("%d, %d, %s, %b, %d, %b, %b, %f, %d, %d, %d, %d, %b",
                    period, household.id, eventType.toString(), decision,
                    household.getAge(), household.hasFarm(), household.hasSettlement(),
                    household.getEstimatedNutritionAvailable(),
                    household.getTotalCornStocks(), household.getEstimateNextYearCorn(),
                    -1, -1, false);
        }
        return String.format("%d, %d, %s, %b, %d, %b, %b, %f, %d, %d, %f, %d, %b",
                period, household.id, eventType.toString(), decision,
                household.getAge(), household.hasFarm(), household.hasSettlement(),
                household.getEstimatedNutritionAvailable(),
                household.getTotalCornStocks(), household.getEstimateNextYearCorn(),
                location.getBaseYield(), distanceToWater,
                location.isAvailable());
    }
}
