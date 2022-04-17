package edu.brook.aa.log;

public class HouseholdDecisions {
    private int id, age, nutritionNeed, totalCornStocks, estNextYearCorn;
    private double fissionRandom;
    private boolean hasFarm;
    private boolean starvation, oldAge, move, depart, fission;

    public HouseholdDecisions(int id) {
        this.id = id;
    }

    public void setDecision(HouseholdEvent event) {
        this.age = event.age;
        this.nutritionNeed = event.nutritionNeed;
        this.totalCornStocks = event.totalCornStocks;
        this.estNextYearCorn = event.estNextYearCorn;
        this.hasFarm = event.hasFarm;

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
        } else if (move) {
            choice = EventType.MOVE;
        } else if (fission) {
            choice = EventType.FISSION;
        }

        return String.format("%d,%b,%d,%d,%d,%f,%s",
                age,
                hasFarm,
                nutritionNeed,
                totalCornStocks,
                estNextYearCorn,
                fissionRandom,
                choice);

    }
}
