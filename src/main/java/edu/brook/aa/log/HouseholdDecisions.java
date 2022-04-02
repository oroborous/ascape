package edu.brook.aa.log;

import edu.brook.aa.HouseholdAggregate;

public class HouseholdDecisions {
    private HouseholdAggregate household;
    private boolean die, move, depart, fission;

    public HouseholdDecisions(HouseholdAggregate household) {
        this.household = household;
    }

    public void setDecision(EventType eventType, boolean decision) {
        switch(eventType) {
            case DIE:
                die = decision;
                break;
            case DEPART:
                depart = decision;
                break;
            case MOVE:
                move = decision;
                break;
            case FISSION:
                fission = decision;
                break;
        }
    }

    public String toString() {
        // period, hhID, eventType, decision,
        // age, hasFarm, hasSettlement,
        // nutritionNeed,
        // nutritionAvail, totalCorn, nextYearCorn

        EventType choice = EventType.NONE;
        if (die) {
            choice = EventType.DIE;
        } else if (depart) {
            choice = EventType.DEPART;
        } else if (fission) {
            choice = EventType.FISSION;
        } else if (move) {
            choice = EventType.MOVE;
        }

        return String.format("%d, %d, %b, %b, %d, %f, %d, %d, %s",
                household.id,
                household.getAge(),
                household.hasFarm(),
                household.hasSettlement(),
                household.getNutritionNeed(),
                household.getEstimatedNutritionAvailable(),
                household.getTotalCornStocks(),
                household.getEstimateNextYearCorn(),
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
