package edu.brook.aa.log;

import edu.brook.aa.HouseholdAggregate;
import edu.brook.aa.Location;

public class HouseholdEvent {
    private int period;
    private EventType eventType;
    private HouseholdAggregate household;
    private Location location;
    private double distanceToWater;
    private boolean decision;

    private static double maxWaterDistance;
    private static int householdMaxNutrition;

    public static void setMaxWaterDistance(double maxWaterDistance) {
        HouseholdEvent.maxWaterDistance = maxWaterDistance;
    }

    public static void setHouseholdMaxNutrition(int householdMaxNutrition) {
        HouseholdEvent.householdMaxNutrition = householdMaxNutrition;
    }

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
                          double distanceToWater) {
        this.period = period;
        this.eventType = eventType;
        this.decision = decision;
        this.household = household;
        this.location = location;
        this.distanceToWater = distanceToWater;
    }


    public String toString() {
        // period, hhID, eventType, decision,
        // age, hasSettlement, hasFarm,
        // nutritionAvail, totalCorn, nextYearCorn,
        // locYield, waterDist, isAvailable

        if (location == null) {
            return String.format("%d, %d, %s, %b, %d, %b, %b, %f, %d, %d",
                    period, household.id, eventType.toString(), decision,
                    household.getAge(), household.hasFarm(), household.hasSettlement(),
                    household.getEstimatedNutritionAvailable(),
                    household.getTotalCornStocks(), household.getEstimateNextYearCorn());
        }
        if (distanceToWater < maxWaterDistance)
            System.out.println("?");
        return String.format("%d, %d, %s, %b, %f, %b, %f, %b, %b",
                period, household.id, eventType.toString(), decision,
                location.getBaseYield(),
                location.getBaseYield() >= householdMaxNutrition,
                distanceToWater,
                distanceToWater <= maxWaterDistance,
                location.isAvailable());
    }
}
