package edu.brook.aa.log;

import edu.brook.aa.HouseholdBase;
import edu.brook.aa.weka.HouseholdAggregateML;

public class HouseholdEvent {
    int period, id, age, nutritionNeed, totalCornStocks, estNextYearCorn;
    boolean hasFarm;
    EventType eventType;
    double fissionRandom, fertility;
    boolean decision;
    boolean isML;

    public HouseholdEvent(int period,
                          EventType eventType,
                          boolean decision,
                          HouseholdBase household,
                          double fissionRandom) {
        this.period = period;
        this.eventType = eventType;
        this.decision = decision;

        this.id = household.id;
        this.age = household.getAge();
        this.hasFarm = household.hasFarm();
        this.nutritionNeed = household.getNutritionNeed();
        this.totalCornStocks = household.getTotalCornStocks();
        this.estNextYearCorn = household.getEstimateNextYearCorn();
        this.fertility = household.getFertility();
        this.fissionRandom = fissionRandom;
        this.isML = household instanceof HouseholdAggregateML;
    }

    public String toString() {
        return String.format("[%d,%d,%d,%b,%d,%d,%d,%.3f,%.3f,%s,%b]%n",
                period,
                id,
                age,
                hasFarm,
                nutritionNeed,
                totalCornStocks,
                estNextYearCorn,
                fissionRandom,
                fertility,
                eventType,
                decision);
    }
}
