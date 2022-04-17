package edu.brook.aa.log;

import edu.brook.aa.HouseholdBase;

public class HouseholdEvent {
    int period, id, age, nutritionNeed, totalCornStocks, estNextYearCorn;
    boolean hasFarm;
    EventType eventType;
    double fissionRandom;
    boolean decision;

    public HouseholdEvent(int period,
                          EventType eventType,
                          boolean decision,
                          HouseholdBase household,
                          double fissionRandom) {
        this(period, eventType, decision, household);
        this.fissionRandom = fissionRandom;
    }

    public HouseholdEvent(int period,
                          EventType eventType,
                          boolean decision,
                          HouseholdBase household) {
        this.period = period;
        this.eventType = eventType;
        this.decision = decision;

        this.id = household.id;
        this.age = household.getAge();
        this.hasFarm = household.hasFarm();
        this.nutritionNeed = household.getNutritionNeed();
        this.totalCornStocks = household.getTotalCornStocks();
        this.estNextYearCorn = household.getEstimateNextYearCorn();
    }
}
