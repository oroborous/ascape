package edu.brook.aa.log;

import edu.brook.aa.HouseholdAggregate;
import edu.brook.aa.Location;

public class BuildFarmDecision {
    private int period;
    private EventType eventType;
    private HouseholdAggregate household;
    private Location location;
    private double distanceToWater;
    private boolean decision;

    private static double maxWaterDistance;

    public static void setMaxWaterDistance(double maxWaterDistance) {
        BuildFarmDecision.maxWaterDistance = maxWaterDistance;
    }

    public BuildFarmDecision(int period,
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
        // baseYield, sufficientBaseYield,
        // waterDist, sufficientWaterDist,
        // isAvailable
        return String.format("%d, %d, %s, %b, %d, %f, %b, %f, %b, %b",
                period, household.id, eventType.toString(), decision,
                household.getNutritionNeed(),
                location.getBaseYield(),
                location.getBaseYield() >= household.getNutritionNeed(),
                distanceToWater,
                distanceToWater <= maxWaterDistance,
                location.isAvailable());
    }
}
