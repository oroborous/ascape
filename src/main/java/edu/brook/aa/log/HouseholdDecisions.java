package edu.brook.aa.log;

import java.util.ArrayList;
import java.util.List;

public class HouseholdDecisions {
    private int period, id, age, nutritionNeed, totalCornStocks, estNextYearCorn;
    private double fissionRandom, fertility;
    private boolean hasFarm;
    private boolean starvation, oldAge, move, depart, fission;

    private List<HouseholdEvent> events = new ArrayList<>();

    public String getDecisionHistory() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < events.size(); i++) {
            if (i == 0)
                sb.append("Predicted: ").append(events.get(i).eventType);
            else if (events.get(i).decision) {
                sb.append(", Actual: ").append(events.get(i).eventType);
                return sb.toString();
            }
        }

        sb.append(", Actual: ").append(EventType.NONE);
        return sb.toString();
    }

    public String getFinalDecision() {
        // period, hhID, eventType, decision,
        // age, hasFarm, hasSettlement,
        // nutritionNeed,
        // nutritionAvail, totalCorn, nextYearCorn
        EventType choice = EventType.NONE;
        if (oldAge) {
            choice = EventType.DIE_OLD_AGE;
        } else if (starvation) {
            choice = EventType.DIE_STARVATION;
        } else if (depart) {
            choice = EventType.DEPART;
        } else if (move) {
            choice = EventType.MOVE;
        } else if (fission) {
            choice = EventType.FISSION;
        }

        return String.format("%d,%d,%d,%b,%d,%d,%d,%.3f,%.3f,%s",
                period,
                id,
                age,
                hasFarm,
                nutritionNeed,
                totalCornStocks,
                estNextYearCorn,
                fissionRandom,
                fertility,
                choice);
    }

    public boolean hasEvents() {
        return events.size() > 1;
    }

    public void setDecision(HouseholdEvent event) {
        if (event.isML || event.age == 0)
            return;

        events.add(event);

        // ignore the first event -- the decision tree ruling
        if (events.size() > 1) {
            this.period = event.period;
            this.id = event.id;
            this.age = event.age;
            this.nutritionNeed = event.nutritionNeed;
            this.totalCornStocks = event.totalCornStocks;
            this.estNextYearCorn = event.estNextYearCorn;
            this.hasFarm = event.hasFarm;
            this.fissionRandom = event.fissionRandom;
            this.fertility = event.fertility;

            switch (event.eventType) {
                case DIE_STARVATION:
                    starvation = event.decision;
                    break;
                case DIE_OLD_AGE:
                    oldAge = event.decision;
                    break;
                case DEPART:
                    depart = event.decision;
                    break;
                case MOVE:
                    move = event.decision;
                    break;
                case FISSION:
                    fission = event.decision;
                    break;
            }
        }
    }
}
