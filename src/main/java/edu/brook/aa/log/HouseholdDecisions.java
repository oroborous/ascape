package edu.brook.aa.log;

import edu.brook.aa.HouseholdAggregate;

public class HouseholdDecisions {
    private HouseholdAggregate household;
    private double fissionRandom;
    private boolean starvation, oldAge, move, depart, fission;

    public HouseholdDecisions(HouseholdAggregate household) {
        this.household = household;
    }

    public void setDecision(HouseholdEvent event) {
        switch(event.eventType) {
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
                fissionRandom = event.fissionRandom;
                break;
        }
    }

    public String toString() {
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
        } else if (fission) {
            choice = EventType.FISSION;
        } else if (move) {
            choice = EventType.MOVE;
        }

        return String.format("%d,%b,%d,%f,%d,%f,%s",
                household.getAge(),
                household.hasFarm(),
//                household.hasSettlement(),
                household.getNutritionNeed(),
                household.getEstimatedNutritionAvailable(),
                household.getTotalCornStocks(),
//                household.getEstimateNextYearCorn(),
                fissionRandom,
                choice.toString());

//        return String.format("%d, %d, %b, %b, %d, %f, %d, %d, DIE %b, DEPART %b, MOVE %b, FISSION %b",
//                household.id,
//                household.getAge(),
//                household.hasFarm(),
//                household.hasSettlement(),
//                household.getNutritionNeed(),
//                household.getEstimatedNutritionAvailable(),
//                household.getTotalCornStocks(),
//                household.getEstimateNextYearCorn(),
//                die,
//                depart,
//                move,
//                fission);
    }
}
