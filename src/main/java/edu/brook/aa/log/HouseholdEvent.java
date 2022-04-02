package edu.brook.aa.log;

import edu.brook.aa.HouseholdAggregate;

public class HouseholdEvent {
    int period;
    EventType eventType;
    HouseholdAggregate household;
    boolean decision;

    public HouseholdEvent(int period,
                          EventType eventType,
                          boolean decision,
                          HouseholdAggregate household) {
        this.period = period;
        this.eventType = eventType;
        this.decision = decision;
        this.household = household;
    }
}
