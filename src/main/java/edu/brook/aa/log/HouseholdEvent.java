package edu.brook.aa.log;

import edu.brook.aa.HouseholdAggregate;
import edu.brook.aa.HouseholdBase;

public class HouseholdEvent {
    int period;
    EventType eventType;
    HouseholdAggregate household;
    double fissionRandom;
    boolean decision;

    public HouseholdEvent(int period,
                          EventType eventType,
                          boolean decision,
                          HouseholdBase household,
                          double fissionRandom) {
        this.period = period;
        this.eventType = eventType;
        this.decision = decision;
        this.household = household instanceof HouseholdAggregate ? (HouseholdAggregate) household : null;
        this.fissionRandom = fissionRandom;
    }

    public HouseholdEvent(int period,
                          EventType eventType,
                          boolean decision,
                          HouseholdBase household) {
        this.period = period;
        this.eventType = eventType;
        this.decision = decision;
        this.household = household instanceof HouseholdAggregate ? (HouseholdAggregate) household : null;
    }
}
