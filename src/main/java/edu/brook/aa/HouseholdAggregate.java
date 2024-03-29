/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package edu.brook.aa;


import edu.brook.aa.log.EventType;
import edu.brook.aa.log.HouseholdEvent;
import edu.brook.aa.log.Logger;
import edu.brook.aa.weka.WekaDecisionClassifier;
import org.ascape.model.Agent;
import org.ascape.model.rule.Rule;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMM;

public class HouseholdAggregate extends HouseholdBase {

    private static final long serialVersionUID = 5091800912116536871L;
    private static int nextId = 1;

    private int age;
    private int nutritionNeed;
    public final Rule DECISION_TREE_RULE_PRE = new Rule("(Pre) Consult Decision Tree") {

        public void execute(Agent agent) {
            HouseholdAggregate hha = (HouseholdAggregate) agent;

            Object[] props = new Object[]{(double) hha.getAge(),
                    Boolean.toString(hha.hasFarm()),
                    (double) hha.getNutritionNeed(),
                    (double) hha.getTotalCornStocks(),
                    (double) hha.getEstimateNextYearCorn(),
                    hha.getFissionRandom(),
                    hha.getFertility()};
            EventType decision = WekaDecisionClassifier.classify(props);

            Logger.INSTANCE.log(new HouseholdEvent(hha.getScape().getPeriod(),
                    decision, true, hha, hha.getFissionRandom()));
        }

        public boolean isCauseRemoval() {
            return false;
        }

        public boolean isRandomExecution() {
            return false;
        }
    };
    private int nutritionNeedRemaining;

    public boolean deathCondition() {
        boolean starvation = nutritionNeedRemaining > 0;
        boolean oldAge = age > deathAge;
        Logger.INSTANCE.log(new HouseholdEvent(getScape().getPeriod(),
                EventType.DIE_STARVATION, starvation, this, fissionRandom));
        Logger.INSTANCE.log(new HouseholdEvent(getScape().getPeriod(),
                EventType.DIE_OLD_AGE, oldAge, this, fissionRandom));

        if (nutritionNeedRemaining > 0) {
            getStatCollector(DEATHS_STARVATION).addValue(0.0);
            return true;
        }
        if (age > deathAge) {
            getStatCollector(DEATHS_OLD_AGE).addValue(0.0);
            return true;
        }
        return false;
        //return ((age > ((LHV) getRoot()).getHouseholdDeathAge())
        // || (nutritionNeedRemaining > 0));
    }

    public void fission() {
        HouseholdAggregate child = new HouseholdAggregate();//(Household) this.clone();
        scape.add(child);
        child.initialize();
        child.age = 0;
        giveMaizeGift(child);
        child.move();
        //if ((child.farm.getLocation() != null) && (child.settlement != null)) {
        //For now, record fissions regardless of successful move to match C++ code
        getStatCollector(FISSIONS).addValue(0.0);
        //}
        //System.out.println(child.age);
    }

    public boolean fissionCondition() {
        //return ((age > ((LHV) getRoot()).getFertilityAge())
        // && (getRandom().nextDouble() < ((LHV) getRoot()).getFertility()));
        //Rob's experiment
        boolean isFission = (age > fertilityAge) && (age <= fertilityEndsAge)
                && (fissionRandom < fertility);
        Logger.INSTANCE.log(new HouseholdEvent(getScape().getPeriod(),
                EventType.FISSION, isFission, this, fissionRandom));
        return isFission;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private double getFissionRandom() {
        return fissionRandom;
    }

    public int getNumAdults() {
        return 1;
    }

    public int getNutritionNeed() {
        return nutritionNeed;
    }

    @Override
    public int getStatCollectorIndex() {
        return 1;
    }

    @Override
    public String getStatCollectorSuffix() {
        return " (RB)";
    }

    public void initialize() {
        super.initialize();
        this.id = nextId++;
        setMembersActive(false);

        age = randomInRange(LHV.householdMinInitialAge, LHV.householdMaxInitialAge);
        nutritionNeed = randomInRange(LHV.householdMinNutritionNeed, LHV.householdMaxNutritionNeed);
    }

    public void metabolism() {
        super.metabolism();
        age++;
        nutritionNeedRemaining = consumeCorn(nutritionNeed);
    }

    public void scapeCreated() {
        super.scapeCreated();

        scape.addRule(METABOLISM_RULE);
        scape.addRule(DECISION_TREE_RULE_PRE);
        scape.addRule(DEATH_RULE);
        scape.addRule(MOVEMENT_RULE);
        scape.addRule(FISSIONING_RULE);

        String suffix = getStatCollectorSuffix();

        StatCollector[] stats = new StatCollector[4];
        stats[0] = new StatCollector(DEATHS_STARVATION + suffix, false);
        stats[1] = new StatCollector(DEATHS_OLD_AGE + suffix, false);
        stats[2] = new StatCollector(BIRTHS + suffix, false);
        stats[3] = new StatCollectorCSAMM(HOUSEHOLD_SIZE + suffix) {

            private static final long serialVersionUID = 5919164193477195628L;

            public final double getValue(Object o) {
                return 5;
            }
        };
        scape.addStatCollectors(stats);
    }
}
